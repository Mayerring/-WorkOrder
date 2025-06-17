import request from '@/utils/request'

// 获取仪表盘数据看板
export function getDashboardOverview() {
    return request({
        url: '/dashboard/data',
        method: 'post',
        baseURL: 'http://localhost:8080'
    })
}

// 获取待办事项
export function getTodoList() {
    return request({
        url: '/dashboard/todo',
        method: 'post',
        baseURL: 'http://localhost:8080'
    })
}

// 获取工单状态统计
export function getStatusData(data) {
    return request({
        url: '/dashboard/status',
        method: 'post',
        data
    })
}

// 获取工单处理数量
export function getHandleQuantity(data) {
    return request({
        url: '/dashboard/handleQuantity',
        method: 'post',
        data
    })
}

// 获取消息列表
export function getMessages(data) {
    return request({
        url: '/dashboard/pageMessages',
        method: 'post',
        baseURL: 'http://localhost:8080',
        data
    })
}


