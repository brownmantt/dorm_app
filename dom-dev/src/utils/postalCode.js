/**
 * 郵便番号を7桁数字に正規化する。
 * @param {string} value
 * @returns {string}
 */
export function normalizePostalCode(value) {
  return String(value || '').replace(/\D/g, '').slice(0, 7)
}

/**
 * 7桁郵便番号を 123-4567 形式で表示する。
 * @param {string} value
 * @returns {string}
 */
export function formatPostalCode(value) {
  const digits = normalizePostalCode(value)
  if (digits.length <= 3) {
    return digits
  }
  return `${digits.slice(0, 3)}-${digits.slice(3)}`
}

/**
 * 郵便番号が7桁かどうか判定する。
 * @param {string} value
 * @returns {boolean}
 */
export function isCompletePostalCode(value) {
  return normalizePostalCode(value).length === 7
}
