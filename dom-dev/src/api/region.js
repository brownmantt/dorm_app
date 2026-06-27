import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getRegions(params) {
  return request.get('/regions', { params: buildQueryParams(params) })
}

/** @param {Record<string, unknown>} params */
export function getRegionsSilent(params) {
  return request.get('/regions', { params: buildQueryParams(params), skipErrorHandler: true })
}

/** @param {object} data */
export function createRegion(data) {
  return request.post('/regions', data)
}

/** @param {string} id @param {object} data */
export function updateRegion(id, data) {
  return request.put(`/regions/${id}`, data)
}

/** @param {string} id */
export function deleteRegion(id) {
  return request.delete(`/regions/${id}`)
}
