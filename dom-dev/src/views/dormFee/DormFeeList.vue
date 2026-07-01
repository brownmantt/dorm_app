<template>
  <div class="page-container">
    <PageHeader title="寮費一覧・算定" subtitle="Dorm Fee" />

    <el-card class="search-card search-form-grid-card search-form-single-row-card dorm-fee-list-search-card">
      <el-form :model="query" label-width="84px" class="search-form-grid search-form-single-row">
        <div class="search-form-grid__cols">
          <el-form-item label="社員ID" class="search-field-employee-id">
            <el-input v-model="query.employeeId" clearable />
          </el-form-item>
          <el-form-item label="対象年月" class="search-field-year-month">
            <el-date-picker v-model="query.targetYearMonth" type="month" value-format="YYYY-MM" />
          </el-form-item>
          <el-form-item label="ステータス" class="search-field-status">
            <el-select v-model="query.status" clearable placeholder="すべて">
              <el-option label="仮定" value="PROVISIONAL" />
              <el-option label="エラー" value="ERROR" />
            </el-select>
          </el-form-item>
          <el-form-item class="search-form-grid__actions">
            <el-button type="primary" @click="handleSearch">検索</el-button>
            <el-button @click="handleReset">リセット</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <div class="table-card-toolbar">
        <el-button type="primary" @click="openCalcDialog">寮費算定</el-button>
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
      <div class="dorm-fee-table-scroll">
        <el-table
          :data="tableData"
          class="data-table dorm-fee-table"
          border
          stripe
          empty-text="データがありません"
          style="width: 100%"
        >
          <el-table-column type="index" label="番号" width="56" />
          <el-table-column label="地域・寮・部屋" min-width="148" class-name="col-location">
            <template #default="{ row }">
              <div class="location-cell">
                <div class="location-cell__region">{{ row.regionName || row.region || '—' }}</div>
                <div class="location-cell__dorm-room">{{ formatDormRoom(row) }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="入居者" width="80" class-name="col-compact">
            <template #default="{ row }">{{ row.employeeName || row.employeeId }}</template>
          </el-table-column>
          <el-table-column prop="targetYearMonth" label="対象年月" width="80" class-name="col-compact col-year-month" />
          <el-table-column prop="moveInDate" label="入居日" width="100" class-name="col-compact col-iso-date" />
          <el-table-column label="退居日" width="100" class-name="col-compact col-iso-date">
            <template #default="{ row }">{{ formatOptionalDate(row.residenceMoveOutDate) }}</template>
          </el-table-column>
          <el-table-column label="利用形態" width="80" class-name="col-compact">
            <template #default="{ row }">{{ row.usageTypeName || row.usageTypeCode }}</template>
          </el-table-column>
          <el-table-column label="利用日数" width="76" align="right" class-name="col-compact">
            <template #default="{ row }">{{ formatUsageDays(row.usageDays) }}</template>
          </el-table-column>
          <el-table-column label="日単価" width="88" align="right" class-name="col-compact">
            <template #default="{ row }">{{ formatYen(row.dailyUnitPrice) }}</template>
          </el-table-column>
          <el-table-column label="金額" width="96" align="right" class-name="col-compact">
            <template #default="{ row }">{{ formatYen(row.amount) }}</template>
          </el-table-column>
          <el-table-column
            prop="residenceHistoryId"
            label="入居履歴"
            width="144"
            class-name="col-compact col-residence-id"
            show-overflow-tooltip
          />
          <el-table-column prop="status" label="ステータス" width="88" class-name="col-compact col-status">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ERROR' ? 'danger' : 'success'" size="small">
                {{ labelOf(DORM_FEE_STATUS, row.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <el-dialog v-model="calcDialogVisible" title="寮費算定" width="640px" destroy-on-close>
      <el-form ref="calcFormRef" :model="calcForm" :rules="calcRules" label-width="120px" class="dialog-form">
        <el-form-item label="対象年月" prop="targetYearMonth">
          <el-date-picker
            v-model="calcForm.targetYearMonth"
            type="month"
            value-format="YYYY-MM"
            @change="handleTargetYearMonthChange"
          />
        </el-form-item>
        <el-form-item label="寮">
          <el-select
            v-model="calcForm.dormitoryId"
            filterable
            clearable
            placeholder="選択"
            :loading="dormitoryOptionsLoading"
            @change="handleDormitoryChange"
          >
            <el-option
              v-for="item in dormitoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部屋">
          <el-select
            v-model="calcForm.roomId"
            filterable
            clearable
            placeholder="選択"
            :disabled="!calcForm.dormitoryId"
            :loading="roomOptionsLoading"
            @change="handleRoomChange"
          >
            <el-option
              v-for="item in roomOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="社員">
          <el-select
            v-model="calcForm.employeeId"
            filterable
            clearable
            placeholder="選択"
            :disabled="!calcForm.targetYearMonth"
            :loading="employeeOptionsLoading"
          >
            <el-option
              v-for="item in employeeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <div class="form-inline-date-range">
          <el-form-item label="入居日" class="form-inline-date-range__item">
            <el-date-picker
              v-model="calcForm.moveInDate"
              type="date"
              value-format="YYYY-MM-DD"
              class="iso-date-editor"
            />
          </el-form-item>
          <el-form-item label="退居日" class="form-inline-date-range__item">
            <el-date-picker
              v-model="calcForm.moveOutDate"
              type="date"
              value-format="YYYY-MM-DD"
              class="iso-date-editor"
            />
          </el-form-item>
        </div>
      </el-form>
      <div v-if="calcResultItems.length > 0" class="calc-result-summary">
        算定合計: {{ formatYen(calcResult.amount) }}
        （仮定 {{ successCount }} 件 / エラー {{ errorCount }} 件）
      </div>
      <template #footer>
        <el-button @click="calcDialogVisible = false">閉じる</el-button>
        <el-button type="primary" :loading="calcLoading" @click="handleCalculate">算定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { calculateDormFee, getDormFees } from '@/api/dormFee'
import { getRoomsByDormitory } from '@/api/dormitory'
import { normalizePageResponse } from '@/utils/pagination'
import { DORM_FEE_STATUS, labelOf } from '@/utils/constants'
import { loadDormFeeEmployeeOptions } from '@/utils/employee'
import { loadDormFeeDormitoryOptions, mapRoomListToOptions } from '@/utils/dormitory'

function formatUsageDays(value) {
  if (value == null || value === '') return '—'
  return `${value}日`
}

function formatYen(value) {
  if (value == null || value === '') return '—'
  return `${Number(value).toLocaleString('ja-JP')}円`
}

function formatOptionalDate(value) {
  return value || '—'
}

function formatDormRoom(row) {
  const dorm = row.dormitoryName || row.dormitoryId
  const room = row.roomName || row.roomId
  if (!dorm && !room) return '—'
  if (!room) return dorm
  if (!dorm) return room
  return `${dorm}・${room}`
}

const loading = ref(false)
const calcLoading = ref(false)
const calcDialogVisible = ref(false)
const employeeOptionsLoading = ref(false)
const dormitoryOptionsLoading = ref(false)
const roomOptionsLoading = ref(false)
const calcFormRef = ref()
const tableData = ref([])
const employeeOptions = ref([])
const dormitoryOptions = ref([])
const roomOptions = ref([])

const query = reactive({
  employeeId: '',
  targetYearMonth: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

const calcForm = reactive({
  employeeId: '',
  dormitoryId: '',
  roomId: '',
  targetYearMonth: '',
  moveInDate: '',
  moveOutDate: ''
})

const calcResult = reactive({
  amount: null,
  items: []
})

const calcRules = {
  targetYearMonth: [{ required: true, message: '対象年月を選択してください', trigger: 'change' }]
}

function buildCalcPayload() {
  return {
    employeeId: calcForm.employeeId || null,
    dormitoryId: calcForm.dormitoryId || null,
    roomId: calcForm.roomId || null,
    targetYearMonth: calcForm.targetYearMonth,
    moveInDate: calcForm.moveInDate || null,
    moveOutDate: calcForm.moveOutDate || null
  }
}

const calcResultItems = computed(() => calcResult.items ?? [])
const successCount = computed(() => calcResultItems.value.filter((i) => i.status === 'PROVISIONAL').length)
const errorCount = computed(() => calcResultItems.value.filter((i) => i.status === 'ERROR').length)

async function fetchList() {
  loading.value = true
  try {
    const data = await getDormFees({
      ...query,
      page: pagination.page - 1,
      size: pagination.size
    })
    const { list, total } = normalizePageResponse(data)
    tableData.value = list
    pagination.total = total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchList()
}

function handleReset() {
  query.employeeId = ''
  query.targetYearMonth = ''
  query.status = ''
  handleSearch()
}

function resetCalcForm() {
  calcForm.employeeId = ''
  calcForm.dormitoryId = ''
  calcForm.roomId = ''
  calcForm.targetYearMonth = ''
  calcForm.moveInDate = ''
  calcForm.moveOutDate = ''
  calcResult.amount = null
  calcResult.items = []
  roomOptions.value = []
  calcFormRef.value?.resetFields()
}

async function fetchDormitoryOptions() {
  dormitoryOptionsLoading.value = true
  try {
    dormitoryOptions.value = await loadDormFeeDormitoryOptions({ silent: true })
  } finally {
    dormitoryOptionsLoading.value = false
  }
}

async function loadRoomOptions(dormitoryId) {
  if (!dormitoryId) {
    roomOptions.value = []
    return
  }
  roomOptionsLoading.value = true
  try {
    const data = await getRoomsByDormitory(dormitoryId, { page: 0, size: 500 })
    const { list } = normalizePageResponse(data)
    roomOptions.value = mapRoomListToOptions(list)
  } finally {
    roomOptionsLoading.value = false
  }
}

function handleDormitoryChange(dormitoryId) {
  calcForm.roomId = ''
  calcForm.employeeId = ''
  loadRoomOptions(dormitoryId)
  fetchEmployeeOptions()
}

function handleRoomChange() {
  calcForm.employeeId = ''
  fetchEmployeeOptions()
}

function handleTargetYearMonthChange() {
  calcForm.employeeId = ''
  fetchEmployeeOptions()
}

async function fetchEmployeeOptions() {
  if (!calcForm.targetYearMonth) {
    employeeOptions.value = []
    return
  }
  employeeOptionsLoading.value = true
  try {
    employeeOptions.value = await loadDormFeeEmployeeOptions({
      silent: true,
      targetYearMonth: calcForm.targetYearMonth,
      dormitoryId: calcForm.dormitoryId || undefined,
      roomId: calcForm.roomId || undefined
    })
    if (calcForm.employeeId && !employeeOptions.value.some((item) => item.value === calcForm.employeeId)) {
      calcForm.employeeId = ''
    }
  } finally {
    employeeOptionsLoading.value = false
  }
}

async function openCalcDialog() {
  resetCalcForm()
  calcDialogVisible.value = true
  await fetchDormitoryOptions()
}

async function handleCalculate() {
  await calcFormRef.value.validate()
  calcLoading.value = true
  try {
    const data = await calculateDormFee(buildCalcPayload())
    calcResult.amount = data.amount
    calcResult.items = data.items ?? []
    if (errorCount.value > 0) {
      ElMessage.warning(`算定完了（仮定 ${successCount.value} 件、エラー ${errorCount.value} 件）`)
    } else {
      ElMessage.success('算定が完了しました')
    }
    fetchList()
  } finally {
    calcLoading.value = false
  }
}

onMounted(fetchList)
</script>

<style scoped>
.dorm-fee-table-scroll {
  width: 100%;
}

.location-cell {
  display: block;
  line-height: 1.4;
}

.location-cell__region {
  font-size: 11px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.location-cell__dorm-room {
  font-size: 14px;
  color: var(--el-text-color-primary);
  white-space: nowrap;
}

:deep(.dorm-fee-table .el-table__header-wrapper),
:deep(.dorm-fee-table .el-table__body-wrapper) {
  overflow: visible !important;
}

:deep(.dorm-fee-table .el-table__body-wrapper .el-scrollbar__wrap) {
  overflow: visible !important;
}

:deep(.col-location .cell) {
  white-space: normal;
}

:deep(.dorm-fee-table .col-compact .cell) {
  padding-left: 6px;
  padding-right: 6px;
}

:deep(.dorm-fee-table .col-residence-id .cell) {
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

:deep(.dorm-fee-table .col-iso-date.el-table__cell) {
  width: 100px;
  min-width: 100px;
  max-width: 100px;
}

:deep(.dorm-fee-table .col-year-month .cell),
:deep(.dorm-fee-table .col-iso-date .cell) {
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

:deep(.dorm-fee-table .col-status .cell) {
  white-space: nowrap;
}

.calc-result-summary {
  margin-top: 16px;
  font-weight: 600;
}
</style>
