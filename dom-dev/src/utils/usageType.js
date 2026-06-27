import { getUsageTypes, getUsageTypesSilent } from '@/api/usageType'
import { normalizePageResponse } from '@/utils/pagination'

/**
 * @typedef {{ value: string, label: string, usageTypeId?: string, displayOrder?: number }} UsageTypeOption
 */

/**
 * @param {object[]} list
 * @returns {UsageTypeOption[]}
 */
export function mapUsageTypeListToOptions(list) {
  return (list || [])
    .filter((item) => item.code)
    .sort((a, b) => {
      const orderDiff = (a.displayOrder ?? 0) - (b.displayOrder ?? 0)
      if (orderDiff !== 0) return orderDiff
      return String(a.code).localeCompare(String(b.code))
    })
    .map((item) => ({
      value: item.code,
      label: item.name,
      usageTypeId: item.usageTypeId,
      displayOrder: item.displayOrder
    }))
}

/**
 * @param {{ silent?: boolean }} [options]
 * @returns {Promise<UsageTypeOption[]>}
 */
export async function loadUsageTypeOptions(options = {}) {
  const fetcher = options.silent ? getUsageTypesSilent : getUsageTypes
  const data = await fetcher({ page: 0, size: 500 })
  const { list } = normalizePageResponse(data)
  return mapUsageTypeListToOptions(list)
}
