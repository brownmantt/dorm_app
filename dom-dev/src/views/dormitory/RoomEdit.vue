<template>
  <div class="page-container form-page">
    <PageHeader title="部屋編集" subtitle="Room Edit" />

    <el-card v-loading="loading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="寮ID">
          <el-input v-model="form.dormitoryId" disabled />
        </el-form-item>
        <el-form-item label="部屋ID">
          <el-input v-model="form.roomId" disabled />
        </el-form-item>
        <el-form-item label="部屋名称" prop="roomName">
          <el-input v-model="form.roomName" />
        </el-form-item>
        <el-form-item label="部屋詳細" prop="roomDetail">
          <el-input v-model="form.roomDetail" placeholder="洋室、手前洋室 等" />
        </el-form-item>
        <el-form-item label="エアコン" prop="hasAirConditioner">
          <el-switch v-model="form.hasAirConditioner" active-text="有" inactive-text="無" />
        </el-form-item>
        <el-form-item label="寮費単価" prop="monthlyFee">
          <el-input-number v-model="form.monthlyFee" :min="0" :step="100" />
        </el-form-item>
        <el-form-item label="面積(㎡)" prop="areaSqm">
          <el-input-number v-model="form.areaSqm" :min="0.01" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="定員" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :step="1" />
        </el-form-item>
        <el-form-item label="部屋種別" prop="roomType">
          <el-select v-model="form.roomType" placeholder="選択">
            <el-option label="標準洋室" value="STANDARD" />
            <el-option label="小部屋" value="SMALL" />
            <el-option label="その他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="goBack">一覧へ戻る</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { getRoomsByDormitory, updateRoom } from '@/api/dormitory'
import { normalizePageResponse } from '@/utils/pagination'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitLoading = ref(false)
const formRef = ref()

const form = reactive({
  dormitoryId: '',
  roomId: '',
  roomName: '',
  roomDetail: '',
  areaSqm: 1,
  capacity: 1,
  roomType: 'STANDARD',
  hasAirConditioner: false,
  monthlyFee: null
})

const rules = {
  roomName: [{ required: true, message: '部屋名称を入力してください', trigger: 'blur' }],
  areaSqm: [{ required: true, message: '面積を入力してください', trigger: 'blur' }],
  capacity: [{ required: true, message: '定員を入力してください', trigger: 'blur' }],
  roomType: [{ required: true, message: '部屋種別を選択してください', trigger: 'change' }]
}

function goBack() {
  router.push(`/dormitories/${route.params.dormId}`)
}

async function fetchRoom() {
  loading.value = true
  try {
    const data = await getRoomsByDormitory(route.params.dormId, { page: 0, size: 1000 })
    const { list } = normalizePageResponse(data)
    const room = list.find((item) => item.roomId === route.params.roomId)
    if (room) {
      Object.assign(form, {
        dormitoryId: route.params.dormId,
        roomId: room.roomId,
        roomName: room.roomName,
        roomDetail: room.roomDetail || '',
        areaSqm: room.areaSqm,
        capacity: room.capacity,
        roomType: room.roomType,
        hasAirConditioner: !!room.hasAirConditioner,
        monthlyFee: room.monthlyFee ?? null
      })
    }
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await updateRoom(form.roomId, {
      dormitoryId: form.dormitoryId,
      roomName: form.roomName,
      roomDetail: form.roomDetail || null,
      areaSqm: form.areaSqm,
      capacity: form.capacity,
      roomType: form.roomType,
      hasAirConditioner: form.hasAirConditioner,
      monthlyFee: form.monthlyFee
    })
    ElMessage.success('部屋情報を更新しました')
    goBack()
  } finally {
    submitLoading.value = false
  }
}

onMounted(fetchRoom)
</script>
