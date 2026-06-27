import request from '@/utils/request'
import { buildQueryParams } from '@/utils/pagination'

/** @param {Record<string, unknown>} params */
export function getOperationLogs(params) {
  return request.get('/operation-logs', { params: buildQueryParams(params) })
}
