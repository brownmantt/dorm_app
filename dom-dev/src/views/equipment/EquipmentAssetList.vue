<template>
  <div class="page-container">
    <PageHeader title="備品管理" subtitle="Equipment Assets" />

    <el-card class="search-card search-form-grid-card search-form-single-row-card equipment-asset-search-card">
      <el-form :model="searchForm" label-width="72px" class="search-form-grid search-form-single-row" @submit.prevent="handleSearch">
        <div class="search-form-grid__cols">
          <el-form-item label="品目" class="search-field-equipment-item">
            <el-select
              v-model="searchForm.equipmentId"
              clearable
              filterable
              placeholder="すべて"
            >
              <el-option
                v-for="item in itemOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
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
        <el-button type="primary" @click="openDialog()">備品登録</el-button>
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
        <el-table-column
          prop="equipmentAssetId"
          label="備品番号"
          min-width="160"
          show-overflow-tooltip
        />
        <el-table-column prop="equipmentName" label="品目" min-width="140" />
        <el-table-column prop="purchaseDate" label="購入日" width="110" />
        <el-table-column prop="purchaseQuantity" label="購入数量" width="90" align="right" />
        <el-table-column label="購入金額" width="110" align="right">
          <template #default="{ row }">{{ formatYen(row.purchaseAmount) }}</template>
        </el-table-column>
        <el-table-column label="購入店" min-width="160">
          <template #default="{ row }">
            <div class="purchase-store-cell">
              <div class="purchase-store-name">{{ displayStoreName(row) }}</div>
              <div v-if="displayStoreTel(row)" class="purchase-store-tel">{{ displayStoreTel(row) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="warrantyExpiryDate" label="保証期限" width="110">
          <template #default="{ row }">{{ row.warrantyExpiryDate || '—' }}</template>
        </el-table-column>
        <el-table-column label="備考" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">{{ row.remarks || '—' }}</template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="820px" destroy-on-close>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="dialog-form equipment-asset-dialog-form"
      >
        <el-row :gutter="20">
          <el-col v-if="form.equipmentAssetId" :span="24">
            <el-form-item label="備品番号">
              <el-input v-model="form.equipmentAssetId" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品目" prop="equipmentId">
              <el-select v-model="form.equipmentId" filterable placeholder="品目を選択" style="width: 100%">
                <el-option
                  v-for="item in itemOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="購入日" prop="purchaseDate">
              <el-date-picker
                v-model="form.purchaseDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="購入日"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="購入数量" prop="purchaseQuantity">
              <el-input-number
                v-model="form.purchaseQuantity"
                :min="1"
                :max="999"
                :controls="true"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="購入金額" prop="purchaseAmount">
              <el-input-number
                v-model="form.purchaseAmount"
                :min="0"
                :step="1000"
                :controls="true"
                style="width: 100%"
              />
              <div class="form-item-hint">※1点あたりの単価を入力してください</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保証期限" prop="warrantyExpiryDate">
              <el-date-picker
                v-model="form.warrantyExpiryDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="保証期限"
                style="width: 100%"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <div class="purchase-store-group">
              <div class="purchase-store-group__title">購入店情報</div>
              <el-row :gutter="20" class="purchase-store-group__body">
                <el-col :span="12">
                  <el-form-item label="購入店" prop="purchaseStore">
                    <el-input v-model="form.purchaseStore" placeholder="例：ニトリ 国分寺店" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="電話番号" prop="purchaseStoreContact">
                    <el-input v-model="form.purchaseStoreContact" placeholder="例：03-1234-5678" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="郵便番号" prop="purchaseStorePostalCode">
                    <el-input
                      v-model="form.purchaseStorePostalCode"
                      maxlength="7"
                      placeholder="7桁（ハイフンなし）"
                      :disabled="addressLookupLoading"
                      @input="handlePostalCodeInput"
                      @blur="handlePostalCodeBlur"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="住所" prop="purchaseStoreAddress">
                    <el-input v-model="form.purchaseStoreAddress" type="textarea" :rows="2" />
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </el-col>
          <el-col :span="24">
            <el-form-item label="備考" prop="remarks">
              <el-input v-model="form.remarks" type="textarea" :rows="3" placeholder="備考" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { getEquipments } from '@/api/equipment'
import {
  createEquipmentAsset,
  deleteEquipmentAsset,
  getEquipmentAssets,
  updateEquipmentAsset
} from '@/api/equipmentAsset'
import { lookupAddressByPostalCode } from '@/api/postalCode'
import { isCompletePostalCode, normalizePostalCode } from '@/utils/postalCode'
import { normalizePageResponse } from '@/utils/pagination'

const loading = ref(false)
const submitLoading = ref(false)
const addressLookupLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])
const itemOptions = ref([])
/** 住所自動入力済み、または編集開始時の郵便番号（7桁）。変更がない blur では住所を上書きしない */
const lastResolvedPostalCode = ref('')

const pagination = reactive({ page: 1, size: 20, total: 0 })
const searchForm = reactive({ equipmentId: '' })

const emptyForm = () => ({
  equipmentAssetId: '',
  equipmentId: '',
  purchaseDate: '',
  purchaseQuantity: 1,
  purchaseAmount: 0,
  purchaseStore: '',
  purchaseStoreContact: '',
  purchaseStorePostalCode: '',
  purchaseStoreAddress: '',
  warrantyExpiryDate: '',
  remarks: ''
})

const form = reactive(emptyForm())

const rules = {
  equipmentId: [{ required: true, message: '品目を選択してください', trigger: 'change' }],
  purchaseDate: [{ required: true, message: '購入日を入力してください', trigger: 'change' }],
  purchaseQuantity: [{ required: true, message: '購入数量を入力してください', trigger: 'blur' }],
  purchaseAmount: [{ required: true, message: '購入金額を入力してください', trigger: 'blur' }],
  purchaseStorePostalCode: [
    {
      pattern: /^(\d{7})?$/,
      message: '購入店郵便番号は7桁の数字で入力してください',
      trigger: 'blur'
    }
  ]
}

const dialogTitle = computed(() => (form.equipmentAssetId ? '備品編集' : '備品新規登録'))

function formatYen(value) {
  if (value == null || value === '') return '—'
  return `${Number(value).toLocaleString('ja-JP')}円`
}

function looksLikePhone(value) {
  const text = String(value || '').trim()
  if (!text) return false
  return /^[\d\-+()（）\s]+$/.test(text) && /\d{2,}/.test(text)
}

function normalizePurchaseFields(row) {
  let store = row?.purchaseStore?.trim() || ''
  let contact = row?.purchaseStoreContact?.trim() || ''
  if (!store && contact && !looksLikePhone(contact)) {
    store = contact
    contact = ''
  }
  return { store, contact }
}

/** 購入店名称（一覧上段） */
function displayStoreName(row) {
  const { store } = normalizePurchaseFields(row)
  return store || '—'
}

/** 購入店連絡先（一覧下段・Tel: 形式） */
function displayStoreTel(row) {
  const { contact } = normalizePurchaseFields(row)
  if (!contact || !looksLikePhone(contact)) return ''
  return `Tel: ${contact}`
}

async function loadItemOptions() {
  const data = await getEquipments({ page: 0, size: 5000 })
  const { list } = normalizePageResponse(data)
  itemOptions.value = (list || []).map((row) => ({
    label: row.name,
    value: row.equipmentId
  }))
}

async function fetchList() {
  loading.value = true
  try {
    const params = { page: pagination.page - 1, size: pagination.size }
    if (searchForm.equipmentId) params.equipmentId = searchForm.equipmentId
    const data = await getEquipmentAssets(params)
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
  searchForm.equipmentId = ''
  handleSearch()
}

function openDialog(row) {
  const { store, contact } = normalizePurchaseFields(row)
  lastResolvedPostalCode.value = ''
  Object.assign(form, emptyForm(), {
    equipmentAssetId: row?.equipmentAssetId || '',
    equipmentId: row?.equipmentId || '',
    purchaseDate: row?.purchaseDate || '',
    purchaseQuantity: row?.purchaseQuantity != null ? Number(row.purchaseQuantity) : 1,
    purchaseAmount: row?.purchaseAmount != null ? Number(row.purchaseAmount) : 0,
    purchaseStore: store,
    purchaseStoreContact: contact,
    purchaseStorePostalCode: row?.purchaseStorePostalCode || '',
    purchaseStoreAddress: row?.purchaseStoreAddress || '',
    warrantyExpiryDate: row?.warrantyExpiryDate || '',
    remarks: row?.remarks || ''
  })
  if (isCompletePostalCode(form.purchaseStorePostalCode)) {
    lastResolvedPostalCode.value = normalizePostalCode(form.purchaseStorePostalCode)
  }
  dialogVisible.value = true
}

function buildPayload() {
  const trim = (v) => (typeof v === 'string' ? v.trim() : v)
  return {
    equipmentId: form.equipmentId,
    purchaseDate: form.purchaseDate,
    purchaseQuantity: form.purchaseQuantity,
    purchaseAmount: form.purchaseAmount,
    purchaseStore: trim(form.purchaseStore) || null,
    purchaseStoreContact: trim(form.purchaseStoreContact) || null,
    purchaseStorePostalCode: trim(form.purchaseStorePostalCode) || null,
    purchaseStoreAddress: trim(form.purchaseStoreAddress) || null,
    warrantyExpiryDate: form.warrantyExpiryDate || null,
    remarks: trim(form.remarks) || null
  }
}

function handlePostalCodeInput(value) {
  form.purchaseStorePostalCode = normalizePostalCode(value)
}

async function handlePostalCodeBlur() {
  if (!isCompletePostalCode(form.purchaseStorePostalCode)) {
    return
  }
  const normalized = normalizePostalCode(form.purchaseStorePostalCode)
  form.purchaseStorePostalCode = normalized
  if (normalized === lastResolvedPostalCode.value) {
    return
  }
  await fetchAddressByPostalCode(normalized)
}

async function fetchAddressByPostalCode(normalizedPostalCode) {
  if (!normalizedPostalCode || normalizedPostalCode.length !== 7) {
    return
  }
  addressLookupLoading.value = true
  try {
    const data = await lookupAddressByPostalCode(normalizedPostalCode)
    if (data?.address) {
      form.purchaseStoreAddress = data.address
      lastResolvedPostalCode.value = normalizedPostalCode
      ElMessage.success('住所を自動入力しました')
    }
  } catch {
    /* 住所自動入力は任意 */
  } finally {
    addressLookupLoading.value = false
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload = buildPayload()
    if (form.equipmentAssetId) {
      await updateEquipmentAsset(form.equipmentAssetId, payload)
      ElMessage.success('備品を更新しました')
    } else {
      await createEquipmentAsset(payload)
      ElMessage.success('備品を登録しました')
    }
    dialogVisible.value = false
    await fetchList()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`備品「${row.equipmentAssetId}」を削除しますか？`, '確認', {
    type: 'warning'
  })
  await deleteEquipmentAsset(row.equipmentAssetId)
  ElMessage.success('備品を削除しました')
  await fetchList()
}

onMounted(async () => {
  await loadItemOptions()
  await fetchList()
})
</script>

<style scoped>
.purchase-store-cell {
  line-height: 1.4;
}

.purchase-store-name {
  word-break: break-word;
}

.purchase-store-tel {
  margin-top: 2px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  word-break: break-word;
}

.equipment-asset-dialog-form .el-input,
.equipment-asset-dialog-form .el-select,
.equipment-asset-dialog-form .el-date-editor,
.equipment-asset-dialog-form .el-input-number {
  width: 100%;
}

.equipment-asset-dialog-form .el-form-item {
  margin-bottom: 18px;
}

.form-item-hint {
  margin-top: 4px;
  font-size: 12px;
  line-height: 1.4;
  color: var(--el-text-color-secondary);
}

.purchase-store-group {
  margin-bottom: 8px;
  padding: 14px 16px 2px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  background-color: var(--el-fill-color-blank);
}

.purchase-store-group__title {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.purchase-store-group__body .el-form-item:last-child {
  margin-bottom: 12px;
}
</style>


