<template>
  <div class="ai-assistant-dialog">
    <!-- 触发按钮 -->
    <div class="ai-trigger" @click="showDialog = true">
      <div class="trigger-icon">
        <n-icon size="24"><SparklesOutline /></n-icon>
      </div>
      <span class="trigger-text">AI 助手</span>
    </div>

    <!-- 对话框 -->
    <n-modal
      v-model:show="showDialog"
      preset="card"
      :style="{ width: '520px', maxHeight: '80vh' }"
      :bordered="false"
      :closable="true"
      :mask-closable="true"
      class="ai-modal"
    >
      <template #header>
        <div class="modal-header">
          <div class="header-left">
            <div class="ai-avatar">
              <n-icon size="20"><SparklesOutline /></n-icon>
            </div>
            <div class="header-info">
              <h3 class="header-title">AI 智能助手</h3>
              <span class="header-status" :class="{ online: aiStatus?.available }">
                {{ aiStatus?.available ? '在线' : '离线' }}
              </span>
            </div>
          </div>
          <div class="header-actions">
            <n-button quaternary circle size="small" @click="clearMessages">
              <template #icon>
                <n-icon><TrashOutline /></n-icon>
              </template>
            </n-button>
          </div>
        </div>
      </template>

      <div class="modal-body">
        <!-- 功能选择 -->
        <div class="feature-tabs">
          <button
            v-for="feature in features"
            :key="feature.type"
            class="feature-tab"
            :class="{ active: activeFeature === feature.type }"
            @click="activeFeature = feature.type"
          >
            <n-icon size="16"><component :is="feature.icon" /></n-icon>
            <span>{{ feature.name }}</span>
          </button>
        </div>

        <!-- 对话区域 -->
        <div v-if="activeFeature === 'chat'" class="chat-area">
          <!-- 消息列表 -->
          <div ref="messagesRef" class="messages-container">
            <div v-if="messages.length === 0" class="empty-messages">
              <div class="welcome-message">
                <n-icon size="48" class="welcome-icon"><SparklesOutline /></n-icon>
                <h4>你好，我是 AI 助手</h4>
                <p>我可以帮你创建线索、查询客户、分析商机等</p>
              </div>
              <div class="quick-prompts">
                <button
                  v-for="prompt in quickPrompts"
                  :key="prompt.id"
                  class="quick-prompt-btn"
                  @click="handleQuickPrompt(prompt)"
                >
                  {{ prompt.title }}
                </button>
              </div>
            </div>

            <div
              v-for="msg in messages"
              :key="msg.id"
              class="message-item"
              :class="msg.role"
            >
              <div class="message-avatar">
                <n-icon v-if="msg.role === 'assistant'" size="16"><SparklesOutline /></n-icon>
                <n-icon v-else size="16"><PersonOutline /></n-icon>
              </div>
              <div class="message-content">
                <div v-if="msg.loading" class="message-loading">
                  <span class="loading-dot"></span>
                  <span class="loading-dot"></span>
                  <span class="loading-dot"></span>
                </div>
                <div v-else-if="msg.error" class="message-error">
                  <n-icon size="14"><AlertCircleOutline /></n-icon>
                  <span>{{ msg.content }}</span>
                </div>
                <div v-else class="message-text">{{ msg.content }}</div>
                <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="input-area">
            <n-input
              v-model:value="inputMessage"
              type="textarea"
              placeholder="输入你的问题..."
              :autosize="{ minRows: 1, maxRows: 4 }"
              :disabled="isLoading"
              @keydown.enter.exact.prevent="handleSend"
            />
            <n-button
              type="primary"
              :loading="isLoading"
              :disabled="!inputMessage.trim()"
              @click="handleSend"
            >
              <template #icon>
                <n-icon><SendOutline /></n-icon>
              </template>
            </n-button>
          </div>
        </div>

        <!-- 智能提取区域 -->
        <div v-else-if="activeFeature === 'extract'" class="extract-area">
          <div class="extract-header">
            <n-select
              v-model:value="extractType"
              :options="extractTypeOptions"
              placeholder="选择提取类型"
              style="width: 140px"
            />
          </div>
          <n-input
            v-model:value="extractInput"
            type="textarea"
            placeholder="输入自然语言描述，例如：张三，电话13800138000，来自官网咨询，对我们的产品很感兴趣"
            :autosize="{ minRows: 4, maxRows: 8 }"
          />
          <n-button
            type="primary"
            block
            :loading="extractLoading"
            :disabled="!extractInput.trim()"
            @click="handleExtract"
          >
            <template #icon>
              <n-icon><FlashOutline /></n-icon>
            </template>
            智能提取
          </n-button>

          <!-- 提取结果 -->
          <div v-if="extractResult" class="extract-result">
            <div class="result-header">
              <span class="result-title">提取结果</span>
              <n-button text type="primary" size="small" @click="handleUseExtractResult">
                使用此结果
              </n-button>
            </div>
            <div class="result-content">
              <div
                v-for="(value, key) in extractResult"
                :key="key"
                class="result-item"
              >
                <span class="result-label">{{ getFieldLabel(key) }}</span>
                <span class="result-value">{{ value || '-' }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 自然语言查询区域 -->
        <div v-else-if="activeFeature === 'query'" class="query-area">
          <div class="query-header">
            <n-select
              v-model:value="queryEntityType"
              :options="entityTypeOptions"
              placeholder="选择查询对象"
              style="width: 140px"
            />
          </div>
          <n-input
            v-model:value="queryInput"
            type="textarea"
            placeholder="用自然语言描述你要查询的内容，例如：查找本月新增的所有A级客户"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
          <n-button
            type="primary"
            block
            :loading="queryLoading"
            :disabled="!queryInput.trim()"
            @click="handleQuery"
          >
            <template #icon>
              <n-icon><SearchOutline /></n-icon>
            </template>
            解析查询
          </n-button>

          <!-- 查询结果 -->
          <div v-if="queryResult" class="query-result">
            <div class="result-header">
              <span class="result-title">解析结果</span>
              <n-button text type="primary" size="small" @click="handleApplyQuery">
                应用查询
              </n-button>
            </div>
            <div class="result-content">
              <div v-if="queryResult.filters?.length" class="filters-list">
                <div class="filter-title">筛选条件：</div>
                <div
                  v-for="(filter, index) in queryResult.filters"
                  :key="index"
                  class="filter-item"
                >
                  <span class="filter-field">{{ filter.field }}</span>
                  <span class="filter-operator">{{ getOperatorLabel(filter.operator) }}</span>
                  <span class="filter-value">{{ filter.value }}</span>
                </div>
              </div>
              <div v-if="queryResult.orderBy" class="order-info">
                <span>排序：{{ queryResult.orderBy }}</span>
                <span>{{ queryResult.orderDirection === 'desc' ? '降序' : '升序' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NModal, NButton, NInput, NIcon, NSelect, useMessage } from 'naive-ui'
import {
  SparklesOutline,
  TrashOutline,
  SendOutline,
  PersonOutline,
  AlertCircleOutline,
  FlashOutline,
  SearchOutline,
  ChatbubbleOutline,
  DocumentTextOutline
} from '@vicons/ionicons5'
import {
  getAiStatus,
  chat,
  extractLeadInfo,
  extractCustomerInfo,
  extractOpportunityInfo,
  extractContactInfo,
  parseQuery
} from '@/api/ai'
import type {
  AiStatusInfo,
  ChatMessage,
  AiFeatureType,
  QueryCondition,
  EntityType,
  QuickPrompt
} from '@/types/ai'
import { QUICK_PROMPTS, ENTITY_TYPE_OPTIONS } from '@/types/ai'

const router = useRouter()
const message = useMessage()

// 对话框状态
const showDialog = ref(false)
const aiStatus = ref<AiStatusInfo | null>(null)

// 功能配置
const features = [
  { type: 'chat' as AiFeatureType, name: '智能对话', icon: ChatbubbleOutline },
  { type: 'extract' as AiFeatureType, name: '智能提取', icon: FlashOutline },
  { type: 'query' as AiFeatureType, name: '智能查询', icon: SearchOutline }
]
const activeFeature = ref<AiFeatureType>('chat')

// 对话相关
const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const isLoading = ref(false)
const messagesRef = ref<HTMLElement | null>(null)
const quickPrompts = QUICK_PROMPTS.filter(p => p.category === 'general' || p.category === 'lead')

// 智能提取相关
const extractType = ref<'lead' | 'customer' | 'opportunity' | 'contact'>('lead')
const extractInput = ref('')
const extractLoading = ref(false)
const extractResult = ref<Record<string, any> | null>(null)
const extractTypeOptions = [
  { label: '线索', value: 'lead' },
  { label: '客户', value: 'customer' },
  { label: '商机', value: 'opportunity' },
  { label: '联系人', value: 'contact' }
]

// 自然语言查询相关
const queryEntityType = ref<EntityType>('lead')
const queryInput = ref('')
const queryLoading = ref(false)
const queryResult = ref<QueryCondition | null>(null)
const entityTypeOptions = ENTITY_TYPE_OPTIONS.map(o => ({ label: o.label, value: o.value }))

// 加载 AI 状态
const loadAiStatus = async () => {
  try {
    aiStatus.value = await getAiStatus()
  } catch (error) {
    console.error('获取 AI 状态失败', error)
  }
}

// 发送消息
const handleSend = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userMessage: ChatMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: inputMessage.value.trim(),
    timestamp: Date.now()
  }
  messages.value.push(userMessage)

  const assistantMessage: ChatMessage = {
    id: (Date.now() + 1).toString(),
    role: 'assistant',
    content: '',
    timestamp: Date.now(),
    loading: true
  }
  messages.value.push(assistantMessage)

  inputMessage.value = ''
  isLoading.value = true
  scrollToBottom()

  try {
    const response = await chat({ message: userMessage.content })
    assistantMessage.content = response
    assistantMessage.loading = false
  } catch (error: any) {
    assistantMessage.content = error.message || '请求失败，请稍后重试'
    assistantMessage.loading = false
    assistantMessage.error = true
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

// 快捷提示
const handleQuickPrompt = (prompt: QuickPrompt) => {
  inputMessage.value = prompt.content
}

// 清空消息
const clearMessages = () => {
  messages.value = []
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

// 格式化时间
const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 智能提取
const handleExtract = async () => {
  if (!extractInput.value.trim()) return

  extractLoading.value = true
  extractResult.value = null

  try {
    let result: Record<string, any>
    switch (extractType.value) {
      case 'lead':
        result = await extractLeadInfo(extractInput.value)
        break
      case 'customer':
        result = await extractCustomerInfo(extractInput.value)
        break
      case 'opportunity':
        result = await extractOpportunityInfo(extractInput.value)
        break
      case 'contact':
        result = await extractContactInfo(extractInput.value)
        break
      default:
        result = {}
    }
    extractResult.value = result
    message.success('提取成功')
  } catch (error: any) {
    message.error(error.message || '提取失败')
  } finally {
    extractLoading.value = false
  }
}

// 使用提取结果
const handleUseExtractResult = () => {
  if (!extractResult.value) return

  // 根据类型跳转到对应的创建页面
  const routes: Record<string, string> = {
    lead: '/business/lead',
    customer: '/business/customer',
    opportunity: '/business/opportunity',
    contact: '/business/contact'
  }

  showDialog.value = false
  router.push({
    path: routes[extractType.value],
    query: { action: 'create', data: JSON.stringify(extractResult.value) }
  })
}

// 字段标签映射
const getFieldLabel = (key: string) => {
  const labels: Record<string, string> = {
    name: '名称',
    contactName: '联系人',
    phone: '电话',
    email: '邮箱',
    company: '公司',
    source: '来源',
    industry: '行业',
    scale: '规模',
    address: '地址',
    website: '网站',
    amount: '金额',
    expectedDate: '预计成交日期',
    stage: '阶段',
    position: '职位',
    department: '部门',
    remark: '备注'
  }
  return labels[key] || key
}

// 自然语言查询
const handleQuery = async () => {
  if (!queryInput.value.trim()) return

  queryLoading.value = true
  queryResult.value = null

  try {
    const result = await parseQuery(queryEntityType.value, queryInput.value)
    queryResult.value = result
    message.success('解析成功')
  } catch (error: any) {
    message.error(error.message || '解析失败')
  } finally {
    queryLoading.value = false
  }
}

// 应用查询
const handleApplyQuery = () => {
  if (!queryResult.value) return

  const routes: Record<string, string> = {
    lead: '/business/lead',
    customer: '/business/customer',
    opportunity: '/business/opportunity',
    contract: '/business/contract'
  }

  showDialog.value = false
  router.push({
    path: routes[queryEntityType.value],
    query: { filters: JSON.stringify(queryResult.value) }
  })
}

// 操作符标签
const getOperatorLabel = (operator: string) => {
  const labels: Record<string, string> = {
    eq: '等于',
    like: '包含',
    gt: '大于',
    lt: '小于',
    gte: '大于等于',
    lte: '小于等于',
    in: '在...中',
    between: '介于'
  }
  return labels[operator] || operator
}

// 初始化
onMounted(() => {
  loadAiStatus()
})

// 监听对话框打开
watch(showDialog, (val) => {
  if (val) {
    loadAiStatus()
  }
})
</script>

<style scoped>
/* 触发按钮 */
.ai-trigger {
  position: fixed;
  right: 24px;
  bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 50px;
  color: white;
  cursor: pointer;
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.4);
  transition: all 0.3s ease;
  z-index: 1000;
}

.ai-trigger:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(99, 102, 241, 0.5);
}

.trigger-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  animation: sparkle 2s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.trigger-text {
  font-size: 14px;
  font-weight: 600;
}

/* 模态框头部 */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.header-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.header-status {
  font-size: 12px;
  color: #94a3b8;
}

.header-status.online {
  color: #22c55e;
}

.header-status.online::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #22c55e;
  margin-right: 4px;
}

/* 功能选项卡 */
.feature-tabs {
  display: flex;
  gap: 8px;
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 16px;
}

.feature-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  background: #f8fafc;
  border-radius: 8px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.feature-tab:hover {
  background: #f1f5f9;
  color: #0f172a;
}

.feature-tab.active {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
}

/* 对话区域 */
.chat-area {
  display: flex;
  flex-direction: column;
  height: 400px;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

/* 欢迎消息 */
.empty-messages {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 24px;
}

.welcome-message {
  text-align: center;
}

.welcome-icon {
  color: #8b5cf6;
  margin-bottom: 12px;
}

.welcome-message h4 {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 8px 0;
}

.welcome-message p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.quick-prompts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.quick-prompt-btn {
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 20px;
  font-size: 13px;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-prompt-btn:hover {
  border-color: #8b5cf6;
  color: #8b5cf6;
  background: #faf5ff;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-item.assistant .message-avatar {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
}

.message-item.user .message-avatar {
  background: #f1f5f9;
  color: #64748b;
}

.message-content {
  max-width: 80%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
}

.message-item.assistant .message-text {
  background: #f8fafc;
  color: #0f172a;
  border-bottom-left-radius: 4px;
}

.message-item.user .message-text {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-time {
  display: block;
  font-size: 11px;
  color: #94a3b8;
  margin-top: 4px;
}

.message-item.user .message-time {
  text-align: right;
}

/* 加载动画 */
.message-loading {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 16px;
  border-bottom-left-radius: 4px;
}

.loading-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #8b5cf6;
  animation: bounce 1.4s ease-in-out infinite;
}

.loading-dot:nth-child(1) { animation-delay: 0s; }
.loading-dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 错误消息 */
.message-error {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 16px;
  background: #fef2f2;
  color: #ef4444;
  border-radius: 16px;
  border-bottom-left-radius: 4px;
  font-size: 14px;
}

/* 输入区域 */
.input-area {
  display: flex;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.input-area :deep(.n-input) {
  flex: 1;
}

/* 智能提取区域 */
.extract-area,
.query-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.extract-header,
.query-header {
  display: flex;
  justify-content: flex-end;
}

/* 结果展示 */
.extract-result,
.query-result {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.result-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: white;
  border-radius: 8px;
}

.result-label {
  font-size: 13px;
  color: #64748b;
}

.result-value {
  font-size: 13px;
  font-weight: 500;
  color: #0f172a;
}

/* 查询结果 */
.filters-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-title {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 4px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: white;
  border-radius: 8px;
}

.filter-field {
  font-size: 13px;
  font-weight: 500;
  color: #8b5cf6;
}

.filter-operator {
  font-size: 12px;
  color: #64748b;
  padding: 2px 8px;
  background: #f1f5f9;
  border-radius: 4px;
}

.filter-value {
  font-size: 13px;
  color: #0f172a;
}

.order-info {
  display: flex;
  gap: 12px;
  padding: 8px 12px;
  background: white;
  border-radius: 8px;
  font-size: 13px;
  color: #64748b;
}

/* 模态框样式覆盖 */
:deep(.n-card-header) {
  padding: 16px 20px;
}

:deep(.n-card__content) {
  padding: 0 20px 20px;
}
</style>
