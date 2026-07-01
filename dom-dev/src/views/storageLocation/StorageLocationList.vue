<template>
  <div class="page-container">
    <PageHeader title="保管場所マスタ" subtitle="Storage Location Master" />

    <el-card class="search-card search-form-grid-card search-form-single-row-card master-list-search-card">
      <el-form :model="query" label-width="96px" class="search-form-grid search-form-single-row">
        <div class="search-form-grid__cols">
          <el-form-item label="保管場所名" class="search-field-name">
            <el-input v-model="query.name" clearable placeholder="名称" />
          </el-form-item>
          <el-form-item class="search-form-grid__actions">
            <el-button type="primary" @click="handleSearch">検索</el-button>
            <el-button @click="handleReset">リセット</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <div class="table-card-toolbar">
        <el-button type="primary" @click="openDialog()">新規登録</el-button>
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
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="storageLocationId" label="保管場所ID" min-width="150" />
        <el-table-column prop="name" label="保管場所名" min-width="200" />
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
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="dialog-form">
        <el-form-item v-if="form.storageLocationId" label="保管場所ID">
          <el-input v-model="form.storageLocationId" disabled />
        </el-form-item>
        <el-form-item label="保管場所名" prop="name">
          <el-input v-model="form.name" maxlength="100" />
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
import {
  createStorageLocation,
  deleteStorageLocation,
  getStorageLocations,
  updateStorageLocation
} from '@/api/storageLocation'
import { normalizePageResponse } from '@/utils/pagination'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])

const query = reactive({ name: '' })
const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  storageLocationId: '',
  name: ''
})

const rules = {
  name: [{ required: true, message: '保管場所名を入力してください', trigger: 'blur' }]
}

const dialogTitle = computed(() => (form.storageLocationId ? '保管場所編集' : '保管場所新規登録'))

async function fetchList() {
  loading.value = true
  try {
    const data = await getStorageLocations({
      name: query.name || undefined,
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
  handleSearch()
}

function openDialog(row) {
  Object.assign(form, {
    storageLocationId: row?.storageLocationId || '',
    name: row?.name || ''
  })
  dialogVisible.value = true
}

function resetCreateForm() {
  Object.assign(form, {
    storageLocationId: '',
    name: ''
  })
  formRef.value?.resetFields()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload = { name: form.name }
    if (form.storageLocationId) {
      await updateStorageLocation(form.storageLocationId, payload)
      ElMessage.success('保管場所を更新しました')
    } else {
      await createStorageLocation(payload)
      ElMessage.success('保管場所を登録しました')
      resetCreateForm()
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`保管場所「${row.name}」を削除しますか？`, '確認', { type: 'warning' })
  await deleteStorageLocation(row.storageLocationId)
  ElMessage.success('削除しました')
  fetchList()
}

onMounted(fetchList)
</script>
