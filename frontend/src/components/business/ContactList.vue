<template>
  <div class="contact-list">
    <!-- 列表头部 -->
    <div class="list-header">
      <h3 class="list-title">联系人列表</h3>
      <n-button type="primary" size="small" @click="handleAdd" class="add-btn">
        <template #icon>
          <n-icon><AddOutline /></n-icon>
        </template>
        添加联系人
      </n-button>
    </div>

    <!-- 联系人卡片列表 -->
    <div class="contact-cards" v-if="contacts.length > 0">
      <div
        v-for="contact in contacts"
        :key="contact.id"
        class="contact-card"
        :class="{ 'is-primary': contact.isPrimary === 1 }"
      >
        <!-- 主要联系人标记 -->
        <div v-if="contact.isPrimary === 1" class="primary-badge">
          <n-icon size="12"><StarOutline /></n-icon>
          主要联系人
        </div>

        <!-- 联系人信息 -->
        <div class="contact-info">
          <div class="contact-avatar">
            <n-icon size="24"><PersonOutline /></n-icon>
          </div>
          <div class="contact-details">
            <div class="contact-name">
              {{ contact.name }}
              <span v-if="contact.position" class="contact-position">{{ contact.position }}</span>
            </div>
            <div class="contact-meta">
              <span v-if="contact.department" class="meta-item">
                <n-icon size="14"><BusinessOutline /></n-icon>
                {{ contact.department }}
              </span>
              <span v-if="contact.mobile" class="meta-item">
                <n-icon size="14"><CallOutline /></n-icon>
                {{ contact.mobile }}
              </span>
              <span v-if="contact.email" class="meta-item">
                <n-icon size="14"><MailOutline /></n-icon>
                {{ contact.email }}
              </span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="contact-actions">
          <n-tooltip trigger="hover">
            <template #trigger>
              <button class="action-icon edit" @click="handleEdit(contact)">
                <n-icon size="16"><CreateOutline /></n-icon>
              </button>
            </template>
            编辑
          </n-tooltip>
          <n-tooltip trigger="hover" v-if="contact.isPrimary !== 1">
            <template #trigger>
              <button class="action-icon primary" @click="handleSetPrimary(contact.id)">
                <n-icon size="16"><StarOutline /></n-icon>
              </button>
            </template>
            设为主要联系人
          </n-tooltip>
          <n-popconfirm @positive-click="handleDelete(contact.id)">
            <template #trigger>
              <n-tooltip trigger="hover">
                <template #trigger>
                  <button class="action-icon delete">
                    <n-icon size="16"><TrashOutline /></n-icon>
                  </button>
                </template>
                删除
              </n-tooltip>
            </template>
            确定要删除该联系人吗？
          </n-popconfirm>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <n-icon size="48" class="empty-icon"><PeopleOutline /></n-icon>
      <p class="empty-text">暂无联系人</p>
      <n-button type="primary" size="small" @click="handleAdd">
        添加第一个联系人
      </n-button>
    </div>

    <!-- 联系人表单弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :title="formTitle"
      preset="card"
      class="contact-form-modal"
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
import { ref, onMounted, watch } from 'vue'
import {
  NButton,
  NIcon,
  NTooltip,
  NPopconfirm,
  NModal,
  useMessage
} from 'naive-ui'
import {
  AddOutline,
  PersonOutline,
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
const handleDelete = async (id: number) => {
  try {
    await deleteContact(id)
    message.success('删除成功')
    loadContacts()
  } catch (error) {
    message.error('删除失败')
  }
}

// 设为主要联系人
const handleSetPrimary = async (id: number) => {
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
.contact-list {
  width: 100%;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.list-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.add-btn {
  border-radius: 8px;
}

/* 联系人卡片 */
.contact-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.contact-card:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.contact-card.is-primary {
  background: linear-gradient(135deg, #fef3c7 0%, #fef9c3 100%);
  border-color: #fcd34d;
}

.primary-badge {
  position: absolute;
  top: -8px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: #f59e0b;
  color: white;
  font-size: 11px;
  font-weight: 500;
  border-radius: 10px;
}

/* 联系人信息 */
.contact-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.contact-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: #dbeafe;
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.contact-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.contact-name {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-position {
  font-size: 12px;
  font-weight: 400;
  color: #64748b;
  padding: 2px 8px;
  background: #f1f5f9;
  border-radius: 4px;
}

.contact-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
}

/* 操作按钮 */
.contact-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-icon {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.action-icon.edit {
  color: #f59e0b;
}

.action-icon.edit:hover {
  background: #fef3c7;
}

.action-icon.primary {
  color: #2563eb;
}

.action-icon.primary:hover {
  background: #dbeafe;
}

.action-icon.delete {
  color: #ef4444;
}

.action-icon.delete:hover {
  background: #fee2e2;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  background: #f8fafc;
  border: 1px dashed #e2e8f0;
  border-radius: 12px;
}

.empty-icon {
  color: #cbd5e1;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
  margin: 0 0 16px 0;
}

/* 弹窗 */
.contact-form-modal {
  width: 600px;
}

/* 响应式 */
@media (max-width: 768px) {
  .contact-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .contact-actions {
    width: 100%;
    justify-content: flex-end;
    padding-top: 12px;
    border-top: 1px solid #e2e8f0;
  }

  .contact-meta {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
