<template>
  <div class="page-container">
    <PageHeader title="空き室一覧" subtitle="Vacancy List" />

    <el-card class="search-card search-form-grid-card search-form-single-row-card vacancy-list-search-card">
      <el-form :model="query" label-width="72px" class="search-form-grid search-form-single-row">
        <div class="search-form-grid__cols">
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
          <el-form-item label="基準日" class="search-field-date">
            <el-date-picker
              v-model="query.asOfDate"
              type="date"
              value-format="YYYY-MM-DD"
              class="iso-date-editor"
            />
          </el-form-item>
          <el-form-item class="search-form-grid__actions">
            <el-button type="primary" @click="handleSearch">検索</el-button>
            <el-button @click="handleReset">リセット</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="dormitoryName" label="寮名称" min-width="200">
          <template #default="{ row }">
            <router-link
              v-if="row.dormitoryId"
              :to="{ name: 'DormitoryDetail', params: { id: row.dormitoryId } }"
              class="allocation-room-link"
            >
              {{ row.dormitoryName }}
            </router-link>
            <span v-else>{{ row.dormitoryName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="roomName" label="部屋名称" min-width="160">
          <template #default="{ row }">
            <router-link
              v-if="row.dormitoryId && row.roomId"
              :to="{ name: 'RoomEdit', params: { dormId: row.dormitoryId, roomId: row.roomId } }"
              class="allocation-room-link"
            >
              {{ row.roomName }}
            </router-link>
            <span v-else>{{ row.roomName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="ステータス" min-width="110">
          <template #default="{ row }">
            {{ labelOf(VACANCY_STATUS, row.status) }}
          </template>
        </el-table-column>
        <el-table-column prop="residentName" label="入居者" min-width="180">
          <template #default="{ row }">
            <div class="vacancy-resident-cell">
              <span class="vacancy-resident-name">{{ row.residentName }}</span>
              <el-button
                v-if="userStore.isAdmin && canRegister(row)"
                link
                type="primary"
                @click="goRegister(row)"
              >
                入居
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="expectedMoveOutDate" label="退寮予定日" min-width="160">
          <template #default="{ row }">
            <div class="vacancy-move-out-cell">
              <span class="vacancy-move-out-date">{{ row.expectedMoveOutDate }}</span>
              <el-button
                v-if="userStore.isAdmin && canCheckout(row)"
                link
                type="primary"
                @click="goCheckout(row)"
              >
                退居
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="assignable" label="入居可能" min-width="110">
          <template #default="{ row }">
            <el-tag :type="row.assignable ? 'success' : 'danger'">
              {{ row.assignable ? '○' : '×' }}
            </el-tag>
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
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import PageHeader from '@/components/PageHeader.vue'
import { getVacancies } from '@/api/vacancy'
import { normalizePageResponse } from '@/utils/pagination'
import { DORMITORY_GENDER_OPTIONS, VACANCY_STATUS, labelOf } from '@/utils/constants'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])

const query = reactive({
  genderType: undefined,
  asOfDate: ''
})

const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

async function fetchList() {
  loading.value = true
  try {
    const data = await getVacancies({
      gender: query.genderType,
      asOfDate: query.asOfDate,
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
  query.genderType = undefined
  query.asOfDate = ''
  handleSearch()
}

function canRegister(row) {
  return Boolean(row.assignable && row.dormitoryId && row.roomId)
}

function canCheckout(row) {
  return Boolean(row.residenceHistoryId)
}

function goRegister(row) {
  router.push({
    name: 'ResidenceRegister',
    query: {
      tab: 'register',
      dormitoryId: row.dormitoryId,
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

onMounted(fetchList)
</script>

<style scoped>
.vacancy-resident-cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.vacancy-resident-name {
  flex: 1;
  min-width: 0;
}

.vacancy-move-out-cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.vacancy-move-out-date {
  flex: 1;
  min-width: 0;
}
</style>
