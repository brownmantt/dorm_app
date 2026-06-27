import request from '@/utils/request'
import { buildQueryParams, normalizePageResponse } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getDormitories(params) {
  return request.get('/dormitories', { params: buildQueryParams(params) })
}

/** 寮名称コンボボックス用の選択肢 */
export async function getDormitoryNameOptions() {
  const data = await getDormitories({ page: 0, size: 2000 })
  const { list } = normalizePageResponse(data)
  return list.sort((a, b) => (a.name || '').localeCompare(b.name || '', 'ja'))
}

/** @param {string} id */
export function getDormitory(id) {
  return request.get(`/dormitories/${id}`)
}

/** @param {object} data */
export function createDormitory(data) {
  return request.post('/dormitories', data)
}

/** @param {string} id @param {object} data */
export function updateDormitory(id, data) {
  return request.put(`/dormitories/${id}`, data)
}

/** @param {string} id */
export function deleteDormitory(id) {
  return request.delete(`/dormitories/${id}`)
}

/** @param {string} dormId @param {Record<string, unknown>} params */
export function getRoomsByDormitory(dormId, params) {
  return request.get(`/dormitories/${dormId}/rooms`, { params: buildQueryParams(params) })
}

/** @param {object} data */
export function createRoom(data) {
  return request.post('/rooms', data)
}

/** @param {string} id @param {object} data */
export function updateRoom(id, data) {
  return request.put(`/rooms/${id}`, data)
}

/** @param {string} id */
export function deleteRoom(id) {
  return request.delete(`/rooms/${id}`)
}

/** @param {string} dormId */
export function getDormitoryManager(dormId) {
  return request.get(`/dormitories/${dormId}/manager`)
}

/** @param {string} dormId */
export function getDormitoryManagerSilent(dormId) {
  return request.get(`/dormitories/${dormId}/manager`, { skipErrorHandler: true })
}

/** @param {string} dormId @param {object} data */
export function setDormitoryManager(dormId, data) {
  return request.put(`/dormitories/${dormId}/manager`, data)
}

/** @param {string} dormId */
export function removeDormitoryManager(dormId) {
  return request.delete(`/dormitories/${dormId}/manager`)
}
