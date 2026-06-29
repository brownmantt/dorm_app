import { getEmployeesForDormFee, getEmployeesForDormFeeSilent } from '@/api/employee'
import { normalizePageResponse } from '@/utils/pagination'

/**
 * @param {string | null | undefined} contactInfo
 * @returns {{ mobilePhone: string, email: string }}
 */
export function parseContactInfo(contactInfo) {
  if (!contactInfo) return { mobilePhone: '', email: '' }
  try {
    const parsed = JSON.parse(contactInfo)
    return {
      mobilePhone: parsed.mobilePhone || '',
      email: parsed.email || ''
    }
  } catch {
    return { mobilePhone: '', email: '' }
  }
}

/**
 * @param {{ employeeId?: string, name?: string, employeeName?: string }} item
 * @returns {string}
 */
export function formatEmployeeOptionLabel(item) {
  if (!item) return ''
  const name = item.name || item.employeeName || ''
  return name ? `${item.employeeId} - ${name}` : (item.employeeId || '')
}

/**
 * @param {object[]} list
 * @returns {Array<{ value: string, label: string, employeeId: string, name?: string, residing?: boolean, updatedAt?: string }>}
 */
export function mapEmployeeListToDormFeeOptions(list) {
  return (list || [])
    .filter((item) => item.employeeId)
    .map((item) => ({
      value: item.employeeId,
      label: formatEmployeeOptionLabel(item),
      employeeId: item.employeeId,
      name: item.name,
      residing: item.residing,
      updatedAt: item.updatedAt
    }))
}

/**
 * @param {{ silent?: boolean, targetYearMonth?: string, dormitoryId?: string, roomId?: string }} [options]
 * @returns {Promise<ReturnType<typeof mapEmployeeListToDormFeeOptions>>}
 */
export async function loadDormFeeEmployeeOptions(options = {}) {
  const { silent, targetYearMonth, dormitoryId, roomId } = options
  const fetcher = silent ? getEmployeesForDormFeeSilent : getEmployeesForDormFee
  const params = {
    page: 0,
    size: 5000,
    ...(targetYearMonth ? { targetYearMonth } : {}),
    ...(dormitoryId ? { dormitoryId } : {}),
    ...(roomId ? { roomId } : {})
  }
  const data = await fetcher(params)
  const { list } = normalizePageResponse(data)
  return mapEmployeeListToDormFeeOptions(list)
}
