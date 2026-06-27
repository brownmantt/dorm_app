import { EMPLOYEE_CATEGORY, MOVE_OUT_WARNING_DAYS, WEEKDAY_JA, labelOf } from '@/utils/constants'
import {
  dateInMonth,
  daysBetween,
  getDaysInMonth,
  monthEndDate,
  monthStartDate,
  parseYearMonth,
  todayDateString,
  weekdayIndex
} from '@/utils/date'

const INFINITE_END = '9999-12-31'

/**
 * @param {string|null|undefined} moveOutDate
 * @returns {string}
 */
function effectiveMoveOut(moveOutDate) {
  return moveOutDate || INFINITE_END
}

/**
 * @param {string} address
 * @returns {keyof typeof REGION}
 */
export function inferRegionFromAddress(address) {
  const text = address || ''
  if (text.includes('東京')) return 'TOKYO'
  if (text.includes('大阪')) return 'OSAKA'
  if (text.includes('名古屋') || text.includes('愛知')) return 'NAGOYA'
  return 'OTHER'
}

/**
 * @param {string} moveInDate
 * @param {string|null|undefined} moveOutDate
 * @param {string} yearMonth
 * @returns {boolean}
 */
export function isVisibleInMonth(moveInDate, moveOutDate, yearMonth) {
  const start = monthStartDate(yearMonth)
  const end = monthEndDate(yearMonth)
  return moveInDate <= end && effectiveMoveOut(moveOutDate) >= start
}

/**
 * @param {string} moveInDate
 * @param {string|null|undefined} moveOutDate
 * @param {string} dateStr YYYY-MM-DD
 * @returns {boolean}
 */
export function isOccupiedOnDate(moveInDate, moveOutDate, dateStr) {
  return moveInDate <= dateStr && effectiveMoveOut(moveOutDate) >= dateStr
}

/**
 * @param {string} moveInDate
 * @param {string|null|undefined} moveOutDate
 * @param {string} yearMonth
 * @returns {number[]}
 */
export function getOccupiedDayNumbers(moveInDate, moveOutDate, yearMonth) {
  const { year, month } = parseYearMonth(yearMonth)
  const days = getDaysInMonth(year, month)
  const result = []
  for (let d = 1; d <= days; d += 1) {
    const dateStr = dateInMonth(yearMonth, d)
    if (isOccupiedOnDate(moveInDate, moveOutDate, dateStr)) {
      result.push(d)
    }
  }
  return result
}

/**
 * @param {string|null|undefined} moveOutDate
 * @param {string} yearMonth
 * @param {string} [asOfDate]
 * @returns {boolean}
 */
export function isMoveOutWarning(moveOutDate, yearMonth, asOfDate = todayDateString()) {
  if (!moveOutDate) return false
  const diff = daysBetween(asOfDate, moveOutDate)
  return diff >= 0 && diff <= MOVE_OUT_WARNING_DAYS
}

/**
 * @param {string|null|undefined} moveOutDate
 * @param {string} yearMonth
 * @returns {boolean}
 */
export function isMoveOutInMonth(moveOutDate, yearMonth) {
  if (!moveOutDate) return false
  return moveOutDate >= monthStartDate(yearMonth) && moveOutDate <= monthEndDate(yearMonth)
}

/**
 * @param {object} row
 * @param {string} dormitoryId
 * @param {Record<string, { employeeId?: string, residenceHistoryId?: string }>} dormitoryManagers
 * @returns {boolean}
 */
export function resolveIsManager(row, dormitoryId, dormitoryManagers = {}) {
  if (row.isManager === true) return true
  const manager = dormitoryManagers[dormitoryId]
  if (!manager) return false
  if (manager.residenceHistoryId && row.residenceHistoryId) {
    return manager.residenceHistoryId === row.residenceHistoryId
  }
  if (manager.employeeId && row.employeeId) {
    return manager.employeeId === row.employeeId
  }
  return false
}

/**
 * @param {object} row
 * @param {Record<string, string>} employeeAffiliationById
 * @returns {string}
 */
export function resolveAffiliationName(row, employeeAffiliationById = {}) {
  if (row.affiliationName && row.affiliationName !== '-') return row.affiliationName
  if (row.employeeId && employeeAffiliationById[row.employeeId]) {
    return employeeAffiliationById[row.employeeId]
  }
  return '-'
}

/**
 * @param {object} row
 * @returns {string}
 */
export function resolveRoomConflictKey(row) {
  return row.roomId || row.roomName || ''
}

/**
 * 寮割カレンダー寮詳細列：部屋名称のみ
 * @param {object} row
 * @returns {string}
 */
export function formatRoomNameLabel(row) {
  return row.roomName || '-'
}

/**
 * @param {object} row
 * @returns {string}
 */
export function formatRoomDetailLabel(row) {
  const base = row.roomDetail || row.roomName || ''
  if (!base) return '-'
  if (row.hasAirConditioner === true) return `${base}（エアコン有）`
  if (row.hasAirConditioner === false) return `${base}（エアコン無）`
  return base
}

/**
 * カレンダー応答に寮名称・種別・地域・入居者氏名の検索条件を適用する
 * @param {object|null|undefined} calendar
 * @param {{ region?: string, dormitoryId?: string, genderType?: string, name?: string }} filters
 * @returns {object|null|undefined}
 */
export function applyCalendarFilters(calendar, filters = {}) {
  if (!calendar?.blocks) return calendar

  const dormitoryIdFilter = (filters.dormitoryId || '').trim()
  const genderTypeFilter = (filters.genderType || '').trim()
  const regionFilter = (filters.region || '').trim()
  const nameFilter = (filters.name || '').trim().toLowerCase()

  let blocks = calendar.blocks.filter((block) => {
    if (dormitoryIdFilter && block.dormitoryId !== dormitoryIdFilter) return false
    if (genderTypeFilter && block.genderType !== genderTypeFilter) return false
    if (regionFilter && (block.region || '') !== regionFilter) return false
    return true
  })

  if (nameFilter) {
    blocks = blocks
      .map((block) => ({
        ...block,
        rows: (block.rows || []).filter(
          (row) => !row.vacant && (row.employeeName || '').toLowerCase().includes(nameFilter)
        )
      }))
      .filter((block) => (block.rows || []).length > 0)
  }

  return { ...calendar, blocks }
}

/**
 * @param {object} room
 * @returns {number}
 */
export function resolveRoomCapacity(room) {
  const capacity = Number(room?.capacity)
  return capacity > 0 ? capacity : 1
}

/**
 * @param {object} room
 * @param {number} [slotIndex]
 * @returns {object}
 */
function buildVacantSlotRow(room, slotIndex = 0) {
  return {
    roomId: room.roomId,
    roomName: room.roomName,
    roomDetail: room.roomDetail,
    hasAirConditioner: room.hasAirConditioner,
    vacant: true,
    slotIndex,
    affiliationName: '-',
    occupiedDays: [],
    conflictDays: [],
    isManager: false,
    moveOutInMonth: false,
    moveOutWarning: false
  }
}

/**
 * @param {object[]} dormitories
 * @param {object[]} residences
 * @param {string} yearMonth
 * @param {{ region?: string, dormitoryId?: string, genderType?: string, name?: string }} filters
 * @param {{ employeeAffiliationById?: Record<string,string>, dormitoryManagers?: Record<string,{employeeId?:string,residenceHistoryId?:string}>, roomsByDormitory?: Record<string,object[]>, regionLabelMap?: Record<string,string> }} enrichment
 * @returns {object}
 */
export function buildCalendarFromData(dormitories, residences, yearMonth, filters = {}, enrichment = {}) {
  const { year, month } = parseYearMonth(yearMonth)
  const daysInMonth = getDaysInMonth(year, month)
  const firstDayOfWeek = WEEKDAY_JA[weekdayIndex(monthStartDate(yearMonth))]
  const nameFilter = (filters.name || '').trim().toLowerCase()
  const regionFilter = (filters.region || '').trim() || null
  const dormitoryIdFilter = (filters.dormitoryId || '').trim()
  const genderTypeFilter = (filters.genderType || '').trim()
  const employeeAffiliationById = enrichment.employeeAffiliationById || {}
  const dormitoryManagers = enrichment.dormitoryManagers || {}
  const roomsByDormitory = enrichment.roomsByDormitory || {}
  const regionLabelMap = enrichment.regionLabelMap || {}

  const visibleResidences = residences.filter((r) => {
    if (!isVisibleInMonth(r.moveInDate, r.moveOutDate, yearMonth)) return false
    if (nameFilter && !(r.employeeName || '').toLowerCase().includes(nameFilter)) return false
    return true
  })

  const residentsByDorm = new Map()
  visibleResidences.forEach((r) => {
    const dormKey = r.dormitoryId || r.dormitoryName
    if (!residentsByDorm.has(dormKey)) residentsByDorm.set(dormKey, [])
    residentsByDorm.get(dormKey).push(r)
  })

  const residentsByRoom = (list) => {
    const map = new Map()
    list.forEach((r) => {
      const key = r.roomId || r.roomName
      if (!map.has(key)) map.set(key, [])
      map.get(key).push(r)
    })
    return map
  }

  const blocks = []

  dormitories.forEach((d) => {
    const region = d.region || inferRegionFromAddress(d.address)
    if (regionFilter && region !== regionFilter) return
    if (dormitoryIdFilter && d.dormitoryId !== dormitoryIdFilter) return
    if (genderTypeFilter && d.genderType !== genderTypeFilter) return

    const dormResidents = residentsByDorm.get(d.dormitoryId) || residentsByDorm.get(d.name) || []
    if (!dormResidents.length && nameFilter) return

    const rooms = roomsByDormitory[d.dormitoryId] || []
    const byRoom = residentsByRoom(dormResidents)
    const rows = []

    rooms.forEach((room) => {
      const capacity = resolveRoomCapacity(room)
      const roomResidents = byRoom.get(room.roomId) || byRoom.get(room.roomName) || []
      if (!roomResidents.length) {
        if (nameFilter) return
        for (let slot = 0; slot < capacity; slot += 1) {
          rows.push(buildVacantSlotRow(room, slot))
        }
        return
      }
      roomResidents.forEach((r) => {
        const occupiedDays = getOccupiedDayNumbers(r.moveInDate, r.moveOutDate, yearMonth)
        rows.push({
          residenceHistoryId: r.residenceHistoryId,
          employeeId: r.employeeId,
          employeeName: r.employeeName,
          affiliationName: resolveAffiliationName(r, employeeAffiliationById),
          roomName: room.roomName || r.roomName,
          roomId: room.roomId || r.roomId,
          roomDetail: room.roomDetail || r.roomDetail,
          hasAirConditioner: room.hasAirConditioner ?? r.hasAirConditioner,
          vacant: false,
          isManager: resolveIsManager(r, d.dormitoryId, dormitoryManagers),
          moveInDate: r.moveInDate,
          moveOutDate: r.moveOutDate,
          moveOutInMonth: isMoveOutInMonth(r.moveOutDate, yearMonth),
          moveOutWarning: isMoveOutWarning(r.moveOutDate, yearMonth),
          occupiedDays,
          conflictDays: []
        })
      })
      const vacantSlots = Math.max(0, capacity - roomResidents.length)
      for (let slot = 0; slot < vacantSlots; slot += 1) {
        rows.push(buildVacantSlotRow(room, roomResidents.length + slot))
      }
    })

    if (!rows.length && !rooms.length) return

    const roomDayCount = new Map()
    rows.forEach((row) => {
      const key = resolveRoomConflictKey(row)
      if (!key) return
      row.occupiedDays.forEach((day) => {
        const rk = `${key}:${day}`
        roomDayCount.set(rk, (roomDayCount.get(rk) || 0) + 1)
      })
    })
    rows.forEach((row) => {
      const key = resolveRoomConflictKey(row)
      row.conflictDays = row.occupiedDays.filter((day) => {
        if (!key) return false
        const rk = `${key}:${day}`
        return (roomDayCount.get(rk) || 0) > 1
      })
    })

    const hasVacancy = rooms.some((room) => room.vacancyStatus === 'VACANT')

    blocks.push({
      dormitoryId: d.dormitoryId,
      dormitoryName: d.name,
      address: d.address,
      postalCode: d.postalCode,
      region,
      layoutType: d.layoutType,
      genderType: d.genderType,
      hasVacancy: d.hasVacancy ?? hasVacancy,
      updatedAt: d.updatedAt ?? d.updateTime ?? d.updatedTime ?? d.gmtModified ?? null,
      rows
    })
  })

  blocks.sort((a, b) => (a.dormitoryName || '').localeCompare(b.dormitoryName || '', 'ja'))

  return {
    yearMonth,
    daysInMonth,
    firstDayOfWeek,
    weekdayLabels: Array.from({ length: daysInMonth }, (_, i) =>
      WEEKDAY_JA[weekdayIndex(dateInMonth(yearMonth, i + 1))]
    ),
    blocks,
    regionLabels: regionFilter
      ? [regionLabelMap[regionFilter] || regionFilter]
      : []
  }
}

/**
 * 社員・所属マスタから所属名称マップを組み立てる
 * @param {object[]} employees
 * @param {object[]} affiliations
 * @returns {Record<string, string>}
 */
export function buildEmployeeAffiliationMap(employees, affiliations = []) {
  const affiliationNameById = Object.fromEntries(
    affiliations
      .filter((item) => item.affiliationId)
      .map((item) => [item.affiliationId, item.name])
  )

  const map = {}
  employees.forEach((employee) => {
    if (!employee?.employeeId) return
    if (employee.affiliationName) {
      map[employee.employeeId] = employee.affiliationName
      return
    }
    if (employee.affiliationId && affiliationNameById[employee.affiliationId]) {
      map[employee.employeeId] = affiliationNameById[employee.affiliationId]
      return
    }
    if (employee.employeeCategory) {
      map[employee.employeeId] = labelOf(EMPLOYEE_CATEGORY, employee.employeeCategory)
    }
  })
  return map
}
