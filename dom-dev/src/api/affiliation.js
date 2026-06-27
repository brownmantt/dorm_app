import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getAffiliations(params) {
  return request.get('/affiliations', { params: buildQueryParams(params) })
}

/** @param {Record<string, unknown>} params */
export function getAffiliationsSilent(params) {
  return request.get('/affiliations', { params: buildQueryParams(params), skipErrorHandler: true })
}

/** @param {object} data */
export function createAffiliation(data) {
  return request.post('/affiliations', data)
}

/** @param {string} id @param {object} data */
export function updateAffiliation(id, data) {
  return request.put(`/affiliations/${id}`, data)
}

/** @param {string} id */
export function deleteAffiliation(id) {
  return request.delete(`/affiliations/${id}`)
}
