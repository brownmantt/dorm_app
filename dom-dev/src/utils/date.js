/**
 * @param {string|Date|null|undefined} value
 * @returns {string}
 */
export function formatDate(value) {
  if (!value) return ''
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

/**
 * @param {string|Date|null|undefined} value
 * @returns {string}
 */
export function formatMonthDay(value) {
  if (!value) return ''
  const text = formatDate(value)
  if (!text) return ''
  const [, month, day] = text.split('-')
  return `${Number(month)}月${Number(day)}日`
}

/**
 * @param {string|Date|null|undefined} value
 * @returns {string}
 */
export function formatDateTime(value) {
  if (!value) return ''
  const date = value instanceof Date ? value : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const sec = String(date.getSeconds()).padStart(2, '0')
  return `${formatDate(date)} ${h}:${min}:${sec}`
}

/**
 * @param {string} yearMonth YYYY-MM
 * @returns {string}
 */
export function formatYearMonth(yearMonth) {
  return yearMonth || ''
}

/**
 * @param {string} yearMonth YYYY-MM
 * @returns {{ year: number, month: number }}
 */
export function parseYearMonth(yearMonth) {
  const [y, m] = (yearMonth || '').split('-').map(Number)
  return { year: y || new Date().getFullYear(), month: m || new Date().getMonth() + 1 }
}

/**
 * @param {string} yearMonth YYYY-MM
 * @returns {string}
 */
export function formatYearMonthLabel(yearMonth) {
  const { year, month } = parseYearMonth(yearMonth)
  return `${year}年${month}月度`
}

/**
 * @param {number} year
 * @param {number} month 1-12
 * @returns {number}
 */
export function getDaysInMonth(year, month) {
  return new Date(year, month, 0).getDate()
}

/**
 * @param {string} yearMonth YYYY-MM
 * @param {number} delta -1 or +1
 * @returns {string}
 */
export function shiftYearMonth(yearMonth, delta) {
  const { year, month } = parseYearMonth(yearMonth)
  const date = new Date(year, month - 1 + delta, 1)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  return `${y}-${m}`
}

/**
 * @param {string} yearMonth YYYY-MM
 * @returns {string} YYYY-MM-DD
 */
export function monthStartDate(yearMonth) {
  const { year, month } = parseYearMonth(yearMonth)
  return `${year}-${String(month).padStart(2, '0')}-01`
}

/**
 * @param {string} yearMonth YYYY-MM
 * @returns {string} YYYY-MM-DD
 */
export function monthEndDate(yearMonth) {
  const { year, month } = parseYearMonth(yearMonth)
  const days = getDaysInMonth(year, month)
  return `${year}-${String(month).padStart(2, '0')}-${String(days).padStart(2, '0')}`
}

/**
 * @param {string} yearMonth YYYY-MM
 * @param {number} day 1-based
 * @returns {string}
 */
export function dateInMonth(yearMonth, day) {
  const { year, month } = parseYearMonth(yearMonth)
  return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
}

/**
 * @param {string} dateStr YYYY-MM-DD
 * @returns {number} 0=日 … 6=土
 */
export function weekdayIndex(dateStr) {
  const d = new Date(`${dateStr}T00:00:00`)
  return d.getDay()
}

/**
 * @param {string|null|undefined} dateStr
 * @returns {string}
 */
export function todayDateString() {
  return formatDate(new Date())
}

/**
 * @param {string} from YYYY-MM-DD
 * @param {string} to YYYY-MM-DD
 * @returns {number}
 */
export function daysBetween(from, to) {
  const a = new Date(`${from}T00:00:00`)
  const b = new Date(`${to}T00:00:00`)
  return Math.round((b - a) / 86400000)
}
