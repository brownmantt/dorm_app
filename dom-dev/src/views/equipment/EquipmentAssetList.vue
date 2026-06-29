<template>
  <div class="page-container">
    <PageHeader title="備品管理" subtitle="Equipment Assets" />

    <el-card class="toolbar-card">
      <el-form :inline="true" class="search-form" @submit.prevent="handleSearch">
        <el-form-item label="品目">
          <el-select
            v-model="searchForm.equipmentId"
            clearable
            filterable
            placeholder="すべて"
            style="width: 220px"
          >
            <el-option
              v-for="item in itemOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">検索</el-button>
          <el-button @click="handleResetSearch">クリア</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="openDialog()">新規登録</el-button>
    </el-card>

    <el-card v-loading="loading" class="table-card">
      <el-table :data="tableData" class="data-table" border stripe empty-text="データがありません">
        <el-table-column type="index" label="番号" width="60" />
        <el-table-column prop="equipmentAssetId" label="備品番号" min-width="140" />
        <el-table-column prop="equipmentName" label="品目" min-width="140" />
        <el-table-column prop="purchaseDate" label="購入日" width="110" />
        <el-table-column label="購入金額" width="110" align="right">
          <template #default="{ row }">{{ formatYen(row.purchaseAmount) }}</template>
        </el-table-column>
        <el-table-column label="購入店" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">{{ displayPurchaseStore(row) }}</template>
        </el-table-column>
        <el-table-column label="連絡先" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">{{ displayPurchaseStoreContact(row) }}</template>
        </el-table-column>
        <el-table-column prop="warrantyExpiryDate" label="保証期限" width="110">
          <template #default="{ row }">{{ row.warrantyExpiryDate || '—' }}</template>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="130px" class="dialog-form">
        <el-form-item v-if="form.equipmentAssetId" label="備品番号">
          <el-input v-model="form.equipmentAssetId" disabled />
        </el-form-item>
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
        <el-form-item label="購入日" prop="purchaseDate">
          <el-date-picker
            v-model="form.purchaseDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="購入日"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="購入金額" prop="purchaseAmount">
          <el-input-number
            v-model="form.purchaseAmount"
            :min="0"
            :step="1000"
            :controls="true"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="購入店" prop="purchaseStore">
          <el-input v-model="form.purchaseStore" placeholder="例：ニトリ 国分寺店" />
        </el-form-item>
        <el-form-item label="電話番号" prop="purchaseStoreContact">
          <el-input v-model="form.purchaseStoreContact" placeholder="例：03-1234-5678" />
        </el-form-item>
        <el-form-item label="購入店郵便番号" prop="purchaseStorePostalCode">
          <el-input
            v-model="form.purchaseStorePostalCode"
            maxlength="7"
            placeholder="7桁（ハイフンなし）"
            @blur="handlePostalBlur"
          />
        </el-form-item>
        <el-form-item label="購入店住所" prop="purchaseStoreAddress">
          <el-input v-model="form.purchaseStoreAddress" type="textarea" :rows="2" />
        </el-form-item>
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
import { normalizePageResponse } from '@/utils/pagination'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const tableData = ref([])
const itemOptions = ref([])

const pagination = reactive({ page: 1, size: 20, total: 0 })
const searchForm = reactive({ equipmentId: '' })

const emptyForm = () => ({
  equipmentAssetId: '',
  equipmentId: '',
  purchaseDate: '',
  purchaseAmount: 0,
  purchaseStore: '',
  purchaseStoreContact: '',
  purchaseStorePostalCode: '',
  purchaseStoreAddress: '',
  warrantyExpiryDate: ''
})

const form = reactive(emptyForm())

const rules = {
  equipmentId: [{ required: true, message: '品目を選択してください', trigger: 'change' }],
  purchaseDate: [{ required: true, message: '購入日を入力してください', trigger: 'change' }],
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

/** 購入店名称（未入力時は連絡先欄の店名をフォールバック表示） */
function displayPurchaseStore(row) {
  const { store, contact } = normalizePurchaseFields(row)
  return store || contact || '—'
}

/** 電話番号等（店名のみ入力されている場合は購入店列に表示済みのため非表示） */
function displayPurchaseStoreContact(row) {
  const { store, contact } = normalizePurchaseFields(row)
  if (!contact) return '—'
  if (!store && contact) return '—'
  return contact
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

function handleResetSearch() {
  searchForm.equipmentId = ''
  handleSearch()
}

function openDialog(row) {
  const { store, contact } = normalizePurchaseFields(row)
  Object.assign(form, emptyForm(), {
    equipmentAssetId: row?.equipmentAssetId || '',
    equipmentId: row?.equipmentId || '',
    purchaseDate: row?.purchaseDate || '',
    purchaseAmount: row?.purchaseAmount != null ? Number(row.purchaseAmount) : 0,
    purchaseStore: store,
    purchaseStoreContact: contact,
    purchaseStorePostalCode: row?.purchaseStorePostalCode || '',
    purchaseStoreAddress: row?.purchaseStoreAddress || '',
    warrantyExpiryDate: row?.warrantyExpiryDate || ''
  })
  dialogVisible.value = true
}

function buildPayload() {
  const trim = (v) => (typeof v === 'string' ? v.trim() : v)
  return {
    equipmentId: form.equipmentId,
    purchaseDate: form.purchaseDate,
    purchaseAmount: form.purchaseAmount,
    purchaseStore: trim(form.purchaseStore) || null,
    purchaseStoreContact: trim(form.purchaseStoreContact) || null,
    purchaseStorePostalCode: trim(form.purchaseStorePostalCode) || null,
    purchaseStoreAddress: trim(form.purchaseStoreAddress) || null,
    warrantyExpiryDate: form.warrantyExpiryDate || null
  }
}

async function handlePostalBlur() {
  const normalized = (form.purchaseStorePostalCode || '').replace(/\D/g, '')
  form.purchaseStorePostalCode = normalized
  if (normalized.length !== 7) return
  try {
    const data = await lookupAddressByPostalCode(normalized)
    if (data?.address && !form.purchaseStoreAddress) {
      form.purchaseStoreAddress = data.address
    }
  } catch {
    /* 住所自動入力は任意 */
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
