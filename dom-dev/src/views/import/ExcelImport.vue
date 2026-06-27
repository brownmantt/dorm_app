<template>
  <div class="page-container">
    <PageHeader title="Excel/CSV 取込・出力" subtitle="Excel / CSV Import & Export" />

    <el-card>
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :limit="1"
        accept=".xlsx"
        :before-upload="beforeUpload"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
      >
        <el-button type="primary">ファイル選択</el-button>
        <template #tip>
          <div class="el-upload__tip">xlsx 形式のみ（最大 10MB）</div>
        </template>
      </el-upload>

      <div class="action-row">
        <el-button type="primary" :disabled="!selectedFile" :loading="previewLoading" @click="handlePreview">
          プレビュー
        </el-button>
        <el-button type="success" :disabled="!previewData" :loading="executeLoading" @click="handleExecute">
          取込実行
        </el-button>
      </div>
    </el-card>

    <el-card class="toolbar-card">
      <span class="export-label">CSV エクスポート：</span>
      <el-button :loading="exportResidenceLoading" @click="handleExportResidences">入居履歴</el-button>
      <el-button :loading="exportFeeLoading" @click="handleExportDormFees">寮費</el-button>
    </el-card>

    <el-card v-if="previewData" v-loading="previewLoading" class="table-card">
      <template #header>プレビュー結果</template>
      <el-descriptions :column="2" border class="detail-descriptions">
        <el-descriptions-item label="登録予定件数">
          {{ previewData.insertCount ?? previewData.totalCount ?? '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="エラー件数">
          {{ previewData.errorCount ?? previewErrors.length }}
        </el-descriptions-item>
      </el-descriptions>

      <el-table v-if="previewErrors.length" :data="previewErrors" class="data-table table-wrap-text error-table" border stripe>
        <el-table-column prop="rowNumber" label="行番号" min-width="90" />
        <el-table-column prop="field" label="項目" min-width="140" />
        <el-table-column prop="message" label="メッセージ" min-width="400" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import {
  executeExcelImport,
  exportDormFeesCsv,
  exportResidencesCsv,
  previewExcelImport
} from '@/api/import'

const MAX_FILE_SIZE = 10 * 1024 * 1024

const uploadRef = ref()
const previewLoading = ref(false)
const executeLoading = ref(false)
const exportResidenceLoading = ref(false)
const exportFeeLoading = ref(false)
const selectedFile = ref(null)
const previewData = ref(null)
const previewErrors = ref([])

function validateFile(file) {
  if (!file) return false
  const isXlsx = file.name.toLowerCase().endsWith('.xlsx')
  if (!isXlsx) {
    ElMessage.error('xlsx 形式のファイルを選択してください')
    return false
  }
  if (file.size > MAX_FILE_SIZE) {
    ElMessage.error('ファイルサイズは 10MB 以下にしてください')
    return false
  }
  return true
}

function beforeUpload(file) {
  return validateFile(file)
}

function handleFileChange(file) {
  if (!validateFile(file.raw)) {
    selectedFile.value = null
    previewData.value = null
    previewErrors.value = []
    uploadRef.value?.clearFiles()
    return
  }
  selectedFile.value = file.raw
  previewData.value = null
  previewErrors.value = []
}

function handleFileRemove() {
  selectedFile.value = null
  previewData.value = null
  previewErrors.value = []
}

function buildFormData() {
  const formData = new FormData()
  formData.append('file', selectedFile.value)
  return formData
}

async function handlePreview() {
  if (!selectedFile.value) return
  if (!validateFile(selectedFile.value)) return
  previewLoading.value = true
  try {
    const data = await previewExcelImport(buildFormData())
    previewData.value = data
    previewErrors.value = data.errors || data.errorDetails || []
    ElMessage.success('プレビューを取得しました')
  } finally {
    previewLoading.value = false
  }
}

function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

async function handleExportResidences() {
  exportResidenceLoading.value = true
  try {
    const blob = await exportResidencesCsv({})
    downloadBlob(blob, 'residences.csv')
    ElMessage.success('入居履歴をエクスポートしました')
  } finally {
    exportResidenceLoading.value = false
  }
}

async function handleExportDormFees() {
  exportFeeLoading.value = true
  try {
    const blob = await exportDormFeesCsv({})
    downloadBlob(blob, 'dorm-fees.csv')
    ElMessage.success('寮費をエクスポートしました')
  } finally {
    exportFeeLoading.value = false
  }
}

async function handleExecute() {
  if (!selectedFile.value || !previewData.value) return
  if (!validateFile(selectedFile.value)) return
  await ElMessageBox.confirm('取込を実行しますか？', '確認', { type: 'warning' })
  executeLoading.value = true
  try {
    await executeExcelImport(buildFormData())
    ElMessage.success('取込が完了しました')
    previewData.value = null
    previewErrors.value = []
    selectedFile.value = null
    uploadRef.value?.clearFiles()
  } finally {
    executeLoading.value = false
  }
}
</script>

<style scoped>
.action-row {
  margin-top: 16px;
  display: flex;
  gap: 8px;
}

.error-table {
  margin-top: 16px;
}

.export-label {
  margin-right: 12px;
  color: var(--cnc-text-secondary);
  font-weight: 500;
}
</style>
