---
title: "OIDC：在 OAuth2 上补一层身份认证"
aliases: [OIDC, OpenID Connect]
tags: [type/literature, domain/development, status/seed]
created: 2026-05-17
updated: 2026-05-17
source:
  - https://sa-token.cc/doc.html#/oauth2/oauth2-oidc
  - https://icyfenix.cn/architect-perspective/general-architecture/system-security/authorization.html#oauth2
---

# OIDC：在 OAuth2 上补一层身份认证

## OAuth2 的局限

OAuth2 解决的是"授权"问题，本质是：**让第三方应用能代表用户访问某些资源**。

但它没有定义"用户是谁"这件事。

拿授权码模式举例，第三方应用拿到 `access_token` 之后，只知道"我有权限访问某些资源"，但：
- 这个 token 对应的是哪个用户？
- 这个用户叫什么名字、邮箱是什么？
- 我该怎么把它和我系统里的账号关联起来？

OAuth2 对这些问题没有统一的规定。各家平台的做法不一，比如用 token 去访问 `/userinfo` 接口，但字段格式各不相同。

---

**OIDC = OAuth2 + 标准化的身份层**

OpenID Connect（OIDC）就是为了解决这个问题，它在 OAuth2 的基础上引入 **JWT (JSON Web Token)** 作为 ID Token，专门用来传递用户身份信息，并规范化了获取用户信息的方式。

## OIDC 新增了什么

相比 OAuth2，OIDC 主要增加了三样东西：

| 新增内容 | 说明 |
|---|---|
| `id_token` | 一个 JWT，包含用户身份信息，随 `access_token` 一起返回 |
| `scope=openid` | 触发 OIDC 流程的魔法参数，加上它才会返回 `id_token` |
| `/userinfo` 端点 | 标准化的用户信息接口，用 `access_token` 访问 |

**核心：ID Token 是什么**

`id_token` 是一个 JWT（JSON Web Token），解码后大概长这样：

```json
{
  "iss": "https://accounts.google.com",   // 谁发的
  "sub": "1234567890",                    // 用户唯一标识
  "aud": "your-client-id",               // 给谁的（client_id）
  "exp": 1716912000,                      // 过期时间
  "iat": 1716908400,                      // 签发时间
  "name": "张三",
  "email": "zhangsan@example.com",
  "picture": "https://..."
}
```

`sub`（subject）是用户在这个授权服务器上的唯一标识，相当于 openid。

---

## OIDC 的完整流程（授权码模式）

和 OAuth2 授权码流程几乎一样，区别只在 scope 里加了 `openid`，响应里多了 `id_token`。

```
① 跳转授权页，scope 里加上 openid
GET https://auth.server.com/authorize
  ?response_type=code
  &client_id=CLIENT_ID
  &redirect_uri=CALLBACK
  &scope=openid profile email    ← 关键

② 用户同意，返回 code

③ 后端用 code 换 token（和 OAuth2 一样）

④ 响应中多了 id_token：
{
  "access_token": "...",
  "id_token": "eyJhbGc...",      ← 新增
  "token_type": "bearer",
  "expires_in": 3600
}

⑤ 客户端解码验证 id_token，拿到用户信息
   也可以用 access_token 访问 /userinfo 端点
```

OIDC 常见 scope：
- `openid`：必须有，触发 OIDC
- `profile`：name、picture、locale 等
- `email`：email 和 email_verified
- `phone`：手机号

---

## OIDC 如何实现 SSO

OIDC 是目前主流的 SSO 实现方案。多个业务系统共用一个**认证中心**（OIDC Provider）：

```
用户第一次登录
  → 跳转到认证中心
  → 认证中心验证身份，建立全局 session
  → 返回 code 给业务系统 A
  → A 换取 id_token，创建本地 session

用户访问业务系统 B
  → 跳转到认证中心
  → 认证中心发现有全局 session，直接静默授权
  → 返回 code 给 B（用户无感知）
  → B 换取 id_token，创建本地 session
```

全局登录态由认证中心维护，业务系统只负责本地 session。登出时需要通知认证中心注销全局 session，否则其他系统还是登录状态。

---

## 用微信登录的完整流程（OIDC 视角）

```
① 用户点击「微信登录」
② 跳转到微信授权页
③ 微信确认用户身份，返回 code
④ 后端用 code 换取 access_token + id_token（或者换 token 再请求 /userinfo）
⑤ 从 id_token / userinfo 拿到 openid / unionid
⑥ 根据 openid 创建或绑定本地用户
⑦ 业务系统自己给用户发 session / cookie
```

这里微信扮演的是 OIDC Provider（授权服务器+身份提供者），你的 App 是 Relying Party（依赖方）。

---

## OIDC vs OAuth2 对比

| 维度 | OAuth2 | OIDC |
|---|---|---|
| 解决的问题 | 授权（你能做什么） | 认证（你是谁） |
| 返回内容 | access_token | access_token + id_token |
| 用户身份 | 没有标准规定 | sub 字段统一标识 |
| 适合场景 | 第三方应用访问资源 | 登录、SSO |

> OIDC 是"站在 OAuth2 肩膀上"的身份层。OAuth2 负责"你有没有权限"，OIDC 负责"你是谁"。两者一起才构成一个完整的认证授权体系。

---

## 参考

- [[1、oauth]] - OAuth2 基础
- [[3、oauth实战（结合框架）]] - Sa-Token 的 OIDC 实现
- [Sa-Token OAuth2/OIDC 文档](https://sa-token.cc/doc.html#/oauth2/oauth2-oidc)
