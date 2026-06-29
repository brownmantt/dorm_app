<template>
  <div class="page-container">
    <PageHeader title="備品保管" subtitle="Equipment Storage" />

    <el-card class="toolbar-card">
      <el-button type="primary" @click="openDialog()">保管登録</el-button>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="storageId" label="保管ID" min-width="140" />
        <el-table-column prop="equipmentName" label="備品名称" min-width="200" />
        <el-table-column prop="storageLocation" label="保管場所" min-width="200" />
        <el-table-column prop="status" label="ステータス" min-width="120">
          <template #default="{ row }">
            {{ labelOf(STORAGE_STATUS, row.status) }}
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

    <el-dialog v-model="dialogVisible" title="保管登録" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="備品" prop="equipmentId">
          <el-select v-model="form.equipmentId" filterable placeholder="選択">
            <el-option
              v-for="item in equipmentOptions"
              :key="item.equipmentId"
              :label="item.name"
              :value="item.equipmentId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="保管場所" prop="storageLocation">
          <el-input v-model="form.storageLocation" />
        </el-form-item>
        <el-form-item label="関連退去ID" prop="linkedMoveoutId">
          <el-input v-model="form.linkedMoveoutId" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">登録</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { createEquipmentStorage, getEquipmentStorages, getEquipments } from '@/api/equipment'
import { normalizePageResponse } from '@/utils/pagination'
import { STORAGE_STATUS, labelOf } from '@/utils/constants'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])
const equipmentOptions = ref([])

const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  equipmentId: '',
  storageLocation: '',
  linkedMoveoutId: ''
})

const rules = {
  equipmentId: [{ required: true, message: '備品を選択してください', trigger: 'change' }],
  storageLocation: [{ required: true, message: '保管場所を入力してください', trigger: 'blur' }]
}

async function fetchList() {
  loading.value = true
  try {
    const data = await getEquipmentStorages({ page: pagination.page - 1, size: pagination.size })
    const { list, total } = normalizePageResponse(data)
    tableData.value = list
    pagination.total = total
  } finally {
    loading.value = false
  }
}

async function fetchEquipments() {
  const data = await getEquipments({ page: 0, size: 1000 })
  const { list } = normalizePageResponse(data)
  equipmentOptions.value = list
}

function openDialog() {
  form.equipmentId = ''
  form.storageLocation = ''
  form.linkedMoveoutId = ''
  dialogVisible.value = true
}

function resetCreateForm() {
  form.equipmentId = ''
  form.storageLocation = ''
  form.linkedMoveoutId = ''
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    await createEquipmentStorage({ ...form })
    ElMessage.success('保管を登録しました')
    resetCreateForm()
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await fetchEquipments()
  await fetchList()
})
</script>
