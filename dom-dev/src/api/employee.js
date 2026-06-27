import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getEmployees(params) {
  return request.get('/employees', { params: buildQueryParams(params) })
}

/** 入居登録画面：未入居者のみ（更新日降順） */
export function getRegisterableEmployees(params = {}) {
  return getEmployees({
    ...params,
    notResidingOnly: true
  })
}

/** @param {Record<string, unknown>} params */
export function searchEmployees(params) {
  return getEmployees(params)
}

/** @param {Record<string, unknown>} params */
export function searchEmployeesSilent(params) {
  return request.get('/employees', { params: buildQueryParams(params), skipErrorHandler: true })
}

/** @param {string} employeeId */
export function getEmployee(employeeId) {
  return request.get(`/employees/${employeeId}`)
}

/** @param {Record<string, unknown>} data */
export function createEmployee(data) {
  return request.post('/employees', data)
}

/** @param {string} employeeId @param {Record<string, unknown>} data */
export function updateEmployee(employeeId, data) {
  return request.put(`/employees/${employeeId}`, data)
}

/** @param {string} employeeId */
export function deleteEmployee(employeeId) {
  return request.delete(`/employees/${employeeId}`)
}
