<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()

const errorMessage = computed(() => {
  const error = route.query.error
  if (error === 'invalid_state') {
    return '登录失败：state 校验未通过'
  }
  if (error === 'no_local_user') {
    const sub = route.query.sub
    return `登录失败：认证中心账号（sub=${sub}）在本系统无对应本地用户，且用户名无法自动匹配。本系统仅预置本地用户 alice（U10001）。`
  }
  if (error === 'login_failed') {
    return '登录失败'
  }
  return null
})

onMounted(() => {
  auth.loadMe()
})
</script>

<template>
  <div v-if="!auth.loaded">加载中…</div>

  <div v-else-if="!auth.user">
    <h2>系统 BFF - OAuth2 / OIDC Client Demo</h2>
    <p>尚未登录</p>
    <button class="btn" type="button" @click="auth.startOAuthLogin">
      使用 DemoAuth 登录（scope=openid profile）
    </button>
    <div class="nav">
      <a href="http://localhost:9000/portal">返回统一认证门户</a>
      ·
      <a href="http://localhost:8060/">进入系统 A（SSR）</a>
    </div>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>

  <div v-else>
    <h2>系统 BFF - 登录成功</h2>
    <p><strong>OIDC sub（认证中心）:</strong> {{ auth.user.oidc.sub }}</p>
    <p><strong>preferred_username:</strong> {{ auth.user.oidc.username }}</p>
    <p><strong>本地用户编码:</strong> {{ auth.user.localUser.userCode }}</p>
    <p><strong>本地用户名:</strong> {{ auth.user.localUser.username }}</p>
    <p><strong>本地昵称:</strong> {{ auth.user.localUser.nickname }}</p>
    <p><strong>access_token:</strong></p>
    <pre class="token">{{ auth.user.accessToken }}</pre>
    <p><strong>id_token:</strong></p>
    <pre class="token">{{ auth.user.idToken }}</pre>
    <button class="btn" type="button" @click="auth.logout">退出登录（仅本系统）</button>
    <div class="nav">
      <a href="http://localhost:9000/portal">返回统一认证门户</a>
      ·
      <a href="http://localhost:8060/">进入系统 A（SSR）</a>
    </div>
    <div class="hint">
      OAuth 在 BFF :8070 完成（Portal 入口也是 /login/oauth）；页面在 :5173，/api 经 Vite 代理到 BFF。localhost 下 Session Cookie 跨端口共享。
    </div>
  </div>
</template>
