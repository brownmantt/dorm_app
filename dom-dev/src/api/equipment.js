import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getEquipments(params) {
  return request.get('/equipments', { params: buildQueryParams(params) })
}

/** @param {object} data */
export function createEquipment(data) {
  return request.post('/equipments', data)
}

/** @param {string} id @param {object} data */
export function updateEquipment(id, data) {
  return request.put(`/equipments/${id}`, data)
}

/** @param {string} id */
export function deleteEquipment(id) {
  return request.delete(`/equipments/${id}`)
}

/** @param {object} data */
export function processEquipmentMoveout(data) {
  return request.post('/equipment-moveouts', data)
}

/** @param {Record<string, unknown>} params */
export function getEquipmentStorages(params) {
  return request.get('/equipment-storages', { params: buildQueryParams(params) })
}

/** @param {object} data */
export function createEquipmentStorage(data) {
  return request.post('/equipment-storages', data)
}
