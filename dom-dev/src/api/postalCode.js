import request from '@/utils/request'
import { normalizePostalCode } from '@/utils/postalCode'

/**
 * 郵便番号から住所を取得する。
 * @param {string} postalCode
 * @returns {Promise<{ postalCode: string, prefecture: string, city: string, town: string, address: string }>}
 */
export function lookupAddressByPostalCode(postalCode) {
  const normalized = normalizePostalCode(postalCode)
  return request.get(`/postal-codes/${normalized}/address`)
}
