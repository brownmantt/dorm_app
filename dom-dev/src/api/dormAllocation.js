import request from '@/utils/request'
import { buildQueryParams, normalizePageResponse } from '@/utils/pagination'
import { getAffiliationsSilent } from '@/api/affiliation'
import { searchEmployeesSilent } from '@/api/employee'
import { getDormitories, getDormitoryManagerSilent, getRoomsByDormitory } from '@/api/dormitory'
import { getResidences } from '@/api/residence'
import { applyCalendarFilters, buildCalendarFromData, buildEmployeeAffiliationMap } from '@/utils/dormAllocation'
import { loadRegionOptions, toRegionLabelMap } from '@/utils/region'

/**
 * 寮割カレンダー用の所属・責任者情報を補完取得
 * @param {object[]} dormitories
 */
async function loadCalendarEnrichment(dormitories) {
  const [employeeResult, affiliationResult, managerResults] = await Promise.all([
    searchEmployeesSilent({ page: 0, size: 5000 }).catch(() => null),
    getAffiliationsSilent({ page: 0, size: 500 }).catch(() => null),
    Promise.all(
      dormitories.map(async (dormitory) => {
        try {
          const manager = await getDormitoryManagerSilent(dormitory.dormitoryId)
          if (manager?.employeeId || manager?.residenceHistoryId) {
            return [dormitory.dormitoryId, manager]
          }
        } catch {
          // API 未実装時はスキップ
        }
        return null
      })
    )
  ])

  const { list: employees } = normalizePageResponse(employeeResult || { content: [] })
  const { list: affiliations } = normalizePageResponse(affiliationResult || { content: [] })

  const dormitoryManagers = Object.fromEntries(
    managerResults.filter(Boolean)
  )

  return {
    employeeAffiliationById: buildEmployeeAffiliationMap(employees, affiliations),
    dormitoryManagers
  }
}

/**
 * バックエンド未実装時のクライアント組み立て
 * @param {{ yearMonth: string, region?: string, dormitoryId?: string, genderType?: string, name?: string }} params
 */
export async function buildCalendarClientSide(params) {
  const [dormData, residenceData] = await Promise.all([
    getDormitories({
      page: 0,
      size: 2000,
      dormitoryId: params.dormitoryId,
      genderType: params.genderType,
      region: params.region
    }),
    getResidences({ page: 0, size: 5000 })
  ])
  const { list: dormitories } = normalizePageResponse(dormData)
  const { list: residences } = normalizePageResponse(residenceData)
  const [enrichment, regionOptions] = await Promise.all([
    loadCalendarEnrichment(dormitories),
    loadRegionOptions({ silent: true })
  ])
  enrichment.regionLabelMap = toRegionLabelMap(regionOptions)
  const roomResults = await Promise.all(
    dormitories.map(async (dormitory) => {
      try {
        const data = await getRoomsByDormitory(dormitory.dormitoryId, { page: 0, size: 500 })
        const { list } = normalizePageResponse(data)
        return [dormitory.dormitoryId, list]
      } catch {
        return [dormitory.dormitoryId, []]
      }
    })
  )
  enrichment.roomsByDormitory = Object.fromEntries(roomResults)

  return buildCalendarFromData(dormitories, residences, params.yearMonth, {
    region: params.region,
    dormitoryId: params.dormitoryId,
    genderType: params.genderType,
    name: params.name
  }, enrichment)
}

/**
 * 寮割カレンダーデータ取得（API 未実装時は既存 API からクライアント組み立て）
 * @param {{ yearMonth: string, region?: string, dormitoryId?: string, genderType?: string, name?: string, sort?: string }} params
 */
export async function fetchDormAllocationCalendar(params) {
  const filters = {
    yearMonth: params.yearMonth,
    dormitoryId: params.dormitoryId,
    genderType: params.genderType,
    region: params.region,
    name: params.name
  }

  try {
    const query = buildQueryParams({
      yearMonth: params.yearMonth,
      dormitoryId: params.dormitoryId,
      genderType: params.genderType,
      name: params.name,
      sort: params.sort || 'dormitoryName,asc',
      regions: params.region || undefined
    })
    const data = await request.get('/dorm-allocation', { params: query, skipErrorHandler: true })
    if (data?.blocks) return applyCalendarFilters(data, filters)
    return buildCalendarClientSide(params)
  } catch {
    return buildCalendarClientSide(params)
  }
}

/**
 * @param {Record<string, unknown>} params
 */
export function getDormAllocationPrint(params) {
  return request.get('/dorm-allocation/print', { params: buildQueryParams(params) })
}

/**
 * @param {Record<string, unknown>} params
 */
export function getMoveOutAlerts(params) {
  return request.get('/alerts/move-out', { params: buildQueryParams(params) })
}
