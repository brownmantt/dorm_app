import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error.response?.status
    const data = error.response?.data

    if (status === 401) {
      ElMessage.error('認証の有効期限が切れました。再度ログインしてください。')
      const userStore = useUserStore()
      userStore.logout()
      router.push({ name: 'Login' })
      return Promise.reject(error)
    }

    const authBypass = import.meta.env.VITE_AUTH_BYPASS === 'true'

    // 開発バイパス中はバックエンド未接続によるネットワークエラー通知を抑制
    if (authBypass && !error.response) {
      return Promise.reject(error)
    }

    const message =
      data?.detail ||
      data?.title ||
      data?.message ||
      error.message ||
      'リクエストに失敗しました'

    if (error.config?.skipErrorHandler) {
      return Promise.reject(error)
    }

    if (status === 403) {
      ElMessage.error('この操作を行う権限がありません。')
    } else if (status !== 401) {
      ElMessage.error(message)
    }

    return Promise.reject(error)
  }
)

export default service
