<template>
  <div class="login-container">
    <!-- 左侧品牌区域 -->
    <div class="brand-section">
      <div class="brand-content">
        <!-- Logo 和标题 -->
        <div class="brand-header">
          <div class="logo-wrapper">
            <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h1 class="brand-title">企业级 CRM 系统</h1>
          <p class="brand-subtitle">智能客户关系管理平台</p>
        </div>

        <!-- 特性列表 -->
        <div class="features-list">
          <div v-for="feature in features" :key="feature.title" class="feature-item">
            <div class="feature-icon">
              <component :is="feature.icon" />
            </div>
            <div class="feature-content">
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-description">{{ feature.description }}</p>
            </div>
          </div>
        </div>

        <!-- 底部信息 -->
        <div class="brand-footer">
          <p class="copyright">© 2026 Enterprise CRM. All rights reserved.</p>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单区域 -->
    <div class="form-section">
      <div class="form-wrapper">
        <!-- 表单头部 -->
        <div class="form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">登录您的账户以继续</p>
        </div>

        <!-- 登录表单 -->
        <n-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          size="large"
          :show-label="false"
        >
          <n-form-item path="username">
            <n-input
              v-model:value="formData.username"
              placeholder="用户名"
              :input-props="{ autocomplete: 'username' }"
            >
              <template #prefix>
                <n-icon :component="UserIcon" />
              </template>
            </n-input>
          </n-form-item>

          <n-form-item path="password">
            <n-input
              v-model:value="formData.password"
              type="password"
              show-password-on="click"
              placeholder="密码"
              :input-props="{ autocomplete: 'current-password' }"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <n-icon :component="LockIcon" />
              </template>
            </n-input>
          </n-form-item>

          <!-- 记住我和忘记密码 -->
          <div class="form-options">
            <n-checkbox v-model:checked="formData.rememberMe">
              记住我
            </n-checkbox>
            <n-button text type="primary" @click="handleForgotPassword">
              忘记密码？
            </n-button>
          </div>

          <!-- 登录按钮 -->
          <n-button
            type="primary"
            size="large"
            block
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </n-button>
        </n-form>

        <!-- 注册链接 -->
        <div class="register-link">
          <span class="register-text">还没有账户？</span>
          <n-button text type="primary" @click="handleRegister">
            联系管理员
          </n-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage, type FormInst, type FormRules } from 'naive-ui'
import { useUserStore } from '@/stores/user'

// 图标组件
const UserIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2' }),
  h('circle', { cx: '12', cy: '7', r: '4' })
])

const LockIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('rect', { x: '3', y: '11', width: '18', height: '11', rx: '2', ry: '2' }),
  h('path', { d: 'M7 11V7a5 5 0 0 1 10 0v4' })
])

const CheckCircleIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M22 11.08V12a10 10 0 1 1-5.93-9.14' }),
  h('polyline', { points: '22 4 12 14.01 9 11.01' })
])

const TrendingUpIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('polyline', { points: '23 6 13.5 15.5 8.5 10.5 1 18' }),
  h('polyline', { points: '17 6 23 6 23 12' })
])

const ShieldIcon = () => h('svg', {
  xmlns: 'http://www.w3.org/2000/svg',
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  'stroke-width': '2',
  'stroke-linecap': 'round',
  'stroke-linejoin': 'round'
}, [
  h('path', { d: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z' })
])

// 路由和状态管理
const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

// 表单引用和数据
const formRef = ref<FormInst | null>(null)
const loading = ref(false)

const formData = ref({
  username: '',
  password: '',
  rememberMe: false
})

// 表单验证规则
const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度应为 3-50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ]
}

// 特性列表
const features = [
  {
    icon: CheckCircleIcon,
    title: '全流程管理',
    description: '从线索到回款的完整业务闭环'
  },
  {
    icon: TrendingUpIcon,
    title: '数据分析',
    description: '实时销售数据分析和业绩追踪'
  },
  {
    icon: ShieldIcon,
    title: '安全可靠',
    description: '企业级安全保障，数据加密存储'
  }
]

// 登录处理
const handleLogin = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true

    await userStore.login({
      username: formData.value.username,
      password: formData.value.password,
      rememberMe: formData.value.rememberMe
    })

    message.success('登录成功')

    // 跳转到首页或之前访问的页面
    const redirect = route.query.redirect as string
    router.push(redirect || '/dashboard')
  } catch (error: any) {
    if (error?.errorFields) {
      message.error('请检查表单输入')
    } else {
      message.error(error?.message || '登录失败，请检查用户名和密码')
    }
  } finally {
    loading.value = false
  }
}

// 忘记密码
const handleForgotPassword = () => {
  message.info('请联系系统管理员重置密码')
}

// 注册
const handleRegister = () => {
  message.info('请联系系统管理员开通账户')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;500;600;700&family=Poppins:wght@400;500;600;700&display=swap');

.login-container {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  color: white;
  padding: 4rem 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  animation: pulse 15s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.brand-content {
  max-width: 500px;
  width: 100%;
  position: relative;
  z-index: 1;
}

.brand-header {
  text-align: center;
  margin-bottom: 4rem;
}

.logo-wrapper {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  margin-bottom: 1.5rem;
  transition: transform 0.3s ease;
  cursor: pointer;
}

.logo-wrapper:hover {
  transform: scale(1.05);
}

.logo-icon {
  width: 48px;
  height: 48px;
  color: white;
}

.brand-title {
  font-family: 'Poppins', sans-serif;
  font-size: 2rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.02em;
}

.brand-subtitle {
  font-family: 'Open Sans', sans-serif;
  font-size: 1.125rem;
  opacity: 0.9;
  margin: 0;
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.feature-item {
  display: flex;
  gap: 1.25rem;
  padding: 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  cursor: pointer;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(8px);
}

.feature-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
}

.feature-icon svg {
  width: 24px;
  height: 24px;
}

.feature-content {
  flex: 1;
}

.feature-title {
  font-family: 'Poppins', sans-serif;
  font-size: 1.125rem;
  font-weight: 600;
  margin: 0 0 0.5rem 0;
}

.feature-description {
  font-family: 'Open Sans', sans-serif;
  font-size: 0.9375rem;
  opacity: 0.9;
  margin: 0;
  line-height: 1.6;
}

.brand-footer {
  margin-top: 4rem;
  text-align: center;
}

.copyright {
  font-family: 'Open Sans', sans-serif;
  font-size: 0.875rem;
  opacity: 0.7;
  margin: 0;
}

/* 右侧表单区域 */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: #f8fafc;
}

.form-wrapper {
  width: 100%;
  max-width: 440px;
  background: white;
  padding: 3rem;
  border-radius: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.form-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.form-title {
  font-family: 'Poppins', sans-serif;
  font-size: 1.875rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 0.5rem 0;
  letter-spacing: -0.02em;
}

.form-subtitle {
  font-family: 'Open Sans', sans-serif;
  font-size: 0.9375rem;
  color: #64748b;
  margin: 0;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.register-link {
  text-align: center;
  font-family: 'Open Sans', sans-serif;
  font-size: 0.9375rem;
  margin-top: 2rem;
}

.register-text {
  color: #64748b;
  margin-right: 0.5rem;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .brand-section {
    display: none;
  }

  .form-section {
    flex: none;
    width: 100%;
  }
}

@media (max-width: 640px) {
  .form-section {
    padding: 1rem;
  }

  .form-wrapper {
    padding: 2rem 1.5rem;
    border-radius: 16px;
  }

  .form-title {
    font-size: 1.5rem;
  }
}

/* 无障碍和动画优化 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* 焦点状态 */
:deep(.n-input:focus-within) {
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

:deep(.n-button--primary) {
  background: #2563eb;
  border-color: #2563eb;
  transition: all 0.2s ease;
}

:deep(.n-button--primary:hover) {
  background: #1e40af;
  border-color: #1e40af;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

:deep(.n-button--primary:active) {
  transform: translateY(0);
}
</style>
