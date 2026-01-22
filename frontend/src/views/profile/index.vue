<template>
  <div class="profile-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-subtitle">管理您的账户信息和安全设置</p>
    </div>

    <div class="profile-content">
      <!-- 左侧：用户卡片 -->
      <div class="profile-card">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <n-avatar
              round
              :size="100"
              :src="userStore.avatar"
              :fallback-src="defaultAvatar"
              class="user-avatar"
            />
            <div class="avatar-overlay" @click="handleAvatarClick">
              <n-icon size="24">
                <CameraOutline />
              </n-icon>
              <span>更换头像</span>
            </div>
          </div>
          <input
            ref="avatarInputRef"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleAvatarChange"
          />
        </div>

        <div class="user-info-brief">
          <h2 class="user-name">{{ userStore.nickname || userStore.username }}</h2>
          <p class="user-role">
            <n-tag :bordered="false" type="info" size="small">
              {{ userStore.roles?.[0] || '普通用户' }}
            </n-tag>
          </p>
        </div>

        <div class="user-stats">
          <div class="stat-item">
            <span class="stat-value">{{ userStore.userInfo?.deptName || '-' }}</span>
            <span class="stat-label">所属部门</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ formatDate(userStore.userInfo?.createTime) }}</span>
            <span class="stat-label">注册时间</span>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions">
          <button class="action-btn" @click="activeTab = 'info'">
            <n-icon size="18"><PersonOutline /></n-icon>
            <span>编辑资料</span>
          </button>
          <button class="action-btn" @click="activeTab = 'security'">
            <n-icon size="18"><LockClosedOutline /></n-icon>
            <span>安全设置</span>
          </button>
        </div>
      </div>

      <!-- 右侧：详细信息 -->
      <div class="profile-detail">
        <!-- Tab 切换 -->
        <div class="tab-header">
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'info' }"
            @click="activeTab = 'info'"
          >
            <n-icon size="18"><PersonOutline /></n-icon>
            基本信息
          </button>
          <button
            class="tab-btn"
            :class="{ active: activeTab === 'security' }"
            @click="activeTab = 'security'"
          >
            <n-icon size="18"><ShieldCheckmarkOutline /></n-icon>
            安全设置
          </button>
        </div>

        <!-- 基本信息表单 -->
        <div v-show="activeTab === 'info'" class="tab-content">
          <n-form
            ref="infoFormRef"
            :model="infoForm"
            :rules="infoRules"
            label-placement="left"
            label-width="100"
            require-mark-placement="right-hanging"
          >
            <div class="form-section">
              <h3 class="section-title">账户信息</h3>
              <div class="form-grid">
                <n-form-item label="用户名" path="username">
                  <n-input
                    v-model:value="infoForm.username"
                    placeholder="用户名"
                    disabled
                  >
                    <template #prefix>
                      <n-icon><PersonOutline /></n-icon>
                    </template>
                  </n-input>
                </n-form-item>

                <n-form-item label="昵称" path="nickname">
                  <n-input
                    v-model:value="infoForm.nickname"
                    placeholder="请输入昵称"
                  >
                    <template #prefix>
                      <n-icon><CreateOutline /></n-icon>
                    </template>
                  </n-input>
                </n-form-item>
              </div>
            </div>

            <div class="form-section">
              <h3 class="section-title">联系方式</h3>
              <div class="form-grid">
                <n-form-item label="邮箱" path="email">
                  <n-input
                    v-model:value="infoForm.email"
                    placeholder="请输入邮箱"
                  >
                    <template #prefix>
                      <n-icon><MailOutline /></n-icon>
                    </template>
                  </n-input>
                </n-form-item>

                <n-form-item label="手机号" path="phone">
                  <n-input
                    v-model:value="infoForm.phone"
                    placeholder="请输入手机号"
                  >
                    <template #prefix>
                      <n-icon><CallOutline /></n-icon>
                    </template>
                  </n-input>
                </n-form-item>
              </div>
            </div>

            <div class="form-actions">
              <n-button @click="resetInfoForm">重置</n-button>
              <n-button type="primary" :loading="infoLoading" @click="handleUpdateInfo">
                保存修改
              </n-button>
            </div>
          </n-form>
        </div>

        <!-- 安全设置 -->
        <div v-show="activeTab === 'security'" class="tab-content">
          <div class="security-section">
            <h3 class="section-title">修改密码</h3>
            <p class="section-desc">定期更换密码可以提高账户安全性</p>

            <n-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-placement="left"
              label-width="100"
              require-mark-placement="right-hanging"
              class="password-form"
            >
              <n-form-item label="当前密码" path="oldPassword">
                <n-input
                  v-model:value="passwordForm.oldPassword"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入当前密码"
                >
                  <template #prefix>
                    <n-icon><LockClosedOutline /></n-icon>
                  </template>
                </n-input>
              </n-form-item>

              <n-form-item label="新密码" path="newPassword">
                <n-input
                  v-model:value="passwordForm.newPassword"
                  type="password"
                  show-password-on="click"
                  placeholder="请输入新密码（至少6位）"
                >
                  <template #prefix>
                    <n-icon><KeyOutline /></n-icon>
                  </template>
                </n-input>
              </n-form-item>

              <n-form-item label="确认密码" path="confirmPassword">
                <n-input
                  v-model:value="passwordForm.confirmPassword"
                  type="password"
                  show-password-on="click"
                  placeholder="请再次输入新密码"
                >
                  <template #prefix>
                    <n-icon><KeyOutline /></n-icon>
                  </template>
                </n-input>
              </n-form-item>

              <div class="form-actions">
                <n-button @click="resetPasswordForm">重置</n-button>
                <n-button type="primary" :loading="passwordLoading" @click="handleChangePassword">
                  修改密码
                </n-button>
              </div>
            </n-form>
          </div>

          <!-- 安全提示 -->
          <div class="security-tips">
            <h4 class="tips-title">
              <n-icon size="18"><InformationCircleOutline /></n-icon>
              安全提示
            </h4>
            <ul class="tips-list">
              <li>密码长度至少6位，建议包含字母、数字和特殊字符</li>
              <li>请勿使用与其他网站相同的密码</li>
              <li>定期更换密码可以提高账户安全性</li>
              <li>请勿将密码告知他人</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {
  NAvatar,
  NTag,
  NIcon,
  NForm,
  NFormItem,
  NInput,
  NButton,
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import {
  PersonOutline,
  CameraOutline,
  LockClosedOutline,
  ShieldCheckmarkOutline,
  CreateOutline,
  MailOutline,
  CallOutline,
  KeyOutline,
  InformationCircleOutline
} from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const message = useMessage()

const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSI+PHJlY3Qgd2lkdGg9IjQwIiBoZWlnaHQ9IjQwIiByeD0iMjAiIGZpbGw9IiM2NDc0OGIiLz48cGF0aCBkPSJNMjAgMjJjMy4zMTQgMCA2LTIuNjg2IDYtNnMtMi42ODYtNi02LTYtNiAyLjY4Ni02IDYgMi42ODYgNiA2IDZ6bTAgMmMtNC40MTggMC04IDMuNTgyLTggOHYyaDE2di0yYzAtNC40MTgtMy41ODItOC04LTh6IiBmaWxsPSJ3aGl0ZSIgZmlsbC1vcGFjaXR5PSIwLjgiLz48L3N2Zz4='

// Tab 状态
const activeTab = ref<'info' | 'security'>('info')

// 头像上传
const avatarInputRef = ref<HTMLInputElement | null>(null)

function handleAvatarClick() {
  avatarInputRef.value?.click()
}

function handleAvatarChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    // TODO: 实现头像上传逻辑
    message.info('头像上传功能开发中...')
  }
}

// 基本信息表单
const infoFormRef = ref<FormInst | null>(null)
const infoLoading = ref(false)
const infoForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: ''
})

const infoRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 20, message: '昵称不能超过20个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

function resetInfoForm() {
  if (userStore.userInfo) {
    infoForm.username = userStore.userInfo.username || ''
    infoForm.nickname = userStore.userInfo.nickname || ''
    infoForm.email = userStore.userInfo.email || ''
    infoForm.phone = userStore.userInfo.phone || ''
  }
}

async function handleUpdateInfo() {
  try {
    await infoFormRef.value?.validate()
    infoLoading.value = true

    await userStore.updateUserInfo({
      nickname: infoForm.nickname,
      email: infoForm.email,
      phone: infoForm.phone
    })

    message.success('个人信息更新成功')
  } catch (error: any) {
    if (error?.message) {
      message.error(error.message)
    }
  } finally {
    infoLoading.value = false
  }
}

// 密码表单
const passwordFormRef = ref<FormInst | null>(null)
const passwordLoading = ref(false)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule, value) => {
        if (value !== passwordForm.newPassword) {
          return new Error('两次输入的密码不一致')
        }
        return true
      },
      trigger: 'blur'
    }
  ]
}

function resetPasswordForm() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.restoreValidation()
}

async function handleChangePassword() {
  try {
    await passwordFormRef.value?.validate()
    passwordLoading.value = true

    await userStore.changePassword(passwordForm.oldPassword, passwordForm.newPassword)

    message.success('密码修改成功')
    resetPasswordForm()
  } catch (error: any) {
    if (error?.message) {
      message.error(error.message)
    }
  } finally {
    passwordLoading.value = false
  }
}

// 格式化日期
function formatDate(dateStr?: string): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  resetInfoForm()
})
</script>

<style scoped>
/* ========================================
   个人中心页面样式
   企业级CRM风格 - 蓝色主题
   ======================================== */

.profile-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px 0;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 内容布局 */
.profile-content {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 24px;
}

/* 左侧用户卡片 */
.profile-card {
  background: white;
  border-radius: 16px;
  padding: 32px 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  height: fit-content;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.user-avatar {
  border: 4px solid #e2e8f0;
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 12px;
  gap: 4px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-wrapper:hover .user-avatar {
  border-color: #2563eb;
}

.user-info-brief {
  text-align: center;
  margin-bottom: 24px;
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px 0;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
}

.user-role {
  margin: 0;
}

.user-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px 0;
  border-top: 1px solid #f1f5f9;
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 24px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 20px;
}

.stat-value {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #94a3b8;
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: #e2e8f0;
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: white;
  color: #475569;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
}

.action-btn:hover {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
}

/* 右侧详细信息 */
.profile-detail {
  background: white;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

/* Tab 头部 */
.tab-header {
  display: flex;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 24px;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  position: relative;
  transition: all 0.2s ease;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
}

.tab-btn:hover {
  color: #2563eb;
}

.tab-btn.active {
  color: #2563eb;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: #2563eb;
  border-radius: 2px 2px 0 0;
}

/* Tab 内容 */
.tab-content {
  padding: 32px;
}

/* 表单区块 */
.form-section {
  margin-bottom: 32px;
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
}

.section-desc {
  font-size: 14px;
  color: #64748b;
  margin: -12px 0 20px 0;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px 24px;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f1f5f9;
}

/* 安全设置 */
.security-section {
  margin-bottom: 32px;
}

.password-form {
  max-width: 480px;
}

/* 安全提示 */
.security-tips {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e2e8f0;
}

.tips-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #475569;
  margin: 0 0 12px 0;
  font-family: 'Microsoft YaHei', '微软雅黑', -apple-system, BlinkMacSystemFont, sans-serif;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.8;
}

.tips-list li {
  margin-bottom: 4px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

/* 表单样式覆盖 */
:deep(.n-form-item) {
  margin-bottom: 0;
}

:deep(.n-input) {
  border-radius: 8px;
}

:deep(.n-input .n-input__prefix) {
  color: #94a3b8;
}

:deep(.n-button) {
  border-radius: 8px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .profile-content {
    grid-template-columns: 1fr;
  }

  .profile-card {
    max-width: 400px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .profile-page {
    padding: 0;
  }

  .page-title {
    font-size: 20px;
  }

  .profile-card {
    max-width: none;
    border-radius: 12px;
    padding: 24px 16px;
  }

  .profile-detail {
    border-radius: 12px;
  }

  .tab-content {
    padding: 20px 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .tab-header {
    padding: 0 16px;
  }

  .tab-btn {
    padding: 14px 12px;
    font-size: 13px;
  }
}
</style>
