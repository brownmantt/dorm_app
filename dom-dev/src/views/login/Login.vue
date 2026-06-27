<template>
  <div class="login-page">
    <header class="login-header">
      <div class="login-header-inner">
        <span class="brand-mark" aria-hidden="true" />
        <div class="brand-text">
          <span class="brand-title">寮管理システム</span>
          <span class="brand-sub">Dormitory Management System</span>
        </div>
      </div>
    </header>

    <div class="login-body">
      <div class="login-card">
        <div class="login-card-head">
          <p class="login-card-en">LOGIN</p>
          <h1 class="login-title">ログイン</h1>
          <p class="login-desc">システムをご利用になるには認証情報を入力してください。</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
          <el-form-item label="ユーザー名" prop="username">
            <el-input v-model="form.username" autocomplete="username" size="large" />
          </el-form-item>
          <el-form-item label="パスワード" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              autocomplete="current-password"
              show-password
              size="large"
            />
          </el-form-item>
          <el-form-item v-if="authBypass" label="ロール">
            <el-radio-group v-model="form.role">
              <el-radio-button label="ROLE_ADMIN">管理者</el-radio-button>
              <el-radio-button label="ROLE_USER">一般</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
              ログイン
            </el-button>
          </el-form-item>
        </el-form>

        <el-alert
          v-if="authBypass"
          type="warning"
          :closable="false"
          title="開発モード：認証バイパス中"
          description="バックエンド未接続のため、任意の入力でログインできます。"
          show-icon
        />
      </div>
    </div>

    <footer class="login-footer">
      <p>Copyright &copy; 2009-2026 Co-creative Network Corporation. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const authBypass = import.meta.env.VITE_AUTH_BYPASS === 'true'

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  role: 'ROLE_ADMIN'
})

const rules = {
  username: [{ required: true, message: 'ユーザー名を入力してください', trigger: 'blur' }],
  password: [{ required: true, message: 'パスワードを入力してください', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    if (authBypass) {
      const roles = form.role === 'ROLE_ADMIN' ? ['ROLE_ADMIN', 'ROLE_USER'] : ['ROLE_USER']
      userStore.setSession({
        token: 'dev-bypass-token',
        username: form.username,
        roles
      })
      ElMessage.success('ログインしました（開発モード）')
      router.push(route.query.redirect || '/allocation')
      return
    }

    const data = await login(form)
    userStore.setSession({
      token: data.token,
      username: data.username || form.username,
      roles: data.roles || []
    })
    ElMessage.success('ログインしました')
    router.push(route.query.redirect || '/allocation')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: var(--cnc-bg-page);
}

.login-header {
  background: var(--cnc-bg-white);
  border-bottom: 3px solid var(--cnc-primary);
}

.login-header-inner {
  display: flex;
  align-items: center;
  gap: 12px;
  max-width: 480px;
  margin: 0 auto;
  padding: 20px 24px;
  width: 100%;
}

.brand-mark {
  width: 36px;
  height: 36px;
  background: var(--cnc-primary);
  border-radius: 2px;
  position: relative;
  flex-shrink: 0;
}

.brand-mark::after {
  content: '';
  position: absolute;
  right: 0;
  bottom: 0;
  width: 12px;
  height: 12px;
  background: var(--cnc-accent);
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--cnc-text-primary);
}

.brand-sub {
  font-family: var(--cnc-font-en);
  font-size: 11px;
  font-weight: 500;
  color: var(--cnc-primary);
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.login-body {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
}

.login-card {
  width: 100%;
  max-width: 440px;
  padding: 36px 40px 32px;
  background: var(--cnc-bg-white);
  border: 1px solid var(--cnc-border);
  border-top: 4px solid var(--cnc-primary);
  border-radius: 2px;
}

.login-card-head {
  margin-bottom: 28px;
}

.login-card-en {
  margin: 0 0 6px;
  font-family: var(--cnc-font-en);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.14em;
  color: var(--cnc-primary);
}

.login-title {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 700;
  color: var(--cnc-text-primary);
}

.login-desc {
  margin: 0;
  font-size: 13px;
  color: var(--cnc-text-secondary);
  line-height: 1.6;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
  margin-top: 8px;
}

.login-footer {
  padding: 16px;
  background: var(--cnc-navy);
  text-align: center;
}

.login-footer p {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
}
</style>
