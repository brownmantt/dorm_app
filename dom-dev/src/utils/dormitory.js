import { getDormitoriesForDormFee, getDormitoriesForDormFeeSilent } from '@/api/dormitory'
import { normalizePageResponse } from '@/utils/pagination'
import { regionLabelFromOptions, loadRegionOptions } from '@/utils/region'

/**
 * @param {{ dormitoryId?: string, name?: string, region?: string, regionLabel?: string }} item
 * @returns {string}
 */
export function formatDormitoryOptionLabel(item) {
  if (!item) return ''
  const regionPart = item.regionLabel || item.region || ''
  const name = item.name || item.dormitoryId || ''
  return regionPart ? `${name}（${regionPart}）` : name
}

/**
 * @param {object[]} list
 * @param {{ value: string, label: string }[]} [regionOptions]
 * @returns {Array<{ value: string, label: string, dormitoryId: string, name?: string, region?: string, hasResidents?: boolean, updatedAt?: string }>}
 */
export function mapDormitoryListToDormFeeOptions(list, regionOptions = []) {
  return (list || [])
    .filter((item) => item.dormitoryId)
    .map((item) => ({
      value: item.dormitoryId,
      label: formatDormitoryOptionLabel({
        ...item,
        regionLabel: regionLabelFromOptions(item.region, regionOptions)
      }),
      dormitoryId: item.dormitoryId,
      name: item.name,
      region: item.region,
      hasResidents: item.hasResidents,
      updatedAt: item.updatedAt
    }))
}

/**
 * @param {{ silent?: boolean }} [options]
 * @returns {Promise<ReturnType<typeof mapDormitoryListToDormFeeOptions>>}
 */
export async function loadDormFeeDormitoryOptions(options = {}) {
  const fetcher = options.silent ? getDormitoriesForDormFeeSilent : getDormitoriesForDormFee
  const [data, regionOptions] = await Promise.all([
    fetcher({ page: 0, size: 5000 }),
    loadRegionOptions({ silent: true })
  ])
  const { list } = normalizePageResponse(data)
  return mapDormitoryListToDormFeeOptions(list, regionOptions)
}

/**
 * @param {object[]} list
 * @returns {Array<{ value: string, label: string, roomId: string, roomName?: string }>}
 */
export function mapRoomListToOptions(list) {
  return (list || [])
    .filter((item) => item.roomId)
    .sort((a, b) => (a.roomName || a.roomId || '').localeCompare(b.roomName || b.roomId || '', 'ja'))
    .map((item) => ({
      value: item.roomId,
      label: item.roomName || item.roomDetail || item.roomId,
      roomId: item.roomId,
      roomName: item.roomName
    }))
}
