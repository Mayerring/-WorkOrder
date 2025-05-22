import request from '@/utils/request'

// 获取工单列表
export function getWorkOrderList(params) {
  return request({
    url: '/workorder/list',
    method: 'get',
    params
  })
}

// 创建工单
export function createWorkOrder(data) {
  return request({
    url: '/workorder/create',
    method: 'post',
    data
  })
}

// 获取工单详情
export function getWorkOrderDetail(id) {
  return request({
    url: `/workorder/detail/${id}`,
    method: 'get'
  })
}

// 更新工单状态
export function updateWorkOrderStatus(id, data) {
  return request({
    url: `/workorder/status/${id}`,
    method: 'put',
    data
  })
}

// 转派工单
export function transferWorkOrder(id, data) {
  return request({
    url: `/workorder/transfer/${id}`,
    method: 'put',
    data
  })
}

// 获取工单统计数据
export function getWorkOrderStats() {
  return request({
    url: '/workorder/stats',
    method: 'get'
  })
} 