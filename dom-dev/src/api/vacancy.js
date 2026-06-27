import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getVacancies(params) {
  return request.get('/vacancies', { params: buildQueryParams(params) })
}

/** @param {Record<string, unknown>} params */
export function getAssignableRooms(params) {
  return request.get('/vacancies/assignable', { params: buildQueryParams(params) })
}
