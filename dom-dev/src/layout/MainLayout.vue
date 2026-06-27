<template>
  <div class="app-shell">
    <header class="site-header">
      <div class="header-inner">
        <router-link to="/allocation" class="brand">
          <span class="brand-mark" aria-hidden="true" />
          <div class="brand-text">
            <span class="brand-title">寮管理システム</span>
            <span class="brand-sub">Dormitory Management System</span>
          </div>
        </router-link>
        <div class="header-actions">
          <span class="user-label">{{ userStore.username }}</span>
          <button type="button" class="logout-btn" @click="handleLogout">ログアウト</button>
        </div>
      </div>
    </header>

    <div class="app-body">
      <aside class="side-nav">
        <div class="side-nav-toolbar">
          <button type="button" class="side-nav-toggle-btn" @click="openAllMenus">
            すべて開く
          </button>
          <button type="button" class="side-nav-toggle-btn" @click="closeAllMenus">
            すべて閉じる
          </button>
        </div>
        <div class="side-nav-scroll">
          <el-menu
            :key="menuRenderKey"
            :default-active="activeMenu"
            :default-openeds="openedMenus"
            router
            class="side-menu"
          >
            <el-menu-item
              v-for="item in visibleMenuItems"
              :key="item.path"
              :index="item.path"
            >
              <el-icon v-if="item.icon">
                <component :is="item.icon" />
              </el-icon>
              <span>{{ item.title }}</span>
            </el-menu-item>
            <el-sub-menu
              v-for="group in visibleMenuGroups"
              :key="group.index"
              :index="group.index"
            >
              <template #title>
                <el-icon v-if="group.icon">
                  <component :is="group.icon" />
                </el-icon>
                <span>{{ group.title }}</span>
              </template>
              <el-menu-item
                v-for="item in group.children"
                :key="item.path"
                :index="item.path"
              >
                {{ item.title }}
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </div>
      </aside>

      <div class="content-area">
        <div class="main-content-scroll">
          <div class="main-content-inner">
            <main class="main-content">
              <router-view />
            </main>
            <footer class="site-footer">
              <p>Copyright &copy; 2009-2026 Co-creative Network Corporation. All rights reserved.</p>
            </footer>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { menuGroups, menuItems } from '@/router/menus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

function hasMenuRole(roles) {
  if (!roles?.length) return true
  return roles.some((role) => userStore.roles.includes(role))
}

function isMenuItemActive(itemPath, currentPath) {
  if (currentPath === itemPath) return true
  if (itemPath === '/residences' && currentPath.startsWith('/residences/register')) {
    return false
  }
  return currentPath.startsWith(`${itemPath}/`)
}

const visibleMenuItems = computed(() =>
  menuItems.filter((item) => hasMenuRole(item.meta?.roles))
)

const visibleMenuGroups = computed(() =>
  menuGroups
    .map((group) => ({
      ...group,
      children: group.children.filter((item) => hasMenuRole(item.meta?.roles))
    }))
    .filter((group) => group.children.length > 0)
)

const menuRenderKey = ref(0)
const openedMenus = ref([])

function getActiveGroupIndexes() {
  const currentPath = route.path
  return visibleMenuGroups.value
    .filter((group) =>
      group.children.some((item) => isMenuItemActive(item.path, currentPath))
    )
    .map((group) => group.index)
}

function getAllGroupIndexes() {
  return visibleMenuGroups.value.map((group) => group.index)
}

function initOpenedMenus() {
  const active = getActiveGroupIndexes()
  openedMenus.value = active.length ? active : getAllGroupIndexes()
}

function applyOpenedMenus(nextOpeneds) {
  openedMenus.value = nextOpeneds
  menuRenderKey.value += 1
}

function openAllMenus() {
  applyOpenedMenus(getAllGroupIndexes())
}

function closeAllMenus() {
  applyOpenedMenus([])
}

watch(visibleMenuGroups, () => {
  if (!openedMenus.value.length) {
    initOpenedMenus()
    menuRenderKey.value += 1
  }
}, { immediate: true })

watch(
  () => route.path,
  () => {
    const active = getActiveGroupIndexes()
    if (!active.length) return
    const merged = [...new Set([...openedMenus.value, ...active])]
    if (merged.length !== openedMenus.value.length) {
      applyOpenedMenus(merged)
    }
  }
)

function handleLogout() {
  userStore.logout()
  router.push({ name: 'Login' })
}
</script>

<style scoped>
.app-shell {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-width: 1280px;
  overflow: hidden;
  background: var(--cnc-bg-page);
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  flex-shrink: 0;
  width: 100%;
  background: var(--cnc-bg-white);
  border-bottom: 3px solid var(--cnc-primary);
  box-shadow: 0 1px 0 rgba(13, 93, 169, 0.08);
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 24px;
  max-width: 1600px;
  margin: 0 auto;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  color: inherit;
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
  letter-spacing: 0.02em;
  line-height: 1.2;
}

.brand-sub {
  font-family: var(--cnc-font-en);
  font-size: 11px;
  font-weight: 500;
  color: var(--cnc-primary);
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-label {
  font-size: 14px;
  color: var(--cnc-text-secondary);
}

.logout-btn {
  padding: 8px 20px;
  font-size: 13px;
  font-weight: 500;
  color: var(--cnc-primary);
  background: var(--cnc-bg-white);
  border: 1px solid var(--cnc-primary);
  border-radius: 2px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.logout-btn:hover {
  background: var(--cnc-primary);
  color: #fff;
}

.app-body {
  display: flex;
  flex: 1;
  min-height: 0;
  max-width: 1600px;
  width: 100%;
  margin: 0 auto;
  overflow: hidden;
}

.side-nav {
  flex-shrink: 0;
  width: 232px;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--cnc-bg-white);
  border-right: 1px solid var(--cnc-border);
  padding-top: 12px;
}

.side-nav-toolbar {
  flex-shrink: 0;
  display: flex;
  gap: 6px;
  padding: 0 12px 10px;
  border-bottom: 1px solid var(--cnc-border);
  margin-bottom: 8px;
}

.side-nav-scroll {
  flex: 1;
  min-height: 0;
  overflow-x: hidden;
  overflow-y: scroll;
  scrollbar-gutter: stable;
  padding-bottom: 16px;
}

.side-nav-toggle-btn {
  flex: 1;
  padding: 5px 4px;
  font-size: 11px;
  font-weight: 500;
  color: var(--cnc-text-secondary);
  background: var(--cnc-bg-white);
  border: 1px solid var(--cnc-border);
  border-radius: 2px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s, border-color 0.2s;
}

.side-nav-toggle-btn:hover {
  color: var(--cnc-primary);
  border-color: var(--cnc-primary);
  background: var(--cnc-primary-light);
}

.side-menu {
  border-right: none;
}

.content-area {
  flex: 1;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
}

.main-content-scroll {
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
}

.main-content-inner {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.main-content {
  flex: 1 0 auto;
  padding: 0;
  background: var(--cnc-bg-page);
}

.site-footer {
  flex-shrink: 0;
  padding: 16px 24px;
  background: var(--cnc-navy);
  text-align: center;
}

.site-footer p {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
  letter-spacing: 0.02em;
}
</style>

<style>
.side-menu.el-menu {
  background: transparent;
}

.side-menu .el-sub-menu__title {
  height: 44px;
  line-height: 44px;
  margin: 2px 12px;
  border-radius: 2px;
  color: var(--cnc-text-primary);
  font-size: 14px;
  font-weight: 600;
}

.side-menu .el-sub-menu__title:hover {
  background: var(--cnc-primary-light);
  color: var(--cnc-primary);
}

.side-menu .el-sub-menu.is-active > .el-sub-menu__title {
  color: var(--cnc-primary);
}

.side-menu > .el-menu-item {
  height: 44px;
  line-height: 44px;
  margin: 2px 12px;
  border-radius: 2px;
  color: var(--cnc-text-secondary);
  font-size: 14px;
}

.side-menu .el-sub-menu .el-menu-item {
  height: 40px;
  line-height: 40px;
  min-width: auto;
  margin: 2px 12px 2px 20px;
  border-radius: 2px;
  color: var(--cnc-text-secondary);
  font-size: 13px;
}

.side-menu .el-menu-item:hover {
  background: var(--cnc-primary-light);
  color: var(--cnc-primary);
}

.side-menu > .el-menu-item.is-active {
  background: var(--cnc-primary-light);
  color: var(--cnc-primary);
  font-weight: 600;
  border-left: 3px solid var(--cnc-primary);
  padding-left: calc(var(--el-menu-base-level-padding) - 3px);
}

.side-menu .el-sub-menu .el-menu-item.is-active {
  background: var(--cnc-primary-light);
  color: var(--cnc-primary);
  font-weight: 600;
  border-left: 3px solid var(--cnc-primary);
  padding-left: calc(var(--el-menu-base-level-padding) + 16px - 3px);
}

.side-menu .el-sub-menu__title .el-icon,
.side-menu .el-menu-item .el-icon {
  color: inherit;
}
</style>
