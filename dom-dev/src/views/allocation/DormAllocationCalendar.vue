<template>
  <div class="page-container allocation-page">
    <section class="dashboard-summary no-print">
      <el-row :gutter="16" class="summary-row" align="stretch">
        <el-col :span="5">
          <div class="metric-card metric-card--indigo">
            <p class="metric-card__title">寮数</p>
            <div class="metric-card__main">
              <span class="metric-card__value">{{ dormCountSummary.total }}</span>
              <span class="metric-card__unit">棟</span>
            </div>
            <div class="metric-card__chips">
              <span class="metric-chip">男性寮 {{ dormCountSummary.male }}</span>
              <span class="metric-chip">女性寮 {{ dormCountSummary.female }}</span>
            </div>
            <div class="metric-card__chips">
              <span
                v-for="region in dormCountSummary.regions"
                :key="region.label"
                class="metric-chip metric-chip--ghost"
              >
                {{ region.label }} {{ region.count }}
              </span>
            </div>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="metric-card metric-card--teal metric-card--stat-only">
            <p class="metric-card__title">部屋数</p>
            <div class="metric-card__main">
              <span class="metric-card__value">{{ roomSummary.total }}</span>
              <span class="metric-card__unit">室</span>
            </div>
          </div>
        </el-col>
        <el-col :span="4">
          <div v-loading="residentSummaryLoading" class="metric-card metric-card--violet">
            <p class="metric-card__title">入居人数</p>
            <div class="metric-card__main">
              <span class="metric-card__value">{{ residentSummary.total }}</span>
              <span class="metric-card__unit">人</span>
            </div>
            <div class="metric-card__chips">
              <span class="metric-chip">男性 {{ residentSummary.male }}</span>
              <span class="metric-chip">女性 {{ residentSummary.female }}</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div v-loading="chartLoading" class="metric-card metric-card--amber">
            <p class="metric-card__title">入居状況</p>
            <div class="occupancy-body">
              <div ref="vacancyChartRef" class="occupancy-chart" />
              <div class="occupancy-stats">
                <div class="occupancy-stat">
                  <span class="dot dot--occupied" />
                  入居中 <strong>{{ roomSummary.occupied }}</strong> 室（{{ roomSummary.occupiedRate }}%）
                </div>
                <div class="occupancy-stat">
                  <span class="dot dot--vacant" />
                  空き室 <strong>{{ roomSummary.vacant }}</strong> 室（{{ roomSummary.vacantRate }}%）
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div v-loading="alertLoading" class="metric-card metric-card--rose">
            <div class="metric-card__head">
              <p class="metric-card__title">長期利用中状況</p>
              <span class="metric-card__count" :class="{ 'is-warning': alertCount > 0 }">{{ alertCount }} 件</span>
            </div>
            <el-table :data="alertList" class="data-table alert-mini-table" border stripe size="small" empty-text="データがありません" max-height="92">
              <el-table-column prop="employeeName" label="社員名" min-width="120" />
              <el-table-column label="利用日数" min-width="100">
                <template #default="{ row }">{{ usageDaysLabel(row) }}</template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
    </section>

    <el-card class="search-card search-form-grid-card search-form-single-row-card allocation-search-card allocation-toolbar no-print">
      <div class="allocation-month-bar">
        <el-button class="allocation-month-bar__btn" :icon="ArrowLeft" circle @click="shiftMonth(-1)" />
        <span class="allocation-month-bar__label">{{ monthLabel }}</span>
        <el-button class="allocation-month-bar__btn" :icon="ArrowRight" circle @click="shiftMonth(1)" />
      </div>
      <el-form :model="query" label-width="84px" class="search-form-grid search-form-single-row">
        <div class="search-form-grid__cols">
          <el-form-item label="寮名称" class="search-field-dormitory">
            <el-select
              v-model="query.dormitoryId"
              clearable
              filterable
              placeholder="すべて"
            >
              <el-option
                v-for="item in dormitoryOptions"
                :key="item.dormitoryId"
                :label="item.name"
                :value="item.dormitoryId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="種別" class="search-field-gender">
            <el-select v-model="query.genderType" clearable placeholder="すべて">
              <el-option
                v-for="item in DORMITORY_GENDER_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="地域" class="search-field-region">
            <el-select
              v-model="query.region"
              clearable
              placeholder="すべて"
            >
              <el-option
                v-for="item in regionOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="入居者氏名" class="search-field-name">
            <el-input
              v-model="query.name"
              clearable
              placeholder="部分一致"
              @keyup.enter="fetchCalendar"
            />
          </el-form-item>
          <el-form-item class="search-form-grid__actions">
            <el-button type="primary" @click="fetchCalendar">表示</el-button>
            <el-button @click="handleReset">リセット</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <div id="allocation-print-area" v-loading="loading" class="allocation-content">
      <div class="allocation-detail-panel">
        <div class="allocation-detail-toolbar no-print">
          <el-button @click="handlePrint">
            <el-icon><Printer /></el-icon>
            印刷
          </el-button>
        </div>
        <div class="allocation-print-title print-only">対象月：{{ monthLabel }}</div>

        <div v-if="!calendar.blocks?.length" class="allocation-empty">
          <el-empty description="表示対象の寮・入居者がありません" />
        </div>

        <table v-else class="allocation-table" :style="allocationTableStyle">
          <colgroup>
            <col class="alloc-col-action-in" />
            <col class="alloc-col-action-out" />
            <col class="alloc-col-room-detail" />
            <col class="alloc-col-name" />
            <col class="alloc-col-affiliation" />
            <col class="alloc-col-manager" />
            <col class="alloc-col-move-in" />
            <col class="alloc-col-move-out" />
            <col
              v-for="day in calendar.daysInMonth"
              :key="`col-day-${day}`"
              class="alloc-col-day"
            />
          </colgroup>
          <template v-for="(block, blockIndex) in sortedBlocks" :key="block.dormitoryId">
          <tbody v-if="blockIndex > 0" class="allocation-block-spacer" aria-hidden="true">
            <tr><td :colspan="tableColCount" /></tr>
          </tbody>
          <tbody
            class="allocation-block-body"
            :class="{ 'print-page-break': blockIndex > 0 }"
          >
            <tr class="allocation-header-group">
              <th colspan="3" class="group-dorm group-dorm-name">
                <router-link
                  :to="{ name: 'DormitoryDetail', params: { id: block.dormitoryId } }"
                  class="allocation-block-title-link cell-ellipsis"
                  :title="block.dormitoryName"
                >
                  {{ block.dormitoryName }}
                </router-link>
              </th>
              <th colspan="5" class="group-resident">入居者情報</th>
              <th :colspan="calendar.daysInMonth" class="group-calendar">カレンダー</th>
            </tr>
            <tr class="allocation-header-columns">
              <th colspan="3" class="cell-dorm-summary">
                <span class="cell-ellipsis" :title="dormSummary(block)">{{ dormSummary(block) }}</span>
              </th>
              <th class="col-name">氏名</th>
              <th class="col-affiliation">所属</th>
              <th class="col-manager">責任者</th>
              <th class="col-move-in">入居日</th>
              <th class="col-move-out">退居日</th>
              <th
                v-for="day in calendar.daysInMonth"
                :key="`d-${block.dormitoryId}-${day}`"
                class="col-day col-day-header"
                :class="{ 'col-day-off': isCalendarOffDay(day) }"
              >
                <span class="day-num">{{ day }}</span>
                <span class="day-weekday">{{ calendar.weekdayLabels[day - 1] }}</span>
              </th>
            </tr>
            <tr
              v-for="(row, rowIndex) in block.rows"
              :key="row.residenceHistoryId || `${block.dormitoryId}-${row.roomId}-${rowIndex}`"
              class="allocation-row"
            >
              <td class="col-action-in">
                <button
                  v-if="userStore.isAdmin && row.vacant"
                  type="button"
                  class="action-btn action-btn-in"
                  title="入居登録"
                  @click="goRegister(block, row)"
                >
                  入
                </button>
              </td>
              <td class="col-action-out">
                <button
                  v-if="userStore.isAdmin && row.residenceHistoryId"
                  type="button"
                  class="action-btn action-btn-out"
                  title="退居処理"
                  @click="goCheckout(row)"
                >
                  退
                </button>
              </td>
              <td class="col-room-detail cell-room-detail">
                <router-link
                  v-if="row.roomId"
                  :to="roomEditRoute(block, row)"
                  class="allocation-room-link cell-ellipsis"
                  :title="formatRoomNameLabel(row)"
                >
                  {{ formatRoomNameLabel(row) }}
                </router-link>
                <span v-else class="cell-ellipsis" :title="formatRoomNameLabel(row)">
                  {{ formatRoomNameLabel(row) }}
                </span>
              </td>
              <td class="col-name">
                <span v-if="row.moveOutWarning" class="warning-icon" title="退寮予定が近づいています">⚠</span>
                <router-link
                  v-if="row.employeeId && row.employeeName"
                  :to="residentDetailRoute(row)"
                  class="allocation-resident-link cell-ellipsis"
                  :title="row.employeeName"
                >
                  {{ row.employeeName }}
                </router-link>
                <span v-else class="cell-ellipsis" :title="row.employeeName || ''">{{ row.employeeName || '' }}</span>
              </td>
              <td class="col-affiliation">
                <span class="cell-ellipsis" :title="row.affiliationName || ''">{{ row.affiliationName || '' }}</span>
              </td>
              <td class="col-manager">{{ row.isManager ? '★' : '' }}</td>
              <td class="col-move-in">{{ formatMonthDay(row.moveInDate) }}</td>
              <td class="col-move-out" :class="{ 'text-move-out-month': row.moveOutInMonth }">
                {{ row.moveOutDate ? formatMonthDay(row.moveOutDate) : (row.moveOutInMonth ? '退寮' : '') }}
              </td>
              <td
                v-for="day in calendar.daysInMonth"
                :key="`c-${row.residenceHistoryId || `${row.roomId}-${rowIndex}`}-${day}`"
                class="col-day"
                :class="dayCellClass(row, day)"
                :title="dayCellTitle(row, day)"
              />
            </tr>
            <tr v-if="!block.rows.length" class="allocation-row allocation-row-empty">
              <td class="col-action-in">&nbsp;</td>
              <td class="col-action-out">&nbsp;</td>
              <td class="col-room-detail cell-room-detail cell-no-room">部屋データなし</td>
              <td class="col-name">&nbsp;</td>
              <td class="col-affiliation">&nbsp;</td>
              <td class="col-manager">&nbsp;</td>
              <td class="col-move-in">&nbsp;</td>
              <td class="col-move-out">&nbsp;</td>
              <td
                v-for="day in calendar.daysInMonth"
                :key="`empty-${block.dormitoryId}-${day}`"
                class="col-day"
              >&nbsp;</td>
            </tr>
          </tbody>
          </template>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight, Printer } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { fetchDormAllocationCalendar } from '@/api/dormAllocation'
import { searchEmployeesSilent } from '@/api/employee'
import { getDormitoryNameOptions } from '@/api/dormitory'
import { getVacancies } from '@/api/vacancy'
import { getLongTermUsageAlerts, getResidences } from '@/api/residence'
import { DORMITORY_GENDER_OPTIONS, GENDER_TYPE, VACANCY_STATUS, labelOf } from '@/utils/constants'
import { isCalendarOffDay as checkCalendarOffDay } from '@/utils/calendarOffDay'
import { formatMonthDay, formatYearMonthLabel, shiftYearMonth, todayDateString } from '@/utils/date'
import { formatRoomNameLabel, inferRegionFromAddress } from '@/utils/dormAllocation'
import { normalizePageResponse } from '@/utils/pagination'
import { loadRegionOptions, regionLabelFromOptions } from '@/utils/region'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const dormitoryOptions = ref([])
const regionOptions = ref([])

const vacancyChartRef = ref()
const chartLoading = ref(false)
const alertLoading = ref(false)
const residentSummaryLoading = ref(false)
const vacancyList = ref([])
const alertList = ref([])
const residentSummary = ref({ total: 0, male: 0, female: 0 })
let chartInstance = null

const dormCountSummary = computed(() => {
  const list = dormitoryOptions.value || []
  const male = list.filter((item) => item.genderType === 'MALE').length
  const female = list.filter((item) => item.genderType === 'FEMALE').length
  const regionOf = (item) => item.region || inferRegionFromAddress(item.address)
  const regions = regionOptions.value.map((option) => ({
    label: option.label,
    count: list.filter((item) => regionOf(item) === option.value).length
  }))
  return { total: list.length, male, female, regions }
})

const roomSummary = computed(() => {
  const list = vacancyList.value || []
  const total = list.length
  const occupied = list.filter((item) => item.status === 'OCCUPIED').length
  const vacant = list.filter((item) => item.status === 'VACANT').length
  const rate = (value) => (total ? Math.round((value / total) * 100) : 0)
  return { total, occupied, vacant, occupiedRate: rate(occupied), vacantRate: rate(vacant) }
})

const alertCount = computed(() => alertList.value.length)

function usageDaysLabel(row) {
  const days = row?.totalUsageDays ?? row?.usageDays ?? row?.days
  return days != null ? `${days} 日` : '-'
}

const now = new Date()
const defaultYearMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`

const query = reactive({
  yearMonth: defaultYearMonth,
  dormitoryId: undefined,
  genderType: undefined,
  region: undefined,
  name: ''
})

const calendar = reactive({
  yearMonth: defaultYearMonth,
  daysInMonth: 30,
  weekdayLabels: [],
  blocks: []
})

const monthLabel = computed(() => formatYearMonthLabel(query.yearMonth))

function blockHasResident(block) {
  return (block.rows || []).some((row) => row.employeeId)
}

function blockHasRooms(block) {
  return (block.rows || []).length > 0
}

function blockUpdatedTime(block) {
  const value = block.updatedAt
  const time = value ? new Date(value).getTime() : NaN
  return Number.isNaN(time) ? 0 : time
}

const sortedBlocks = computed(() =>
  [...(calendar.blocks || [])].sort((a, b) => {
    const residentDiff = (blockHasResident(a) ? 0 : 1) - (blockHasResident(b) ? 0 : 1)
    if (residentDiff !== 0) return residentDiff
    const roomDiff = (blockHasRooms(a) ? 0 : 1) - (blockHasRooms(b) ? 0 : 1)
    if (roomDiff !== 0) return roomDiff
    const timeDiff = blockUpdatedTime(b) - blockUpdatedTime(a)
    if (timeDiff !== 0) return timeDiff
    return (a.dormitoryName || '').localeCompare(b.dormitoryName || '', 'ja')
  })
)

const tableColCount = computed(() => 8 + (calendar.daysInMonth || 30))

const allocationTableStyle = computed(() => ({
  '--alloc-day-count': calendar.daysInMonth || 31
}))

function isCalendarOffDay(day) {
  return checkCalendarOffDay(calendar.yearMonth, day, calendar.weekdayLabels[day - 1])
}

function dormSummary(block) {
  const layout = block.layoutType || '-'
  const gender = labelOf(GENDER_TYPE, block.genderType) || '-'
  const vacancy = block.hasVacancy === true ? '空室有' : block.hasVacancy === false ? '空室無' : '-'
  const regionCode = block.region || inferRegionFromAddress(block.address)
  const region = regionLabelFromOptions(regionCode, regionOptions.value) || '-'
  return `${layout} / ${gender} / ${vacancy} / ${region}`
}

function dayCellClass(row, day) {
  const classes = []
  if (row.occupiedDays?.includes(day)) classes.push('cell-occupied')
  if (row.conflictDays?.includes(day)) classes.push('cell-conflict')
  return classes
}

function dayCellTitle(row, day) {
  if (row.conflictDays?.includes(day)) {
    return `部屋の定員を超えています（${row.roomName} / ${day}日）`
  }
  if (row.occupiedDays?.includes(day)) return '在籍日'
  return ''
}

function shiftMonth(delta) {
  query.yearMonth = shiftYearMonth(query.yearMonth, delta)
  fetchCalendar()
}

function handleReset() {
  query.dormitoryId = undefined
  query.genderType = undefined
  query.region = undefined
  query.name = ''
  query.yearMonth = defaultYearMonth
  fetchCalendar()
}

async function loadDormitoryOptions() {
  dormitoryOptions.value = await getDormitoryNameOptions()
}

function buildCalendarSearchParams() {
  return {
    yearMonth: query.yearMonth,
    dormitoryId: query.dormitoryId != null && query.dormitoryId !== ''
      ? String(query.dormitoryId)
      : undefined,
    genderType: query.genderType != null && query.genderType !== ''
      ? String(query.genderType)
      : undefined,
    region: query.region != null && query.region !== ''
      ? String(query.region)
      : undefined,
    name: query.name?.trim() || undefined
  }
}

async function fetchCalendar() {
  loading.value = true
  try {
    const data = await fetchDormAllocationCalendar(buildCalendarSearchParams())
    Object.assign(calendar, {
      yearMonth: data.yearMonth || query.yearMonth,
      daysInMonth: data.daysInMonth || 30,
      weekdayLabels: data.weekdayLabels || [],
      blocks: data.blocks || []
    })
  } finally {
    loading.value = false
  }
}

function roomEditRoute(block, row) {
  return {
    name: 'RoomEdit',
    params: { dormId: block.dormitoryId, roomId: row.roomId }
  }
}

function residentDetailRoute(row) {
  return {
    name: 'EmployeeList',
    query: { employeeId: row.employeeId }
  }
}

function goRegister(block, row) {
  router.push({
    name: 'ResidenceRegister',
    query: {
      tab: 'register',
      dormitoryId: block.dormitoryId,
      roomId: row.roomId
    }
  })
}

function goCheckout(row) {
  router.push({
    name: 'ResidenceRegister',
    query: {
      tab: 'checkout',
      residenceHistoryId: row.residenceHistoryId
    }
  })
}

function handlePrint() {
  window.print()
}

function initChart() {
  if (!vacancyChartRef.value) return
  chartInstance = echarts.init(vacancyChartRef.value)
  updateChart()
}

function updateChart() {
  if (!chartInstance) return
  const vacant = roomSummary.value.vacant ?? 0
  const occupied = roomSummary.value.occupied ?? 0
  chartInstance.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['52%', '78%'],
        center: ['50%', '50%'],
        label: { show: false },
        labelLine: { show: false },
        data: [
          { name: VACANCY_STATUS.VACANT, value: vacant, itemStyle: { color: '#0d5da9' } },
          { name: VACANCY_STATUS.OCCUPIED, value: occupied, itemStyle: { color: '#f6a504' } }
        ]
      }
    ]
  })
}

function handleResize() {
  chartInstance?.resize()
}

async function fetchVacancies() {
  chartLoading.value = true
  try {
    const data = await getVacancies({ page: 0, size: 1000 })
    const { list } = normalizePageResponse(data)
    vacancyList.value = list
    updateChart()
  } finally {
    chartLoading.value = false
  }
}

async function fetchAlerts() {
  alertLoading.value = true
  try {
    const data = await getLongTermUsageAlerts({ page: 0, size: 10 })
    const { list } = normalizePageResponse(data)
    alertList.value = list
  } finally {
    alertLoading.value = false
  }
}

function isActiveResidence(residence, asOfDate) {
  if (!residence?.moveInDate || residence.moveInDate > asOfDate) return false
  return !residence.moveOutDate || residence.moveOutDate >= asOfDate
}

async function fetchResidentSummary() {
  residentSummaryLoading.value = true
  try {
    const asOfDate = todayDateString()
    const [residenceData, employeeData] = await Promise.all([
      getResidences({ page: 0, size: 5000 }),
      searchEmployeesSilent({ page: 0, size: 5000 })
    ])
    const { list: residences } = normalizePageResponse(residenceData)
    const { list: employees } = normalizePageResponse(employeeData)
    const genderByEmployeeId = Object.fromEntries(
      employees.map((employee) => [employee.employeeId, employee.gender])
    )
    const activeResidences = residences.filter((item) => isActiveResidence(item, asOfDate))
    let male = 0
    let female = 0
    activeResidences.forEach((item) => {
      const gender = genderByEmployeeId[item.employeeId]
      if (gender === 'MALE') male += 1
      else if (gender === 'FEMALE') female += 1
    })
    residentSummary.value = {
      total: activeResidences.length,
      male,
      female
    }
  } finally {
    residentSummaryLoading.value = false
  }
}

onMounted(async () => {
  regionOptions.value = await loadRegionOptions({ silent: true })
  await loadDormitoryOptions()
  fetchCalendar()
  initChart()
  window.addEventListener('resize', handleResize)
  await Promise.all([fetchVacancies(), fetchAlerts(), fetchResidentSummary()])
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.dashboard-summary {
  margin-bottom: 20px;
}

.summary-row {
  margin-bottom: 4px;
}

.summary-row .el-col {
  display: flex;
}

.metric-card {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px 14px;
  border: 1px solid transparent;
  border-radius: 14px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
}

.metric-card--stat-only {
  justify-content: flex-start;
}

.metric-card__title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--cnc-text-secondary);
}

.metric-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.metric-card__main {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.metric-card__value {
  font-family: var(--cnc-font-en);
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
}

.metric-card__unit {
  font-size: 15px;
  color: var(--cnc-text-secondary);
}

.metric-card__count {
  font-family: var(--cnc-font-en);
  font-size: 24px;
  font-weight: 700;
  color: var(--cnc-text-secondary);
}

.metric-card__count.is-warning {
  color: #e11d48;
}

.metric-card__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.metric-chip {
  font-size: 13px;
  line-height: 1.7;
  padding: 1px 9px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.75);
  border: 1px solid rgba(15, 23, 42, 0.08);
  color: var(--cnc-text-primary);
}

.metric-chip--ghost {
  background: transparent;
}

/* カラー別カード */
.metric-card--indigo {
  background: #eef2ff;
  border-color: #c7d2fe;
}

.metric-card--indigo .metric-card__value {
  color: #4f46e5;
}

.metric-card--teal {
  background: #ecfeff;
  border-color: #a5f3fc;
}

.metric-card--teal .metric-card__value {
  color: #0e7490;
}

.metric-card--violet {
  background: #f5f3ff;
  border-color: #ddd6fe;
}

.metric-card--violet .metric-card__value {
  color: #7c3aed;
}

.metric-card--amber {
  background: #fffbeb;
  border-color: #fde68a;
}

.metric-card--rose {
  background: #fff1f2;
  border-color: #fecdd3;
}

.occupancy-body {
  display: flex;
  align-items: center;
  gap: 12px;
}

.occupancy-chart {
  flex: none;
  width: 78px;
  height: 78px;
}

.occupancy-stats {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
  min-width: 0;
  gap: 10px;
  font-size: 14px;
  color: var(--cnc-text-primary);
}

.occupancy-stat strong {
  font-family: var(--cnc-font-en);
  font-size: 18px;
  color: var(--cnc-text-primary);
}

.dot {
  display: inline-block;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  margin-right: 6px;
}

.dot--occupied {
  background: #f6a504;
}

.dot--vacant {
  background: #0d5da9;
}

.alert-mini-table {
  border-radius: 8px;
  overflow: hidden;
}

.alert-mini-table :deep(.el-table__cell) {
  font-size: 14px;
}

.alert-mini-table :deep(.el-table__empty-text) {
  font-size: 14px;
}

/* 月度ナビ：矢印アイコンを月度ラベルの左右に配置（中央寄せ） */
.allocation-month-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 14px;
  margin-bottom: 12px;
}

.allocation-month-bar__label {
  min-width: 140px;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: var(--cnc-text-primary);
  font-family: var(--cnc-font-en);
}

.allocation-detail-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 8px;
}

/* 寮ブロック間の白い隙間（スペーサー） */
.allocation-table .allocation-block-spacer td {
  height: 12px;
  padding: 0;
  border: none;
  background: #ffffff;
}

/* 寮単位で枠を付ける */
.allocation-table .allocation-block-body > tr > *:first-child {
  border-left: 2px solid #c5d0dc;
}

.allocation-table .allocation-block-body > tr > *:last-child {
  border-right: 2px solid #c5d0dc;
}

.allocation-table .allocation-block-body > tr:first-child > * {
  border-top: 2px solid #c5d0dc;
}

.allocation-table .allocation-block-body > tr:last-child > * {
  border-bottom: 2px solid #c5d0dc;
}

/* ヘッダ「寮情報」セルに寮名称を表示 */
.allocation-table .group-dorm-name {
  text-align: left;
  padding: 4px 8px;
}

.allocation-table .group-dorm-name .allocation-block-title-link {
  display: block;
  max-width: 100%;
  font-size: 14px;
  font-weight: 700;
  color: var(--cnc-primary-dark);
}

.allocation-table .group-dorm-name .allocation-block-title-link:hover {
  color: var(--cnc-primary);
  text-decoration: underline;
}

.allocation-print-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}
</style>
