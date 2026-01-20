/**
 * 应用主入口文件
 * 初始化 Vue 应用、路由、状态管理等
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from './router'

// 导入 Naive UI
import naive from 'naive-ui'

// 导入全局样式
import './styles/global.css'

// 创建 Vue 应用实例
const app = createApp(App)

// 创建 Pinia 实例并添加持久化插件
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

// 注册插件
app.use(pinia)
app.use(router)
app.use(naive)

// 挂载应用
app.mount('#app')

// 开发环境下的调试信息
if (import.meta.env.DEV) {
  console.log('CRM 系统启动成功')
  console.log('环境:', import.meta.env.MODE)
  console.log('API 地址:', import.meta.env.VITE_API_BASE_URL)
}
