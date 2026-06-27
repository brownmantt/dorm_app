import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ROLE_ADMIN, ROLE_USER } from '@/utils/constants'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => roles.value.includes(ROLE_ADMIN))

  function setSession(payload) {
    token.value = payload.token
    username.value = payload.username || ''
    roles.value = payload.roles || [ROLE_USER]
    localStorage.setItem('token', token.value)
    localStorage.setItem('username', username.value)
    localStorage.setItem('roles', JSON.stringify(roles.value))
  }

  function logout() {
    token.value = ''
    username.value = ''
    roles.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('roles')
  }

  /**
   * @param {string|string[]} permission
   * @returns {boolean}
   */
  function hasPermission(permission) {
    const required = Array.isArray(permission) ? permission : [permission]
    if (required.includes(ROLE_ADMIN)) {
      return isAdmin.value
    }
    return isLoggedIn.value
  }

  return {
    token,
    username,
    roles,
    isLoggedIn,
    isAdmin,
    setSession,
    logout,
    hasPermission
  }
})
