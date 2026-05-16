# OAuth2 登录协议学习 DEMO — PRD & 技术架构设计

## 一、项目目标

实现一个完整的 OAuth2 登录协议学习型 DEMO，用于：

* 理解 OAuth2 Authorization Code 授权流程
* 理解 Client / Authorization Server / Resource Server 的职责划分
* 理解 Spring Security OAuth2 体系
* 演示第三方登录完整链路
* 支持后续扩展：

  * JWT
  * RBAC
  * 多客户端
  * OpenID Connect
  * SSO

项目定位：

```text
教学型 + 演示型 + 可扩展型
```

不是生产级 IAM 系统。

---

# 二、系统整体架构

系统采用经典 OAuth2 三角色架构。

```text
┌─────────────────────┐
│     Browser         │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│ oauth-client        │
│ OAuth2 Client       │
│ 第三方应用          │
└─────────┬───────────┘
          │ Authorization Code Flow
          ▼
┌─────────────────────┐
│ oauth-server        │
│ AuthorizationServer │
│ 授权中心            │
└─────────┬───────────┘
          │ Access Token
          ▼
┌─────────────────────┐
│ oauth-resource      │
│ Resource Server     │
│ 用户资源服务        │
└─────────────────────┘
```

---

# 三、技术选型

## 1. 后端技术栈

| 技术                          | 说明          |
| --------------------------- | ----------- |
| Java 21                     | 主语言         |
| Spring Boot 3.x             | 基础框架        |
| Spring Security 6           | 安全框架        |
| Spring Authorization Server | OAuth2 授权中心 |
| Spring OAuth2 Client        | OAuth2 客户端  |
| JWT                         | Token 格式    |
| Maven                       | 构建工具        |

---

## 2. 前端技术

学习阶段建议：

```text
Thymeleaf
```

原因：

* 少引入前后端分离复杂度
* 聚焦 OAuth 流程
* Spring Security 集成简单
* 调试方便

后续可扩展 Vue3。

---

## 3. 数据库

建议：

```text
PostgreSQL
```

原因：

* 与 Spring 生态兼容良好
* OAuth 表结构适合关系型
* 后续 JWT/审计扩展方便

---

## 4. Token 方案

第一阶段：

```text
JWT Access Token
```

优点：

* 无状态
* 容易观察结构
* 学习成本低

---

# 四、系统模块设计

---

# 1. oauth-server（授权服务端）

## 角色

OAuth2 Authorization Server。

负责：

* 用户登录
* 客户端认证
* 授权确认
* code 签发
* token 签发
* token 校验

---

## 核心功能

### 用户登录

```text
/login
```

功能：

* 用户名密码登录
* Session 管理

---

### OAuth2 授权接口

```text
/oauth2/authorize
```

职责：

* 校验 client_id
* 校验 redirect_uri
* 生成 authorization code

---

### Token 接口

```text
/oauth2/token
```

职责：

* code 换 access_token
* JWT 签发

---

### JWK 接口

```text
/oauth2/jwks
```

用于 JWT 公钥暴露。

---

## 数据模型

### 用户表

```sql
sys_user
```

字段：

```text
id
username
password
nickname
status
create_time
```

---

### OAuth 客户端表

```sql
oauth_client
```

字段：

```text
client_id
client_secret
redirect_uri
scope
grant_types
```

---

### 授权码表（可选）

如果使用 JDBC 持久化。

```text
oauth2_authorization
```

---

## 分层设计

```text
controller
service
repository
domain
security
oauth
config
```

---

## 文件结构建议

```text
oauth-server
├── config
│   ├── SecurityConfig
│   ├── AuthorizationServerConfig
│   └── JwtConfig
│
├── controller
│   ├── LoginController
│   └── UserController
│
├── service
│   ├── UserService
│   └── OAuthClientService
│
├── repository
│   ├── UserRepository
│   └── OAuthClientRepository
│
├── security
│   ├── CustomUserDetailsService
│   ├── JwtTokenCustomizer
│   └── PasswordEncoderConfig
│
├── oauth
│   ├── OAuth2AuthorizationService
│   └── OAuth2TokenService
│
└── resources
    ├── templates
    ├── static
    └── application.yml
```

---

# 2. oauth-client（客户端）

## 角色

模拟：

```text
第三方网站
```

例如：

* GitHub 登录接入方
* 微信登录接入方

---

## 功能

### 首页

```text
/
```

显示：

```text
未登录
点击 OAuth 登录
```

---

### OAuth 登录入口

```text
/oauth2/authorization/demo-client
```

由 Spring 自动生成。

---

### 登录成功页

```text
/home
```

展示：

```text
用户名
token
用户信息
```

---

## 核心职责

### 发起授权请求

重定向：

```text
/oauth2/authorize
```

---

### 处理 callback

```text
/login/oauth2/code/demo-client
```

---

### 使用 token 请求资源服务

```text
/userinfo
```

---

## 分层设计

客户端业务较轻：

```text
controller
service
config
security
```

---

## 文件结构

```text
oauth-client
├── config
│   └── OAuth2ClientConfig
│
├── controller
│   ├── HomeController
│   └── LoginController
│
├── service
│   └── UserInfoService
│
├── security
│   └── OAuth2LoginSuccessHandler
│
└── resources
    ├── templates
    └── application.yml
```

---

# 3. oauth-resource（资源服务）

## 角色

OAuth2 Resource Server。

负责：

* 校验 JWT
* 返回用户资源

---

## 功能

### 用户信息接口

```text
GET /userinfo
```

返回：

```json
{
  "id": 1,
  "username": "admin",
  "nickname": "管理员"
}
```

---

## 核心能力

### JWT 校验

```text
Bearer Token 验证
```

---

## 分层

```text
controller
service
security
config
```

---

## 文件结构

```text
oauth-resource
├── config
│   └── ResourceServerConfig
│
├── controller
│   └── UserInfoController
│
├── service
│   └── UserInfoService
│
├── security
│   └── JwtAuthenticationConverter
│
└── resources
    └── application.yml
```

---

# 五、OAuth2 核心流程设计

---

# 1. 登录流程

## Step1

用户访问：

```text
http://localhost:8080
```

---

## Step2

点击：

```text
OAuth 登录
```

---

## Step3

客户端跳转：

```text
/oauth2/authorize
```

---

## Step4

授权服务端登录。

---

## Step5

授权成功：

```text
302 -> callback?code=xxx
```

---

## Step6

客户端：

```text
code -> access_token
```

---

## Step7

客户端：

```text
Bearer Token -> /userinfo
```

---

## Step8

完成登录。

---

# 六、权限模型设计

第一阶段：

```text
用户 -> 角色
```

即可。

例如：

```text
ROLE_ADMIN
ROLE_USER
```

JWT 中保存：

```json
{
  "sub": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

---

# 七、日志与调试设计

建议：

## 开启 Spring Security DEBUG

```yaml
logging:
  level:
    org.springframework.security: TRACE
```

可以清晰看到：

```text
FilterChain
Authentication
Authorization
Token生成
```

非常适合学习。

---

# 八、学习阶段建议

---

# 第一阶段

只完成：

```text
授权码模式登录
```

---

# 第二阶段

加入：

```text
JWT
RBAC
数据库
```

---

# 第三阶段

加入：

```text
OIDC
Refresh Token
多客户端
注销
单点登录
```

---

# 九、后续扩展方向

## 1. Vue3 前后端分离

```text
oauth-client-vue
```

---

## 2. OpenID Connect

实现：

```text
id_token
userinfo
openid scope
```

---

## 3. SSO

多个 client：

```text
client-a
client-b
```

共享登录态。

---

## 4. 多租户

```text
tenant_id
```

---

## 5. 第三方登录聚合

模拟：

* 微信登录
* GitHub 登录
* 钉钉登录

---

# 十、推荐目录（最终）

```text
oauth-demo
├── oauth-server
├── oauth-client
├── oauth-resource
├── docs
│   ├── architecture
│   ├── sequence-diagram
│   └── api
└── docker
```

---

# 十一、推荐补充内容

建议你再额外做：

## 1. 时序图

OAuth2 登录时序。

---

## 2. FilterChain 图

Spring Security Filter 执行链。

---

## 3. JWT 结构分析

Header / Payload / Signature。

---

## 4. OAuth2 四种模式对比

```text
authorization_code
client_credentials
password（废弃）
implicit（废弃）
```

---

# 十二、最终目标

这个 DEMO 最终应该达到：

```text
像一个微型 Keycloak
```

但：

* 代码量小
* 结构清晰
* 易调试
* 易学习
* 易扩展

核心重点不在“业务”，而在：

```text
OAuth2 协议流转
Spring Security 架构
认证授权体系
```
