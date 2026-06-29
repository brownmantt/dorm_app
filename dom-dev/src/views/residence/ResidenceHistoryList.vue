<template>
  <div class="page-container">
    <PageHeader title="入居履歴一覧" subtitle="Residence History" />

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="入居者氏名">
          <el-input v-model="query.name" clearable placeholder="部分一致" />
        </el-form-item>
        <el-form-item label="社員ID">
          <el-input v-model="query.employeeId" clearable />
        </el-form-item>
        <el-form-item label="寮名称">
          <el-input v-model="query.dormitoryName" clearable placeholder="部分一致" />
        </el-form-item>
        <el-form-item label="入居日範囲">
          <div class="date-range-field">
            <el-date-picker
              v-model="query.moveInDateFrom"
              type="date"
              value-format="YYYY-MM-DD"
              class="iso-date-editor"
            />
            <span class="date-range-separator">～</span>
            <el-date-picker
              v-model="query.moveInDateTo"
              type="date"
              value-format="YYYY-MM-DD"
              class="iso-date-editor"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">検索</el-button>
          <el-button @click="handleReset">リセット</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table table-wrap-text" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="employeeId" label="社員ID" min-width="140" />
        <el-table-column prop="employeeName" label="入居者氏名" min-width="160" />
        <el-table-column prop="dormitoryName" label="寮名称" min-width="200" />
        <el-table-column prop="roomName" label="部屋" min-width="140" />
        <el-table-column label="利用形態" min-width="120">
          <template #default="{ row }">
            {{ row.usageTypeName || row.usageTypeCode || '-' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="moveInDate"
          label="入居日"
          width="120"
          class-name="col-iso-date"
          label-class-name="col-iso-date"
        />
        <el-table-column
          prop="moveOutDate"
          label="退居日"
          width="120"
          class-name="col-iso-date"
          label-class-name="col-iso-date"
        />
        <el-table-column prop="moveOutReason" label="退寮理由" min-width="320" />
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '@/components/PageHeader.vue'
import { getResidences } from '@/api/residence'
import { normalizePageResponse } from '@/utils/pagination'

const route = useRoute()
const loading = ref(false)
const tableData = ref([])

const query = reactive({
  name: '',
  employeeId: '',
  dormitoryName: '',
  moveInDateFrom: '',
  moveInDateTo: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

async function fetchList() {
  loading.value = true
  try {
    const data = await getResidences({
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
  query.name = ''
  query.employeeId = ''
  query.dormitoryName = ''
  query.moveInDateFrom = ''
  query.moveInDateTo = ''
  handleSearch()
}

onMounted(() => {
  if (route.query.name) {
    query.name = String(route.query.name)
  }
  if (route.query.employeeId) {
    query.employeeId = String(route.query.employeeId)
  }
  if (route.query.dormitoryName) {
    query.dormitoryName = String(route.query.dormitoryName)
  }
  fetchList()
})
</script>
