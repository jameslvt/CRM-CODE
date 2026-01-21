<template>
  <div class="login-page">
    <!-- 左侧品牌区域 - 深色背景 -->
    <div class="brand-panel">
      <!-- 动态背景效果 -->
      <div class="brand-bg">
        <div class="grid-pattern"></div>
        <div class="glow-orb glow-1"></div>
        <div class="glow-orb glow-2"></div>
      </div>

      <div class="brand-content">
        <!-- Logo -->
        <div class="brand-logo">
          <div class="logo-icon">
            <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="40" height="40" rx="8" fill="#2563eb"/>
              <path d="M12 20C12 15.5817 15.5817 12 20 12V12C24.4183 12 28 15.5817 28 20V28H20C15.5817 28 12 24.4183 12 20V20Z" fill="white" fill-opacity="0.9"/>
              <circle cx="20" cy="20" r="4" fill="#2563eb"/>
            </svg>
          </div>
          <div class="logo-text">
            <span class="logo-name">CRM Pro</span>
            <span class="logo-tagline">企业级客户关系管理</span>
          </div>
        </div>

        <!-- 功能特点 -->
        <div class="features">
          <div
            class="feature-item"
            v-for="(feature, index) in features"
            :key="index"
            :style="{ animationDelay: `${index * 0.1}s` }"
          >
            <div class="feature-icon">
              <component :is="feature.icon" />
            </div>
            <div class="feature-info">
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.desc }}</p>
            </div>
          </div>
        </div>

        <!-- 统计数据 -->
        <div class="stats">
          <div class="stat-item" v-for="(stat, index) in stats" :key="index">
            <span class="stat-value">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>

        <!-- 底部版权 -->
        <div class="brand-footer">
          <p>© 2026 CRM Pro. All rights reserved.</p>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 - 浅色背景 -->
    <div class="form-panel">
      <div class="form-container">
        <!-- 移动端Logo -->
        <div class="mobile-logo">
          <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="40" height="40" rx="8" fill="#2563eb"/>
            <path d="M12 20C12 15.5817 15.5817 12 20 12V12C24.4183 12 28 15.5817 28 20V28H20C15.5817 28 12 24.4183 12 20V20Z" fill="white" fill-opacity="0.9"/>
            <circle cx="20" cy="20" r="4" fill="#2563eb"/>
          </svg>
        </div>

        <!-- 表单头部 -->
        <div class="form-header">
          <h1>欢迎回来</h1>
          <p>请登录您的账户以继续</p>
        </div>

        <!-- 登录表单 -->
        <n-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          class="login-form"
        >
          <n-form-item path="username" :show-label="false">
            <n-input
              v-model:value="formData.username"
              placeholder="用户名"
              size="large"
              :input-props="{ autocomplete: 'username' }"
            >
              <template #prefix>
                <n-icon :component="PersonOutline" class="input-icon" />
              </template>
            </n-input>
          </n-form-item>

          <n-form-item path="password" :show-label="false">
            <n-input
              v-model:value="formData.password"
              type="password"
              placeholder="密码"
              size="large"
              show-password-on="click"
              :input-props="{ autocomplete: 'current-password' }"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <n-icon :component="LockClosedOutline" class="input-icon" />
              </template>
            </n-input>
          </n-form-item>

          <div class="form-options">
            <n-checkbox v-model:checked="formData.rememberMe">
              记住我
            </n-checkbox>
            <a href="javascript:;" class="forgot-link" @click="handleForgotPassword">
              忘记密码？
            </a>
          </div>

          <n-button
            type="primary"
            size="large"
            block
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </n-button>
        </n-form>

        <!-- 分隔线 -->
        <div class="divider">
          <span>或</span>
        </div>

        <!-- 其他登录方式 -->
        <div class="alt-login">
          <button class="alt-btn" @click="handleSSOLogin">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 5C13.66 5 15 6.34 15 8C15 9.66 13.66 11 12 11C10.34 11 9 9.66 9 8C9 6.34 10.34 5 12 5ZM12 19.2C9.5 19.2 7.29 17.92 6 15.98C6.03 13.99 10 12.9 12 12.9C13.99 12.9 17.97 13.99 18 15.98C16.71 17.92 14.5 19.2 12 19.2Z" fill="currentColor"/>
            </svg>
            企业SSO登录
          </button>
        </div>

        <!-- 注册提示 -->
        <div class="register-hint">
          <span>还没有账户？</span>
          <a href="javascript:;" @click="handleRegister">联系管理员</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage, type FormInst, type FormRules } from 'naive-ui'
import { PersonOutline, LockClosedOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'

// 功能特点图标组件
const TrendingIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', xmlns: 'http://www.w3.org/2000/svg' }, [
  h('path', { d: 'M3.5 18.5L9.5 12.5L13.5 16.5L22 6.5', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
  h('path', { d: 'M17 6.5H22V11.5', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
])

const UsersIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', xmlns: 'http://www.w3.org/2000/svg' }, [
  h('path', { d: 'M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
  h('circle', { cx: '9', cy: '7', r: '4', stroke: 'currentColor', 'stroke-width': '2' }),
  h('path', { d: 'M23 21V19C22.9993 18.1137 22.7044 17.2528 22.1614 16.5523C21.6184 15.8519 20.8581 15.3516 20 15.13', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
  h('path', { d: 'M16 3.13C16.8604 3.35031 17.623 3.85071 18.1676 4.55232C18.7122 5.25392 19.0078 6.11683 19.0078 7.005C19.0078 7.89318 18.7122 8.75608 18.1676 9.45769C17.623 10.1593 16.8604 10.6597 16 10.88', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
])

const ShieldIcon = () => h('svg', { viewBox: '0 0 24 24', fill: 'none', xmlns: 'http://www.w3.org/2000/svg' }, [
  h('path', { d: 'M12 22C12 22 20 18 20 12V5L12 2L4 5V12C4 18 12 22 12 22Z', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }),
  h('path', { d: 'M9 12L11 14L15 10', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' })
])

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const formRef = ref<FormInst | null>(null)
const loading = ref(false)

const formData = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const features = [
  {
    icon: TrendingIcon,
    title: '销售漏斗管理',
    desc: '可视化追踪从线索到成交的全流程'
  },
  {
    icon: UsersIcon,
    title: '客户360°视图',
    desc: '整合客户所有交互数据，洞察客户需求'
  },
  {
    icon: ShieldIcon,
    title: '企业级安全',
    desc: '多层数据加密，角色权限精细管控'
  }
]

const stats = [
  { value: '10K+', label: '活跃用户' },
  { value: '99.9%', label: '系统可用性' },
  { value: '50M+', label: '管理客户数' }
]

const handleLogin = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true

    await userStore.login({
      username: formData.username,
      password: formData.password,
      rememberMe: formData.rememberMe
    })

    message.success('登录成功')

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

const handleForgotPassword = () => {
  message.info('请联系系统管理员重置密码')
}

const handleSSOLogin = () => {
  message.info('SSO登录功能开发中')
}

const handleRegister = () => {
  message.info('请联系系统管理员开通账户')
}
</script>

<style scoped>
/* ========================================
   企业级CRM登录页面样式
   遵循章程UI/UX设计规范
   ======================================== */

/* 页面容器 */
.login-page {
  display: flex;
  min-height: 100vh;
  background: #f8fafc; /* Slate-50 */
}

/* ========================================
   左侧品牌面板 - 深色背景 Slate-900
   ======================================== */
.brand-panel {
  flex: 1;
  position: relative;
  background: #0f172a; /* Slate-900 */
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

/* 动态背景效果 */
.brand-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.grid-pattern {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
  animation: float 20s ease-in-out infinite;
}

.glow-1 {
  width: 400px;
  height: 400px;
  background: #2563eb; /* Blue-600 */
  top: -100px;
  right: -100px;
}

.glow-2 {
  width: 300px;
  height: 300px;
  background: #6366f1;
  bottom: -50px;
  left: -50px;
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(30px, -30px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}

/* 品牌内容 */
.brand-content {
  position: relative;
  z-index: 1;
  padding: 48px;
  max-width: 520px;
  width: 100%;
}

/* Logo */
.brand-logo {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 64px;
}

.logo-icon svg {
  width: 48px;
  height: 48px;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-name {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: white;
}

.logo-tagline {
  font-size: 13px;
  color: #94a3b8; /* Slate-400 */
  margin-top: 2px;
}

/* 功能特点 */
.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 48px;
}

.feature-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  transition: all 0.3s ease;
  animation: slideIn 0.5s ease-out backwards;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.1);
  transform: translateX(8px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.feature-icon {
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2563eb; /* Blue-600 */
  border-radius: 10px;
  color: white;
}

.feature-icon svg {
  width: 22px;
  height: 22px;
}

.feature-info h3 {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 4px 0;
  color: white;
}

.feature-info p {
  font-size: 13px;
  color: #94a3b8; /* Slate-400 */
  margin: 0;
  line-height: 1.5;
}

/* 统计数据 */
.stats {
  display: flex;
  gap: 32px;
  padding: 24px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: white;
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 12px;
  color: #64748b; /* Slate-500 */
  margin-top: 4px;
}

/* 底部版权 */
.brand-footer {
  color: #475569; /* Slate-600 */
  font-size: 12px;
}

.brand-footer p {
  margin: 0;
}

/* ========================================
   右侧表单面板 - 浅色背景 Slate-50
   ======================================== */
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: #f8fafc; /* Slate-50 */
}

.form-container {
  width: 100%;
  max-width: 400px;
}

/* 移动端Logo */
.mobile-logo {
  display: none;
  justify-content: center;
  margin-bottom: 32px;
}

.mobile-logo svg {
  width: 56px;
  height: 56px;
}

/* 表单头部 */
.form-header {
  margin-bottom: 32px;
}

.form-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a; /* Slate-900 */
  margin: 0 0 8px 0;
  letter-spacing: -0.02em;
}

.form-header p {
  font-size: 15px;
  color: #64748b; /* Slate-500 */
  margin: 0;
}

/* 登录表单 */
.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.n-form-item) {
  margin-bottom: 16px;
}

.login-form :deep(.n-input) {
  --n-height: 48px;
  --n-border-radius: 10px;
  --n-font-size: 15px;
  --n-padding-left: 44px;
  --n-border: 1px solid #e2e8f0; /* Slate-200 */
  --n-border-hover: 1px solid #cbd5e1; /* Slate-300 */
  --n-border-focus: 1px solid #2563eb; /* Blue-600 */
  --n-box-shadow-focus: 0 0 0 3px rgba(37, 99, 235, 0.1);
  --n-color: white;
  --n-color-focus: white;
  background: white;
}

.login-form :deep(.n-input__input-el) {
  color: #0f172a; /* Slate-900 */
}

.login-form :deep(.n-input__input-el::placeholder) {
  color: #94a3b8; /* Slate-400 */
}

.input-icon {
  color: #94a3b8; /* Slate-400 */
  font-size: 18px;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.form-options :deep(.n-checkbox) {
  --n-font-size: 14px;
  --n-text-color: #475569; /* Slate-600 */
}

.forgot-link {
  font-size: 14px;
  color: #2563eb; /* Blue-600 */
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #1d4ed8; /* Blue-700 */
}

/* 登录按钮 */
.login-btn {
  --n-height: 48px;
  --n-border-radius: 10px;
  --n-font-size: 15px;
  --n-font-weight: 600;
  --n-color: #2563eb; /* Blue-600 */
  --n-color-hover: #1d4ed8; /* Blue-700 */
  --n-color-pressed: #1d4ed8;
  --n-color-focus: #1d4ed8;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 40px rgba(37, 99, 235, 0.3);
}

.login-btn:active {
  transform: translateY(0);
}

/* 分隔线 */
.divider {
  display: flex;
  align-items: center;
  margin: 24px 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #e2e8f0; /* Slate-200 */
}

.divider span {
  padding: 0 16px;
  font-size: 13px;
  color: #94a3b8; /* Slate-400 */
}

/* 其他登录方式 */
.alt-login {
  margin-bottom: 24px;
}

.alt-btn {
  width: 100%;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: white;
  border: 1px solid #e2e8f0; /* Slate-200 */
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #334155; /* Slate-700 */
  cursor: pointer;
  transition: all 0.3s ease;
}

.alt-btn:hover {
  background: #f8fafc; /* Slate-50 */
  border-color: #cbd5e1; /* Slate-300 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.alt-btn svg {
  width: 20px;
  height: 20px;
  color: #64748b; /* Slate-500 */
}

/* 注册提示 */
.register-hint {
  text-align: center;
  font-size: 14px;
  color: #64748b; /* Slate-500 */
}

.register-hint a {
  color: #2563eb; /* Blue-600 */
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
  transition: color 0.3s ease;
}

.register-hint a:hover {
  color: #1d4ed8; /* Blue-700 */
}

/* ========================================
   响应式设计
   ======================================== */
@media (max-width: 1024px) {
  .brand-panel {
    display: none;
  }

  .form-panel {
    flex: none;
    width: 100%;
  }

  .mobile-logo {
    display: flex;
  }
}

@media (max-width: 480px) {
  .form-panel {
    padding: 24px;
  }

  .form-header h1 {
    font-size: 24px;
  }
}

/* 减少动画 - 无障碍 */
@media (prefers-reduced-motion: reduce) {
  .glow-orb,
  .feature-item {
    animation: none;
  }

  .feature-item:hover,
  .login-btn:hover,
  .alt-btn:hover {
    transform: none;
  }
}
</style>
