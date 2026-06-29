export const GENDER_TYPE = {
  MALE: '男性寮',
  FEMALE: '女性寮'
}

export const DORMITORY_GENDER_OPTIONS = [
  { label: '男性寮', value: 'MALE' },
  { label: '女性寮', value: 'FEMALE' }
]

export const EMPLOYEE_GENDER = {
  MALE: '男性',
  FEMALE: '女性'
}

export const EMPLOYEE_CATEGORY = {
  JAPAN: '日本社員',
  CHINA_ASSIGN: '中国出張'
}

export const ROOM_TYPE = {
  STANDARD: '標準洋室',
  SMALL: '小部屋',
  OTHER: 'その他'
}

export const DORM_FEE_STATUS = {
  PROVISIONAL: '仮定',
  ERROR: 'エラー'
}

export const EQUIPMENT_DISPOSITION = {
  DISCARD: '廃棄',
  STORE: '保管',
  REUSE: '再利用'
}

export const STORAGE_STATUS = {
  IN_STORAGE: '保管中',
  REUSED: '再利用済'
}

export const VACANCY_STATUS = {
  VACANT: '空き',
  OCCUPIED: '入居中'
}

export const ROLE_ADMIN = 'ROLE_ADMIN'
export const ROLE_USER = 'ROLE_USER'

export const LAYOUT_TYPE_OPTIONS = ['3DK', '2DK', '1K', '1DK', 'OTHER']

export const REGION = {
  TOKYO: '東京',
  OSAKA: '大阪',
  NAGOYA: '名古屋',
  OTHER: 'その他'
}

export const REGION_OPTIONS = [
  { value: 'TOKYO', label: '東京' },
  { value: 'OSAKA', label: '大阪' },
  { value: 'NAGOYA', label: '名古屋' },
  { value: 'OTHER', label: 'その他' }
]

export const WEEKDAY_JA = ['日', '月', '火', '水', '木', '金', '土']

export const MOVE_OUT_WARNING_DAYS = 14

/**
 * @param {Record<string, string>} map
 * @param {string} code
 * @returns {string}
 */
export function labelOf(map, code) {
  return map[code] ?? code ?? ''
}
