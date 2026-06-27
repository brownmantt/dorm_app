<template>
  <div class="page-container">
    <PageHeader title="初回利用日・長期利用" subtitle="First Use & Long Term" />

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="社員ID">
          <el-input v-model="query.employeeId" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearchEmployee">照会</el-button>
        </el-form-item>
      </el-form>
      <el-descriptions v-if="employeeInfo.employeeId" :column="2" border class="detail-descriptions employee-info">
        <el-descriptions-item label="社員ID">{{ employeeInfo.employeeId }}</el-descriptions-item>
        <el-descriptions-item label="初回利用日">{{ employeeInfo.firstUseDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="通算利用日数">{{ employeeInfo.totalUsageDays ?? '-' }} 日</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-loading="alertLoading" class="table-card">
      <template #header>長期利用警告一覧</template>
      <el-table :data="alertList" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="employeeId" label="社員ID" min-width="140" />
        <el-table-column prop="employeeName" label="氏名" min-width="160" />
        <el-table-column prop="firstUseDate" label="初回利用日" min-width="130" />
        <el-table-column prop="elapsedYears" label="経過年数" min-width="110" />
        <el-table-column prop="dormitoryName" label="現在の寮" min-width="200" />
        <el-table-column prop="roomName" label="部屋" min-width="140" />
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchAlerts"
          @current-change="fetchAlerts"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { getFirstUseDate, getLongTermUsageAlerts, getTotalUsageDays } from '@/api/residence'
import { normalizePageResponse } from '@/utils/pagination'
import { formatDate } from '@/utils/date'

const alertLoading = ref(false)
const alertList = ref([])

const query = reactive({
  employeeId: ''
})

const employeeInfo = reactive({
  employeeId: '',
  firstUseDate: '',
  totalUsageDays: null
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

async function handleSearchEmployee() {
  if (!query.employeeId) return
  employeeInfo.employeeId = query.employeeId
  employeeInfo.firstUseDate = ''
  employeeInfo.totalUsageDays = null
  try {
    const [firstUse, totalDays] = await Promise.all([
      getFirstUseDate(query.employeeId),
      getTotalUsageDays(query.employeeId)
    ])
    employeeInfo.firstUseDate = firstUse?.firstUseDate ? formatDate(firstUse.firstUseDate) : ''
    employeeInfo.totalUsageDays = totalDays?.totalUsageDays ?? totalDays?.days ?? null
  } catch {
    /* API応答に依存 */
  }
}

async function fetchAlerts() {
  alertLoading.value = true
  try {
    const data = await getLongTermUsageAlerts({
      page: pagination.page - 1,
      size: pagination.size
    })
    const { list, total } = normalizePageResponse(data)
    alertList.value = list
    pagination.total = total
  } finally {
    alertLoading.value = false
  }
}

onMounted(fetchAlerts)
</script>

<style scoped>
.employee-info {
  margin-top: 16px;
}
</style>
