import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getResidences(params) {
  return request.get('/residences', { params: buildQueryParams(params) })
}

/** @param {object} data */
export function createResidence(data) {
  return request.post('/residences', data)
}

/** @param {object} data */
export function validateResidence(data) {
  return request.post('/residences/validate', data)
}

/** @param {string} id @param {object} data */
export function checkoutResidence(id, data) {
  return request.put(`/residences/${id}/checkout`, data)
}

/** @param {string} empId */
export function getFirstUseDate(empId) {
  return request.get(`/employees/${empId}/first-use-date`)
}

/** @param {string} empId @param {Record<string, unknown>} params */
export function getTotalUsageDays(empId, params) {
  return request.get(`/employees/${empId}/total-usage-days`, { params: buildQueryParams(params) })
}

/** @param {Record<string, unknown>} params */
export function getLongTermUsageAlerts(params) {
  return request.get('/alerts/long-term-usage', { params: buildQueryParams(params) })
}
