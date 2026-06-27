import { createRouter, createWebHistory } from 'vue-router'

import { useUserStore } from '@/store/user'

import { ROLE_ADMIN } from '@/utils/constants'

import MainLayout from '@/layout/MainLayout.vue'



const router = createRouter({

  history: createWebHistory(),

  routes: [

    {

      path: '/login',

      name: 'Login',

      component: () => import('@/views/login/Login.vue'),

      meta: { title: 'ログイン', public: true }

    },

    {

      path: '/',

      component: MainLayout,

      redirect: '/allocation',

      children: [

        {

          path: 'allocation',

          name: 'DormAllocationCalendar',

          component: () => import('@/views/allocation/DormAllocationCalendar.vue'),

          meta: { title: '寮割カレンダー', roles: ['ROLE_ADMIN', 'ROLE_USER'] }

        },

        {

          path: 'dormitories',

          name: 'DormitoryList',

          component: () => import('@/views/dormitory/DormitoryList.vue'),

          meta: { title: '寮一覧', roles: ['ROLE_ADMIN', 'ROLE_USER'] }

        },

        {

          path: 'dormitories/:id',

          name: 'DormitoryDetail',

          component: () => import('@/views/dormitory/DormitoryDetail.vue'),

          meta: { title: '寮詳細', roles: ['ROLE_ADMIN', 'ROLE_USER'] }

        },

        {

          path: 'dormitories/:dormId/rooms/:roomId/edit',

          name: 'RoomEdit',

          component: () => import('@/views/dormitory/RoomEdit.vue'),

          meta: { title: '部屋編集', roles: [ROLE_ADMIN] }

        },

        {

          path: 'residences',

          name: 'ResidenceHistoryList',

          component: () => import('@/views/residence/ResidenceHistoryList.vue'),

          meta: { title: '入居履歴一覧', roles: ['ROLE_ADMIN', 'ROLE_USER'] }

        },

        {

          path: 'residences/register',

          name: 'ResidenceRegister',

          component: () => import('@/views/residence/ResidenceRegister.vue'),

          meta: { title: '入居登録・退居', roles: [ROLE_ADMIN] }

        },

        {

          path: 'first-use-long-term',

          name: 'FirstUseLongTerm',

          component: () => import('@/views/residence/FirstUseLongTerm.vue'),

          meta: { title: '初回利用日・長期利用', roles: ['ROLE_ADMIN', 'ROLE_USER'] }

        },

        {

          path: 'dorm-fees',

          name: 'DormFeeList',

          component: () => import('@/views/dormFee/DormFeeList.vue'),

          meta: { title: '寮費一覧・算定', roles: [ROLE_ADMIN] }

        },

        {

          path: 'affiliations',

          name: 'AffiliationList',

          component: () => import('@/views/affiliation/AffiliationList.vue'),

          meta: { title: '所属マスタ', roles: [ROLE_ADMIN] }

        },

        {

          path: 'regions',

          name: 'RegionList',

          component: () => import('@/views/region/RegionList.vue'),

          meta: { title: '地域マスタ', roles: [ROLE_ADMIN] }

        },

        {

          path: 'usage-types',

          name: 'UsageTypeList',

          component: () => import('@/views/usageType/UsageTypeList.vue'),

          meta: { title: '利用形態マスタ', roles: [ROLE_ADMIN] }

        },

        {

          path: 'unit-prices',

          name: 'UnitPriceList',

          component: () => import('@/views/unitPrice/UnitPriceList.vue'),

          meta: { title: '単価マスタ', roles: [ROLE_ADMIN] }

        },

        {

          path: 'employees',

          name: 'EmployeeList',

          component: () => import('@/views/employee/EmployeeList.vue'),

          meta: { title: '社員マスタ', roles: [ROLE_ADMIN] }

        },

        {

          path: 'equipments',

          name: 'EquipmentMasterList',

          component: () => import('@/views/equipment/EquipmentMasterList.vue'),

          meta: { title: '備品マスタ', roles: [ROLE_ADMIN] }

        },

        {

          path: 'equipment-moveouts',

          name: 'MoveOutEquipment',

          component: () => import('@/views/equipment/MoveOutEquipment.vue'),

          meta: { title: '退去備品処理', roles: [ROLE_ADMIN] }

        },

        {

          path: 'equipment-storages',

          name: 'EquipmentStorage',

          component: () => import('@/views/equipment/EquipmentStorage.vue'),

          meta: { title: '備品保管', roles: [ROLE_ADMIN] }

        },

        {

          path: 'vacancies',

          name: 'VacancyList',

          component: () => import('@/views/vacancy/VacancyList.vue'),

          meta: { title: '空き室一覧', roles: ['ROLE_ADMIN', 'ROLE_USER'] }

        },

        {

          path: 'import',

          name: 'ExcelImport',

          component: () => import('@/views/import/ExcelImport.vue'),

          meta: { title: 'Excel/CSV取込', roles: [ROLE_ADMIN] }

        },

        {

          path: 'operation-logs',

          name: 'OperationLogList',

          component: () => import('@/views/log/OperationLogList.vue'),

          meta: { title: '操作ログ', roles: [ROLE_ADMIN] }

        },

        {

          path: '403',

          name: 'Forbidden',

          component: () => import('@/views/error/Forbidden.vue'),

          meta: { title: '権限エラー', public: true }

        }

      ]

    }

  ]

})



router.beforeEach((to, _from, next) => {

  const userStore = useUserStore()



  if (to.meta.public || to.name === 'Login') {

    if (to.name === 'Login' && userStore.isLoggedIn) {

      next({ path: '/allocation' })

      return

    }

    next()

    return

  }



  if (!userStore.isLoggedIn) {

    next({ name: 'Login', query: { redirect: to.fullPath } })

    return

  }



  const roles = to.meta.roles

  if (roles && !roles.some((role) => userStore.roles.includes(role))) {

    next({ name: 'Forbidden' })

    return

  }



  next()

})



export default router


