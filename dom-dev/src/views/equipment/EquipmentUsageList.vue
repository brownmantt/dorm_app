<template>
  <div class="page-container">
    <PageHeader title="備品利用・解除" subtitle="Equipment Usage & Release" />

    <el-card class="search-card search-form-grid-card search-form-single-row-card equipment-usage-search-card">
      <el-form :model="searchForm" label-width="72px" class="search-form-grid search-form-single-row" @submit.prevent="handleSearch">
        <div class="search-form-grid__cols">
          <el-form-item label="備品" class="search-field-equipment">
            <el-select
              v-model="searchForm.equipmentAssetId"
              clearable
              filterable
              placeholder="すべて"
            >
              <el-option
                v-for="item in assetOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="寮" class="search-field-dormitory">
            <el-select
              v-model="searchForm.dormitoryId"
              clearable
              filterable
              placeholder="すべて"
              @change="handleSearchDormitoryChange"
            >
              <el-option
                v-for="item in dormitoryOptions"
                :key="item.dormitoryId"
                :label="item.name"
                :value="item.dormitoryId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="部屋" class="search-field-room">
            <el-select
              v-model="searchForm.roomId"
              clearable
              filterable
              placeholder="すべて"
              :disabled="!searchForm.dormitoryId"
            >
              <el-option
                v-for="item in searchRoomOptions"
                :key="item.roomId"
                :label="item.roomName || item.roomId"
                :value="item.roomId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="入居者" class="search-field-employee">
            <el-select
              v-model="searchForm.employeeId"
              clearable
              filterable
              placeholder="すべて"
            >
              <el-option
                v-for="item in employeeOptions"
                :key="item.employeeId"
                :label="formatEmployeeLabel(item)"
                :value="item.employeeId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="利用中のみ" class="search-field-active-only">
            <el-switch v-model="searchForm.activeOnly" />
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
        <el-button type="primary" @click="openDialog()">利用登録</el-button>
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
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column label="備品" min-width="180">
          <template #default="{ row }">
            <div>{{ row.equipmentAssetId }}</div>
            <div class="sub-text">{{ row.equipmentName || '—' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="dormitoryName" label="寮" min-width="120">
          <template #default="{ row }">{{ row.dormitoryName || '—' }}</template>
        </el-table-column>
        <el-table-column prop="roomName" label="部屋" min-width="100">
          <template #default="{ row }">{{ row.roomName || '—' }}</template>
        </el-table-column>
        <el-table-column label="入居者" min-width="140">
          <template #default="{ row }">
            {{ formatEmployeeDisplay(row.employeeId, row.employeeName) }}
          </template>
        </el-table-column>
        <el-table-column prop="usageStartDate" label="利用開始日" width="110" />
        <el-table-column label="利用終了日" width="110">
          <template #default="{ row }">
            <el-tag v-if="!row.usageEndDate" type="success" size="small">利用中</el-tag>
            <span v-else>{{ row.usageEndDate }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="usageQuantity" label="利用個数" width="90" align="right" />
        <el-table-column label="備考" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">{{ row.remarks || '—' }}</template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="100"
          fixed="right"
          align="center"
          header-align="center"
          class-name="col-actions"
        >
          <template #default="{ row }">
            <el-button
              v-if="!row.usageEndDate"
              link
              type="warning"
              @click="handleRelease(row)"
            >
              利用解除
            </el-button>
            <span v-else class="sub-text">—</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="備品利用登録" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="dialog-form">
        <el-form-item label="備品" prop="equipmentAssetId">
          <el-select
            v-model="form.equipmentAssetId"
            filterable
            placeholder="備品を選択"
            style="width: 100%"
            @change="handleAssetChange"
          >
            <el-option
              v-for="item in assetOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div v-if="selectedAssetHint" class="form-hint">{{ selectedAssetHint }}</div>
        </el-form-item>
        <el-form-item label="寮" prop="dormitoryId">
          <el-select
            v-model="form.dormitoryId"
            clearable
            filterable
            placeholder="寮を選択"
            style="width: 100%"
            @change="handleFormDormitoryChange"
          >
            <el-option
              v-for="item in dormitoryOptions"
              :key="item.dormitoryId"
              :label="item.name"
              :value="item.dormitoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部屋" prop="roomId">
          <el-select
            v-model="form.roomId"
            clearable
            filterable
            placeholder="部屋を選択（任意）"
            style="width: 100%"
            :disabled="!form.dormitoryId"
          >
            <el-option
              v-for="item in formRoomOptions"
              :key="item.roomId"
              :label="item.roomName || item.roomId"
              :value="item.roomId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入居者" prop="employeeId">
          <el-select
            v-model="form.employeeId"
            clearable
            filterable
            placeholder="入居者を選択"
            style="width: 100%"
          >
            <el-option
              v-for="item in employeeOptions"
              :key="item.employeeId"
              :label="formatEmployeeLabel(item)"
              :value="item.employeeId"
            />
          </el-select>
          <div class="form-hint">※寮・入居者のいずれか必須（部屋は任意）</div>
        </el-form-item>
        <el-form-item label="利用開始日" prop="usageStartDate">
          <el-date-picker
            v-model="form.usageStartDate"
            type="date"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="利用個数" prop="usageQuantity">
          <el-input-number v-model="form.usageQuantity" :min="1" :max="9999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="備考" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" :rows="3" maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">登録</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="releaseDialogVisible" title="備品利用解除" width="480px" destroy-on-close>
      <p v-if="releaseTarget" class="release-summary">
        {{ releaseTarget.equipmentName || '備品' }}の利用を解除します。
      </p>
      <el-form ref="releaseFormRef" :model="releaseForm" :rules="releaseRules" label-width="120px" class="dialog-form">
        <el-form-item label="利用終了日" prop="usageEndDate">
          <el-date-picker
            v-model="releaseForm.usageEndDate"
            type="date"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="releaseDialogVisible = false">キャンセル</el-button>
        <el-button type="warning" :loading="releaseLoading" @click="confirmRelease">解除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { getEquipmentAssets } from '@/api/equipmentAsset'
import {
  createEquipmentUsage,
  getEquipmentUsages,
  releaseEquipmentUsage
} from '@/api/equipmentUsage'
import { getDormitories, getRoomsByDormitory } from '@/api/dormitory'
import { getEmployees } from '@/api/employee'
import { normalizePageResponse } from '@/utils/pagination'
import { todayDateString } from '@/utils/date'

const loading = ref(false)
const submitLoading = ref(false)
const releaseLoading = ref(false)
const dialogVisible = ref(false)
const releaseDialogVisible = ref(false)
const formRef = ref()
const releaseFormRef = ref()
const tableData = ref([])
const assetOptions = ref([])
const assetMap = ref({})
const dormitoryOptions = ref([])
const employeeOptions = ref([])
const searchRoomOptions = ref([])
const formRoomOptions = ref([])
const releaseTarget = ref(null)

const pagination = reactive({ page: 1, size: 20, total: 0 })

const searchForm = reactive({
  equipmentAssetId: '',
  dormitoryId: '',
  roomId: '',
  employeeId: '',
  activeOnly: true
})

const form = reactive({
  equipmentAssetId: '',
  dormitoryId: '',
  roomId: '',
  employeeId: '',
  usageStartDate: '',
  usageQuantity: 1,
  remarks: ''
})

const releaseForm = reactive({
  usageEndDate: ''
})

const rules = {
  equipmentAssetId: [{ required: true, message: '備品を選択してください', trigger: 'change' }],
  dormitoryId: [{ validator: validateDormitoryOrEmployee, trigger: 'change' }],
  employeeId: [{ validator: validateDormitoryOrEmployee, trigger: 'change' }],
  usageStartDate: [{ required: true, message: '利用開始日を選択してください', trigger: 'change' }],
  usageQuantity: [{ required: true, message: '利用個数を入力してください', trigger: 'change' }]
}

const releaseRules = {
  usageEndDate: [{ required: true, message: '利用終了日を選択してください', trigger: 'change' }]
}

const selectedAssetHint = computed(() => {
  const asset = assetMap.value[form.equipmentAssetId]
  if (!asset) return ''
  const qty = asset.purchaseQuantity ?? 1
  return `購入数量: ${qty}（利用中の合計がこの数量を超えないよう登録されます）`
})

function validateDormitoryOrEmployee(_rule, _value, callback) {
  if (form.dormitoryId || form.employeeId) {
    callback()
  } else {
    callback(new Error('寮または入居者のいずれかを選択してください'))
  }
}

function formatEmployeeLabel(item) {
  if (!item) return ''
  const name = item.name || item.employeeName || ''
  return name ? `${item.employeeId} - ${name}` : item.employeeId
}

function formatEmployeeDisplay(employeeId, employeeName) {
  if (!employeeId) return '—'
  return employeeName ? `${employeeId} - ${employeeName}` : employeeId
}

function buildAssetLabel(row) {
  return row.equipmentName || row.equipmentAssetId
}

async function fetchAssetOptions() {
  const data = await getEquipmentAssets({ page: 0, size: 5000 })
  const { list } = normalizePageResponse(data)
  const map = {}
  assetOptions.value = list.map((row) => {
    map[row.equipmentAssetId] = row
    return { value: row.equipmentAssetId, label: buildAssetLabel(row) }
  })
  assetMap.value = map
}

async function fetchDormitoryOptions() {
  const data = await getDormitories({ page: 0, size: 2000 })
  const { list } = normalizePageResponse(data)
  dormitoryOptions.value = list.sort((a, b) => (a.name || '').localeCompare(b.name || '', 'ja'))
}

async function fetchEmployeeOptions() {
  const data = await getEmployees({ page: 0, size: 5000 })
  const { list } = normalizePageResponse(data)
  employeeOptions.value = list
}

async function loadSearchRooms(dormitoryId) {
  if (!dormitoryId) {
    searchRoomOptions.value = []
    return
  }
  const data = await getRoomsByDormitory(dormitoryId, { page: 0, size: 500 })
  const { list } = normalizePageResponse(data)
  searchRoomOptions.value = list
}

async function loadFormRooms(dormitoryId) {
  if (!dormitoryId) {
    formRoomOptions.value = []
    return
  }
  const data = await getRoomsByDormitory(dormitoryId, { page: 0, size: 500 })
  const { list } = normalizePageResponse(data)
  formRoomOptions.value = list
}

function handleSearchDormitoryChange() {
  searchForm.roomId = ''
  loadSearchRooms(searchForm.dormitoryId)
}

function handleFormDormitoryChange() {
  form.roomId = ''
  loadFormRooms(form.dormitoryId)
}

function handleAssetChange() {
  // hint updates via computed
}

async function fetchList() {
  loading.value = true
  try {
    const params = {
      page: pagination.page - 1,
      size: pagination.size
    }
    if (searchForm.equipmentAssetId) params.equipmentAssetId = searchForm.equipmentAssetId
    if (searchForm.dormitoryId) params.dormitoryId = searchForm.dormitoryId
    if (searchForm.roomId) params.roomId = searchForm.roomId
    if (searchForm.employeeId) params.employeeId = searchForm.employeeId
    if (searchForm.activeOnly) params.activeOnly = true

    const data = await getEquipmentUsages(params)
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
  searchForm.equipmentAssetId = ''
  searchForm.dormitoryId = ''
  searchForm.roomId = ''
  searchForm.employeeId = ''
  searchForm.activeOnly = true
  searchRoomOptions.value = []
  handleSearch()
}

function resetForm() {
  form.equipmentAssetId = ''
  form.dormitoryId = ''
  form.roomId = ''
  form.employeeId = ''
  form.usageStartDate = todayDateString()
  form.usageQuantity = 1
  form.remarks = ''
  formRoomOptions.value = []
  formRef.value?.resetFields()
}

function openDialog() {
  resetForm()
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await createEquipmentUsage({
      equipmentAssetId: form.equipmentAssetId,
      dormitoryId: form.dormitoryId || null,
      roomId: form.roomId || null,
      employeeId: form.employeeId || null,
      usageStartDate: form.usageStartDate,
      usageQuantity: form.usageQuantity,
      remarks: form.remarks || null
    })
    ElMessage.success('備品利用を登録しました')
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

function handleRelease(row) {
  releaseTarget.value = row
  releaseForm.usageEndDate = todayDateString()
  releaseDialogVisible.value = true
}

async function confirmRelease() {
  await releaseFormRef.value.validate()
  if (!releaseTarget.value) return
  releaseLoading.value = true
  try {
    await releaseEquipmentUsage(releaseTarget.value.usageId, {
      usageEndDate: releaseForm.usageEndDate
    })
    ElMessage.success('備品利用を解除しました')
    releaseDialogVisible.value = false
    releaseTarget.value = null
    fetchList()
  } finally {
    releaseLoading.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    fetchAssetOptions(),
    fetchDormitoryOptions(),
    fetchEmployeeOptions()
  ])
  fetchList()
})
</script>

<style scoped>
.sub-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.form-hint {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.release-summary {
  margin: 0 0 16px;
  color: var(--el-text-color-regular);
}
</style>
