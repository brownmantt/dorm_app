<template>
  <div class="page-container">
    <PageHeader title="単価マスタ" subtitle="Unit Price Master" />

    <el-card class="search-card search-form-grid-card search-form-single-row-card unit-price-list-search-card">
      <el-form :model="query" label-width="84px" class="search-form-grid search-form-single-row">
        <div class="search-form-grid__cols">
          <el-form-item label="単価コード" class="search-field-code">
            <el-input v-model="query.code" clearable placeholder="コード" @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="地域" class="search-field-region">
            <el-select v-model="query.region" clearable placeholder="すべて" @change="handleSearchRegionChange">
              <el-option
                v-for="item in regionOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="寮" class="search-field-dormitory">
            <el-select
              v-model="query.dormitoryId"
              clearable
              filterable
              placeholder="すべて"
              :disabled="!query.region"
            >
              <el-option
                v-for="item in searchDormitoryOptions"
                :key="item.dormitoryId"
                :label="item.name"
                :value="item.dormitoryId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="利用形態" class="search-field-usage-type">
            <el-select v-model="query.usageTypeCode" clearable placeholder="すべて">
              <el-option
                v-for="item in usageTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
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
        <el-button type="primary" @click="openDialog()">新規登録</el-button>
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
        <el-table-column prop="code" label="単価コード" min-width="120" />
        <el-table-column label="地域" min-width="100">
          <template #default="{ row }">
            {{ row.regionName || row.region }}
          </template>
        </el-table-column>
        <el-table-column label="寮" min-width="140">
          <template #default="{ row }">
            {{ row.dormitoryName || row.dormitoryId || '—' }}
          </template>
        </el-table-column>
        <el-table-column label="部屋" min-width="120">
          <template #default="{ row }">
            {{ row.roomName || row.roomId || '—' }}
          </template>
        </el-table-column>
        <el-table-column label="利用形態" min-width="120">
          <template #default="{ row }">
            {{ row.usageTypeName || row.usageTypeCode }}
          </template>
        </el-table-column>
        <el-table-column prop="dailyUnitPrice" label="日単価" min-width="100" align="right" />
        <el-table-column
          label="操作"
          width="120"
          fixed="right"
          align="center"
          header-align="center"
          class-name="col-actions"
        >
          <template #default="{ row }">
            <div class="table-actions-inline">
              <el-button link type="primary" @click="openDialog(row)">編集</el-button>
              <el-button link type="danger" @click="handleDelete(row)">削除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close @open="handleDialogOpen">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="dialog-form">
        <el-form-item v-if="form.unitPriceId" label="単価コード">
          <el-input :model-value="form.code" disabled />
        </el-form-item>
        <el-form-item label="地域" prop="region">
          <el-select v-model="form.region" placeholder="選択" @change="handleFormRegionChange">
            <el-option
              v-for="item in regionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="寮">
          <el-select
            v-model="form.dormitoryId"
            clearable
            filterable
            placeholder="選択"
            :disabled="!form.region"
            @change="handleFormDormitoryChange"
          >
            <el-option
              v-for="item in formDormitoryOptions"
              :key="item.dormitoryId"
              :label="item.name"
              :value="item.dormitoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部屋">
          <el-select
            v-model="form.roomId"
            clearable
            filterable
            placeholder="選択"
            :disabled="!form.dormitoryId"
            :loading="roomLoading"
          >
            <el-option
              v-for="item in formRoomOptions"
              :key="item.roomId"
              :label="item.roomName"
              :value="item.roomId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="利用形態" prop="usageTypeCode">
          <el-select v-model="form.usageTypeCode" placeholder="選択">
            <el-option
              v-for="item in usageTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日単価" prop="dailyUnitPrice">
          <el-input-number v-model="form.dailyUnitPrice" :min="0" :precision="2" :step="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { getDormitories, getRoomsByDormitory } from '@/api/dormitory'
import { createUnitPrice, deleteUnitPrice, getUnitPrices, updateUnitPrice } from '@/api/unitPrice'
import { normalizePageResponse } from '@/utils/pagination'
import { loadRegionOptions } from '@/utils/region'
import { loadUsageTypeOptions } from '@/utils/usageType'

const loading = ref(false)
const submitLoading = ref(false)
const roomLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])

const regionOptions = ref([])
const usageTypeOptions = ref([])
const allDormitories = ref([])
const searchDormitoryOptions = ref([])
const formDormitoryOptions = ref([])
const formRoomOptions = ref([])

const query = reactive({
  code: '',
  region: undefined,
  dormitoryId: undefined,
  usageTypeCode: undefined
})

const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  unitPriceId: '',
  code: '',
  region: '',
  dormitoryId: '',
  roomId: '',
  usageTypeCode: '',
  dailyUnitPrice: null
})

const rules = {
  region: [{ required: true, message: '地域を選択してください', trigger: 'change' }],
  usageTypeCode: [{ required: true, message: '利用形態を選択してください', trigger: 'change' }],
  dailyUnitPrice: [{ required: true, message: '日単価を入力してください', trigger: 'change' }]
}

const dialogTitle = computed(() => (form.unitPriceId ? '単価編集' : '単価新規登録'))

const editingRow = ref(null)

function filterDormitoriesByRegion(region) {
  if (!region) return []
  return allDormitories.value.filter((item) => item.region === region)
}

async function loadMasterOptions() {
  const [regions, usageTypes, dormData] = await Promise.all([
    loadRegionOptions(),
    loadUsageTypeOptions(),
    getDormitories({ page: 0, size: 2000 })
  ])
  regionOptions.value = regions
  usageTypeOptions.value = usageTypes
  const { list } = normalizePageResponse(dormData)
  allDormitories.value = list.sort((a, b) => (a.name || '').localeCompare(b.name || '', 'ja'))
}

async function loadFormRooms(dormitoryId, preserveRoomId) {
  if (!dormitoryId) {
    formRoomOptions.value = []
    return
  }
  roomLoading.value = true
  try {
    const data = await getRoomsByDormitory(dormitoryId, { page: 0, size: 500 })
    const { list } = normalizePageResponse(data)
    formRoomOptions.value = list
    if (preserveRoomId && !list.some((item) => item.roomId === preserveRoomId)) {
      form.roomId = ''
    }
  } finally {
    roomLoading.value = false
  }
}

function handleSearchRegionChange() {
  query.dormitoryId = undefined
  searchDormitoryOptions.value = filterDormitoriesByRegion(query.region)
}

function handleFormRegionChange() {
  form.dormitoryId = ''
  form.roomId = ''
  formDormitoryOptions.value = filterDormitoriesByRegion(form.region)
  formRoomOptions.value = []
}

async function handleFormDormitoryChange() {
  if (!form.dormitoryId) {
    form.roomId = ''
    formRoomOptions.value = []
    return
  }
  form.roomId = ''
  await loadFormRooms(form.dormitoryId)
}

async function fetchList() {
  loading.value = true
  try {
    const data = await getUnitPrices({
      code: query.code || undefined,
      region: query.region || undefined,
      dormitoryId: query.dormitoryId || undefined,
      usageTypeCode: query.usageTypeCode || undefined,
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
  query.code = ''
  query.region = undefined
  query.dormitoryId = undefined
  query.usageTypeCode = undefined
  searchDormitoryOptions.value = []
  handleSearch()
}

function openDialog(row) {
  editingRow.value = row || null
  dialogVisible.value = true
}

async function handleDialogOpen() {
  const row = editingRow.value
  Object.assign(form, {
    unitPriceId: row?.unitPriceId || '',
    code: row?.code || '',
    region: row?.region || '',
    dormitoryId: row?.dormitoryId || '',
    roomId: row?.roomId || '',
    usageTypeCode: row?.usageTypeCode || '',
    dailyUnitPrice: row?.dailyUnitPrice ?? null
  })
  formDormitoryOptions.value = filterDormitoriesByRegion(form.region)
  if (form.dormitoryId) {
    await loadFormRooms(form.dormitoryId, form.roomId)
  } else {
    formRoomOptions.value = []
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload = {
      region: form.region,
      dormitoryId: form.dormitoryId || null,
      roomId: form.roomId || null,
      usageTypeCode: form.usageTypeCode,
      dailyUnitPrice: form.dailyUnitPrice
    }
    if (form.unitPriceId) {
      await updateUnitPrice(form.unitPriceId, payload)
      ElMessage.success('単価を更新しました')
    } else {
      await createUnitPrice(payload)
      ElMessage.success('単価を登録しました')
      editingRow.value = null
      Object.assign(form, {
        unitPriceId: '',
        code: '',
        region: '',
        dormitoryId: '',
        roomId: '',
        usageTypeCode: '',
        dailyUnitPrice: null
      })
      formDormitoryOptions.value = []
      formRoomOptions.value = []
      formRef.value?.resetFields()
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`単価「${row.code}」を削除しますか？`, '確認', { type: 'warning' })
  await deleteUnitPrice(row.unitPriceId)
  ElMessage.success('削除しました')
  fetchList()
}

watch(
  () => query.region,
  (region) => {
    searchDormitoryOptions.value = filterDormitoriesByRegion(region)
    if (query.dormitoryId && !searchDormitoryOptions.value.some((item) => item.dormitoryId === query.dormitoryId)) {
      query.dormitoryId = undefined
    }
  }
)

onMounted(async () => {
  await loadMasterOptions()
  fetchList()
})
</script>
