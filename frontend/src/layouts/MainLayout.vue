<template>
  <n-layout has-sider class="main-layout">
    <!-- 侧边栏 - 深色背景 256px -->
    <n-layout-sider
      v-if="!appStore.isMobile"
      collapse-mode="width"
      :collapsed-width="72"
      :width="256"
      :collapsed="appStore.sidebarCollapsed"
      show-trigger
      @collapse="appStore.setSidebarCollapsed(true)"
      @expand="appStore.setSidebarCollapsed(false)"
      class="layout-sider"
    >
      <!-- Logo区域 -->
      <div class="logo-section">
        <div v-if="!appStore.sidebarCollapsed" class="logo-full">
          <div class="logo-icon">
            <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="40" height="40" rx="8" fill="#2563eb"/>
              <path d="M12 20C12 15.5817 15.5817 12 20 12V12C24.4183 12 28 15.5817 28 20V28H20C15.5817 28 12 24.4183 12 20V20Z" fill="white" fill-opacity="0.9"/>
              <circle cx="20" cy="20" r="4" fill="#2563eb"/>
            </svg>
          </div>
          <div class="logo-text">
            <span class="logo-name">CRM Pro</span>
            <span class="logo-version">Enterprise</span>
          </div>
        </div>
        <div v-else class="logo-collapsed">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="40" height="40" rx="8" fill="#2563eb"/>
            <path d="M12 20C12 15.5817 15.5817 12 20 12V12C24.4183 12 28 15.5817 28 20V28H20C15.5817 28 12 24.4183 12 20V20Z" fill="white" fill-opacity="0.9"/>
            <circle cx="20" cy="20" r="4" fill="#2563eb"/>
          </svg>
        </div>
      </div>

      <!-- 导航菜单 -->
      <div class="nav-section">
        <n-menu
          :collapsed="appStore.sidebarCollapsed"
          :collapsed-width="72"
          :collapsed-icon-size="22"
          :options="menuOptions"
          :value="activeKey"
          :indent="24"
          @update:value="handleMenuSelect"
          class="sidebar-menu"
        />
      </div>

      <!-- 底部用户信息 -->
      <div class="sidebar-footer">
        <n-dropdown :options="userMenuOptions" @select="handleUserMenuSelect" placement="top-start">
          <div class="user-card" :class="{ collapsed: appStore.sidebarCollapsed }">
            <n-avatar
              round
              :size="appStore.sidebarCollapsed ? 36 : 40"
              :src="userStore.avatar"
              :fallback-src="defaultAvatar"
              class="user-avatar"
            />
            <div v-if="!appStore.sidebarCollapsed" class="user-details">
              <span class="user-name">{{ userStore.realName || userStore.username }}</span>
              <span class="user-role">{{ userStore.roles?.[0] || '用户' }}</span>
            </div>
            <n-icon v-if="!appStore.sidebarCollapsed" class="user-more">
              <EllipsisVertical />
            </n-icon>
          </div>
        </n-dropdown>
      </div>
    </n-layout-sider>

    <!-- 移动端侧边栏抽屉 -->
    <n-drawer
      v-model:show="appStore.sidebarMobileVisible"
      :width="280"
      placement="left"
      class="mobile-drawer"
      :native-scrollbar="false"
    >
      <div class="mobile-drawer-content">
        <!-- Logo -->
        <div class="logo-section mobile">
          <div class="logo-full">
            <div class="logo-icon">
              <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect width="40" height="40" rx="8" fill="#2563eb"/>
                <path d="M12 20C12 15.5817 15.5817 12 20 12V12C24.4183 12 28 15.5817 28 20V28H20C15.5817 28 12 24.4183 12 20V20Z" fill="white" fill-opacity="0.9"/>
                <circle cx="20" cy="20" r="4" fill="#2563eb"/>
              </svg>
            </div>
            <div class="logo-text">
              <span class="logo-name">CRM Pro</span>
              <span class="logo-version">Enterprise</span>
            </div>
          </div>
        </div>

        <!-- 菜单 -->
        <n-menu
          :options="menuOptions"
          :value="activeKey"
          :indent="24"
          @update:value="handleMobileMenuSelect"
          class="sidebar-menu mobile"
        />
      </div>
    </n-drawer>

    <!-- 主内容区 -->
    <n-layout class="main-content-layout">
      <!-- 顶栏 -->
      <n-layout-header class="layout-header">
        <div class="header-left">
          <!-- 移动端菜单按钮 -->
          <button
            v-if="appStore.isMobile"
            class="menu-trigger"
            @click="appStore.toggleMobileSidebar"
          >
            <n-icon size="22">
              <MenuOutline />
            </n-icon>
          </button>

          <!-- 面包屑导航 -->
          <nav v-if="!appStore.isMobile" class="breadcrumb-nav">
            <template v-for="(item, index) in appStore.breadcrumbs" :key="index">
              <span
                class="breadcrumb-item"
                :class="{ clickable: !!item.path, active: index === appStore.breadcrumbs.length - 1 }"
                @click="item.path && router.push(item.path)"
              >
                {{ item.title }}
              </span>
              <span v-if="index < appStore.breadcrumbs.length - 1" class="breadcrumb-separator">/</span>
            </template>
          </nav>
        </div>

        <div class="header-right">
          <!-- 搜索按钮 -->
          <button class="header-btn" title="搜索">
            <n-icon size="20">
              <SearchOutline />
            </n-icon>
          </button>

          <!-- 通知按钮 -->
          <button class="header-btn" title="通知">
            <n-badge :value="3" :max="99">
              <n-icon size="20">
                <NotificationsOutline />
              </n-icon>
            </n-badge>
          </button>
        </div>
      </n-layout-header>

      <!-- 内容区 -->
      <n-layout-content class="layout-content" :native-scrollbar="false">
        <div class="content-wrapper">
          <router-view v-slot="{ Component, route }">
            <transition name="page-fade" mode="out-in">
              <keep-alive :include="cachedViews">
                <component :is="Component" :key="route.path" />
              </keep-alive>
            </transition>
          </router-view>
        </div>
      </n-layout-content>
    </n-layout>

    <!-- AI 助手 -->
    <AiAssistantDialog />
  </n-layout>
</template>

<script setup lang="ts">
import { computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  NLayout,
  NLayoutSider,
  NLayoutHeader,
  NLayoutContent,
  NMenu,
  NButton,
  NIcon,
  NAvatar,
  NDropdown,
  NDrawer,
  NBadge,
  type MenuOption
} from 'naive-ui'
import {
  MenuOutline,
  SearchOutline,
  NotificationsOutline,
  HomeOutline,
  PersonOutline,
  PeopleOutline,
  PersonAddOutline,
  TrendingUpOutline,
  CubeOutline,
  DocumentTextOutline,
  WalletOutline,
  SettingsOutline,
  LogOutOutline,
  PersonCircleOutline,
  EllipsisVertical
} from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import AiAssistantDialog from '@/components/ai/AiAssistantDialog.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSI+PHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiByeD0iMjAiIGZpbGw9IiM2NDc0OGIiLz48cGF0aCBkPSJNMjAgMjJjMy4zMTQgMCA2LTIuNjg2IDYtNnMtMi42ODYtNi02LTYtNiAyLjY4Ni02IDYgMi42ODYgNiA2IDZ6bTAgMmMtNC40MTggMC04IDMuNTgyLTggOHYyaDE2di0yYzAtNC40MTgtMy41ODItOC04LTh6IiBmaWxsPSJ3aGl0ZSIgZmlsbC1vcGFjaXR5PSIwLjgiLz48L3N2Zz4='

const activeKey = computed(() => route.path)

const cachedViews = computed(() => {
  const views: string[] = []
  router.getRoutes().forEach((r) => {
    if (r.meta.keepAlive && r.name) {
      views.push(r.name as string)
    }
  })
  return views
})

function renderIcon(icon: any) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions = computed<MenuOption[]>(() => {
  const options: MenuOption[] = [
    {
      label: '仪表盘',
      key: '/dashboard',
      icon: renderIcon(HomeOutline)
    },
    {
      label: '线索管理',
      key: '/leads',
      icon: renderIcon(PersonOutline),
      show: userStore.hasPermission('lead:view')
    },
    {
      label: '客户管理',
      key: '/customers',
      icon: renderIcon(PeopleOutline),
      show: userStore.hasPermission('customer:view')
    },
    {
      label: '联系人管理',
      key: '/contacts',
      icon: renderIcon(PersonAddOutline),
      show: userStore.hasPermission('contact:view')
    },
    {
      label: '商机管理',
      key: '/opportunities',
      icon: renderIcon(TrendingUpOutline),
      show: userStore.hasPermission('opportunity:view')
    },
    {
      label: '产品管理',
      key: '/products',
      icon: renderIcon(CubeOutline),
      show: userStore.hasPermission('product:view')
    },
    {
      label: '合同管理',
      key: '/contracts',
      icon: renderIcon(DocumentTextOutline),
      show: userStore.hasPermission('contract:view')
    },
    {
      label: '回款管理',
      key: '/payments',
      icon: renderIcon(WalletOutline),
      show: userStore.hasPermission('payment:view')
    },
    {
      label: '系统管理',
      key: '/system',
      icon: renderIcon(SettingsOutline),
      show: userStore.hasRole('admin'),
      children: [
        {
          label: '用户管理',
          key: '/system/user',
          show: userStore.hasPermission('system:user:view')
        },
        {
          label: '角色管理',
          key: '/system/role',
          show: userStore.hasPermission('system:role:view')
        },
        {
          label: '部门管理',
          key: '/system/department',
          show: userStore.hasPermission('system:dept:view')
        }
      ]
    }
  ]

  return options.filter((item) => item.show !== false)
})

const userMenuOptions = computed(() => [
  {
    label: '个人中心',
    key: 'profile',
    icon: renderIcon(PersonCircleOutline)
  },
  {
    type: 'divider',
    key: 'divider'
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: renderIcon(LogOutOutline)
  }
])

function handleMenuSelect(key: string) {
  router.push(key)
}

function handleMobileMenuSelect(key: string) {
  router.push(key)
  appStore.setSidebarMobileVisible(false)
}

async function handleUserMenuSelect(key: string) {
  if (key === 'profile') {
    router.push('/profile')
  } else if (key === 'logout') {
    await userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
/* ========================================
   企业级CRM主布局样式
   深色侧边栏 + 浅色内容区
   ======================================== */

.main-layout {
  height: 100vh;
  background: #f8fafc;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

/* ========================================
   侧边栏 - 深色背景（图7风格）
   ======================================== */
.layout-sider {
  background: #0f172a !important;
  border-right: none !important;
  display: flex;
  flex-direction: column;
}

.layout-sider :deep(.n-layout-sider-scroll-container) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

/* Logo区域 */
.logo-section {
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-section.mobile {
  padding: 24px;
  background: #0f172a;
}

.logo-full {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon svg {
  width: 40px;
  height: 40px;
}

.logo-collapsed svg {
  width: 36px;
  height: 36px;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-name {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
  letter-spacing: -0.01em;
}

.logo-version {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
  letter-spacing: 0.02em;
}

.logo-collapsed {
  display: flex;
  justify-content: center;
}

/* 导航区域 */
.nav-section {
  flex: 1;
  padding: 12px 16px;
  overflow-y: auto;
}

/* 侧边栏菜单样式 - 深色主题 */
.sidebar-menu {
  background: transparent !important;
}

.sidebar-menu :deep(.n-menu-item) {
  margin-bottom: 4px;
  border-radius: 10px;
  transition: all 0.2s ease;
  height: 44px;
}

.sidebar-menu :deep(.n-menu-item-content) {
  color: #ffffff !important;
  font-weight: 600;
  font-size: 14px;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
  padding: 0 16px !important;
  height: 44px;
}

.sidebar-menu :deep(.n-menu-item-content-header) {
  color: #ffffff !important;
}

.sidebar-menu :deep(.n-menu-item-content-header a) {
  color: #ffffff !important;
}

.sidebar-menu :deep(.n-menu-item:hover) {
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.15) 0%, rgba(37, 99, 235, 0.08) 100%) !important;
  border-left: 3px solid #3b82f6;
  margin-left: -3px;
}

.sidebar-menu :deep(.n-menu-item:hover .n-menu-item-content) {
  color: #93c5fd !important;
  text-shadow: 0 0 8px rgba(147, 197, 253, 0.3);
}

.sidebar-menu :deep(.n-menu-item:hover .n-menu-item-content-header) {
  color: #000 !important;
  text-shadow: 0 0 8px rgba(147, 197, 253, 0.3);
}

/* 选中状态 - 蓝色背景圆角 */
.sidebar-menu :deep(.n-menu-item-content--selected) {
  background: #2563eb !important;
  color: #ffffff !important;
  border-radius: 8px;
  font-weight: 600;
}

.sidebar-menu :deep(.n-menu-item-content--child-active) {
  background: transparent !important;
  color: #60a5fa !important;
}

.sidebar-menu :deep(.n-menu-item-content--selected::before) {
  display: none;
}

.sidebar-menu :deep(.n-menu-item-content__icon) {
  color: inherit !important;
  font-size: 18px;
}

/* 子菜单样式 - 参考图7风格 */
.sidebar-menu :deep(.n-submenu-children) {
  background: transparent !important;
  margin: 0;
  padding: 4px 0;
}

.sidebar-menu :deep(.n-submenu-children .n-menu-item) {
  height: 40px;
  margin: 2px 0;
  border-radius: 8px;
}

.sidebar-menu :deep(.n-submenu-children .n-menu-item-content) {
  color: #ffffff !important;
  font-weight: 600;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
  padding-left: 48px !important;
  font-size: 14px;
  height: 40px;
}

.sidebar-menu :deep(.n-submenu-children .n-menu-item-content-header) {
  color: #ffffff !important;
}

.sidebar-menu :deep(.n-submenu-children .n-menu-item-content-header a) {
  color: #ffffff !important;
}

.sidebar-menu :deep(.n-submenu-children .n-menu-item:hover) {
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.15) 0%, rgba(37, 99, 235, 0.08) 100%) !important;
  border-left: 3px solid #3b82f6;
  margin-left: -3px;
}

.sidebar-menu :deep(.n-submenu-children .n-menu-item:hover .n-menu-item-content) {
  color: #93c5fd !important;
  text-shadow: 0 0 8px rgba(147, 197, 253, 0.3);
}

.sidebar-menu :deep(.n-submenu-children .n-menu-item-content--selected) {
  color: #ffffff !important;
  background: #2563eb !important;
  border-radius: 8px;
}

/* 展开箭头 */
.sidebar-menu :deep(.n-menu-item-content__arrow) {
  color: rgba(255, 255, 255, 0.5) !important;
  font-size: 14px;
  transition: transform 0.2s ease;
}

.sidebar-menu :deep(.n-submenu.n-submenu--show-arrow .n-menu-item-content__arrow) {
  color: #ffffff !important;
}

.sidebar-menu :deep(.n-menu-item-group-title) {
  color: rgba(255, 255, 255, 0.35) !important;
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 16px 16px 8px;
}

/* 底部用户卡片 - 深色主题 */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  margin-top: auto;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.03);
}

.user-card:hover {
  background: rgba(255, 255, 255, 0.08);
}

.user-card.collapsed {
  justify-content: center;
  padding: 8px;
}

.user-avatar {
  flex-shrink: 0;
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.user-details {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
}

.user-more {
  color: rgba(255, 255, 255, 0.4);
  flex-shrink: 0;
}

/* ========================================
   主内容区
   ======================================== */
.main-content-layout {
  background: #f8fafc;
}

/* 顶栏 */
.layout-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: white;
  border-bottom: 1px solid #e2e8f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 8px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.menu-trigger:hover {
  background: #f1f5f9;
  color: #0f172a;
}

/* 面包屑 */
.breadcrumb-nav {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.breadcrumb-item {
  color: #64748b;
  transition: color 0.2s ease;
}

.breadcrumb-item.clickable {
  cursor: pointer;
}

.breadcrumb-item.clickable:hover {
  color: #2563eb;
}

.breadcrumb-item.active {
  color: #0f172a;
  font-weight: 500;
}

.breadcrumb-separator {
  color: #cbd5e1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 8px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.header-btn:hover {
  background: #f1f5f9;
  color: #0f172a;
}

/* 内容区 */
.layout-content {
  height: calc(100vh - 64px);
  background: #f8fafc;
}

.content-wrapper {
  padding: 24px;
  min-height: 100%;
}

/* ========================================
   移动端抽屉 - 深色主题
   ======================================== */
.mobile-drawer :deep(.n-drawer-body-content-wrapper) {
  padding: 0 !important;
}

.mobile-drawer-content {
  height: 100%;
  background: #0f172a;
  display: flex;
  flex-direction: column;
}

.sidebar-menu.mobile {
  flex: 1;
  padding: 12px 16px;
}

/* ========================================
   页面过渡动画
   ======================================== */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* ========================================
   折叠触发器样式 - 深色主题
   ======================================== */
.layout-sider :deep(.n-layout-toggle-button) {
  background: #1e293b !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.6) !important;
  right: -14px !important;
}

.layout-sider :deep(.n-layout-toggle-button:hover) {
  background: #334155 !important;
  color: #ffffff !important;
}

/* ========================================
   响应式设计
   ======================================== */
@media (max-width: 768px) {
  .layout-header {
    padding: 0 16px;
  }

  .content-wrapper {
    padding: 16px;
  }
}

/* ========================================
   减少动画 - 无障碍
   ======================================== */
@media (prefers-reduced-motion: reduce) {
  .page-fade-enter-active,
  .page-fade-leave-active {
    transition: none;
  }
}
</style>
