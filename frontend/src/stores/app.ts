/**
 * 应用状态管理
 * 管理应用全局状态，如侧边栏、主题、设置等
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 主题模式
 */
export type ThemeMode = 'light' | 'dark' | 'auto'

/**
 * 设备类型
 */
export type DeviceType = 'desktop' | 'tablet' | 'mobile'

/**
 * 应用 Store
 */
export const useAppStore = defineStore(
  'app',
  () => {
    // ========== 状态 ==========

    /** 侧边栏是否折叠 */
    const sidebarCollapsed = ref(false)

    /** 侧边栏是否在移动端显示 */
    const sidebarMobileVisible = ref(false)

    /** 主题模式 */
    const themeMode = ref<ThemeMode>('light')

    /** 设备类型 */
    const deviceType = ref<DeviceType>('desktop')

    /** 是否显示设置抽屉 */
    const settingsVisible = ref(false)

    /** 页面加载状态 */
    const pageLoading = ref(false)

    /** 全局加载状态 */
    const globalLoading = ref(false)

    /** 面包屑导航 */
    const breadcrumbs = ref<Array<{ title: string; path?: string }>>([])

    // ========== 计算属性 ==========

    /** 是否是移动端 */
    const isMobile = computed(() => deviceType.value === 'mobile')

    /** 是否是平板 */
    const isTablet = computed(() => deviceType.value === 'tablet')

    /** 是否是桌面端 */
    const isDesktop = computed(() => deviceType.value === 'desktop')

    /** 是否是暗色主题 */
    const isDark = computed(() => {
      if (themeMode.value === 'auto') {
        // 自动模式：根据系统主题判断
        return window.matchMedia('(prefers-color-scheme: dark)').matches
      }
      return themeMode.value === 'dark'
    })

    // ========== 方法 ==========

    /**
     * 切换侧边栏折叠状态
     */
    function toggleSidebar(): void {
      sidebarCollapsed.value = !sidebarCollapsed.value
    }

    /**
     * 设置侧边栏折叠状态
     * @param collapsed 是否折叠
     */
    function setSidebarCollapsed(collapsed: boolean): void {
      sidebarCollapsed.value = collapsed
    }

    /**
     * 切换移动端侧边栏显示状态
     */
    function toggleMobileSidebar(): void {
      sidebarMobileVisible.value = !sidebarMobileVisible.value
    }

    /**
     * 设置移动端侧边栏显示状态
     * @param visible 是否显示
     */
    function setMobileSidebarVisible(visible: boolean): void {
      sidebarMobileVisible.value = visible
    }

    /**
     * 关闭移动端侧边栏
     */
    function closeMobileSidebar(): void {
      sidebarMobileVisible.value = false
    }

    /**
     * 切换主题模式
     */
    function toggleTheme(): void {
      themeMode.value = themeMode.value === 'light' ? 'dark' : 'light'
      applyTheme()
    }

    /**
     * 设置主题模式
     * @param mode 主题模式
     */
    function setThemeMode(mode: ThemeMode): void {
      themeMode.value = mode
      applyTheme()
    }

    /**
     * 应用主题
     */
    function applyTheme(): void {
      const html = document.documentElement
      if (isDark.value) {
        html.classList.add('dark')
      } else {
        html.classList.remove('dark')
      }
    }

    /**
     * 设置设备类型
     * @param type 设备类型
     */
    function setDeviceType(type: DeviceType): void {
      deviceType.value = type

      // 移动端自动折叠侧边栏
      if (type === 'mobile') {
        sidebarCollapsed.value = true
        sidebarMobileVisible.value = false
      }
    }

    /**
     * 根据窗口宽度自动设置设备类型
     */
    function updateDeviceType(): void {
      const width = window.innerWidth
      if (width < 768) {
        setDeviceType('mobile')
      } else if (width < 1024) {
        setDeviceType('tablet')
      } else {
        setDeviceType('desktop')
      }
    }

    /**
     * 切换设置抽屉显示状态
     */
    function toggleSettings(): void {
      settingsVisible.value = !settingsVisible.value
    }

    /**
     * 设置设置抽屉显示状态
     * @param visible 是否显示
     */
    function setSettingsVisible(visible: boolean): void {
      settingsVisible.value = visible
    }

    /**
     * 设置页面加载状态
     * @param loading 是否加载中
     */
    function setPageLoading(loading: boolean): void {
      pageLoading.value = loading
    }

    /**
     * 设置全局加载状态
     * @param loading 是否加载中
     */
    function setGlobalLoading(loading: boolean): void {
      globalLoading.value = loading
    }

    /**
     * 设置面包屑导航
     * @param items 面包屑项
     */
    function setBreadcrumbs(items: Array<{ title: string; path?: string }>): void {
      breadcrumbs.value = items
    }

    /**
     * 初始化应用
     * 在应用启动时调用
     */
    function init(): void {
      // 应用主题
      applyTheme()

      // 设置设备类型
      updateDeviceType()

      // 监听窗口大小变化
      window.addEventListener('resize', updateDeviceType)

      // 监听系统主题变化（仅在自动模式下）
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      mediaQuery.addEventListener('change', () => {
        if (themeMode.value === 'auto') {
          applyTheme()
        }
      })
    }

    /**
     * 重置状态
     */
    function reset(): void {
      sidebarCollapsed.value = false
      sidebarMobileVisible.value = false
      themeMode.value = 'light'
      deviceType.value = 'desktop'
      settingsVisible.value = false
      pageLoading.value = false
      globalLoading.value = false
      breadcrumbs.value = []
    }

    return {
      // 状态
      sidebarCollapsed,
      sidebarMobileVisible,
      themeMode,
      deviceType,
      settingsVisible,
      pageLoading,
      globalLoading,
      breadcrumbs,

      // 计算属性
      isMobile,
      isTablet,
      isDesktop,
      isDark,

      // 方法
      toggleSidebar,
      setSidebarCollapsed,
      toggleMobileSidebar,
      setMobileSidebarVisible,
      closeMobileSidebar,
      toggleTheme,
      setThemeMode,
      applyTheme,
      setDeviceType,
      updateDeviceType,
      toggleSettings,
      setSettingsVisible,
      setPageLoading,
      setGlobalLoading,
      setBreadcrumbs,
      init,
      reset
    }
  },
  {
    // 持久化配置
    persist: {
      key: 'app-store',
      storage: localStorage,
      paths: ['sidebarCollapsed', 'themeMode'] // 只持久化这些字段
    }
  }
)
