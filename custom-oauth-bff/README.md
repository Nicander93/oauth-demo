# custom-oauth-bff

Spring Boot BFF + Vue 分离部署示例。

- **OAuth 握手**：在 BFF `:8070` 完成（`/login/oauth`、`/callback`）
- **业务页面**：Vue `:5173`，`/api` 经 Vite 代理到 BFF
- **Portal 入口**：http://localhost:9000/portal →「进入系统 BFF」→ `http://localhost:8070/login/oauth`

## 架构

```
Portal / Vue 登录按钮
    → BFF :8070 /login/oauth → auth-server :9000
    → 回调 BFF :8070 /callback（写 Session）
    → 302 到 Vue :5173/
    → GET /api/me（Vite 代理 → BFF）
```

| 组件 | 端口 | 说明 |
|------|------|------|
| auth-server | 9000 | 认证中心 |
| bff-server | 8070 | OAuth + REST API；Portal 直接跳转此端口 |
| ui (Vite) | 5173 | 页面展示；OAuth 完成后跳回此处 |

## 启动

**1. 授权服务器**（改 `ClientRepository` 后需重启）

```bash
cd custom-oauth-demo/auth-server
mvn spring-boot:run
```

**2. BFF 后端**

```bash
cd custom-oauth-bff/bff-server
mvn spring-boot:run
```

**3. Vue 前端**（看页面需要；Portal OAuth 完成后会跳 5173）

```bash
cd custom-oauth-bff/ui
npm install
npm run dev
```

## Portal SSO 流程

1. 登录 http://localhost:9000/portal
2. 点击「进入系统 BFF」→ `8070/login/oauth`
3. IdP 已有 Session 则跳过密码，发 code
4. BFF `/callback` 换票 → **302 到 http://localhost:5173/**
5. Vue 调 `GET /api/me` 显示登录信息

**注意**：步骤 4 之后需要 **ui 在运行**，否则 5173 打不开；但 OAuth 本身只需 auth-server + bff-server。

## 预置数据

- 本地用户：`alice` / `password`，编码 `U10001`

## 开发说明

- `redirect_uri`：`http://localhost:8070/callback`（在 auth-server 注册）
- axios `withCredentials: true`；localhost Cookie 不区分端口，`8070` 写的 Session 在 `5173` 调 `/api/me` 仍有效
- Vite 代理 `/api`、`/login` 到 BFF；若误开 `5173/login/oauth` 也会转发到 `8070`（勿删 `/login` 代理，否则会白屏）
- Portal 入口请用 `8070/login/oauth`；改 `ClientRepository` 后需重启 auth-server
