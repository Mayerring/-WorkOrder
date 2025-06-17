import request from '@/utils/request'

// 获取工作流分页列表
export function getFlowPage(data) {
  return request({
    url: '/flow/page',
    method: 'post',
    data
  })
}

// 创建工作流
export function createWorkflow(data) {
  return request({
    url: '/flow/create',
    method: 'post',
    data
  })
}

// 删除工作流
export function deleteFlow(data) {
  return request({
    url: `/flow/delete`,
    method: 'post',
    data
  })
}

//编辑工作流
export function editFlow(data) {
  return request({
    url: `/flow/edit`,
    method: 'post',
    data
  })
}
