<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="560px" destroy-on-close @open="handleOpen">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
      <el-form-item label="寮名称" prop="name">
        <el-input v-model="form.name" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="郵便番号" prop="postalCode">
        <el-input
          v-model="form.postalCode"
          placeholder="123-4567"
          maxlength="8"
          :disabled="addressLookupLoading"
          @input="handlePostalCodeInput"
          @blur="handlePostalCodeBlur"
        />
      </el-form-item>
      <el-form-item label="住所" prop="address">
        <el-input
          v-model="form.address"
          type="textarea"
          :rows="3"
          placeholder="番地・建物名を含めて入力"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="地域" prop="region">
        <el-select v-model="form.region" placeholder="選択">
          <el-option
            v-for="item in regionOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="間取り" prop="layoutType">
        <el-select v-model="form.layoutType" placeholder="選択">
          <el-option v-for="item in LAYOUT_TYPE_OPTIONS" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="種別" prop="genderType">
        <el-select v-model="form.genderType" :disabled="!!form.dormitoryId" placeholder="選択">
          <el-option label="男性寮" value="MALE" />
          <el-option label="女性寮" value="FEMALE" />
        </el-select>
      </el-form-item>
      <el-form-item label="最寄駅">
        <div class="nearest-station-row">
          <el-input v-model="form.nearestStation1" maxlength="20" placeholder="最寄駅１" />
          <el-input v-model="form.nearestStation2" maxlength="20" placeholder="最寄駅２" />
          <el-input v-model="form.nearestStation3" maxlength="20" placeholder="最寄駅３" />
        </div>
      </el-form-item>
      <el-form-item label="備考" prop="remarks">
        <el-input v-model="form.remarks" type="textarea" rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">キャンセル</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createDormitory, updateDormitory } from '@/api/dormitory'
import { lookupAddressByPostalCode } from '@/api/postalCode'
import { formatPostalCode, isCompletePostalCode, normalizePostalCode } from '@/utils/postalCode'
import { LAYOUT_TYPE_OPTIONS } from '@/utils/constants'
import { inferRegionFromAddress } from '@/utils/dormAllocation'
import { loadRegionOptions } from '@/utils/region'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  /** 編集対象。未指定時は新規登録 */
  editData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'saved'])

const submitLoading = ref(false)
const addressLookupLoading = ref(false)
const formRef = ref()
const regionOptions = ref([])
/** 住所自動入力済み、または編集開始時の郵便番号（7桁）。変更がない blur では住所を上書きしない */
const lastResolvedPostalCode = ref('')

const form = reactive({
  dormitoryId: '',
  name: '',
  postalCode: '',
  address: '',
  region: '',
  layoutType: '',
  genderType: '',
  nearestStation1: '',
  nearestStation2: '',
  nearestStation3: '',
  remarks: ''
})

const rules = {
  name: [
    { required: true, message: '寮名称を入力してください', trigger: 'blur' },
    { max: 100, message: '寮名称は100文字以内で入力してください', trigger: 'blur' }
  ],
  postalCode: [
    { required: true, message: '郵便番号を入力してください', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (!isCompletePostalCode(value)) {
          callback(new Error('郵便番号は7桁の数字で入力してください'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  address: [
    { required: true, message: '住所を入力してください', trigger: 'blur' },
    { max: 200, message: '住所は200文字以内で入力してください', trigger: 'blur' }
  ],
  region: [{ required: true, message: '地域を選択してください', trigger: 'change' }],
  layoutType: [{ required: true, message: '間取りを選択してください', trigger: 'change' }],
  genderType: [{ required: true, message: '種別を選択してください', trigger: 'change' }]
}

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const dialogTitle = computed(() => (form.dormitoryId ? '寮編集' : '寮新規登録'))

function resetForm() {
  Object.assign(form, {
    dormitoryId: '',
    name: '',
    postalCode: '',
    address: '',
    region: '',
    layoutType: '',
    genderType: '',
    nearestStation1: '',
    nearestStation2: '',
    nearestStation3: '',
    remarks: ''
  })
}

function fillFormFromEditData(row) {
  Object.assign(form, {
    dormitoryId: row.dormitoryId,
    name: row.name,
    postalCode: formatPostalCode(row.postalCode || ''),
    address: row.address,
    region: row.region || inferRegionFromAddress(row.address),
    layoutType: row.layoutType,
    genderType: row.genderType,
    nearestStation1: row.nearestStation1 || '',
    nearestStation2: row.nearestStation2 || '',
    nearestStation3: row.nearestStation3 || '',
    remarks: row.remarks || ''
  })
}

async function handleOpen() {
  resetForm()
  lastResolvedPostalCode.value = ''
  const options = await loadRegionOptions()
  regionOptions.value = options
  if (props.editData) {
    fillFormFromEditData(props.editData)
  } else if (options.length) {
    form.region = options[0].value
  }
  if (isCompletePostalCode(form.postalCode)) {
    lastResolvedPostalCode.value = normalizePostalCode(form.postalCode)
  }
}

function handlePostalCodeInput(value) {
  form.postalCode = formatPostalCode(value)
}

async function handlePostalCodeBlur() {
  if (!isCompletePostalCode(form.postalCode)) {
    return
  }
  const normalized = normalizePostalCode(form.postalCode)
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
      form.address = data.address
      lastResolvedPostalCode.value = normalizedPostalCode
      ElMessage.success('住所を自動入力しました')
    }
  } finally {
    addressLookupLoading.value = false
  }
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    const payload = {
      name: form.name,
      postalCode: normalizePostalCode(form.postalCode),
      address: form.address,
      region: form.region,
      layoutType: form.layoutType,
      genderType: form.genderType,
      nearestStation1: form.nearestStation1 || null,
      nearestStation2: form.nearestStation2 || null,
      nearestStation3: form.nearestStation3 || null,
      remarks: form.remarks || null
    }
    if (form.dormitoryId) {
      await updateDormitory(form.dormitoryId, payload)
      ElMessage.success('寮情報を更新しました')
    } else {
      await createDormitory(payload)
      ElMessage.success('寮を登録しました')
    }
    visible.value = false
    emit('saved')
  } finally {
    submitLoading.value = false
  }
}
</script>
