import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({
    staffNumber: '',
    name: '',
    phone: '',
    email: '',
    company: '',
    department: '',
    position: '',
    role: localStorage.getItem('role') || '',
    avatar: '',
    status: 0,
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

  const clearUserInfo = () => {
    token.value = ''
    userInfo.value = {
      staffNumber: '',
      name: '',
      phone: '',
      email: '',
      company: '',
      department: '',
      position: '',
      role: '',
      avatar: '',
      status: 0,
      permissions: []
    }
    localStorage.removeItem('token')
    localStorage.removeItem('role')
  }

  const fetchUserInfo = async () => {
    try {
      const res = await getUserInfo()
      if (res.code === 1 && res.data) {
        setUserInfo(res.data)
        return res.data
      }
      return null
    } catch (error) {
      console.error('获取用户信息失败：', error)
      return null
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    setToken,
    setUserInfo,
    clearUserInfo,
    fetchUserInfo
  }
}) 