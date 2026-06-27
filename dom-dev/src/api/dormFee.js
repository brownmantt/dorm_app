import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getDormFees(params) {
  return request.get('/dorm-fees', { params: buildQueryParams(params) })
}

/** @param {object} data */
export function calculateDormFee(data) {
  return request.post('/dorm-fees/calculate', data)
}

/** @param {object} data */
export function createDormFee(data) {
  return request.post('/dorm-fees', data)
}

/** @param {string} id */
export function confirmDormFee(id) {
  return request.put(`/dorm-fees/${id}/confirm`)
}
