import {
  Box,
  Calendar,
  Collection,
  Money,
  OfficeBuilding,
  Setting,
  User
} from '@element-plus/icons-vue'
import { ROLE_ADMIN, ROLE_USER } from '@/utils/constants'

/**
 * 1階層目メニュー（グループなし）
 * @type {Array<{ path: string, title: string, icon: import('vue').Component, meta?: { roles?: string[] } }>}
 */
export const menuItems = [
  { path: '/allocation', title: '寮割カレンダー', icon: Calendar, meta: { roles: [ROLE_ADMIN, ROLE_USER] } }
]

/**
 * 階層メニューグループ
 * @type {Array<{ index: string, title: string, icon: import('vue').Component, children: Array<{ path: string, title: string, meta?: { roles?: string[] } }> }>}
 */
export const menuGroups = [
  {
    index: 'dormitory',
    title: '寮・部屋',
    icon: OfficeBuilding,
    children: [
      { path: '/dormitories', title: '寮一覧', meta: { roles: [ROLE_ADMIN, ROLE_USER] } },
      { path: '/vacancies', title: '空き室一覧', meta: { roles: [ROLE_ADMIN, ROLE_USER] } }
    ]
  },
  {
    index: 'residence',
    title: '入居管理',
    icon: User,
    children: [
      { path: '/residences/register', title: '入居登録・退居', meta: { roles: [ROLE_ADMIN] } },
      { path: '/residences', title: '入居履歴', meta: { roles: [ROLE_ADMIN, ROLE_USER] } },
      { path: '/first-use-long-term', title: '初回利用日・長期利用', meta: { roles: [ROLE_ADMIN, ROLE_USER] } }
    ]
  },
  {
    index: 'dorm-fee',
    title: '寮費',
    icon: Money,
    children: [
      { path: '/dorm-fees', title: '寮費一覧・算定', meta: { roles: [ROLE_ADMIN] } }
    ]
  },
  {
    index: 'master',
    title: 'マスタ',
    icon: Collection,
    children: [
      { path: '/affiliations', title: '所属マスタ', meta: { roles: [ROLE_ADMIN] } },
      { path: '/regions', title: '地域マスタ', meta: { roles: [ROLE_ADMIN] } },
      { path: '/usage-types', title: '利用形態マスタ', meta: { roles: [ROLE_ADMIN] } },
      { path: '/unit-prices', title: '単価マスタ', meta: { roles: [ROLE_ADMIN] } },
      { path: '/employees', title: '社員マスタ', meta: { roles: [ROLE_ADMIN] } },
      { path: '/equipments', title: '品目マスタ', meta: { roles: [ROLE_ADMIN] } }
    ]
  },
  {
    index: 'equipment',
    title: '備品',
    icon: Box,
    children: [
      { path: '/equipment-assets', title: '備品管理', meta: { roles: [ROLE_ADMIN] } },
      { path: '/equipment-moveouts', title: '退去備品処理', meta: { roles: [ROLE_ADMIN] } },
      { path: '/equipment-storages', title: '備品保管', meta: { roles: [ROLE_ADMIN] } }
    ]
  },
  {
    index: 'system',
    title: 'データ・ログ',
    icon: Setting,
    children: [
      { path: '/import', title: 'Excel/CSV取込', meta: { roles: [ROLE_ADMIN] } },
      { path: '/operation-logs', title: '操作ログ', meta: { roles: [ROLE_ADMIN] } }
    ]
  }
]

/** @deprecated 互換用フラット一覧 */
export const menuRoutes = [
  ...menuItems,
  ...menuGroups.flatMap((group) =>
    group.children.map((item) => ({
      path: item.path,
      title: item.title,
      icon: group.icon,
      meta: item.meta
    }))
  )
]
