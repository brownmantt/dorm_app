<template>
  <div class="page-container">
    <PageHeader title="品目マスタ" subtitle="Item Master" />

    <el-card class="toolbar-card">
      <el-button type="primary" @click="openDialog()">新規登録</el-button>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="equipmentId" label="品目ID" min-width="140" />
        <el-table-column prop="name" label="品目名称" min-width="200" />
        <el-table-column
          label="操作"
          width="120"
          fixed="right"
          align="center"
          header-align="center"
          class-name="col-actions"
        >
          <template #default="{ row }">
            <div class="table-actions-inline">
              <el-button link type="primary" @click="openDialog(row)">編集</el-button>
              <el-button link type="danger" @click="handleDelete(row)">削除</el-button>
            </div>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item v-if="form.equipmentId" label="品目ID">
          <el-input v-model="form.equipmentId" disabled />
        </el-form-item>
        <el-form-item label="品目名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">キャンセル</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { createEquipment, deleteEquipment, getEquipments, updateEquipment } from '@/api/equipment'
import { normalizePageResponse } from '@/utils/pagination'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])

const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  equipmentId: '',
  name: ''
})

const rules = {
  name: [{ required: true, message: '品目名称を入力してください', trigger: 'blur' }]
}

const dialogTitle = computed(() => (form.equipmentId ? '品目編集' : '品目新規登録'))

async function fetchList() {
  loading.value = true
  try {
    const data = await getEquipments({ page: pagination.page - 1, size: pagination.size })
    const { list, total } = normalizePageResponse(data)
    tableData.value = list
    pagination.total = total
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  Object.assign(form, {
    equipmentId: row?.equipmentId || '',
    name: row?.name || ''
  })
  dialogVisible.value = true
}

function resetCreateForm() {
  Object.assign(form, {
    equipmentId: '',
    name: ''
  })
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.equipmentId) {
      await updateEquipment(form.equipmentId, { name: form.name })
      ElMessage.success('品目を更新しました')
    } else {
      await createEquipment({ name: form.name })
      ElMessage.success('品目を登録しました')
      resetCreateForm()
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`品目「${row.name}」を削除しますか？`, '確認', { type: 'warning' })
  await deleteEquipment(row.equipmentId)
  ElMessage.success('品目を削除しました')
  fetchList()
}

onMounted(fetchList)
</script>
