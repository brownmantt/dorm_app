<template>
  <div class="page-container form-page">
    <PageHeader title="退去備品処理" subtitle="Move-out Equipment" />

    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="140px">
        <el-form-item label="入居履歴ID" prop="residenceHistoryId">
          <el-input v-model="form.residenceHistoryId" />
        </el-form-item>
        <el-form-item label="備品" prop="equipmentId">
          <el-select v-model="form.equipmentId" filterable placeholder="選択">
            <el-option
              v-for="item in equipmentOptions"
              :key="item.equipmentId"
              :label="`${item.equipmentId} - ${item.name}`"
              :value="item.equipmentId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="処分方法" prop="disposition">
          <el-select v-model="form.disposition" placeholder="選択">
            <el-option label="廃棄" value="DISCARD" />
            <el-option label="保管" value="STORE" />
            <el-option label="再利用" value="REUSE" />
          </el-select>
        </el-form-item>
        <el-form-item label="備考" prop="remarks">
          <el-input v-model="form.remarks" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">処理実行</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { getEquipments, processEquipmentMoveout } from '@/api/equipment'
import { normalizePageResponse } from '@/utils/pagination'

const formRef = ref()
const submitLoading = ref(false)
const equipmentOptions = ref([])

const form = reactive({
  residenceHistoryId: '',
  equipmentId: '',
  disposition: '',
  remarks: ''
})

const rules = {
  residenceHistoryId: [{ required: true, message: '入居履歴IDを入力してください', trigger: 'blur' }],
  equipmentId: [{ required: true, message: '備品を選択してください', trigger: 'change' }],
  disposition: [{ required: true, message: '処分方法を選択してください', trigger: 'change' }]
}

async function fetchEquipments() {
  const data = await getEquipments({ page: 0, size: 1000 })
  const { list } = normalizePageResponse(data)
  equipmentOptions.value = list
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await processEquipmentMoveout({ ...form })
    ElMessage.success('退去備品処理を完了しました')
    form.residenceHistoryId = ''
    form.equipmentId = ''
    form.disposition = ''
    form.remarks = ''
  } finally {
    submitLoading.value = false
  }
}

onMounted(fetchEquipments)
</script>
