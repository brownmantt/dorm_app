<template>
  <div class="page-container">
    <PageHeader title="備品マスタ" subtitle="Equipment Master" />

    <el-card class="toolbar-card">
      <el-button type="primary" @click="openDialog()">新規登録</el-button>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="equipmentId" label="備品ID" min-width="140" />
        <el-table-column prop="name" label="名称" min-width="200" />
        <el-table-column prop="equipmentType" label="種別" min-width="160" />
        <el-table-column
          label="操作"
          width="88"
          fixed="right"
          align="center"
          header-align="center"
          class-name="col-actions"
        >
          <template #default="{ row }">
            <div class="table-actions-inline">
              <el-button link type="primary" @click="openDialog(row)">編集</el-button>
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
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="種別" prop="equipmentType">
          <el-input v-model="form.equipmentType" />
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
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { createEquipment, getEquipments, updateEquipment } from '@/api/equipment'
import { normalizePageResponse } from '@/utils/pagination'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])

const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  equipmentId: '',
  name: '',
  equipmentType: ''
})

const rules = {
  name: [{ required: true, message: '名称を入力してください', trigger: 'blur' }],
  equipmentType: [{ required: true, message: '種別を入力してください', trigger: 'blur' }]
}

const dialogTitle = computed(() => (form.equipmentId ? '備品編集' : '備品新規登録'))

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
    name: row?.name || '',
    equipmentType: row?.equipmentType || ''
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.equipmentId) {
      await updateEquipment(form.equipmentId, { name: form.name, equipmentType: form.equipmentType })
      ElMessage.success('備品を更新しました')
    } else {
      await createEquipment({ name: form.name, equipmentType: form.equipmentType })
      ElMessage.success('備品を登録しました')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

onMounted(fetchList)
</script>
