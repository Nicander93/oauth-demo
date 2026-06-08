---
title: "OAuth2 实战：Sa-Token 框架接入"
aliases: [Sa-Token OAuth2, oauth2实战]
tags: [type/literature, domain/development, status/seed]
created: 2026-05-17
updated: 2026-05-17
source:
  - https://sa-token.cc/doc.html#/oauth2/oauth2-oidc
---

# OAuth2 实战：Sa-Token 框架接入

## Sa-Token 的 OAuth2 模块定位

Sa-Token 是国内常用的 Java 权限框架，它内置了一套 OAuth2 服务端实现，可以让你的系统**作为授权服务器**，给其他第三方应用提供 OAuth2 登录授权。

也就是说，你用 Sa-Token 搭一个"你自己的认证中心"，然后你旗下的多个子系统（或者真正的第三方）都来这里做统一登录。

---

## 项目结构思路

通常需要两类服务：

```
auth-server   → 认证中心，集成 sa-token-oauth2-server
business-app  → 业务系统，集成 sa-token-oauth2-client（或手动处理）
```

如果是纯内部 SSO，business-app 也可以不用 SDK，自己处理 code 换 token 的逻辑。

---

## 依赖引入（auth-server 侧）

```xml
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-oauth2</artifactId>
    <version>${sa-token.version}</version>
</dependency>
```

---

## 核心配置

### application.yml

```yaml
sa-token:
  oauth2-server:
    enable: true
    # 是否开启 OIDC（如果需要 id_token 就开启）
    enable-oidc: true
    # OIDC 签发者，一般是你的域名
    oidc-issuer: https://your-auth.com
```

### 应用信息注册（相当于 client_id / client_secret）

Sa-Token 通过实现 `SaOAuth2DataLoader` 接口来管理应用信息，可以从数据库加载：

```java
@Component
public class OAuth2DataLoader implements SaOAuth2DataLoader {

    @Override
    public SaClientModel getClientModel(String clientId) {
        // 从数据库查询应用信息
        // 演示用，写死一个
        if ("app-001".equals(clientId)) {
            return new SaClientModel()
                .setClientId("app-001")
                .setClientSecret("secret-xxx")
                // 允许的回调地址
                .addAllowRedirectUris("http://localhost:8081/callback")
                // 允许的授权模式
                .addContractScopes("openid", "profile", "email")
                .setIsCode(true)       // 开启授权码模式
                .setIsPassword(false)  // 关闭密码模式
                .setIsClient(false);   // 关闭客户端模式
        }
        return null;
    }

    @Override
    public Object getUserId(String username, String password) {
        // 验证用户名密码，返回用户 id
        // 接对接你自己的用户服务
        return userService.checkLogin(username, password);
    }
}
```

---

## 授权码模式完整流程

### 第一步：跳转授权页

业务系统构造授权链接，引导用户跳转：

```
GET https://your-auth.com/oauth2/authorize
  ?response_type=code
  &client_id=app-001
  &redirect_uri=http://localhost:8081/callback
  &scope=openid profile
  &state=随机字符串（防CSRF）
```

Sa-Token 内置了授权页面，也可以自定义。

### 第二步：用户确认授权

用户在认证中心登录并点击"同意授权"，认证中心重定向回来：

```
GET http://localhost:8081/callback?code=xxxxxx&state=随机字符串
```

### 第三步：后端用 code 换 token

```java
// 业务系统后端发起请求
POST https://your-auth.com/oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code
&code=xxxxxx
&client_id=app-001
&client_secret=secret-xxx
&redirect_uri=http://localhost:8081/callback
```

响应：

```json
{
  "access_token": "...",
  "refresh_token": "...",
  "id_token": "eyJhbGc...",
  "token_type": "bearer",
  "expires_in": 7200
}
```

### 第四步：用 access_token 获取用户信息

```
GET https://your-auth.com/oauth2/userinfo
Authorization: Bearer ACCESS_TOKEN
```

如果开了 OIDC，也可以直接解码 `id_token`（JWT），拿到 `sub`（用户 ID）等信息。

---

## 开启 OIDC 后的额外端点

Sa-Token 开启 OIDC 后会自动暴露标准端点：

| 端点 | 说明 |
|---|---|
| `/.well-known/openid-configuration` | OIDC 发现文档，客户端可自动获取配置 |
| `/oauth2/userinfo` | 获取用户信息 |
| `/oauth2/jwks` | 公钥，用于验证 id_token 签名 |

客户端（业务系统）只需要知道认证中心地址，就能自动拉取所有端点配置。

---

## OIDC 典型实现：用户表关联

OIDC 里用户身份的唯一标识是 `sub`（subject），由认证中心发出，全局唯一。业务系统需要把它和本地用户表关联起来。

### 用户表设计

通常有两种思路：

**方案 A：在用户表里加字段**（适合只对接一个认证中心）

```sql
CREATE TABLE user (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  username   VARCHAR(64),
  email      VARCHAR(128),
  oidc_sub   VARCHAR(128) UNIQUE,   -- 认证中心返回的 sub
  oidc_provider VARCHAR(32),        -- 'internal' / 'google' 等
  created_at DATETIME
);
```

**方案 B：单独建绑定表**（适合多个 OIDC Provider，如微信、Google、内部SSO都要接）

```sql
CREATE TABLE user (
  id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64),
  email    VARCHAR(128)
);

CREATE TABLE user_oauth_binding (
  id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id    BIGINT NOT NULL,
  provider   VARCHAR(32),           -- 'google' / 'wechat' / 'internal'
  sub        VARCHAR(128),          -- 对应 id_token 里的 sub
  open_id    VARCHAR(128),          -- 部分平台额外有 openid
  UNIQUE KEY uk_provider_sub (provider, sub)
);
```

方案 B 更灵活，一个本地用户可以绑定多个第三方账号。

---

### 首次登录 vs 再次登录的处理逻辑

```java
@Service
public class OidcUserService {

    public Long resolveUser(OidcUserInfo info, String provider) {
        // 1. 用 provider + sub 查绑定关系
        UserOauthBinding binding = bindingRepo.findByProviderAndSub(provider, info.getSub());

        if (binding != null) {
            // 已绑定，直接返回本地用户 id
            return binding.getUserId();
        }

        // 2. 没有绑定记录 → 首次登录，自动创建本地用户
        User newUser = new User();
        newUser.setUsername(info.getName());
        newUser.setEmail(info.getEmail());
        userRepo.save(newUser);

        // 3. 建立绑定关系
        UserOauthBinding newBinding = new UserOauthBinding();
        newBinding.setUserId(newUser.getId());
        newBinding.setProvider(provider);
        newBinding.setSub(info.getSub());
        bindingRepo.save(newBinding);

        return newUser.getId();
    }
}
```

注意首次自动创建用户有一个坑：**如果邮箱已经存在怎么办**？

两种处理方式：
- 自动合并（按邮箱匹配已有用户，加绑定记录）→ 方便，但有安全风险（邮箱未验证的情况）
- 引导用户手动绑定（跳转到"账号关联"页面）→ 更安全，但体验略差

通常做法是：email 已验证（`email_verified: true`）才允许自动合并，否则走手动绑定。

---

### 完整 callback 流程

```java
@GetMapping("/callback")
public String callback(String code, String state) {
    // 1. 防 CSRF：验证 state 和 session 里存的一致
    validateState(state);

    // 2. 用 code 换 token
    TokenResponse tokenResp = oauth2Client.exchangeToken(code);

    // 3. 解析 id_token（验证签名 + 过期时间）
    OidcUserInfo userInfo = jwtParser.parse(tokenResp.getIdToken());

    // 4. 查/建用户，拿到本地 userId
    Long localUserId = oidcUserService.resolveUser(userInfo, "internal");

    // 5. 建立本地登录态
    StpUtil.login(localUserId);

    return "redirect:/home";
}
```

---

## 退出登录（单点登出）

单点登出要注意：光退本地 session 是不够的，还要通知认证中心注销全局登录态，否则其他系统还是登录的。

```java
// 退出本地
StpUtil.logout();

// 跳转到认证中心退出
return "redirect:https://your-auth.com/oauth2/logout?redirect_uri=http://localhost:8081/login";
```

认证中心退出后，再跳回业务系统的登录页。

---

## 常见问题

**Q: redirect_uri 不匹配报错**

检查注册的 `allowRedirectUris` 和请求里的 `redirect_uri` 是否完全一致，包括末尾的斜杠、http/https。

**Q: id_token 验证失败**

id_token 是 JWT，需要用认证中心的公钥（`/oauth2/jwks`）验证签名。如果是内部系统，也可以先用 HS256 对称密钥简化处理。

**Q: access_token 有效期多长合适**

- access_token：1-2 小时
- refresh_token：7-30 天
- id_token：和 access_token 一样短，因为它只用来一次性传递身份信息

**Q: 客户端模式用在哪**

微服务之间调用时，不涉及用户，服务直接用 `client_id + client_secret` 换 token，然后带着 token 调其他服务的接口。Sa-Token 也支持在 Gateway 层统一验证这种 token。

---

## 参考

- [[1、oauth]] - OAuth2 基础
- [[2、oauth OIDC]] - OIDC 身份层
- [Sa-Token OAuth2 文档](https://sa-token.cc/doc.html#/oauth2/oauth2-oidc)
- [JustAuth 开源第三方登录库](https://www.justauth.cn/)
