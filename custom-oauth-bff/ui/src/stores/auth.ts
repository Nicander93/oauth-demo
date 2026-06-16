import { ref } from 'vue'
import { defineStore } from 'pinia'
import { fetchMe, logout as apiLogout, type MeResponse } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<MeResponse | null>(null)
  const loaded = ref(false)

  async function loadMe() {
    try {
      user.value = await fetchMe()
    } catch {
      user.value = null
    } finally {
      loaded.value = true
    }
  }

  function startOAuthLogin() {
    window.location.href = 'http://localhost:8070/login/oauth'
  }

  async function logout() {
    await apiLogout()
    user.value = null
  }

  return { user, loaded, loadMe, startOAuthLogin, logout }
})
