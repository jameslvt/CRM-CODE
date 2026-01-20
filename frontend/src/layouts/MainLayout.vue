<template>
  <n-layout has-sider class="main-layout">
    <!-- 侧边栏 -->
    <n-layout-sider
      v-if="!appStore.isMobile"
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="appStore.sidebarCollapsed"
      show-trigger
      @collapse="appStore.setSidebarCollapsed(true)"
      @expand="appStore.setSidebarCollapsed(false)"
      class="layout-sider"
    >
      <!-- Logo -->
      <div class="logo-container">
        <div v-if="!appStore.sidebarCollapsed" class="logo">
          <span class="logo-text">CRM 系统</span>
        </div>
        <div v-else class="logo-collapsed">
          <span class="logo-icon">C</span>
        </div>
      </div>

      <!-- 菜单 -->
      <n-menu
        :collapsed="appStore.sidebarCollapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>

    <!-- 移动端侧边栏抽屉 -->
    <n-drawer
      v-model:show="appStore.sidebarMobileVisible"
      :width="240"
      placement="left"
      class="mobile-drawer"
    >
      <!-- Logo -->
      <div class="logo-container">
        <div class="logo">
          <span class="logo-text">CRM 系统</span>
        </div>
      </div>

      <!-- 菜单 -->
      <n-menu
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuSelect"
      />
    </n-drawer>

    <!-- 主内容区 -->
    <n-layout>
      <!-- 顶栏 -->
      <n-layout-header bordered class="layout-header">
        <div class="header-left">
          <!-- 移动端菜单按钮 -->
          <n-button
            v-if="appStore.isMobile"
            text
            @click="appStore.toggleMobileSidebar"
            class="menu-trigger"
          >
            <template #icon>
              <n-icon size="20">
                <MenuOutlined />
              </n-icon>
            </template>
          </n-button>

          <!-- 面包屑导航 -->
          <n-breadcrumb v-if="!appStore.isMobile && appStore.breadcrumbs.length > 0">
            <n-breadcrumb-item
              v-for="(item, index) in appStore.breadcrumbs"
              :key="index"
              :clickable="!!item.path"
              @click="item.path && router.push(item.path)"
            >
              {{ item.title }}
            </n-breadcrumb-item>
          </n-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 主题切换 -->
          <n-button text @click="appStore.toggleTheme" class="header-action">
            <template #icon>
              <n-icon size="18">
                <SunOutlined v-if="!appStore.isDark" />
                <MoonOutlined v-else />
              </n-icon>
            </template>
          </n-button>

          <!-- 用户信息 -->
          <n-dropdown :options="userMenuOptions" @select="handleUserMenuSelect">
            <div class="user-info">
              <n-avatar
                round
                size="small"
                :src="userStore.avatar"
                :fallback-src="defaultAvatar"
              />
              <span v-if="!appStore.isMobile" class="username">
                {{ userStore.realName || userStore.username }}
              </span>
            </div>
          </n-dropdown>
        </div>
      </n-layout-header>

      <!-- 内容区 -->
      <n-layout-content class="layout-content" :native-scrollbar="false">
        <div class="content-wrapper">
          <router-view v-slot="{ Component, route }">
            <transition name="fade" mode="out-in">
              <keep-alive :include="cachedViews">
                <component :is="Component" :key="route.path" />
              </keep-alive>
            </transition>
          </router-view>
        </div>
      </n-layout-content>
    </n-layout>
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
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NIcon,
  NAvatar,
  NDropdown,
  NDrawer,
  type MenuOption
} from 'naive-ui'
import {
  MenuOutlined,
  SunOutlined,
  MoonOutlined,
  DashboardOutlined,
  UserOutlined,
  TeamOutlined,
  ShopOutlined,
  ContactsOutlined,
  FundOutlined,
  ShoppingOutlined,
  FileTextOutlined,
  DollarOutlined,
  SettingOutlined,
  LogoutOutlined,
  ProfileOutlined
} from '@vicons/antd'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

// 路由和 Store
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

// 默认头像
const defaultAvatar = 'https://via.placeholder.com/40'

// 当前激活的菜单项
const activeKey = computed(() => route.path)

// 缓存的视图
const cachedViews = computed(() => {
  const views: string[] = []
  router.getRoutes().forEach((route) => {
    if (route.meta.keepAlive && route.name) {
      views.push(route.name as string)
    }
  })
  return views
})

// 渲染图标
function renderIcon(icon: any) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

// 菜单选项
const menuOptions = computed<MenuOption[]>(() => {
  const options: MenuOption[] = [
    {
      label: '仪表盘',
      key: '/dashboard',
      icon: renderIcon(DashboardOutlined)
    },
    {
      label: '线索管理',
      key: '/leads',
      icon: renderIcon(UserOutlined),
      show: userStore.hasPermission('lead:view')
    },
    {
      label: '客户管理',
      key: '/customers',
      icon: renderIcon(TeamOutlined),
      show: userStore.hasPermission('customer:view')
    },
    {
      label: '联系人管理',
      key: '/contacts',
      icon: renderIcon(ContactsOutlined),
      show: userStore.hasPermission('contact:view')
    },
    {
      label: '商机管理',
      key: '/opportunities',
      icon: renderIcon(FundOutlined),
      show: userStore.hasPermission('opportunity:view')
    },
    {
      label: '产品管理',
      key: '/products',
      icon: renderIcon(ShoppingOutlined),
      show: userStore.hasPermission('product:view')
    },
    {
      label: '合同管理',
      key: '/contracts',
      icon: renderIcon(FileTextOutlined),
      show: userStore.hasPermission('contract:view')
    },
    {
      label: '回款管理',
      key: '/payments',
      icon: renderIcon(DollarOutlined),
      show: userStore.hasPermission('payment:view')
    },
    {
      label: '系统管理',
      key: '/system',
      icon: renderIcon(SettingOutlined),
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

  // 过滤没有权限的菜单
  return options.filter((item) => item.show !== false)
})

// 用户菜单选项
const userMenuOptions = computed(() => [
  {
    label: '个人中心',
    key: 'profile',
    icon: renderIcon(ProfileOutlined)
  },
  {
    type: 'divider',
    key: 'divider'
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: renderIcon(LogoutOutlined)
  }
])

// 菜单选择处理
function handleMenuSelect(key: string) {
  router.push(key)
}

// 用户菜单选择处理
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
.main-layout {
  height: 100vh;
}

.layout-sider {
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid var(--n-border-color);
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: var(--n-text-color);
}

.logo-collapsed {
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  font-size: 24px;
  font-weight: 700;
  color: var(--n-primary-color);
}

.layout-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.menu-trigger {
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-action {
  font-size: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: var(--n-color-hover);
}

.username {
  font-size: 14px;
  color: var(--n-text-color);
}

.layout-content {
  height: calc(100vh - 64px);
}

.content-wrapper {
  padding: 24px;
  min-height: 100%;
}

/* 移动端样式 */
.mobile-drawer .logo-container {
  margin-bottom: 16px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .layout-header {
    padding: 0 16px;
  }

  .content-wrapper {
    padding: 16px;
  }
}
</style>
