<template>
  <div class="page-container">
    <PageHeader title="利用形態マスタ" subtitle="Usage Type Master" />

    <el-card class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="名称">
          <el-input v-model="query.name" clearable placeholder="名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">検索</el-button>
          <el-button @click="handleReset">リセット</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card">
      <el-button type="primary" @click="openDialog()">新規登録</el-button>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="usageTypeId" label="利用形態ID" min-width="140" />
        <el-table-column prop="code" label="コード値" min-width="140" />
        <el-table-column prop="name" label="名称" min-width="200" />
        <el-table-column prop="displayOrder" label="表示順" min-width="90" />
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="dialog-form">
        <el-form-item label="コード値" prop="code">
          <el-input v-model="form.code" maxlength="30" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" maxlength="100" />
        </el-form-item>
        <el-form-item label="表示順" prop="displayOrder">
          <el-input-number v-model="form.displayOrder" :min="0" :step="1" />
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
  createUsageType,
  deleteUsageType,
  getUsageTypes,
  updateUsageType
} from '@/api/usageType'
import { normalizePageResponse } from '@/utils/pagination'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])

const query = reactive({ name: '' })
const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  usageTypeId: '',
  code: '',
  name: '',
  displayOrder: 0
})

const rules = {
  code: [{ required: true, message: 'コード値を入力してください', trigger: 'blur' }],
  name: [{ required: true, message: '名称を入力してください', trigger: 'blur' }]
}

const dialogTitle = computed(() => (form.usageTypeId ? '利用形態編集' : '利用形態新規登録'))

async function fetchList() {
  loading.value = true
  try {
    const data = await getUsageTypes({
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
    usageTypeId: row?.usageTypeId || '',
    code: row?.code || '',
    name: row?.name || '',
    displayOrder: row?.displayOrder ?? 0
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload = {
      code: form.code,
      name: form.name,
      displayOrder: form.displayOrder
    }
    if (form.usageTypeId) {
      await updateUsageType(form.usageTypeId, payload)
      ElMessage.success('利用形態を更新しました')
    } else {
      await createUsageType(payload)
      ElMessage.success('利用形態を登録しました')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`利用形態「${row.name}」を削除しますか？`, '確認', { type: 'warning' })
  await deleteUsageType(row.usageTypeId)
  ElMessage.success('削除しました')
  fetchList()
}

onMounted(fetchList)
</script>
