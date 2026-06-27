<template>
  <div class="page-container">
    <PageHeader title="社員マスタ" subtitle="Employee Master" />

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="キーワード">
          <el-input
            v-model="query.keyword"
            clearable
            placeholder="社員ID / 氏名"
            style="width: 180px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="性別">
          <el-select v-model="query.gender" clearable placeholder="すべて" style="width: 120px">
            <el-option
              v-for="item in genderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入居者区分">
          <el-select v-model="query.employeeCategory" clearable placeholder="すべて" style="width: 140px">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属">
          <el-select
            v-model="query.affiliationId"
            clearable
            filterable
            placeholder="すべて"
            style="min-width: 180px"
          >
            <el-option
              v-for="item in affiliationOptions"
              :key="item.affiliationId"
              :label="item.name"
              :value="item.affiliationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">検索</el-button>
          <el-button @click="handleReset">リセット</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card">
      <el-button type="primary" @click="openDialog()">新規登録</el-button>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table table-wrap-text" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="employeeId" label="社員ID" min-width="120" />
        <el-table-column prop="name" label="氏名" min-width="140" />
        <el-table-column label="性別" min-width="80">
          <template #default="{ row }">{{ labelOf(EMPLOYEE_GENDER, row.gender) }}</template>
        </el-table-column>
        <el-table-column label="入居者区分" min-width="110">
          <template #default="{ row }">{{ labelOf(EMPLOYEE_CATEGORY, row.employeeCategory) }}</template>
        </el-table-column>
        <el-table-column prop="affiliationName" label="所属" min-width="140" />
        <el-table-column prop="businessDivision" label="事業部" min-width="120" />
        <el-table-column label="最寄駅" min-width="160">
          <template #default="{ row }">{{ formatNearestStations(row) }}</template>
        </el-table-column>
        <el-table-column label="連絡先" min-width="180">
          <template #default="{ row }">{{ formatContact(row.contactInfo) }}</template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="dialog-form">
        <el-form-item label="社員ID" prop="employeeId">
          <el-input v-model="form.employeeId" :disabled="!!form._editId" maxlength="20" />
        </el-form-item>
        <el-form-item label="氏名" prop="name">
          <el-input v-model="form.name" maxlength="100" />
        </el-form-item>
        <el-form-item label="性別" prop="gender">
          <el-select v-model="form.gender" style="width: 100%">
            <el-option
              v-for="item in genderOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入居者区分" prop="employeeCategory">
          <el-select v-model="form.employeeCategory" style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属" prop="affiliationId">
          <el-select v-model="form.affiliationId" clearable filterable style="width: 100%">
            <el-option
              v-for="item in affiliationOptions"
              :key="item.affiliationId"
              :label="item.name"
              :value="item.affiliationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="事業部" prop="businessDivision">
          <el-input v-model="form.businessDivision" maxlength="30" />
        </el-form-item>
        <el-form-item label="最寄駅">
          <div class="nearest-station-row">
            <el-input v-model="form.nearestStation1" maxlength="20" placeholder="最寄駅１" />
            <el-input v-model="form.nearestStation2" maxlength="20" placeholder="最寄駅２" />
            <el-input v-model="form.nearestStation3" maxlength="20" placeholder="最寄駅３" />
          </div>
        </el-form-item>
        <el-form-item label="携帯電話" prop="mobilePhone">
          <el-input v-model="form.mobilePhone" maxlength="30" />
        </el-form-item>
        <el-form-item label="メール" prop="email">
          <el-input v-model="form.email" maxlength="100" />
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
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { getAffiliations } from '@/api/affiliation'
import { createEmployee, deleteEmployee, getEmployee, getEmployees, updateEmployee } from '@/api/employee'
import { EMPLOYEE_CATEGORY, EMPLOYEE_GENDER, labelOf } from '@/utils/constants'
import { parseContactInfo } from '@/utils/employee'
import { normalizePageResponse } from '@/utils/pagination'

const route = useRoute()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])
const affiliationOptions = ref([])

const genderOptions = Object.entries(EMPLOYEE_GENDER).map(([value, label]) => ({ value, label }))
const categoryOptions = Object.entries(EMPLOYEE_CATEGORY).map(([value, label]) => ({ value, label }))

const query = reactive({
  keyword: '',
  gender: '',
  employeeCategory: '',
  affiliationId: ''
})

const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  _editId: '',
  employeeId: '',
  name: '',
  gender: 'MALE',
  employeeCategory: 'JAPAN',
  affiliationId: '',
  businessDivision: '',
  nearestStation1: '',
  nearestStation2: '',
  nearestStation3: '',
  mobilePhone: '',
  email: ''
})

const rules = {
  employeeId: [{ required: true, message: '社員IDを入力してください', trigger: 'blur' }],
  name: [{ required: true, message: '氏名を入力してください', trigger: 'blur' }],
  gender: [{ required: true, message: '性別を選択してください', trigger: 'change' }],
  employeeCategory: [{ required: true, message: '入居者区分を選択してください', trigger: 'change' }]
}

const dialogTitle = computed(() => (form._editId ? '社員編集' : '社員新規登録'))

function formatContact(contactInfo) {
  const { mobilePhone, email } = parseContactInfo(contactInfo)
  return [mobilePhone, email].filter(Boolean).join(' / ')
}

function formatNearestStations(row) {
  return [row.nearestStation1, row.nearestStation2, row.nearestStation3].filter(Boolean).join(' / ')
}

async function fetchAffiliations() {
  const data = await getAffiliations({ page: 0, size: 500 })
  const { list } = normalizePageResponse(data)
  affiliationOptions.value = list
}

async function fetchList() {
  loading.value = true
  try {
    const data = await getEmployees({
      keyword: query.keyword || undefined,
      gender: query.gender || undefined,
      employeeCategory: query.employeeCategory || undefined,
      affiliationId: query.affiliationId || undefined,
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
  query.keyword = ''
  query.gender = ''
  query.employeeCategory = ''
  query.affiliationId = ''
  handleSearch()
}

async function openDialog(row) {
  if (row?.employeeId) {
    const detail = await getEmployee(row.employeeId)
    const contact = parseContactInfo(detail.contactInfo)
    Object.assign(form, {
      _editId: detail.employeeId,
      employeeId: detail.employeeId,
      name: detail.name || '',
      gender: detail.gender || 'MALE',
      employeeCategory: detail.employeeCategory || 'JAPAN',
      affiliationId: detail.affiliationId || '',
      businessDivision: detail.businessDivision || '',
      nearestStation1: detail.nearestStation1 || '',
      nearestStation2: detail.nearestStation2 || '',
      nearestStation3: detail.nearestStation3 || '',
      mobilePhone: contact.mobilePhone,
      email: contact.email
    })
  } else {
    Object.assign(form, {
      _editId: '',
      employeeId: '',
      name: '',
      gender: 'MALE',
      employeeCategory: 'JAPAN',
      affiliationId: '',
      businessDivision: '',
      nearestStation1: '',
      nearestStation2: '',
      nearestStation3: '',
      mobilePhone: '',
      email: ''
    })
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload = {
      employeeId: form.employeeId,
      name: form.name,
      gender: form.gender,
      employeeCategory: form.employeeCategory,
      affiliationId: form.affiliationId || null,
      businessDivision: form.businessDivision || null,
      nearestStation1: form.nearestStation1 || null,
      nearestStation2: form.nearestStation2 || null,
      nearestStation3: form.nearestStation3 || null,
      mobilePhone: form.mobilePhone || null,
      email: form.email || null
    }
    if (form._editId) {
      await updateEmployee(form._editId, payload)
      ElMessage.success('社員を更新しました')
    } else {
      await createEmployee(payload)
      ElMessage.success('社員を登録しました')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`社員「${row.name}」を削除しますか？`, '確認', { type: 'warning' })
  await deleteEmployee(row.employeeId)
  ElMessage.success('削除しました')
  fetchList()
}

onMounted(async () => {
  await fetchAffiliations()
  if (route.query.employeeId) {
    query.keyword = String(route.query.employeeId)
  }
  await fetchList()
})
</script>
