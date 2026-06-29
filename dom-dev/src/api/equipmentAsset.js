import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getEquipmentAssets(params) {
  return request.get('/equipment-assets', { params: buildQueryParams(params) })
}

/** @param {object} data */
export function createEquipmentAsset(data) {
  return request.post('/equipment-assets', data)
}

/** @param {string} id @param {object} data */
export function updateEquipmentAsset(id, data) {
  return request.put(`/equipment-assets/${id}`, data)
}

/** @param {string} id */
export function deleteEquipmentAsset(id) {
  return request.delete(`/equipment-assets/${id}`)
}
