import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getStorageLocations(params) {
  return request.get('/storage-locations', { params: buildQueryParams(params) })
}

/** @param {Record<string, unknown>} params */
export function getStorageLocationsSilent(params) {
  return request.get('/storage-locations', { params: buildQueryParams(params), skipErrorHandler: true })
}

/** @param {object} data */
export function createStorageLocation(data) {
  return request.post('/storage-locations', data)
}

/** @param {string} id @param {object} data */
export function updateStorageLocation(id, data) {
  return request.put(`/storage-locations/${id}`, data)
}

/** @param {string} id */
export function deleteStorageLocation(id) {
  return request.delete(`/storage-locations/${id}`)
}
