# 手写 OAuth2 Authorization Code DEMO — PRD

## 一、项目背景

OAuth2 是现代系统中最核心的认证授权协议之一。

但对于初学者而言：

* Spring Security 体系复杂
* Spring Authorization Server 抽象层级过高
* OAuth2 实际协议流转不直观
* 很难理解：

  * code 从哪里生成
  * token 如何签发
  * redirect_uri 为什么重要
  * state 如何防攻击
  * client 与 authorization server 的真实关系

因此，需要实现一个：

```text
不依赖 Spring Security
不依赖 Spring OAuth
不依赖 Authorization Server
```

的最小 OAuth2 DEMO。

目标是：

```text
用最少代码理解 OAuth2 Authorization Code 模式
```

---

# 二、项目目标

实现一个完整但极简的：

```text
OAuth2 Authorization Code Flow
```

学习型 DEMO。

---

# 三、项目定位

项目定位：

```text
教学型 / 演示型 / 协议学习型
```

不是生产级认证中心。

---

# 四、项目目标用户

目标用户：

* OAuth2 初学者
* Spring Boot 初学者
* 认证授权学习者
* 想理解 OAuth2 底层机制的开发者

---

# 五、项目范围

---

## 包含内容

### OAuth2 核心流程

* authorization code
* access token
* userinfo
* redirect
* state
* bearer token

---

## 用户登录

基于：

```text
HttpSession
```

实现简单登录。

---

## OAuth2 三角色

实现：

| 角色                   | 是否实现    |
| -------------------- | ------- |
| Client               | 是       |
| Authorization Server | 是       |
| Resource Server      | 合并到授权服务 |

---

---

## 不包含内容

第一阶段不实现：

```text
Spring Security
JWT
OIDC
Refresh Token
PKCE
RBAC
数据库
Consent 授权页
动态客户端注册
多租户
```

**后续已实现（SSO 演示）**：多 RP（`client-app-a` + `client-app-b`）+ 统一门户 `/portal`；单点登出联动、`prompt=none` 仍不做。

---

# 六、系统整体架构

```text
manual-oauth-demo
├── auth-server
├── client-app-a
└── client-app-b
```

---

# 七、系统架构图

```text
┌──────────────────┐
│ Browser          │
└────────┬─────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌─────────┐ ┌─────────┐
│client-app-a│ │client-app-b│
│ OAuth2  │ │ OAuth2  │
│ Client  │ │ Client  │
└────┬────┘ └────┬────┘
     │ redirect  │
     └─────┬─────┘
           ▼
┌──────────────────┐
│ auth-server      │
│ Authorization    │
│ Server + /portal │
└──────────────────┘
```

---

# 八、技术选型

## 后端

| 技术            | 作用         |
| ------------- | ---------- |
| Java 21       | 主语言        |
| Spring Boot 3 | Web 容器     |
| Spring MVC    | Controller |
| Thymeleaf     | 页面模板       |
| HttpSession   | 登录态        |
| RestClient    | HTTP 调用    |

---

## 数据存储

第一阶段：

```text
ConcurrentHashMap
```

内存存储。

---

## 构建工具

```text
Maven
```

---

# 九、端口设计

| 服务          | 端口   |
| ----------- | ---- |
| auth-server | 9000 |
| client-app-a  | 8080 |

---

# 十、功能模块设计

---

# 1. auth-server

## 功能定位

OAuth2 授权服务端。

负责：

* 用户登录
* code 签发
* token 签发
* userinfo 查询

---

## 模块列表

| 模块          | 说明       |
| ----------- | -------- |
| 登录模块        | 用户登录     |
| OAuth2 授权模块 | code 生成  |
| Token 模块    | token 签发 |
| UserInfo 模块 | 用户信息返回   |

---

# 十一、auth-server 功能设计

---

# 1. 登录模块

## 页面

```text
GET /login
```

显示登录页。

---

## 登录接口

```text
POST /login
```

参数：

```text
username
password
```

---

## 登录成功

写入：

```java
session.setAttribute("LOGIN_USER", user)
```

---

# 2. OAuth 授权接口

## 接口

```text
GET /oauth/authorize
```

---

## 请求参数

| 参数            | 说明      |
| ------------- | ------- |
| response_type | 固定 code |
| client_id     | 客户端ID   |
| redirect_uri  | 回调地址    |
| scope         | 权限范围    |
| state         | 防CSRF   |

---

## 功能逻辑

### Step1

检查用户是否登录。

未登录：

```text
302 -> /login
```

---

### Step2

校验：

```text
client_id
redirect_uri
response_type
scope
```

---

### Step3

生成：

```text
authorization_code
```

---

### Step4

保存 code。

---

### Step5

302 回客户端：

```text
redirect_uri?code=xxx&state=yyy
```

---

# 3. Token 接口

## 接口

```text
POST /oauth/token
```

---

## Header

```text
Authorization: Basic base64(client_id:client_secret)
```

---

## Body

```text
grant_type=authorization_code
code=xxx
redirect_uri=xxx
```

---

## 功能逻辑

### Step1

解析 Basic Auth。

---

### Step2

校验：

```text
client_id
client_secret
```

---

### Step3

查询 code。

---

### Step4

检查：

```text
是否过期
是否已使用
redirect_uri 是否一致
```

---

### Step5

生成：

```text
access_token
```

---

### Step6

标记 code 已使用。

---

### Step7

返回 token。

---

# 4. UserInfo 接口

## 接口

```text
GET /userinfo
```

---

## Header

```text
Authorization: Bearer xxx
```

---

## 功能逻辑

### Step1

解析 token。

---

### Step2

查询 token。

---

### Step3

校验：

```text
是否存在
是否过期
```

---

### Step4

返回用户信息。

---

# 十二、client-app-a 功能设计

---

# 1. 首页

## 接口

```text
GET /
```

---

## 页面内容

未登录：

```text
使用 DemoAuth 登录
```

已登录：

```text
当前用户信息
access_token
退出登录
```

---

# 2. OAuth 登录入口

## 接口

```text
GET /login/oauth
```

---

## 功能

拼接：

```text
/oauth/authorize
```

授权地址。

---

# 3. OAuth Callback

## 接口

```text
GET /callback
```

---

## 功能逻辑

### Step1

获取：

```text
code
state
```

---

### Step2

校验 state。

---

### Step3

调用：

```text
/oauth/token
```

换 token。

---

### Step4

调用：

```text
/userinfo
```

获取用户信息。

---

### Step5

写入 Session。

---

# 十三、数据模型设计

---

# 1. User

```java
public record User(
    Long id,
    String username,
    String password,
    String nickname
)
```

---

# 2. OAuthClient

```java
public record OAuthClient(
    String clientId,
    String clientSecret,
    Set<String> redirectUris,
    Set<String> scopes
)
```

---

# 3. OAuthCode

```java
public record OAuthCode(
    String code,
    String clientId,
    String username,
    String redirectUri,
    Set<String> scopes,
    Instant expiresAt,
    boolean used
)
```

---

# 4. AccessToken

```java
public record AccessToken(
    String token,
    String clientId,
    String username,
    Set<String> scopes,
    Instant expiresAt
)
```

---

# 十四、项目目录结构

---

# auth-server

```text
auth-server
├── controller
│   ├── LoginController
│   ├── OAuthAuthorizeController
│   ├── OAuthTokenController
│   └── UserInfoController
│
├── service
│   ├── LoginService
│   ├── OAuthClientService
│   ├── OAuthCodeService
│   ├── AccessTokenService
│   └── UserService
│
├── repository
│   ├── UserRepository
│   ├── ClientRepository
│   ├── CodeRepository
│   └── TokenRepository
│
├── model
│   ├── User
│   ├── OAuthClient
│   ├── OAuthCode
│   └── AccessToken
│
├── util
│   ├── RandomTokenUtil
│   ├── Base64Util
│   └── UrlUtil
│
└── resources
    ├── templates
    └── application.yml
```

---

# client-app-a

```text
client-app-a
├── controller
│   ├── HomeController
│   ├── OAuthLoginController
│   └── CallbackController
│
├── service
│   ├── OAuthAuthorizeUrlBuilder
│   ├── OAuthTokenService
│   └── UserInfoService
│
├── model
│   ├── TokenResponse
│   └── UserInfo
│
├── config
│   └── OAuthClientProperties
│
└── resources
    ├── templates
    └── application.yml
```

---

# 十五、关键安全规则

即使是 Demo，也保留基础安全规则。

---

## 1. redirect_uri 必须精确匹配

防止：

```text
code 被重定向到恶意网站
```

---

## 2. code 只能使用一次

防止：

```text
授权码重放
```

---

## 3. code 需要过期时间

建议：

```text
5分钟
```

---

## 4. access_token 需要过期

建议：

```text
1小时
```

---

## 5. state 必须校验

防止：

```text
CSRF
```

---

## 6. token 接口必须 POST

避免：

```text
token 出现在 URL
```

---

# 十六、页面设计

---

# auth-server

## 登录页

```text
用户名
密码
登录按钮
```

---

# client-app-a

## 首页

```text
使用 DemoAuth 登录
```

---

## 登录成功页

显示：

```text
username
nickname
access_token
```

---

# 十七、日志设计

建议打印：

```text
生成 code
校验 code
生成 token
校验 token
redirect_uri 校验
state 校验
```

方便观察协议流转。

---

# 十八、后续扩展方向

第二阶段可以扩展：

---

## JWT

token 改为 JWT。

---

## Spring Security

替换 Session 登录。

---

## Resource Server

拆分 userinfo。

---

## Refresh Token

支持续期。

---

## OIDC

增加：

```text
id_token
openid
```

---

## PKCE

支持 SPA / 移动端。

---

# 十九、项目最终目标

最终目标：

```text
通过最少代码，
彻底理解 OAuth2 Authorization Code 流程
```

核心重点：

```text
协议流转
code 生命周期
token 生命周期
client/server 职责
redirect 安全性
```
