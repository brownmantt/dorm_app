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
