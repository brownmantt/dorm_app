<template>
  <div class="page-container">
    <PageHeader title="操作ログ" subtitle="Operation Log" />

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="操作種別">
          <el-input v-model="query.operationType" clearable />
        </el-form-item>
        <el-form-item label="操作者">
          <el-input v-model="query.operatedBy" clearable />
        </el-form-item>
        <el-form-item label="操作日（開始）">
          <el-date-picker v-model="query.operatedAtFrom" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="操作日（終了）">
          <el-date-picker v-model="query.operatedAtTo" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">検索</el-button>
          <el-button @click="handleReset">リセット</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="logId" label="ログID" min-width="100" />
        <el-table-column prop="operationType" label="操作種別" min-width="200" />
        <el-table-column prop="targetTable" label="対象テーブル" min-width="180" />
        <el-table-column prop="targetId" label="対象ID" min-width="140" />
        <el-table-column prop="operatedBy" label="操作者" min-width="140" />
        <el-table-column prop="operatedAt" label="操作日時" min-width="180" />
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
import PageHeader from '@/components/PageHeader.vue'
import { getOperationLogs } from '@/api/operationLog'
import { normalizePageResponse } from '@/utils/pagination'

const loading = ref(false)
const tableData = ref([])

const query = reactive({
  operationType: '',
  operatedBy: '',
  operatedAtFrom: '',
  operatedAtTo: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

async function fetchList() {
  loading.value = true
  try {
    const data = await getOperationLogs({
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
  query.operationType = ''
  query.operatedBy = ''
  query.operatedAtFrom = ''
  query.operatedAtTo = ''
  handleSearch()
}

onMounted(fetchList)
</script>
