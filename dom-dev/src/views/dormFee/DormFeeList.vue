<template>
  <div class="page-container">
    <PageHeader title="寮費一覧・算定" subtitle="Dorm Fee" />

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="社員ID">
          <el-input v-model="query.employeeId" clearable />
        </el-form-item>
        <el-form-item label="対象年月">
          <el-date-picker v-model="query.targetYearMonth" type="month" value-format="YYYY-MM" />
        </el-form-item>
        <el-form-item label="ステータス">
          <el-select v-model="query.status" clearable placeholder="すべて">
            <el-option label="下書き" value="DRAFT" />
            <el-option label="確定" value="CONFIRMED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">検索</el-button>
          <el-button @click="handleReset">リセット</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card">
      <el-button type="primary" @click="calcDialogVisible = true">寮費算定</el-button>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="dormFeeId" label="寮費ID" min-width="140" />
        <el-table-column prop="employeeId" label="社員ID" min-width="140" />
        <el-table-column prop="targetYearMonth" label="対象年月" min-width="120" />
        <el-table-column prop="amount" label="金額" min-width="120" />
        <el-table-column prop="status" label="ステータス" min-width="110">
          <template #default="{ row }">
            {{ labelOf(DORM_FEE_STATUS, row.status) }}
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="88"
          fixed="right"
          align="center"
          header-align="center"
          class-name="col-actions"
        >
          <template #default="{ row }">
            <div class="table-actions-inline">
              <el-button
                v-if="row.status === 'DRAFT'"
                link
                type="primary"
                @click="handleConfirm(row)"
              >
                確定
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
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
    </el-card>

    <el-dialog v-model="calcDialogVisible" title="寮費算定" width="560px" destroy-on-close>
      <el-form ref="calcFormRef" :model="calcForm" :rules="calcRules" label-width="120px" class="dialog-form">
        <el-form-item label="社員ID" prop="employeeId">
          <el-input v-model="calcForm.employeeId" />
        </el-form-item>
        <el-form-item label="部屋ID" prop="roomId">
          <el-input v-model="calcForm.roomId" />
        </el-form-item>
        <el-form-item label="対象年月" prop="targetYearMonth">
          <el-date-picker v-model="calcForm.targetYearMonth" type="month" value-format="YYYY-MM" />
        </el-form-item>
        <div class="form-inline-date-range">
          <el-form-item label="入居日" prop="moveInDate" class="form-inline-date-range__item">
            <el-date-picker
              v-model="calcForm.moveInDate"
              type="date"
              value-format="YYYY-MM-DD"
              class="iso-date-editor"
            />
          </el-form-item>
          <el-form-item label="退居日" prop="moveOutDate" class="form-inline-date-range__item">
            <el-date-picker
              v-model="calcForm.moveOutDate"
              type="date"
              value-format="YYYY-MM-DD"
              class="iso-date-editor"
            />
          </el-form-item>
        </div>
      </el-form>
      <el-descriptions v-if="calcResult.amount != null" :column="1" border class="detail-descriptions calc-result">
        <el-descriptions-item label="算定金額">{{ calcResult.amount }} 円</el-descriptions-item>
        <el-descriptions-item label="算定根拠">{{ calcBasisText }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="calcDialogVisible = false">閉じる</el-button>
        <el-button type="primary" :loading="calcLoading" @click="handleCalculate">算定</el-button>
        <el-button type="success" :disabled="calcResult.amount == null" @click="handleSaveFee">登録</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import {
  calculateDormFee,
  confirmDormFee,
  createDormFee,
  getDormFees
} from '@/api/dormFee'
import { normalizePageResponse } from '@/utils/pagination'
import { DORM_FEE_STATUS, labelOf } from '@/utils/constants'

const loading = ref(false)
const calcLoading = ref(false)
const calcDialogVisible = ref(false)
const calcFormRef = ref()
const tableData = ref([])

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
  roomId: '',
  targetYearMonth: '',
  moveInDate: '',
  moveOutDate: ''
})

const calcResult = reactive({
  amount: null,
  basis: null
})

const calcRules = {
  employeeId: [{ required: true, message: '社員IDを入力してください', trigger: 'blur' }],
  roomId: [{ required: true, message: '部屋IDを入力してください', trigger: 'blur' }],
  targetYearMonth: [{ required: true, message: '対象年月を選択してください', trigger: 'change' }],
  moveInDate: [{ required: true, message: '入居日を選択してください', trigger: 'change' }]
}

const calcBasisText = computed(() => {
  if (!calcResult.basis) return '-'
  const b = calcResult.basis
  return `面積: ${b.roomAreaSqm}㎡ / 種別: ${b.roomType} / 請求日数: ${b.billableDays} / 日額: ${b.dailyRate} / 式: ${b.formula || '-'}`
})

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

async function handleCalculate() {
  await calcFormRef.value.validate()
  calcLoading.value = true
  try {
    const data = await calculateDormFee({ ...calcForm })
    calcResult.amount = data.amount
    calcResult.basis = data.basis
    ElMessage.success('算定が完了しました')
  } finally {
    calcLoading.value = false
  }
}

async function handleSaveFee() {
  await createDormFee({
    employeeId: calcForm.employeeId,
    roomId: calcForm.roomId,
    targetYearMonth: calcForm.targetYearMonth,
    amount: calcResult.amount,
    basisDetail: calcResult.basis
  })
  ElMessage.success('寮費を登録しました')
  calcDialogVisible.value = false
  calcResult.amount = null
  calcResult.basis = null
  fetchList()
}

async function handleConfirm(row) {
  await ElMessageBox.confirm('この寮費を確定しますか？', '確認', { type: 'warning' })
  await confirmDormFee(row.dormFeeId)
  ElMessage.success('確定しました')
  fetchList()
}

onMounted(fetchList)
</script>

<style scoped>
.calc-result {
  margin-top: 16px;
}
</style>
