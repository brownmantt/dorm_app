import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getEquipmentUsages(params) {
  return request.get('/equipment-usages', { params: buildQueryParams(params) })
}

/** @param {object} data */
export function createEquipmentUsage(data) {
  return request.post('/equipment-usages', data)
}

/** @param {string} id @param {object} [data] */
export function releaseEquipmentUsage(id, data) {
  return request.put(`/equipment-usages/${id}/release`, data || {})
}
