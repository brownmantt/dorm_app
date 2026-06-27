import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getUnitPrices(params) {
  return request.get('/unit-prices', { params: buildQueryParams(params) })
}

/** @param {object} data */
export function createUnitPrice(data) {
  return request.post('/unit-prices', data)
}

/** @param {string} id @param {object} data */
export function updateUnitPrice(id, data) {
  return request.put(`/unit-prices/${id}`, data)
}

/** @param {string} id */
export function deleteUnitPrice(id) {
  return request.delete(`/unit-prices/${id}`)
}
