import request from '@/utils/request'

// 查询员工列表
export function listEmployee(query) {
  return request({
    url: '/system/employee/list',
    method: 'get',
    params: query
  })
}

// 查询员工详情
export function getEmployee(employeeId) {
  return request({
    url: `/system/employee/${employeeId}`,
    method: 'get'
  })
}

// 新增员工
export function addEmployee(data) {
  return request({
    url: '/system/employee',
    method: 'post',
    data
  })
}

// 修改员工
export function updateEmployee(data) {
  return request({
    url: '/system/employee',
    method: 'put',
    data
  })
}

// 删除员工
export function delEmployee(employeeId) {
  return request({
    url: `/system/employee/${employeeId}`,
    method: 'delete'
  })
}

// 员工状态修改
export function changeEmployeeStatus(employeeId, status) {
  const data = {
    employeeId,
    status
  }
  return request({
    url: '/system/employee/changeStatus',
    method: 'put',
    data
  })
}

// 导出员工
export function exportEmployee(query) {
  return request({
    url: '/system/employee/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}

// 查询员工下拉选项（含可选绑定项）
export function listEmployeeOptions(query) {
  return request({
    url: '/system/employee/options',
    method: 'get',
    params: query
  })
}

// 查询可绑定账号列表
export function listEmployeeBindCandidates(query) {
  return request({
    url: '/system/employee/bind/candidates',
    method: 'get',
    params: query
  })
}

// 绑定账号
export function bindEmployeeAccount(data) {
  return request({
    url: '/system/employee/bind',
    method: 'post',
    data
  })
}

// 解绑账号
export function unbindEmployeeAccount(employeeId) {
  return request({
    url: `/system/employee/${employeeId}/bind`,
    method: 'delete'
  })
}
