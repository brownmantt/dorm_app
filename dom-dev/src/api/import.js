import request from '@/utils/request'

/**
 * @param {FormData} formData
 */
export function previewExcelImport(formData) {
  return request.post('/imports/excel/preview', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * @param {FormData} formData
 */
export function executeExcelImport(formData) {
  return request.post('/imports/excel/execute', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** @param {Record<string, unknown>} params */
export function exportResidencesCsv(params) {
  return request.get('/exports/csv/residences', {
    params,
    responseType: 'blob'
  })
}

/** @param {Record<string, unknown>} params */
export function exportDormFeesCsv(params) {
  return request.get('/exports/csv/dorm-fees', {
    params,
    responseType: 'blob'
  })
}
