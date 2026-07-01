<template>
  <div class="page-container">
    <PageHeader title="備品保管" subtitle="Equipment Storage" />

    <el-card class="search-card search-form-grid-card search-form-single-row-card equipment-storage-search-card">
      <el-form :model="query" label-width="72px" class="search-form-grid search-form-single-row">
        <div class="search-form-grid__cols">
          <el-form-item label="備品" class="search-field-equipment">
            <el-select
              v-model="query.equipmentAssetId"
              clearable
              filterable
              placeholder="すべて"
            >
              <el-option
                v-for="item in assetOptions"
                :key="item.equipmentAssetId"
                :label="formatAssetLabel(item)"
                :value="item.equipmentAssetId"
              />
            </el-select>
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
        <el-button type="primary" @click="openDialog()">保管登録</el-button>
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
        <el-table-column prop="equipmentAssetId" label="備品番号" min-width="150" />
        <el-table-column prop="equipmentName" label="品目名称" min-width="160" />
        <el-table-column prop="storageLocationName" label="保管場所" min-width="160" />
        <el-table-column prop="storageQuantity" label="保管数量" width="100" align="right" />
        <el-table-column prop="status" label="ステータス" min-width="110">
          <template #default="{ row }">
            {{ labelOf(STORAGE_STATUS, row.status) }}
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="140"
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="720px"
      destroy-on-close
      @closed="resetDialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="dialog-form">
        <el-form-item label="備品" prop="equipmentAssetId">
          <el-select
            v-model="form.equipmentAssetId"
            filterable
            placeholder="選択"
            style="width: 100%"
            :disabled="isEditMode"
            @change="handleAssetChange"
          >
            <el-option
              v-for="item in assetOptions"
              :key="item.equipmentAssetId"
              :label="formatAssetLabel(item)"
              :value="item.equipmentAssetId"
            />
          </el-select>
          <div v-if="selectedAsset" class="form-hint">
            購入数量: {{ selectedAsset.purchaseQuantity ?? 1 }}
            ／ 保管合計: {{ totalStorageQuantity }}
            <span v-if="quantityMismatch" class="form-hint-error">（購入数量と一致させてください）</span>
          </div>
        </el-form-item>

        <div class="storage-lines-header">
          <span>
            保管明細
            <span v-if="showLineAction" class="storage-lines-hint">（「行追加」で複数行にした後、不要な行を削除できます）</span>
          </span>
          <el-button
            v-if="canAddLine"
            type="primary"
            link
            @click="addLine"
          >
            行追加
          </el-button>
        </div>

        <div class="storage-lines-grid" :class="{ 'with-line-action': showLineAction }">
          <div class="storage-lines-grid-head">
            <span>保管場所</span>
            <span>保管数量</span>
            <span>ステータス</span>
            <span>関連退去ID</span>
            <span v-if="showLineAction">操作</span>
          </div>
          <div
            v-for="(row, index) in form.lines"
            :key="index"
            class="storage-lines-grid-row-block"
          >
            <div class="storage-lines-grid-row">
              <el-form-item
                :prop="`lines.${index}.storageLocationId`"
                :rules="lineRules.storageLocationId"
                class="inline-form-item storage-line-field"
              >
                <el-select v-model="row.storageLocationId" filterable placeholder="選択" class="storage-line-select">
                  <el-option
                    v-for="item in storageLocationOptions"
                    :key="item.storageLocationId"
                    :label="item.name"
                    :value="item.storageLocationId"
                  />
                </el-select>
              </el-form-item>
              <el-form-item
                :prop="`lines.${index}.storageQuantity`"
                :rules="lineRules.storageQuantity"
                class="inline-form-item storage-line-field"
              >
                <el-input-number
                  v-model="row.storageQuantity"
                  :min="1"
                  :max="selectedPurchaseQuantity"
                  :disabled="isSingleQuantityFixed"
                  controls-position="right"
                  class="storage-line-qty"
                />
              </el-form-item>
              <el-select v-model="row.status" class="storage-line-select storage-line-field-plain">
                <el-option
                  v-for="item in storageStatusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <el-input v-model="row.linkedMoveoutId" placeholder="任意" class="storage-line-input storage-line-field-plain" />
              <div v-if="showLineAction" class="storage-line-action storage-line-field-plain">
                <el-button
                  v-if="form.lines.length > 1"
                  link
                  type="danger"
                  @click="removeLine(index)"
                >
                  削除
                </el-button>
                <span v-else class="storage-line-action-hint">—</span>
              </div>
            </div>
          </div>
          <div v-if="!form.lines.length" class="storage-lines-empty">保管明細がありません</div>
        </div>
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
import { getEquipmentAssets } from '@/api/equipmentAsset'
import {
  deleteEquipmentStorage,
  getEquipmentStorages,
  getEquipmentStoragesByAsset,
  saveEquipmentStoragesByAsset
} from '@/api/equipmentStorage'
import { getStorageLocationsSilent } from '@/api/storageLocation'
import { normalizePageResponse } from '@/utils/pagination'
import { STORAGE_STATUS, labelOf } from '@/utils/constants'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])
const assetOptions = ref([])
const storageLocationOptions = ref([])
const isEditMode = ref(false)

const query = reactive({ equipmentAssetId: '' })
const pagination = reactive({ page: 1, size: 20, total: 0 })

const form = reactive({
  equipmentAssetId: '',
  lines: []
})

const rules = {
  equipmentAssetId: [{ required: true, message: '備品を選択してください', trigger: 'change' }]
}

const lineRules = {
  storageLocationId: [{ required: true, message: '保管場所を選択してください', trigger: 'change' }],
  storageQuantity: [{ required: true, message: '保管数量を入力してください', trigger: 'change' }]
}

const storageStatusOptions = Object.entries(STORAGE_STATUS).map(([value, label]) => ({ value, label }))

const dialogTitle = computed(() => (isEditMode.value ? '保管編集' : '保管登録'))

const selectedAsset = computed(() =>
  assetOptions.value.find((item) => item.equipmentAssetId === form.equipmentAssetId)
)

const selectedPurchaseQuantity = computed(() => selectedAsset.value?.purchaseQuantity ?? 1)

const totalStorageQuantity = computed(() =>
  form.lines.reduce((sum, line) => sum + (Number(line.storageQuantity) || 0), 0)
)

const quantityMismatch = computed(() =>
  form.equipmentAssetId && totalStorageQuantity.value !== selectedPurchaseQuantity.value
)

const isSingleQuantityFixed = computed(() => selectedPurchaseQuantity.value === 1)

/** 購入数量 2 以上のときのみ行削除列を表示 */
const showLineAction = computed(() => !isSingleQuantityFixed.value && Boolean(form.equipmentAssetId))

const canAddLine = computed(() => {
  if (!form.equipmentAssetId) return false
  if (selectedPurchaseQuantity.value === 1) return false
  return form.lines.length < selectedPurchaseQuantity.value
})

function formatAssetLabel(item) {
  const name = item.equipmentName || item.name || ''
  return name ? `${item.equipmentAssetId} - ${name}` : item.equipmentAssetId
}

function createEmptyLine() {
  return {
    storageLocationId: '',
    storageQuantity: selectedPurchaseQuantity.value === 1 ? 1 : 1,
    status: 'IN_STORAGE',
    linkedMoveoutId: ''
  }
}

async function fetchList() {
  loading.value = true
  try {
    const data = await getEquipmentStorages({
      equipmentAssetId: query.equipmentAssetId || undefined,
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

async function fetchAssetOptions() {
  const data = await getEquipmentAssets({ page: 0, size: 5000 })
  const { list } = normalizePageResponse(data)
  assetOptions.value = list
}

async function fetchStorageLocations() {
  const data = await getStorageLocationsSilent({ page: 0, size: 1000 })
  const { list } = normalizePageResponse(data)
  storageLocationOptions.value = list
}

function handleSearch() {
  pagination.page = 1
  fetchList()
}

function handleReset() {
  query.equipmentAssetId = ''
  handleSearch()
}

async function loadLinesForAsset(equipmentAssetId) {
  const list = await getEquipmentStoragesByAsset(equipmentAssetId)
  if (!list?.length) {
    form.lines = [createEmptyLine()]
    return
  }
  form.lines = list.map((row) => ({
    storageLocationId: row.storageLocationId,
    storageQuantity: row.storageQuantity,
    status: row.status || 'IN_STORAGE',
    linkedMoveoutId: row.linkedMoveoutId || ''
  }))
}

function openDialog(row) {
  isEditMode.value = Boolean(row?.equipmentAssetId)
  form.equipmentAssetId = row?.equipmentAssetId || ''
  if (form.equipmentAssetId) {
    loadLinesForAsset(form.equipmentAssetId)
  } else {
    form.lines = []
  }
  dialogVisible.value = true
}

function resetDialog() {
  form.equipmentAssetId = ''
  form.lines = []
  isEditMode.value = false
  formRef.value?.resetFields()
}

function handleAssetChange() {
  form.lines = [createEmptyLine()]
}

function addLine() {
  form.lines.push(createEmptyLine())
}

function removeLine(index) {
  form.lines.splice(index, 1)
}

async function handleSubmit() {
  await formRef.value.validate()
  if (!form.lines.length) {
    ElMessage.warning('保管明細を1件以上入力してください')
    return
  }
  if (quantityMismatch.value) {
    ElMessage.warning(`保管数量の合計を購入数量（${selectedPurchaseQuantity.value}）に合わせてください`)
    return
  }
  submitLoading.value = true
  try {
    await saveEquipmentStoragesByAsset(form.equipmentAssetId, {
      lines: form.lines.map((line) => ({
        storageLocationId: line.storageLocationId,
        storageQuantity: line.storageQuantity,
        status: line.status,
        linkedMoveoutId: line.linkedMoveoutId || null
      }))
    })
    ElMessage.success('保管情報を保存しました')
    dialogVisible.value = false
    fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('この保管明細を削除しますか？', '確認', { type: 'warning' })
  await deleteEquipmentStorage(row.storageId)
  ElMessage.success('削除しました')
  fetchList()
}

onMounted(async () => {
  await Promise.all([fetchAssetOptions(), fetchStorageLocations()])
  await fetchList()
})
</script>

<style scoped>
.form-hint {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.form-hint-error {
  color: var(--el-color-danger);
}

.storage-lines-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 8px 0 12px;
  font-weight: 600;
}

.storage-lines-hint {
  margin-left: 6px;
  font-weight: 400;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.storage-lines-grid {
  border: 1px solid var(--el-table-border-color);
  border-radius: 4px;
  margin-bottom: 8px;
}

.storage-lines-grid-head,
.storage-lines-grid-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 100px 100px 108px;
  column-gap: 8px;
  align-items: start;
  padding: 8px 10px;
}

.storage-lines-grid.with-line-action .storage-lines-grid-head,
.storage-lines-grid.with-line-action .storage-lines-grid-row {
  grid-template-columns: minmax(0, 1fr) 100px 100px 108px 52px;
}

.storage-lines-grid-head {
  background: #f0f5fa;
  font-weight: 600;
  font-size: 14px;
  color: var(--el-text-color-primary);
  border-bottom: 1px solid var(--el-table-border-color);
  align-items: center;
}

.storage-lines-grid-row-block + .storage-lines-grid-row-block {
  border-top: 1px solid var(--el-table-border-color);
}

.storage-lines-grid-row-block {
  padding-bottom: 4px;
}

.storage-lines-empty {
  padding: 16px;
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.storage-lines-grid :deep(.storage-line-field.inline-form-item) {
  margin-bottom: 0;
  min-width: 0;
}

.storage-lines-grid :deep(.storage-line-field .el-form-item__content) {
  margin-left: 0 !important;
  min-width: 0;
  line-height: normal;
}

.storage-lines-grid :deep(.storage-line-field .el-form-item__error) {
  position: static;
  margin-top: 4px;
  padding-top: 0;
  line-height: 1.4;
  white-space: normal;
  word-break: keep-all;
}

.storage-line-field-plain {
  align-self: center;
  min-height: 32px;
}

.storage-line-select,
.storage-line-input {
  width: 100%;
  min-width: 0;
}

.storage-line-qty {
  width: 100%;
}

.storage-lines-grid :deep(.storage-line-qty .el-input__wrapper) {
  padding-left: 8px;
  padding-right: 36px;
}

.storage-line-action {
  text-align: center;
}

.storage-line-action-hint {
  color: var(--el-text-color-placeholder);
  font-size: 14px;
}
</style>
