/** @typedef {{ content?: unknown[], totalElements?: number, totalPages?: number }} PageResponse */

/**
 * Spring Data 形式または配列レスポンスを正規化
 * @param {unknown} data
 * @returns {{ list: unknown[], total: number }}
 */
export function normalizePageResponse(data) {
  if (Array.isArray(data)) {
    return { list: data, total: data.length }
  }
  if (data && typeof data === 'object') {
    const page = /** @type {PageResponse} */ (data)
    return {
      list: page.content ?? [],
      total: page.totalElements ?? page.content?.length ?? 0
    }
  }
  return { list: [], total: 0 }
}

/**
 * @param {Record<string, unknown>} params
 * @returns {Record<string, string>}
 */
export function buildQueryParams(params) {
  const query = {}
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query[key] = String(value)
    }
  })
  return query
}
