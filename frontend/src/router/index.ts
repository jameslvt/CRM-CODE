/**
 * 路由配置
 * 定义应用的路由规则和导航守卫
 */

import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

/**
 * 路由元信息
 */
interface RouteMeta {
  /** 页面标题 */
  title?: string
  /** 图标 */
  icon?: string
  /** 是否需要登录 */
  requiresAuth?: boolean
  /** 需要的权限 */
  permissions?: string[]
  /** 需要的角色 */
  roles?: string[]
  /** 是否隐藏在菜单中 */
  hidden?: boolean
  /** 是否缓存页面 */
  keepAlive?: boolean
  /** 面包屑导航 */
  breadcrumb?: boolean
}

/**
 * 基础路由
 * 不需要权限验证的路由
 */
const basicRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false,
      hidden: true
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      requiresAuth: false,
      hidden: true
    }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: {
      title: '无权限',
      requiresAuth: false,
      hidden: true
    }
  }
]

/**
 * 主要路由
 * 需要权限验证的路由
 */
const mainRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      // 仪表盘
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '工作台',
          icon: 'dashboard',
          requiresAuth: true,
          keepAlive: true
        }
      },
      // 业务仪表盘 (L2C数据分析)
      {
        path: '/business/dashboard',
        name: 'BusinessDashboard',
        component: () => import('@/views/business/dashboard/index.vue'),
        meta: {
          title: '数据分析',
          icon: 'chart',
          requiresAuth: true,
          keepAlive: true
        }
      },

      // 线索管理
      {
        path: '/leads',
        name: 'Leads',
        component: () => import('@/views/business/lead/index.vue'),
        meta: {
          title: '销售线索列表',
          icon: 'leads',
          requiresAuth: true,
          permissions: ['lead:view'],
          keepAlive: true
        }
      },
      // 线索详情
      {
        path: '/business/lead/:id',
        name: 'LeadDetail',
        component: () => import('@/views/business/lead/detail.vue'),
        meta: {
          title: '线索详情',
          icon: 'leads',
          requiresAuth: true,
          permissions: ['lead:view'],
          hidden: true
        }
      },

      // 客户管理
      {
        path: '/customers',
        name: 'Customers',
        component: () => import('@/views/business/customer/index.vue'),
        meta: {
          title: '客户名录',
          icon: 'customers',
          requiresAuth: true,
          permissions: ['customer:view'],
          keepAlive: true
        }
      },
      // 客户详情
      {
        path: '/business/customer/pool',
        name: 'CustomerPool',
        component: () => import('@/views/business/customer/pool.vue'),
        meta: {
          title: '公海池',
          icon: 'customers',
          requiresAuth: true,
          permissions: ['customer:view'],
          keepAlive: true
        }
      },
      // 客户详情
      {
        path: '/business/customer/:id',
        name: 'CustomerDetail',
        component: () => import('@/views/business/customer/detail.vue'),
        meta: {
          title: '客户详情',
          icon: 'customers',
          requiresAuth: true,
          permissions: ['customer:view'],
          hidden: true
        }
      },

      // 联系人管理
      {
        path: '/contacts',
        name: 'Contacts',
        component: () => import('@/views/business/contact/index.vue'),
        meta: {
          title: '通讯录',
          icon: 'contacts',
          requiresAuth: true,
          permissions: ['contact:view'],
          keepAlive: true
        }
      },

      // 商机管理
      {
        path: '/opportunities',
        name: 'Opportunities',
        component: () => import('@/views/business/opportunity/index.vue'),
        meta: {
          title: '商机看板',
          icon: 'opportunities',
          requiresAuth: true,
          permissions: ['opportunity:view'],
          keepAlive: true
        }
      },
      // 商机详情
      {
        path: '/business/opportunity/:id',
        name: 'OpportunityDetail',
        component: () => import('@/views/business/opportunity/detail.vue'),
        meta: {
          title: '商机详情',
          icon: 'opportunities',
          requiresAuth: true,
          permissions: ['opportunity:view'],
          hidden: true
        }
      },

      // 产品管理
      {
        path: '/products',
        name: 'Products',
        component: () => import('@/views/business/product/index.vue'),
        meta: {
          title: '产品目录',
          icon: 'products',
          requiresAuth: true,
          permissions: ['product:view'],
          keepAlive: true
        }
      },

      // 合同管理
      {
        path: '/contracts',
        name: 'Contracts',
        component: () => import('@/views/business/contract/index.vue'),
        meta: {
          title: '合同库',
          icon: 'contracts',
          requiresAuth: true,
          permissions: ['contract:view'],
          keepAlive: true
        }
      },
      // 合同详情
      {
        path: '/business/contract/:id',
        name: 'ContractDetail',
        component: () => import('@/views/business/contract/detail.vue'),
        meta: {
          title: '合同详情',
          icon: 'contracts',
          requiresAuth: true,
          permissions: ['contract:view'],
          hidden: true
        }
      },

      // 回款管理
      {
        path: '/payments',
        name: 'Payments',
        component: () => import('@/views/business/payment/index.vue'),
        meta: {
          title: '回款明细',
          icon: 'payments',
          requiresAuth: true,
          permissions: ['payment:view'],
          keepAlive: true
        }
      },

      // 系统管理 - 用户管理
      {
        path: '/system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: {
          title: '用户管理',
          icon: 'user',
          requiresAuth: true,
          permissions: ['system:user:view']
        }
      },
      // 系统管理 - 角色管理
      {
        path: '/system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: {
          title: '角色管理',
          icon: 'role',
          requiresAuth: true,
          permissions: ['system:role:view']
        }
      },
      // 系统管理 - 部门管理
      {
        path: '/system/department',
        name: 'SystemDepartment',
        component: () => import('@/views/system/department/index.vue'),
        meta: {
          title: '部门管理',
          icon: 'department',
          requiresAuth: true,
          permissions: ['system:dept:view']
        }
      },

      // 个人中心
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: {
          title: '个人中心',
          icon: 'profile',
          requiresAuth: true,
          hidden: true
        }
      }
    ]
  }
]

/**
 * 创建路由实例
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...basicRoutes, ...mainRoutes],
  scrollBehavior() {
    // 路由切换时滚动到顶部
    return { top: 0 }
  }
})

/**
 * 全局前置守卫
 * 在路由跳转前进行权限验证
 */
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const appStore = useAppStore()

  // 设置页面加载状态
  appStore.setPageLoading(true)

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - CRM 系统`
  }

  // 白名单路由，不需要登录
  const whiteList = ['/login', '/404', '/403']
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth !== false) {
    // 未登录，跳转到登录页
    if (!userStore.isLoggedIn) {
      next({
        path: '/login',
        query: { redirect: to.fullPath } // 保存目标路由，登录后跳转
      })
      return
    }

    // 如果用户信息为空，尝试获取用户信息
    if (!userStore.userInfo) {
      try {
        await userStore.getUserInfo()
      } catch (error) {
        // 获取用户信息失败，跳转到登录页
        next({
          path: '/login',
          query: { redirect: to.fullPath }
        })
        return
      }
    }

    // 检查权限
    const meta = to.meta as RouteMeta
    if (meta.permissions && meta.permissions.length > 0) {
      const hasPermission = userStore.hasAnyPermission(meta.permissions)
      if (!hasPermission) {
        next('/403')
        return
      }
    }

    // 检查角色
    if (meta.roles && meta.roles.length > 0) {
      const hasRole = userStore.hasAnyRole(meta.roles)
      if (!hasRole) {
        next('/403')
        return
      }
    }
  }

  next()
})

/**
 * 全局后置守卫
 * 在路由跳转后执行
 */
router.afterEach((to) => {
  const appStore = useAppStore()

  // 关闭页面加载状态
  appStore.setPageLoading(false)

  // 移动端关闭侧边栏
  if (appStore.isMobile) {
    appStore.closeMobileSidebar()
  }

  // 生成面包屑导航
  if (to.meta.breadcrumb !== false) {
    const breadcrumbs: Array<{ title: string; path?: string }> = []
    to.matched.forEach((route) => {
      if (route.meta.title && route.meta.hidden !== true) {
        breadcrumbs.push({
          title: route.meta.title as string,
          path: route.path
        })
      }
    })
    appStore.setBreadcrumbs(breadcrumbs)
  }
})

/**
 * 路由错误处理
 */
router.onError((error) => {
  console.error('路由错误:', error)
})

export default router
