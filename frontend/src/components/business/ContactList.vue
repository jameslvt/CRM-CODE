<template>
  <div class="contact-list-container">
    <!-- 卡片头部 -->
    <div class="contact-header">
      <div class="header-left">
        <div class="header-icon">
          <n-icon size="18"><PeopleOutline /></n-icon>
        </div>
        <div class="header-text">
          <h3 class="header-title">联系人</h3>
          <span class="header-count">{{ contacts.length }} 位联系人</span>
        </div>
      </div>
      <n-button
        type="primary"
        size="small"
        @click="handleAdd"
        :style="{ borderRadius: '8px' }"
      >
        <template #icon>
          <n-icon><AddOutline /></n-icon>
        </template>
        添加
      </n-button>
    </div>

    <!-- 联系人列表 -->
    <div class="contact-content">
      <template v-if="contacts.length > 0">
        <div class="contact-grid">
          <div
            v-for="(contact, index) in contacts"
            :key="contact.id"
            class="contact-item"
            :class="{ 'is-primary': contact.isPrimary === 1 }"
            :style="{ animationDelay: `${index * 50}ms` }"
          >
            <!-- 主要联系人标识 -->
            <div v-if="contact.isPrimary === 1" class="primary-indicator">
              <n-icon size="10"><StarOutline /></n-icon>
              <span>主要</span>
            </div>

            <!-- 头像区域 -->
            <div class="avatar-section">
              <div class="avatar" :class="{ 'avatar-primary': contact.isPrimary === 1 }">
                <span class="avatar-text">{{ getInitials(contact.name) }}</span>
              </div>
            </div>

            <!-- 信息区域 -->
            <div class="info-section">
              <div class="name-row">
                <span class="contact-name">{{ contact.name }}</span>
                <n-tag
                  v-if="contact.position"
                  size="tiny"
                  :bordered="false"
                  :style="{ background: '#f1f5f9', color: '#475569' }"
                >
                  {{ contact.position }}
                </n-tag>
              </div>

              <div class="detail-row" v-if="contact.department">
                <n-icon size="12" color="#94a3b8"><BusinessOutline /></n-icon>
                <span>{{ contact.department }}</span>
              </div>

              <div class="detail-row" v-if="contact.mobile">
                <n-icon size="12" color="#94a3b8"><CallOutline /></n-icon>
                <span>{{ contact.mobile }}</span>
              </div>

              <div class="detail-row" v-if="contact.email">
                <n-icon size="12" color="#94a3b8"><MailOutline /></n-icon>
                <span class="email-text">{{ contact.email }}</span>
              </div>
            </div>

            <!-- 操作区域 -->
            <div class="action-section">
              <n-tooltip trigger="hover" placement="top">
                <template #trigger>
                  <button class="action-btn action-edit" @click="handleEdit(contact)">
                    <n-icon size="14"><CreateOutline /></n-icon>
                  </button>
                </template>
                编辑联系人
              </n-tooltip>

              <n-tooltip v-if="contact.isPrimary !== 1" trigger="hover" placement="top">
                <template #trigger>
                  <button class="action-btn action-star" @click="handleSetPrimary(contact.id)">
                    <n-icon size="14"><StarOutline /></n-icon>
                  </button>
                </template>
                设为主要联系人
              </n-tooltip>

              <n-popconfirm
                @positive-click="handleDelete(contact.id)"
                positive-text="确认删除"
                negative-text="取消"
              >
                <template #trigger>
                  <n-tooltip trigger="hover" placement="top">
                    <template #trigger>
                      <button class="action-btn action-delete">
                        <n-icon size="14"><TrashOutline /></n-icon>
                      </button>
                    </template>
                    删除联系人
                  </n-tooltip>
                </template>
                <div style="max-width: 200px">
                  确定要删除联系人 <strong>{{ contact.name }}</strong> 吗？此操作不可恢复。
                </div>
              </n-popconfirm>
            </div>
          </div>
        </div>
      </template>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-illustration">
          <div class="empty-circle">
            <div class="empty-icon-wrapper">
              <n-icon size="32" color="#94a3b8"><PeopleOutline /></n-icon>
            </div>
          </div>
          <div class="empty-dots">
            <span class="dot dot-1"></span>
            <span class="dot dot-2"></span>
            <span class="dot dot-3"></span>
          </div>
        </div>
        <div class="empty-content">
          <h4 class="empty-title">暂无联系人</h4>
          <p class="empty-desc">添加联系人以便更好地管理客户关系</p>
          <n-button
            type="primary"
            @click="handleAdd"
            :style="{ borderRadius: '8px', marginTop: '8px' }"
          >
            <template #icon>
              <n-icon><AddOutline /></n-icon>
            </template>
            添加第一位联系人
          </n-button>
        </div>
      </div>
    </div>

    <!-- 联系人表单弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :title="formTitle"
      preset="card"
      :style="{ width: '560px', borderRadius: '16px' }"
      :segmented="{ content: true }"
    >
      <contact-form
        ref="contactFormRef"
        :form-data="currentContact"
        :customer-id="customerId"
        @submit="handleFormSubmit"
        @cancel="showFormModal = false"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import {
  NButton,
  NIcon,
  NTooltip,
  NPopconfirm,
  NModal,
  NTag,
  useMessage
} from 'naive-ui'
import {
  AddOutline,
  BusinessOutline,
  CallOutline,
  MailOutline,
  CreateOutline,
  StarOutline,
  TrashOutline,
  PeopleOutline
} from '@vicons/ionicons5'
import { getContactsByCustomerId, deleteContact, setPrimaryContact } from '@/api/business/contact'
import type { Contact } from '@/types/business/customer'
import ContactForm from './ContactForm.vue'

const props = defineProps<{
  customerId: number
}>()

// 获取姓名首字母
const getInitials = (name: string): string => {
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}

const message = useMessage()

const contacts = ref<Contact[]>([])
const loading = ref(false)
const showFormModal = ref(false)
const formTitle = ref('添加联系人')
const currentContact = ref<Partial<Contact>>({})
const contactFormRef = ref()

// 加载联系人列表
const loadContacts = async () => {
  if (!props.customerId) return

  loading.value = true
  try {
    contacts.value = await getContactsByCustomerId(props.customerId)
  } catch (error) {
    message.error('加载联系人失败')
  } finally {
    loading.value = false
  }
}

// 监听客户ID变化
watch(
  () => props.customerId,
  (newVal) => {
    if (newVal) {
      loadContacts()
    }
  },
  { immediate: true }
)

// 添加联系人
const handleAdd = () => {
  formTitle.value = '添加联系人'
  currentContact.value = { customerId: props.customerId }
  showFormModal.value = true
}

// 编辑联系人
const handleEdit = (contact: Contact) => {
  formTitle.value = '编辑联系人'
  currentContact.value = { ...contact }
  showFormModal.value = true
}

// 删除联系人
const handleDelete = async (id: string) => {
  try {
    await deleteContact(id)
    message.success('删除成功')
    loadContacts()
  } catch (error) {
    message.error('删除失败')
  }
}

// 设为主要联系人
const handleSetPrimary = async (id: string) => {
  try {
    await setPrimaryContact(id)
    message.success('已设为主要联系人')
    loadContacts()
  } catch (error) {
    message.error('设置失败')
  }
}

// 表单提交
const handleFormSubmit = () => {
  showFormModal.value = false
  loadContacts()
}

// 暴露方法
defineExpose({
  loadContacts
})
</script>

<style scoped>
/* 容器 */
.contact-list-container {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

/* 头部 */
.contact-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(to right, #f8fafc, #ffffff);
  border-bottom: 1px solid #f1f5f9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
  line-height: 1.2;
}

.header-count {
  font-size: 12px;
  color: #64748b;
}

/* 内容区 */
.contact-content {
  padding: 16px;
}

/* 联系人网格 */
.contact-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 联系人卡片 */
.contact-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: #f8fafc;
  border: 1px solid transparent;
  border-radius: 12px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  animation: slideIn 0.3s ease forwards;
  opacity: 0;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.contact-item:hover {
  background: #ffffff;
  border-color: #e2e8f0;
  box-shadow: 0 4px 12px -2px rgba(15, 23, 42, 0.08);
}

.contact-item.is-primary {
  background: linear-gradient(135deg, #eff6ff 0%, #f0f9ff 100%);
  border-color: #bfdbfe;
}

.contact-item.is-primary:hover {
  border-color: #93c5fd;
  box-shadow: 0 4px 12px -2px rgba(37, 99, 235, 0.12);
}

/* 主要联系人标识 */
.primary-indicator {
  position: absolute;
  top: -1px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 3px 8px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: #ffffff;
  font-size: 10px;
  font-weight: 500;
  border-radius: 0 0 6px 6px;
  box-shadow: 0 2px 4px rgba(37, 99, 235, 0.3);
}

/* 头像 */
.avatar-section {
  flex-shrink: 0;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
}

.avatar-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 4px 8px -2px rgba(37, 99, 235, 0.4);
}

.avatar-text {
  font-size: 16px;
  font-weight: 600;
  color: #475569;
}

.avatar-primary .avatar-text {
  color: #ffffff;
}

/* 信息区域 */
.info-section {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.contact-name {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
}

.email-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 180px;
}

/* 操作区域 */
.action-section {
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.contact-item:hover .action-section {
  opacity: 1;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.action-edit {
  color: #64748b;
}

.action-edit:hover {
  background: #f1f5f9;
  color: #2563eb;
}

.action-star {
  color: #64748b;
}

.action-star:hover {
  background: #fef3c7;
  color: #f59e0b;
}

.action-delete {
  color: #64748b;
}

.action-delete:hover {
  background: #fee2e2;
  color: #ef4444;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 24px;
}

.empty-illustration {
  position: relative;
  margin-bottom: 20px;
}

.empty-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.empty-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty-dots {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
}

.dot {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #cbd5e1;
  animation: pulse 2s ease-in-out infinite;
}

.dot-1 {
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  animation-delay: 0s;
}

.dot-2 {
  bottom: 10px;
  left: 10px;
  animation-delay: 0.4s;
}

.dot-3 {
  bottom: 10px;
  right: 10px;
  animation-delay: 0.8s;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.2);
  }
}

.empty-content {
  text-align: center;
}

.empty-title {
  font-size: 15px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 6px 0;
}

.empty-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
  line-height: 1.5;
}

/* 响应式 */
@media (max-width: 640px) {
  .contact-item {
    flex-wrap: wrap;
  }

  .action-section {
    width: 100%;
    justify-content: flex-end;
    padding-top: 10px;
    margin-top: 6px;
    border-top: 1px solid #f1f5f9;
    opacity: 1;
  }

  .email-text {
    max-width: 140px;
  }
}
</style>
