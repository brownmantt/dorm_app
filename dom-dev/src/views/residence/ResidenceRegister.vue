<template>
  <div class="page-container form-page">
    <PageHeader title="入居登録・退居" subtitle="Residence Register" />

    <el-tabs v-model="activeTab">
      <el-tab-pane label="入居登録" name="register">
        <el-card>
          <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="120px">
            <el-form-item label="入居者" prop="employeeId">
              <el-select
                v-model="registerForm.employeeId"
                filterable
                clearable
                placeholder="選択"
                :loading="employeeOptionsLoading"
                @change="loadEmployeeInfo"
              >
                <el-option
                  v-for="item in employeeOptions"
                  :key="item.employeeId"
                  :label="formatEmployeeOptionLabel(item)"
                  :value="item.employeeId"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="入居者区分">
              <el-input v-model="employeeCategoryLabel" disabled />
            </el-form-item>
            <el-form-item label="初回利用日">
              <el-input v-model="firstUseDateDisplay" disabled />
            </el-form-item>
            <el-form-item label="寮" prop="dormitoryId">
              <el-select
                v-model="registerForm.dormitoryId"
                filterable
                placeholder="選択"
                :disabled="!registerForm.employeeId || isCalendarPreset"
                :loading="dormitoryOptionsLoading"
                @change="handleDormitoryChange"
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
                v-model="registerForm.roomId"
                filterable
                placeholder="選択"
                :disabled="!registerForm.dormitoryId || isCalendarPreset"
              >
                <el-option
                  v-for="item in filteredAssignableRooms"
                  :key="item.roomId"
                  :label="item.roomName"
                  :value="item.roomId"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="入居日" prop="moveInDate">
              <el-date-picker
                v-model="registerForm.moveInDate"
                type="date"
                value-format="YYYY-MM-DD"
                class="iso-date-editor"
              />
            </el-form-item>
            <el-form-item>
              <el-button @click="handleValidate">業務検証</el-button>
              <el-button type="primary" :loading="registerLoading" @click="handleRegister">登録</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="退居処理" name="checkout">
        <el-card>
          <el-alert
            v-if="checkoutPrefillInfo"
            class="checkout-prefill-alert"
            :title="checkoutPrefillInfo"
            type="info"
            show-icon
            :closable="false"
          />
          <el-form ref="checkoutFormRef" :model="checkoutForm" :rules="checkoutRules" label-width="120px">
            <el-form-item label="入居履歴ID" prop="residenceHistoryId">
              <el-input v-model="checkoutForm.residenceHistoryId" />
            </el-form-item>
            <el-form-item label="退居日" prop="moveOutDate">
              <el-date-picker
                v-model="checkoutForm.moveOutDate"
                type="date"
                value-format="YYYY-MM-DD"
                class="iso-date-editor"
              />
            </el-form-item>
            <el-form-item label="退寮理由" prop="moveOutReason">
              <el-input v-model="checkoutForm.moveOutReason" type="textarea" rows="3" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="checkoutLoading" @click="handleCheckout">退寮</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { getRegisterableEmployees } from '@/api/employee'
import { getDormitories, getDormitory, getRoomsByDormitory } from '@/api/dormitory'
import {
  checkoutResidence,
  createResidence,
  getFirstUseDate,
  getResidences,
  validateResidence
} from '@/api/residence'
import { getAssignableRooms } from '@/api/vacancy'
import { normalizePageResponse } from '@/utils/pagination'
import { EMPLOYEE_CATEGORY, labelOf } from '@/utils/constants'
import { formatDate, todayDateString } from '@/utils/date'

const route = useRoute()

const activeTab = ref('register')
const checkoutPrefillInfo = ref('')
const registerFormRef = ref()
const checkoutFormRef = ref()
const registerLoading = ref(false)
const checkoutLoading = ref(false)
const employeeOptionsLoading = ref(false)
const dormitoryOptionsLoading = ref(false)

const dormitoryOptions = ref([])
const employeeOptions = ref([])
const assignableRooms = ref([])
/** 寮割カレンダー「入」から遷移した場合、寮・部屋を固定する */
const calendarPreset = ref(null)
const isCalendarPreset = computed(() => calendarPreset.value !== null)
const employeeCategory = ref('')
const firstUseDate = ref('')

const registerForm = reactive({
  employeeId: '',
  dormitoryId: '',
  roomId: '',
  moveInDate: ''
})

const checkoutForm = reactive({
  residenceHistoryId: '',
  moveOutDate: '',
  moveOutReason: ''
})

const registerRules = {
  employeeId: [{ required: true, message: '入居者を選択してください', trigger: 'change' }],
  dormitoryId: [{ required: true, message: '寮を選択してください', trigger: 'change' }],
  roomId: [{ required: true, message: '部屋を選択してください', trigger: 'change' }],
  moveInDate: [{ required: true, message: '入居日を選択してください', trigger: 'change' }]
}

const checkoutRules = {
  residenceHistoryId: [{ required: true, message: '入居履歴IDを入力してください', trigger: 'blur' }],
  moveOutDate: [{ required: true, message: '退居日を選択してください', trigger: 'change' }]
}

const employeeCategoryLabel = computed(() => labelOf(EMPLOYEE_CATEGORY, employeeCategory.value) || '-')
const firstUseDateDisplay = computed(() => firstUseDate.value || '-')

const filteredAssignableRooms = computed(() => {
  if (!registerForm.dormitoryId) return []
  return assignableRooms.value.filter(
    (room) => room.dormitoryId === registerForm.dormitoryId
  )
})

function ensureDefaultMoveInDate() {
  if (!registerForm.moveInDate) {
    registerForm.moveInDate = todayDateString()
  }
}

function buildRegisterPayload() {
  ensureDefaultMoveInDate()
  return {
    employeeId: registerForm.employeeId,
    dormitoryId: registerForm.dormitoryId,
    roomId: registerForm.roomId,
    moveInDate: registerForm.moveInDate
  }
}

function clearDormitorySelection() {
  dormitoryOptions.value = []
  assignableRooms.value = []
  registerForm.dormitoryId = ''
  registerForm.roomId = ''
}

function resolveEmployeeGender(employeeId) {
  const employee = employeeOptions.value.find((item) => item.employeeId === employeeId)
  return employee?.gender || ''
}

async function refreshDormitoryOptions() {
  if (isCalendarPreset.value) return

  dormitoryOptionsLoading.value = true
  try {
    ensureDefaultMoveInDate()
    const gender = resolveEmployeeGender(registerForm.employeeId)
    if (!gender) {
      clearDormitorySelection()
      return
    }

    const assignableData = await getAssignableRooms({
      employeeId: registerForm.employeeId,
      asOfDate: registerForm.moveInDate || undefined,
      page: 0,
      size: 5000
    })
    const { list: rooms } = normalizePageResponse(assignableData)
    assignableRooms.value = rooms

    const dormitoryIdsWithVacancy = new Set(
      rooms.map((room) => room.dormitoryId).filter(Boolean)
    )
    if (!dormitoryIdsWithVacancy.size) {
      dormitoryOptions.value = []
      registerForm.dormitoryId = ''
      registerForm.roomId = ''
      return
    }

    const dormData = await getDormitories({
      genderType: gender,
      page: 0,
      size: 1000
    })
    const { list: dorms } = normalizePageResponse(dormData)
    dormitoryOptions.value = dorms
      .filter((dorm) => dormitoryIdsWithVacancy.has(dorm.dormitoryId))
      .sort((a, b) => (a.name || '').localeCompare(b.name || '', 'ja'))

    if (
      registerForm.dormitoryId
      && !dormitoryOptions.value.some((dorm) => dorm.dormitoryId === registerForm.dormitoryId)
    ) {
      registerForm.dormitoryId = ''
      registerForm.roomId = ''
    } else if (
      registerForm.roomId
      && !filteredAssignableRooms.value.some((room) => room.roomId === registerForm.roomId)
    ) {
      registerForm.roomId = ''
    }
  } finally {
    dormitoryOptionsLoading.value = false
  }
}

function formatEmployeeOptionLabel(item) {
  if (!item) return ''
  const name = item.name || item.employeeName || ''
  return name ? `${item.employeeId} - ${name}` : item.employeeId
}

async function fetchEmployeeOptions() {
  employeeOptionsLoading.value = true
  try {
    const data = await getRegisterableEmployees({ page: 0, size: 5000 })
    const { list } = normalizePageResponse(data)
    employeeOptions.value = list
  } finally {
    employeeOptionsLoading.value = false
  }
}

function ensureEmployeeInOptions(employeeId, name) {
  if (!employeeId) return
  const existing = employeeOptions.value.find((item) => item.employeeId === employeeId)
  if (existing) {
    if (name) existing.name = name
    return
  }
  employeeOptions.value.push({ employeeId, name: name || employeeId })
}

async function loadEmployeeInfo() {
  employeeCategory.value = ''
  firstUseDate.value = ''
  if (!registerForm.employeeId) {
    if (!isCalendarPreset.value) {
      clearDormitorySelection()
    }
    return
  }
  try {
    const data = await getFirstUseDate(registerForm.employeeId)
    firstUseDate.value = data.firstUseDate ? formatDate(data.firstUseDate) : ''
    employeeCategory.value = data.employeeCategory || ''
    if (data.employeeName) {
      ensureEmployeeInOptions(registerForm.employeeId, data.employeeName)
    }
  } catch {
    ensureEmployeeInOptions(registerForm.employeeId, registerForm.employeeId)
  }
}

async function loadPresetRooms(dormitoryId, roomId) {
  const data = await getRoomsByDormitory(dormitoryId, { page: 0, size: 500 })
  const { list } = normalizePageResponse(data)
  assignableRooms.value = list.map((room) => ({ ...room, dormitoryId }))
  if (roomId && !assignableRooms.value.some((room) => room.roomId === roomId)) {
    assignableRooms.value.push({ roomId, roomName: roomId, dormitoryId })
  }
}

function handleDormitoryChange() {
  registerForm.roomId = ''
}

async function runValidate() {
  const result = await validateResidence(buildRegisterPayload())
  if (result?.valid === false) {
    ElMessage.warning(result.message || '業務検証で問題が見つかりました')
    return false
  }
  return true
}

async function handleValidate() {
  ensureDefaultMoveInDate()
  await registerFormRef.value.validate()
  const ok = await runValidate()
  if (ok) {
    ElMessage.success('業務検証に合格しました')
  }
}

async function handleRegister() {
  ensureDefaultMoveInDate()
  await registerFormRef.value.validate()
  registerLoading.value = true
  try {
    const ok = await runValidate()
    if (!ok) return

    const result = await createResidence({
      ...buildRegisterPayload(),
      moveOutDate: null,
      moveOutReason: null
    })
    if (result?.firstUseDate) {
      firstUseDate.value = formatDate(result.firstUseDate)
    }
    ElMessage.success('入居を登録しました')
  } finally {
    registerLoading.value = false
  }
}

async function handleCheckout() {
  await checkoutFormRef.value.validate()
  checkoutLoading.value = true
  try {
    await checkoutResidence(checkoutForm.residenceHistoryId, {
      moveOutDate: checkoutForm.moveOutDate,
      moveOutReason: checkoutForm.moveOutReason || null
    })
    ElMessage.success('退居処理を完了しました')
    checkoutForm.residenceHistoryId = ''
    checkoutForm.moveOutDate = ''
    checkoutForm.moveOutReason = ''
  } finally {
    checkoutLoading.value = false
  }
}

watch(
  () => [registerForm.employeeId, registerForm.moveInDate],
  async () => {
    if (isCalendarPreset.value || !registerForm.employeeId) return
    await refreshDormitoryOptions()
  }
)

async function applyRouteQuery() {
  const historyId = route.query.residenceHistoryId
  const dormitoryId = route.query.dormitoryId
  const roomId = route.query.roomId
  const tab = route.query.tab

  if (tab === 'register' || dormitoryId || roomId) {
    activeTab.value = 'register'
    if (dormitoryId && roomId) {
      calendarPreset.value = {
        dormitoryId: String(dormitoryId),
        roomId: String(roomId)
      }
    }
    if (dormitoryId) {
      registerForm.dormitoryId = String(dormitoryId)
      try {
        const dorm = await getDormitory(String(dormitoryId))
        dormitoryOptions.value = [{
          dormitoryId: dorm.dormitoryId || String(dormitoryId),
          name: dorm.name || String(dormitoryId)
        }]
      } catch {
        dormitoryOptions.value = [{
          dormitoryId: String(dormitoryId),
          name: String(dormitoryId)
        }]
      }
      await loadPresetRooms(String(dormitoryId), roomId ? String(roomId) : '')
    }
    if (roomId) registerForm.roomId = String(roomId)
    return
  }

  if (!historyId) return

  activeTab.value = tab === 'register' ? 'register' : 'checkout'
  checkoutForm.residenceHistoryId = String(historyId)

  try {
    const data = await getResidences({ page: 0, size: 5000 })
    const { list } = normalizePageResponse(data)
    const record = list.find((item) => item.residenceHistoryId === String(historyId))
    if (record) {
      checkoutPrefillInfo.value = `対象: ${record.employeeName || record.employeeId} / ${record.dormitoryName || '-'} / ${record.roomName || '-'}（入居日: ${record.moveInDate || '-'}）`
    } else {
      checkoutPrefillInfo.value = `寮割カレンダーから遷移しました（履歴ID: ${historyId}）`
    }
  } catch {
    checkoutPrefillInfo.value = `寮割カレンダーから遷移しました（履歴ID: ${historyId}）`
  }
}

onMounted(async () => {
  await fetchEmployeeOptions()
  await applyRouteQuery()
  ensureDefaultMoveInDate()
})
</script>

<style scoped>
.checkout-prefill-alert {
  margin-bottom: 16px;
}
</style>
