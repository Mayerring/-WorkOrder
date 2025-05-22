import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({
    username: '',
    role: localStorage.getItem('role') || '',
    avatar: '',
    permissions: []
  })

  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = { ...userInfo.value, ...info }
    if (info.role) {
      localStorage.setItem('role', info.role)
    }
  }

  const login = async (username, password) => {
    // 模拟登录API调用
    if (username === 'admin' && password === '123456') {
      const token = 'demo-token'
      const userInfo = {
        username: 'admin',
        role: 'admin',
        avatar: '',
        permissions: ['*']
      }
      setToken(token)
      setUserInfo(userInfo)
      return true
    }
    throw new Error('用户名或密码错误')
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {
      username: '',
      role: '',
      avatar: '',
      permissions: []
    }
    localStorage.removeItem('token')
    localStorage.removeItem('role')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    login,
    logout
  }
}) 