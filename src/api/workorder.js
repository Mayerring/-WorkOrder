import request from '@/utils/request'

// 获取工单分页列表
export function getWorkOrderPage(data) {
  return request({
    url: '/workOrder/page',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 获取工单详情
export function getWorkOrderDetail(data) {
  return request({
    url: '/workOrder/detail',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 处理工单
export function handleWorkOrder(data) {
  return request({
    url: '/workOrder/handle',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 删除工单
export function deleteWorkOrder(data) {
  return request({
    url: '/workOrder/delete',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 取消工单
export function cancelWorkOrder(data) {
  return request({
    url: '/workOrder/cancel',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 创建工单
export function createWorkOrder(data) {
  return request({
    url: '/workOrder/create',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

//审批工单
export function approvalWorkOrder(data) {
  return request({
    url: '/workOrder/approval',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 获取可分配的员工列表（用于派单）
export function getAssignableStaff() {
  return request({
    url: '/workOrder/assignableStaff',
    method: 'post',
    baseURL: 'http://localhost:8080'
  })
}

// 打印工单
export function printWorkOrder(data) {
  return request({
    url: '/workOrder/print',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data,
    responseType: 'blob'
  })
}
