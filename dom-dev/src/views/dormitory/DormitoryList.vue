<template>
  <div class="page-container">
    <PageHeader title="寮一覧" subtitle="Dormitory List" />

    <el-card class="search-card search-form-grid-card search-form-single-row-card dormitory-list-search-card">
      <el-form :model="query" label-width="72px" class="search-form-grid search-form-single-row">
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
          <el-form-item label="地域" class="search-field-region">
            <el-select v-model="query.region" clearable placeholder="すべて">
              <el-option
                v-for="item in regionOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
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
          <el-form-item label="住所" class="search-field-address">
            <el-input v-model="query.address" clearable placeholder="住所" @keyup.enter="handleSearch" />
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
        <div class="table-card-toolbar__actions">
          <el-button v-if="userStore.isAdmin" type="primary" @click="openDialog()">新規登録</el-button>
          <el-button
            v-if="userStore.isAdmin"
            type="danger"
            :disabled="!selectedRows.length"
            @click="handleBatchDelete"
          >
            一括削除
          </el-button>
        </div>
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
      <el-table
        :data="tableData"
        class="data-table table-wrap-text"
        border
        stripe
        empty-text="データがありません"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="50"
          align="center"
          header-align="center"
          class-name="col-selection"
        />
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="name" label="寮名称" min-width="140" />
        <el-table-column prop="genderType" label="種別" min-width="100">
          <template #default="{ row }">
            {{ labelOf(GENDER_TYPE, row.genderType) }}
          </template>
        </el-table-column>
        <el-table-column prop="layoutType" label="間取り" min-width="90" />
        <el-table-column prop="region" label="地域" min-width="90">
          <template #default="{ row }">
            {{ regionLabelFromOptions(row.region || inferRegionFromAddress(row.address), regionOptions) }}
          </template>
        </el-table-column>
        <el-table-column label="住所" min-width="280">
          <template #default="{ row }">
            <div class="table-cell-multiline">
              <div v-if="row.postalCode">〒{{ formatPostalCode(row.postalCode) }}</div>
              <div v-if="row.address">{{ row.address }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="最寄駅" min-width="120">
          <template #default="{ row }">
            <div class="table-cell-multiline">
              <div v-if="row.nearestStation1">{{ row.nearestStation1 }}</div>
              <div v-if="row.nearestStation2">{{ row.nearestStation2 }}</div>
              <div v-if="row.nearestStation3">{{ row.nearestStation3 }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="168"
          fixed="right"
          align="center"
          header-align="center"
          class-name="col-actions"
        >
          <template #default="{ row }">
            <div class="table-actions-inline">
              <el-button link type="primary" @click="goDetail(row.dormitoryId)">詳細</el-button>
              <el-button v-if="userStore.isAdmin" link type="primary" @click="openDialog(row)">編集</el-button>
              <el-button v-if="userStore.isAdmin" link type="danger" @click="handleDelete(row)">削除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <DormitoryFormDialog v-model="dialogVisible" :edit-data="editingDormitory" @saved="fetchList" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import DormitoryFormDialog from '@/components/DormitoryFormDialog.vue'
import { deleteDormitory, getDormitories, getDormitoryNameOptions } from '@/api/dormitory'
import { normalizePageResponse } from '@/utils/pagination'
import { formatPostalCode } from '@/utils/postalCode'
import { DORMITORY_GENDER_OPTIONS, GENDER_TYPE, labelOf } from '@/utils/constants'
import { inferRegionFromAddress } from '@/utils/dormAllocation'
import { loadRegionOptions, regionLabelFromOptions } from '@/utils/region'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const dialogVisible = ref(false)
const editingDormitory = ref(null)
const tableData = ref([])
const selectedRows = ref([])
const dormitoryOptions = ref([])
const regionOptions = ref([])

const query = reactive({
  dormitoryId: undefined,
  region: undefined,
  genderType: undefined,
  address: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

async function fetchList() {
  loading.value = true
  try {
    const data = await getDormitories({
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
  query.dormitoryId = undefined
  query.region = undefined
  query.genderType = undefined
  query.address = ''
  handleSearch()
}

async function loadDormitoryOptions() {
  dormitoryOptions.value = await getDormitoryNameOptions()
}

function handleSelectionChange(rows) {
  selectedRows.value = rows
}

function goDetail(id) {
  router.push(`/dormitories/${id}`)
}

function openDialog(row) {
  editingDormitory.value = row || null
  dialogVisible.value = true
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`寮「${row.name}」を削除しますか？`, '確認', { type: 'warning' })
  await deleteDormitory(row.dormitoryId)
  ElMessage.success('削除しました')
  fetchList()
}

async function handleBatchDelete() {
  await ElMessageBox.confirm(`${selectedRows.value.length}件を削除しますか？`, '確認', { type: 'warning' })
  await Promise.all(selectedRows.value.map((row) => deleteDormitory(row.dormitoryId)))
  ElMessage.success('削除しました')
  fetchList()
}

onMounted(async () => {
  regionOptions.value = await loadRegionOptions()
  await loadDormitoryOptions()
  fetchList()
})
</script>
