import request from '@/utils/request'

// 获取个人信息
export function getUserInfo() {
  return request({
    url: '/user/me',
    method: 'post',
    baseURL: 'http://localhost:8080'
  })
}

// 用户登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 获取员工信息
export function getStaffInfo(staffId) {
  return request({
    url: '/user/belong',
    method: 'post',
    baseURL: 'http://localhost:8080',
    params: {
      staffId
    }
  })
}

// 修改个人信息
export function updateUserInfo(data) {
  return request({
    url: '/user/change',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 分页查询所有员工
export function getStaffList(data) {
  return request({
    url: '/user/page',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 查看组织架构
export function getOrganization() {
  return request({
    url: '/user/organization',
    method: 'post',
    baseURL: 'http://localhost:8080'
  })
}

// 获取所有员工
export function getAllStaff() {
  return request({
    url: '/user/all',
    method: 'post',
    baseURL: 'http://localhost:8080'
  })
}