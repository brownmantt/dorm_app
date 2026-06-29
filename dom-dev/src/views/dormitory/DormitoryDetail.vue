<template>
  <div class="page-container">
    <PageHeader :title="dormitory.name || '寮詳細'" subtitle="Dormitory Detail" />

    <el-card v-loading="detailLoading" class="search-card">
      <el-descriptions :column="3" border class="detail-descriptions dormitory-detail-descriptions">
        <el-descriptions-item label="寮名称">
          <div class="table-cell-multiline">
            <div v-if="dormitory.dormitoryId">{{ dormitory.dormitoryId }}</div>
            <div>{{ dormitory.name || '-' }}</div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="種類">{{ labelOf(GENDER_TYPE, dormitory.genderType) }}</el-descriptions-item>
        <el-descriptions-item label="間取り">{{ dormitory.layoutType }}</el-descriptions-item>
        <el-descriptions-item label="地域">
          {{ regionLabelFromOptions(dormitory.region || inferRegionFromAddress(dormitory.address), regionOptions) }}
        </el-descriptions-item>
        <el-descriptions-item label="住所" class-name="dormitory-detail-address-cell">
          <div class="table-cell-multiline dormitory-detail-address">
            <div v-if="dormitory.postalCode">〒{{ formatPostalCode(dormitory.postalCode) }}</div>
            <div v-if="dormitory.address" class="dormitory-detail-address-line">{{ dormitory.address }}</div>
            <span v-if="!dormitory.postalCode && !dormitory.address">-</span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="最寄駅">
          <div class="table-cell-multiline">
            <div v-if="dormitory.nearestStation1">{{ dormitory.nearestStation1 }}</div>
            <div v-if="dormitory.nearestStation2">{{ dormitory.nearestStation2 }}</div>
            <div v-if="dormitory.nearestStation3">{{ dormitory.nearestStation3 }}</div>
            <span v-if="!dormitory.nearestStation1 && !dormitory.nearestStation2 && !dormitory.nearestStation3">-</span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="責任者">
          <span v-if="manager.employeeName">
            {{ manager.employeeName }} ★
            <span v-if="manager.autoAssigned" class="manager-auto-tag">（自動）</span>
          </span>
          <span v-else>-</span>
          <template v-if="userStore.isAdmin">
            <el-button link type="primary" style="margin-left: 8px" @click="openManagerDialog">設定</el-button>
          </template>
        </el-descriptions-item>
        <el-descriptions-item label="備考" :span="2">{{ dormitory.remarks || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="toolbar-card">
      <el-button @click="$router.push('/dormitories')">一覧へ戻る</el-button>
      <el-button v-if="userStore.isAdmin" @click="openEditDialog">編集</el-button>
      <el-button v-if="userStore.isAdmin" type="primary" @click="openRoomDialog()">部屋追加</el-button>
    </el-card>

    <el-card v-loading="roomLoading" class="table-card">
      <template #header>部屋一覧</template>
      <el-table :data="roomList" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="roomId" label="部屋ID" min-width="140" />
        <el-table-column prop="roomName" label="部屋名称" min-width="140" />
        <el-table-column prop="roomDetail" label="部屋詳細" min-width="120" />
        <el-table-column prop="hasAirConditioner" label="エアコン" min-width="80">
          <template #default="{ row }">
            {{ row.hasAirConditioner ? '有' : '無' }}
          </template>
        </el-table-column>
        <el-table-column prop="monthlyFee" label="寮費単価" min-width="100" />
        <el-table-column prop="areaSqm" label="面積(㎡)" min-width="100" />
        <el-table-column prop="capacity" label="定員" min-width="80" />
        <el-table-column prop="roomType" label="部屋種別" min-width="120">
          <template #default="{ row }">
            {{ labelOf(ROOM_TYPE, row.roomType) }}
          </template>
        </el-table-column>
        <el-table-column prop="vacancyStatus" label="空き状況" min-width="110">
          <template #default="{ row }">
            <el-tag :type="row.vacancyStatus === 'VACANT' ? 'success' : 'info'">
              {{ labelOf(VACANCY_STATUS, row.vacancyStatus) || row.vacancyStatus || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          v-if="userStore.isAdmin"
          label="操作"
          width="120"
          fixed="right"
          align="center"
          header-align="center"
          class-name="col-actions"
        >
          <template #default="{ row }">
            <div class="table-actions-inline">
              <el-button link type="primary" @click="goRoomEdit(row)">編集</el-button>
              <el-button link type="danger" @click="handleDeleteRoom(row)">削除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <DormitoryFormDialog v-model="editDialogVisible" :edit-data="dormitory" @saved="fetchDetail" />

    <el-dialog v-model="roomDialogVisible" title="部屋新規登録" width="520px" destroy-on-close>
      <el-form ref="roomFormRef" :model="roomForm" :rules="roomRules" label-width="100px" class="dialog-form">
        <el-form-item label="部屋名称" prop="roomName">
          <el-input v-model="roomForm.roomName" />
        </el-form-item>
        <el-form-item label="面積(㎡)" prop="areaSqm">
          <el-input-number v-model="roomForm.areaSqm" :min="0.01" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="定員" prop="capacity">
          <el-input-number v-model="roomForm.capacity" :min="1" :step="1" />
        </el-form-item>
        <el-form-item label="部屋詳細" prop="roomDetail">
          <el-input v-model="roomForm.roomDetail" placeholder="洋室、手前洋室 等" />
        </el-form-item>
        <el-form-item label="エアコン" prop="hasAirConditioner">
          <el-switch v-model="roomForm.hasAirConditioner" active-text="有" inactive-text="無" />
        </el-form-item>
        <el-form-item label="寮費単価" prop="monthlyFee">
          <el-input-number v-model="roomForm.monthlyFee" :min="0" :step="100" />
        </el-form-item>
        <el-form-item label="部屋種別" prop="roomType">
          <el-select v-model="roomForm.roomType" placeholder="選択">
            <el-option label="標準洋室" value="STANDARD" />
            <el-option label="小部屋" value="SMALL" />
            <el-option label="その他" value="OTHER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roomDialogVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="roomSubmitLoading" @click="handleRoomSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="managerDialogVisible" title="責任者設定" width="480px" destroy-on-close>
      <el-form label-width="120px" class="dialog-form">
        <el-form-item label="責任者">
          <el-select v-model="managerForm.residenceHistoryId" clearable placeholder="現入居者から選択">
            <el-option
              v-for="item in currentResidents"
              :key="item.residenceHistoryId"
              :label="`${item.employeeName}（${item.roomName}）`"
              :value="item.residenceHistoryId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="managerDialogVisible = false">キャンセル</el-button>
        <el-button v-if="manager.employeeId" type="danger" @click="handleRemoveManager">解除</el-button>
        <el-button type="primary" :loading="managerSubmitLoading" @click="handleSaveManager">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import DormitoryFormDialog from '@/components/DormitoryFormDialog.vue'
import {
  createRoom,
  deleteRoom,
  getDormitory,
  getDormitoryManager,
  getRoomsByDormitory,
  removeDormitoryManager,
  setDormitoryManager
} from '@/api/dormitory'
import { getResidences } from '@/api/residence'
import { normalizePageResponse } from '@/utils/pagination'
import { formatPostalCode } from '@/utils/postalCode'
import { GENDER_TYPE, ROOM_TYPE, VACANCY_STATUS, labelOf } from '@/utils/constants'
import { inferRegionFromAddress } from '@/utils/dormAllocation'
import { loadRegionOptions, regionLabelFromOptions } from '@/utils/region'
import { todayDateString } from '@/utils/date'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const detailLoading = ref(false)
const roomLoading = ref(false)
const roomSubmitLoading = ref(false)
const managerSubmitLoading = ref(false)
const editDialogVisible = ref(false)
const roomDialogVisible = ref(false)
const managerDialogVisible = ref(false)
const roomFormRef = ref()
const regionOptions = ref([])

const dormitory = reactive({
  dormitoryId: '',
  name: '',
  postalCode: '',
  address: '',
  layoutType: '',
  genderType: '',
  region: '',
  nearestStation1: '',
  nearestStation2: '',
  nearestStation3: '',
  remarks: ''
})

const manager = reactive({
  employeeId: '',
  employeeName: '',
  residenceHistoryId: '',
  autoAssigned: false
})

const managerForm = reactive({
  residenceHistoryId: ''
})

const currentResidents = ref([])
const roomList = ref([])

const roomForm = reactive({
  roomId: '',
  roomName: '',
  roomDetail: '',
  areaSqm: 15,
  capacity: 1,
  roomType: 'STANDARD',
  hasAirConditioner: false,
  monthlyFee: null
})

const roomRules = {
  roomName: [{ required: true, message: '部屋名称を入力してください', trigger: 'blur' }],
  areaSqm: [{ required: true, message: '面積を入力してください', trigger: 'blur' }],
  capacity: [{ required: true, message: '定員を入力してください', trigger: 'blur' }],
  roomType: [{ required: true, message: '部屋種別を選択してください', trigger: 'change' }]
}

function openEditDialog() {
  editDialogVisible.value = true
}

function goRoomEdit(row) {
  router.push({
    name: 'RoomEdit',
    params: { dormId: dormitory.dormitoryId, roomId: row.roomId }
  })
}

async function fetchDetail() {
  detailLoading.value = true
  try {
    const data = await getDormitory(route.params.id)
    Object.assign(dormitory, data)
  } finally {
    detailLoading.value = false
  }
}

async function fetchRooms() {
  roomLoading.value = true
  try {
    const data = await getRoomsByDormitory(route.params.id, { page: 0, size: 1000 })
    const { list } = normalizePageResponse(data)
    roomList.value = list
  } finally {
    roomLoading.value = false
  }
}

async function fetchManager() {
  try {
    const data = await getDormitoryManager(route.params.id)
    Object.assign(manager, data || { employeeId: '', employeeName: '', residenceHistoryId: '', autoAssigned: false })
  } catch {
    Object.assign(manager, { employeeId: '', employeeName: '', residenceHistoryId: '', autoAssigned: false })
  }
}

async function fetchCurrentResidents() {
  const data = await getResidences({
    dormitoryId: route.params.id,
    page: 0,
    size: 500
  })
  const { list } = normalizePageResponse(data)
  const today = todayDateString()
  currentResidents.value = list.filter(
    (item) => !item.moveOutDate || item.moveOutDate >= today
  )
}

function openManagerDialog() {
  managerForm.residenceHistoryId = manager.residenceHistoryId || ''
  managerDialogVisible.value = true
}

async function handleSaveManager() {
  if (!managerForm.residenceHistoryId) {
    ElMessage.warning('責任者を選択してください')
    return
  }
  const resident = currentResidents.value.find(
    (item) => item.residenceHistoryId === managerForm.residenceHistoryId
  )
  managerSubmitLoading.value = true
  try {
    await setDormitoryManager(route.params.id, {
      employeeId: resident?.employeeId,
      residenceHistoryId: managerForm.residenceHistoryId
    })
    ElMessage.success('責任者を設定しました')
    managerDialogVisible.value = false
    await fetchManager()
  } finally {
    managerSubmitLoading.value = false
  }
}

async function handleRemoveManager() {
  managerSubmitLoading.value = true
  try {
    await removeDormitoryManager(route.params.id)
    ElMessage.success('責任者を解除しました')
    managerDialogVisible.value = false
    await fetchManager()
  } finally {
    managerSubmitLoading.value = false
  }
}

function resetRoomForm() {
  Object.assign(roomForm, {
    roomId: '',
    roomName: '',
    roomDetail: '',
    areaSqm: 15,
    capacity: 1,
    roomType: 'STANDARD',
    hasAirConditioner: false,
    monthlyFee: null
  })
}

function openRoomDialog() {
  resetRoomForm()
  roomDialogVisible.value = true
}

async function handleRoomSubmit() {
  await roomFormRef.value.validate()
  roomSubmitLoading.value = true
  try {
    const payload = {
      dormitoryId: dormitory.dormitoryId,
      roomName: roomForm.roomName,
      roomDetail: roomForm.roomDetail || null,
      areaSqm: roomForm.areaSqm,
      capacity: roomForm.capacity,
      roomType: roomForm.roomType,
      hasAirConditioner: roomForm.hasAirConditioner,
      monthlyFee: roomForm.monthlyFee
    }
    await createRoom(payload)
    ElMessage.success('部屋を登録しました')
    resetRoomForm()
    roomFormRef.value?.resetFields()
    roomDialogVisible.value = false
    fetchRooms()
  } finally {
    roomSubmitLoading.value = false
  }
}

async function handleDeleteRoom(row) {
  await ElMessageBox.confirm(`部屋「${row.roomName}」を削除しますか？`, '確認', { type: 'warning' })
  await deleteRoom(row.roomId)
  ElMessage.success('削除しました')
  fetchRooms()
}

onMounted(async () => {
  regionOptions.value = await loadRegionOptions()
  await fetchDetail()
  await Promise.all([fetchRooms(), fetchManager(), fetchCurrentResidents()])
})
</script>
