import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getUsageTypes(params) {
  return request.get('/usage-types', { params: buildQueryParams(params) })
}

/** @param {Record<string, unknown>} params */
export function getUsageTypesSilent(params) {
  return request.get('/usage-types', { params: buildQueryParams(params), skipErrorHandler: true })
}

/** @param {object} data */
export function createUsageType(data) {
  return request.post('/usage-types', data)
}

/** @param {string} id @param {object} data */
export function updateUsageType(id, data) {
  return request.put(`/usage-types/${id}`, data)
}

/** @param {string} id */
export function deleteUsageType(id) {
  return request.delete(`/usage-types/${id}`)
}
