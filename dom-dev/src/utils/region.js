import { getRegions, getRegionsSilent } from '@/api/region'
import { REGION, REGION_OPTIONS, labelOf } from '@/utils/constants'
import { normalizePageResponse } from '@/utils/pagination'

/**
 * @typedef {{ value: string, label: string, regionId?: string, displayOrder?: number }} RegionOption
 */

/**
 * @param {object[]} list
 * @returns {RegionOption[]}
 */
export function mapRegionListToOptions(list) {
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
      regionId: item.regionId,
      displayOrder: item.displayOrder
    }))
}

/**
 * @param {{ silent?: boolean }} [options]
 * @returns {Promise<RegionOption[]>}
 */
export async function loadRegionOptions(options = {}) {
  const fetcher = options.silent ? getRegionsSilent : getRegions
  try {
    const data = await fetcher({ page: 0, size: 500 })
    const { list } = normalizePageResponse(data)
    const mapped = mapRegionListToOptions(list)
    return mapped.length ? mapped : [...REGION_OPTIONS]
  } catch {
    return [...REGION_OPTIONS]
  }
}

/**
 * @param {RegionOption[]} regionOptions
 * @returns {Record<string, string>}
 */
export function toRegionLabelMap(regionOptions) {
  const map = { ...REGION }
  regionOptions.forEach((item) => {
    map[item.value] = item.label
  })
  return map
}

/**
 * @param {string|null|undefined} code
 * @param {Record<string, string>} [regionLabelMap]
 * @returns {string}
 */
export function resolveRegionLabel(code, regionLabelMap) {
  if (!code) return ''
  return regionLabelMap?.[code] ?? labelOf(REGION, code) ?? code
}

/**
 * @param {string|null|undefined} code
 * @param {RegionOption[]} regionOptions
 * @returns {string}
 */
export function regionLabelFromOptions(code, regionOptions) {
  if (!code) return ''
  const found = regionOptions.find((item) => item.value === code)
  return found?.label ?? labelOf(REGION, code) ?? code
}
