import request from '@/utils/request'

// 员工相关接口，只有管理员可以操作
// 删除员工
export function deleteStaff(staffId) {
  const params = new URLSearchParams()
  params.append('id', staffId)

  return request({
    url: '/admin/staff/delete',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data: params,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

//修改员工信息
export function updateStaff(data) {
  return request({
    url: '/admin/staff/change',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

//分页查询员工
export function getStaffPage(params) {
  return request({
    url: '/admin/staff/page',
    method: 'post',
    baseURL: 'http://localhost:8080',
    params
  })
}

//新增员工
export function addStaff(data) {
  return request({
    url: '/admin/staff/add',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// //查询所有员工
// export function getAllStaff() {
//   return request({
//     url: '/admin/staff/all',
//     method: 'post',
//     baseURL: 'http://localhost:8080',
//   })
// }

// 部门相关接口
// 新增部门，这个只有管理员可以操作
export function addDepartment(data) {
  return request({
    url: '/admin/department/add',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

//查询某公司所有部门
export function getAllDepartments(companyName) {
  return request({
    url: '/admin/department/all',
    method: 'post',
    baseURL: 'http://localhost:8080',
    params: { companyName }
  })
}

//修改部门信息，这个只有管理员才能修改，普通员工不可以
export function updateDepartment(data) {
  return request({
    url: '/admin/department/change',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 公司相关接口
// 新增公司，这个只有管理员可以操作
export function addCompany(data) {
  return request({
    url: '/admin/company/add',
    method: 'post',
    baseURL: 'http://localhost:8080',
    data
  })
}

// 查询所有公司
export function getAllCompanies() {
  return request({
    url: '/admin/company/all',
    method: 'post',
    baseURL: 'http://localhost:8080'
  })
} 