import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

const BFF_OAUTH_LOGIN = 'http://localhost:8070/login/oauth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login/oauth',
      name: 'oauth-login',
      beforeEnter: () => {
        window.location.href = BFF_OAUTH_LOGIN
        return false
      },
      component: HomeView,
    },
  ],
})

export default router
