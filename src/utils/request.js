import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080',  // 修改为实际的后端地址
  timeout: 10000,
  withCredentials: true  // 允许跨域携带cookie
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 直接使用token，因为后端返回的token已经包含了Bearer前缀
      config.headers['Authorization'] = token
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 关键：如果是文件流，直接返回，保证打印功能
    if (response.config.responseType === 'blob') {
      return response
    }
    const res = response.data
    // 如果响应成功但业务状态码不为1，说明业务出错
    if (res.code !== 1) {
      ElMessage.error(res.msg || '操作失败')
      // 如果是token相关错误，清除用户信息并跳转到登录页
      if (res.code === 401 || res.code === 403) {
        const userStore = useUserStore()
        userStore.clearUserInfo()
        router.push('/login')
      }
      return Promise.reject(new Error(res.msg || '操作失败'))
    }
    console.log('打印接口返回：', res)
    return res
  },
  error => {
    console.error('响应错误：', error)
    // 处理HTTP错误状态码
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          // 清除用户信息
          const userStore = useUserStore()
          userStore.clearUserInfo()
          // 跳转到登录页
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.response.data?.msg || '请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      ElMessage.error('请求配置错误')
    }
    return Promise.reject(error)
  }
)

export default service 