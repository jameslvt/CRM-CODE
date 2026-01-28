<template>
  <div 
    class="ai-assistant-container"
    ref="containerRef"
    :style="containerStyle"
  >
    <!-- 悬浮面板 -->
    <transition name="panel-slide">
      <div 
        v-show="showDialog" 
        class="ai-panel glass-effect"
        :class="{ 'align-left': isLeftSide }"
        :style="panelStyle"
      >
        <!-- Resize Handle -->
        <div 
          class="resize-handle" 
          :class="isLeftSide ? 'handle-right' : 'handle-left'"
          @mousedown.stop.prevent="startResize"
        ></div>

        <!-- 头部 -->
        <div class="panel-header">
          <div class="header-left">
            <div class="avatar-wrapper">
              <n-icon size="24" color="#ffffff"><SparklesOutline /></n-icon>
            </div>
            <div class="header-info">
              <h3 class="title">AI 智能助手</h3>
              <div class="status-indicator" :class="{ online: aiStatus?.available }">
                <span class="dot"></span>
                <span>{{ aiStatus?.available ? 'Online' : 'Offline' }}</span>
              </div>
            </div>
          </div>
          <div class="header-actions">
            <button class="icon-btn" @click="clearMessages" title="清空对话">
              <n-icon size="18"><TrashOutline /></n-icon>
            </button>
            <button class="icon-btn close-btn" @click="showDialog = false">
              <n-icon size="20"><CloseOutline /></n-icon>
            </button>
          </div>
        </div>

        <!-- 功能导航 -->
        <div class="nav-tabs">
          <button
            v-for="feature in features"
            :key="feature.type"
            class="tab-item"
            :class="{ active: activeFeature === feature.type }"
            @click="activeFeature = feature.type"
          >
            <span class="tab-icon">
              <component :is="feature.icon" />
            </span>
            {{ feature.name }}
          </button>
          <div class="tab-indicator" :style="indicatorStyle"></div>
        </div>

        <!-- 内容区域 -->
        <div class="panel-content">
          <!-- 1. 智能对话 -->
          <div v-if="activeFeature === 'chat'" class="view-container chat-view">
            <div ref="messagesRef" class="messages-area">
              <!-- 空状态 -->
              <div v-if="messages.length === 0" class="empty-state">
                <div class="empty-icon">
                  <n-icon size="48" color="#6366f1"><SparklesOutline /></n-icon>
                </div>
                <h4>我有什么可以帮助您的?</h4>
                <p>我是您的智能 CRM 助手，可以帮您查询数据、创建线索或解答疑问。</p>
                <div class="quick-actions">
                  <button
                    v-for="prompt in quickPrompts"
                    :key="prompt.id"
                    class="action-chip"
                    @click="handleQuickPrompt(prompt)"
                  >
                    {{ prompt.title }}
                    <n-icon size="14"><ArrowForwardOutline /></n-icon>
                  </button>
                </div>
              </div>

              <!-- 消息列表 -->
              <div v-else class="message-list">
                <div
                  v-for="msg in messages"
                  :key="msg.id"
                  class="message-row"
                  :class="msg.role"
                >
                  <div class="message-bubble">
                    <div v-if="msg.loading" class="typing-dots">
                      <span></span><span></span><span></span>
                    </div>
                    <div v-else-if="msg.error" class="error-content">
                      <n-icon size="16"><AlertCircleOutline /></n-icon>
                      {{ msg.content }}
                    </div>
                    <div v-else class="text-content">{{ msg.content }}</div>
                  </div>
                  <span class="timestamp">{{ formatTime(msg.timestamp) }}</span>
                </div>
              </div>
            </div>

            <!-- 输入框 -->
            <div class="input-section">
              <div class="input-wrapper glass-inner">
                <n-input
                  v-model:value="inputMessage"
                  type="textarea"
                  placeholder="输入问题或指令..."
                  :autosize="{ minRows: 1, maxRows: 4 }"
                  class="custom-textarea"
                  :disabled="isLoading"
                  @keydown.enter.exact.prevent="handleSend"
                />
                <button 
                  class="send-btn"
                  :class="{ disabled: !inputMessage.trim() || isLoading }"
                  @click="handleSend"
                >
                  <n-icon size="18"><SendOutline /></n-icon>
                </button>
              </div>
            </div>
          </div>

          <!-- 2. 智能提取 -->
          <div v-else-if="activeFeature === 'extract'" class="view-container feature-view">
            <div class="form-group">
              <label>提取类型</label>
              <n-select v-model:value="extractType" :options="extractTypeOptions" />
            </div>
            
            <div class="form-group flex-1">
              <label>文本内容</label>
              <n-input
                v-model:value="extractInput"
                type="textarea"
                class="glass-input full-height"
                placeholder="请粘贴包含客户信息的文本，例如聊天记录、邮件内容等..."
              />
            </div>

            <div class="action-footer">
              <n-button
                type="primary"
                block
                round
                size="large"
                class="gradient-btn"
                :loading="extractLoading"
                :disabled="!extractInput.trim()"
                @click="handleExtract"
              >
                <template #icon><n-icon><FlashOutline /></n-icon></template>
                开始智能提取
              </n-button>
            </div>

            <!-- 提取结果卡片 -->
            <div v-if="extractResult" class="result-card">
              <div class="card-header">
                <span class="label">识别结果</span>
                <n-button size="tiny" type="primary" secondary @click="handleUseExtractResult">
                  使用数据创建
                </n-button>
              </div>
              <div class="key-value-list">
                <div v-for="(value, key) in extractResult" :key="key" class="kv-item">
                  <span class="k">{{ getFieldLabel(key) }}</span>
                  <span class="v">{{ value || '-' }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 3. 智能查询 -->
          <div v-else-if="activeFeature === 'query'" class="view-container feature-view">
           <div class="form-group">
              <label>查询对象</label>
              <n-select v-model:value="queryEntityType" :options="entityTypeOptions" />
            </div>

            <div class="form-group flex-1">
              <label>自然语言描述</label>
              <n-input
                v-model:value="queryInput"
                type="textarea"
                class="glass-input full-height"
                placeholder="例如：查询上个月所有成交金额超过100万的 VIP 客户..."
              />
            </div>

            <div class="action-footer">
              <n-button
                type="primary"
                block
                round
                size="large"
                class="gradient-btn"
                :loading="queryLoading"
                :disabled="!queryInput.trim()"
                @click="handleQuery"
              >
                <template #icon><n-icon><SearchOutline /></n-icon></template>
                {{ queryListResult.length > 0 ? '重新查询' : '立即查询' }}
              </n-button>
            </div>

            <!-- 查询结果列表 -->
            <div v-if="queryListResult.length > 0" class="result-list-container">
               <div class="list-header">
                 <span>找到 {{ queryTotal }} 条结果</span>
                 <n-button text type="primary" size="tiny" @click="handleApplyQuery">查看全部</n-button>
               </div>
               <div class="result-list">
                 <div 
                   v-for="item in queryListResult" 
                   :key="item.id" 
                   class="result-item"
                   @click="handleResultClick(item)"
                 >
                   <div class="item-icon-box" :class="queryEntityType">
                     <n-icon v-if="queryEntityType==='lead'"><PersonOutline /></n-icon>
                     <n-icon v-else-if="queryEntityType==='customer'"><PeopleOutline /></n-icon>
                     <n-icon v-else-if="queryEntityType==='opportunity'"><BriefcaseOutline /></n-icon>
                     <n-icon v-else-if="queryEntityType==='contract'"><DocumentTextOutline /></n-icon>
                     <n-icon v-else-if="queryEntityType==='product'"><CubeOutline /></n-icon>
                     <n-icon v-else-if="queryEntityType==='payment'"><CardOutline /></n-icon>
                   </div>
                   <div class="item-info">
                     <div class="item-title">{{ getItemTitle(item) }}</div>
                     <div class="item-sub">{{ getItemSub(item) }}</div>
                   </div>
                   <n-icon class="arrow" size="16"><ArrowForwardOutline /></n-icon>
                 </div>
               </div>
            </div>
            
            <!-- Filter Debug View (Optional, can be hidden if user wants direct results) -->
            <!-- <div v-if="queryResult && queryListResult.length === 0" class="result-card">...</div> -->
          </div>
        </div>
      </div>
    </transition>

    <!-- 悬浮球 Trigger (可拖动) -->
    <div 
      class="ai-floater" 
      :class="{ active: showDialog, dragging: isDragging }" 
      @mousedown="startDrag"
      @click="handleClick"
    >
      <div class="floater-icon">
        <n-icon size="28" v-if="!showDialog"><SparklesOutline /></n-icon>
        <n-icon size="28" v-else><CloseOutline /></n-icon>
      </div>
      <div class="glow-ring"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onUnmounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NInput, NIcon, NSelect, useMessage } from 'naive-ui'
import {
  SparklesOutline,
  TrashOutline,
  SendOutline,
  AlertCircleOutline,
  FlashOutline,
  SearchOutline,
  ChatbubbleOutline,
  ArrowForwardOutline,
  CloseOutline,
  PersonOutline,
  PeopleOutline,
  BriefcaseOutline,
  DocumentTextOutline,
  CubeOutline,
  CardOutline
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
import { pageLeads } from '@/api/business/lead'
import { pageCustomers } from '@/api/business/customer'
import { pageOpportunities } from '@/api/business/opportunity'
import { pageContacts } from '@/api/business/contact'
import { pageContracts } from '@/api/business/contract'
import { pageProducts } from '@/api/business/product'
import { pagePaymentPlans } from '@/api/business/payment'
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

// 状态
const showDialog = ref(false)
const aiStatus = ref<AiStatusInfo | null>(null)
const activeFeature = ref<AiFeatureType>('chat')
const isLoading = ref(false)

// --- 拖拽相关状态 ---
const containerRef = ref<HTMLElement | null>(null)
const isDragging = ref(false)
const pos = ref({ right: 24, bottom: 24 })
const windowWidth = ref(window.innerWidth)

const isLeftSide = computed(() => {
  return pos.value.right > windowWidth.value / 2
})

const onResize = () => {
  windowWidth.value = window.innerWidth
}
onMounted(() => window.addEventListener('resize', onResize))
onUnmounted(() => window.removeEventListener('resize', onResize))

const startDrag = (e: MouseEvent) => {
  e.preventDefault()
  isDragging.value = false
  
  const startX = e.clientX
  const startY = e.clientY
  const startRight = pos.value.right
  const startBottom = pos.value.bottom
  
  let hasMoved = false

  const onMove = (moveEvent: MouseEvent) => {
    const deltaX = startX - moveEvent.clientX 
    const deltaY = startY - moveEvent.clientY 
    
    if (!hasMoved && (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5)) {
      hasMoved = true
      isDragging.value = true
    }

    if (hasMoved) {
      pos.value.right = startRight + deltaX
      pos.value.bottom = startBottom + deltaY
    }
  }

  const onUp = () => {
    document.removeEventListener('mousemove', onMove)
    document.removeEventListener('mouseup', onUp)
    setTimeout(() => isDragging.value = false, 0)
  }

  document.addEventListener('mousemove', onMove)
  document.addEventListener('mouseup', onUp)
}

const handleClick = () => {
  if (!isDragging.value) {
    showDialog.value = !showDialog.value
  }
}

const containerStyle = computed(() => ({
  right: `${pos.value.right}px`,
  bottom: `${pos.value.bottom}px`
}))

// --- Resize 相关状态 ---
const panelSize = ref({ width: 380, height: 600 })
const isResizing = ref(false)

const startResize = (e: MouseEvent) => {
  e.preventDefault()
  isResizing.value = true
  
  const startX = e.clientX
  const startY = e.clientY
  const startW = panelSize.value.width
  const startH = panelSize.value.height
  const isLeft = isLeftSide.value

  const onMove = (moveEvent: MouseEvent) => {
    const deltaY = startY - moveEvent.clientY
    
    let deltaX
    if (isLeft) {
      deltaX = moveEvent.clientX - startX
    } else {
      deltaX = startX - moveEvent.clientX
    }
    
    let newW = startW + deltaX
    let newH = startH + deltaY
    
    if (newW < 300) newW = 300
    if (newH < 400) newH = 400
    if (newW > 800) newW = 800
    if (newH > window.innerHeight - 100) newH = window.innerHeight - 100

    panelSize.value = { width: newW, height: newH }
  }

  const onUp = () => {
    isResizing.value = false
    document.removeEventListener('mousemove', onMove)
    document.removeEventListener('mouseup', onUp)
  }

  document.addEventListener('mousemove', onMove)
  document.addEventListener('mouseup', onUp)
}

const panelStyle = computed(() => {
  const style: any = {
    width: `${panelSize.value.width}px`,
    height: `${panelSize.value.height}px`
  }
  
  if (isLeftSide.value) {
    style.left = '0'
    style.right = 'auto'
    style.transformOrigin = 'bottom left'
  } else {
    style.right = '0'
    style.left = 'auto'
    style.transformOrigin = 'bottom right'
  }
  
  return style
})

// Tab 配置
const features = [
  { type: 'chat' as AiFeatureType, name: '对话', icon: markRaw(ChatbubbleOutline) },
  { type: 'extract' as AiFeatureType, name: '提取', icon: markRaw(FlashOutline) },
  { type: 'query' as AiFeatureType, name: '查询', icon: markRaw(SearchOutline) }
]

const indicatorStyle = computed(() => {
  const index = features.findIndex(f => f.type === activeFeature.value)
  return {
    transform: `translateX(${index * 100}%)`
  }
})

// Chat Logic
const messages = ref<ChatMessage[]>([])
const inputMessage = ref('')
const messagesRef = ref<HTMLElement | null>(null)
const quickPrompts = QUICK_PROMPTS.filter(p => p.category === 'general' || p.category === 'lead')

// Extract Logic
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

// Query Logic
const queryEntityType = ref<EntityType>('lead')
const queryInput = ref('')
const queryLoading = ref(false)
const queryResult = ref<QueryCondition | null>(null)
const queryListResult = ref<any[]>([])
const queryTotal = ref(0)
const entityTypeOptions = ENTITY_TYPE_OPTIONS.map(o => ({ label: o.label, value: o.value }))

// Methods
const loadAiStatus = async () => {
  try {
    aiStatus.value = await getAiStatus()
  } catch (error) {
    console.error('AI Status Error', error)
  }
}

const clearMessages = () => messages.value = []

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const formatTime = (ts: number) => {
  const d = new Date(ts)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

const handleQuickPrompt = (p: QuickPrompt) => {
  inputMessage.value = p.content
}

const handleSend = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return
  const content = inputMessage.value.trim()
  const userMsg: ChatMessage = {
    id: Date.now().toString(),
    role: 'user',
    content,
    timestamp: Date.now()
  }
  messages.value.push(userMsg)
  inputMessage.value = ''
  isLoading.value = true
  scrollToBottom()

  const aiMsg: ChatMessage = {
    id: (Date.now() + 1).toString(),
    role: 'assistant',
    content: '',
    timestamp: Date.now(),
    loading: true
  }
  messages.value.push(aiMsg)

  try {
    const res = await chat({ message: content })
    
    // 解析 AI 指令 :::action{...}:::
    let displayContent = res
    const actionMatch = res.match(/:::action(\{.*\}):::/)
    
    if (actionMatch) {
      try {
        const action = JSON.parse(actionMatch[1])
        displayContent = res.replace(actionMatch[0], '').trim()
        
        // 执行指令
        if (action.type === 'query' && action.content) {
          setTimeout(() => {
            activeFeature.value = 'query'
            if (action.entityType) {
              queryEntityType.value = action.entityType
            }
            queryInput.value = action.content
            handleQuery()
          }, 800)
        }
      } catch (e: any) {
        console.error('Failed to parse AI action', e)
      }
    }

    aiMsg.content = displayContent
    aiMsg.loading = false
  } catch (err: any) {
    aiMsg.content = err.message || '请求失败'
    aiMsg.error = true
    aiMsg.loading = false
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

const handleExtract = async () => {
  if (!extractInput.value.trim()) return
  extractLoading.value = true
  extractResult.value = null
  try {
    let res
    switch (extractType.value) {
      case 'lead': res = await extractLeadInfo(extractInput.value); break
      case 'customer': res = await extractCustomerInfo(extractInput.value); break
      case 'opportunity': res = await extractOpportunityInfo(extractInput.value); break
      case 'contact': res = await extractContactInfo(extractInput.value); break
    }
    extractResult.value = res || {}
    message.success('提取成功')
  } catch (e: any) {
    message.error(e.message || '失败')
  } finally {
    extractLoading.value = false
  }
}

const handleUseExtractResult = () => {
  if (!extractResult.value) return
  const routes: any = {
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

const getFieldLabel = (key: string) => {
  const map: any = { name:'名称', phone:'电话', email:'邮箱', company:'公司', source:'来源', remark:'备注', amount:'金额' }
  return map[key] || key
}

// 核心查询逻辑
const handleQuery = async () => {
  if (!queryInput.value.trim()) return
  
  queryLoading.value = true
  queryResult.value = null
  queryListResult.value = []
  queryTotal.value = 0
  
  try {
    // 1. 解析自然语言条件
    const parseRes = await parseQuery(queryEntityType.value, queryInput.value)
    queryResult.value = parseRes // 保留原始 Filter 备用
    
    // 2. 转换为列表查询参数
    const params: any = { pageNum: 1, pageSize: 5 } // 预览前5条
    if (parseRes && parseRes.filters) {
       parseRes.filters.forEach((f: any) => {
         // 简单映射：无论 = 还是 like，都作为字段参数传递
         // TODO: 处理 gt/lt 等复杂操作符，目前后端列表接口可能只支持部分字段的精确/模糊匹配
         params[f.field] = f.value
       })
    }
    
    // 3. 调用业务查询 API
    let listRes
    switch (queryEntityType.value) {
      case 'lead': listRes = await pageLeads(params); break
      case 'customer': listRes = await pageCustomers(params); break
      case 'opportunity': listRes = await pageOpportunities(params); break
      case 'contact': listRes = await pageContacts(params); break
      case 'contract': listRes = await pageContracts(params); break
      case 'product': listRes = await pageProducts(params); break
      case 'payment': listRes = await pagePaymentPlans(params); break
    }

    if (listRes && listRes.records) {
      queryListResult.value = listRes.records
      queryTotal.value = listRes.total
      if (listRes.total > 0) {
        message.success(`已为您找到 ${listRes.total} 条相关数据`)
      } else {
        message.info('未找到符合条件的数据，请尝试调整描述')
      }
    }
  } catch (e: any) {
    message.error(e.message || '查询失败')
  } finally {
    queryLoading.value = false
  }
}

const handleApplyQuery = () => {
  const routes: any = {
    lead: '/business/lead',
    customer: '/business/customer',
    opportunity: '/business/opportunity',
    contract: '/business/contract',
    contact: '/business/contact',
    product: '/business/product',
    payment: '/business/payment'
  }
  showDialog.value = false
  // 如果有 filters，带上；如果没有（可能是直接搜索关键词），也可以带 query
  const queryParams: any = {}
  if (queryResult.value) {
    queryParams.filters = JSON.stringify(queryResult.value)
  }
  
  router.push({
    path: routes[queryEntityType.value],
    query: queryParams
  })
}

const handleResultClick = (item: any) => {
  showDialog.value = false
  const pathPrefixMap: any = {
    lead: '/business/lead',
    customer: '/business/customer',
    opportunity: '/business/opportunity',
    contract: '/business/contract',
    contact: '/business/contact',
    product: '/business/product',
    payment: '/business/payment'
  }
  // 跳转详情页
  // 注意：payment 可能没有单独详情页，或者路由不同。这里暂假设一致。
  router.push(`${pathPrefixMap[queryEntityType.value]}/${item.id}`)
}

const getItemTitle = (item: any) => {
  if (queryEntityType.value === 'payment') return `${item.customerName || '未知客户'} - 第${item.period || 1}期`
  return item.name || item.customerName || item.opportunityName || item.contractName || item.title || '未命名'
}

const getItemSub = (item: any) => {
  if (queryEntityType.value === 'lead') return item.company || item.phone || item.status
  if (queryEntityType.value === 'customer') return item.industry || item.level
  if (queryEntityType.value === 'opportunity') return `¥${item.amount || 0}`
  if (queryEntityType.value === 'contract') return `¥${item.amount || 0}`
  if (queryEntityType.value === 'product') return `¥${item.price || 0}`
  if (queryEntityType.value === 'payment') return `¥${item.plannedAmount || item.amount || 0} | ${item.returnDate || item.plannedDate}`
  return item.createTime || ''
}

const getOperatorLabel = (op: string) => {
  const map: any = { eq:'等于', like:'包含', gt:'大于', lt:'小于' }
  return map[op] || op
}

watch(showDialog, (v) => v && loadAiStatus())
onMounted(loadAiStatus)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');

.ai-assistant-container {
  position: fixed;
  z-index: 1200;
  font-family: 'Inter', sans-serif;
  /* right/bottom set via JS */
}

/* 玻璃拟态面板 */
.ai-panel {
  position: absolute;
  bottom: 80px;
  /* left/right set via JS */
  /* width/height set via JS */
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  border-radius: 24px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  /* transform-origin set via JS */
}

/* Resize Handle */
.resize-handle {
  position: absolute;
  top: 0;
  width: 20px;
  height: 20px;
  z-index: 10;
}

/* 默认靠右时：Handle在左上角 */
.handle-left {
  left: 0;
  cursor: nwse-resize; 
}
.handle-left::after {
  content: '';
  position: absolute;
  top: 6px;
  left: 6px;
  width: 8px;
  height: 8px;
  border-top: 2px solid #cbd5e1;
  border-left: 2px solid #cbd5e1;
  border-top-left-radius: 4px;
  transition: all 0.2s;
}
.handle-left:hover::after {
  border-color: #6366f1;
}

/* 靠左时：Handle在右上角 */
.handle-right {
  right: 0;
  cursor: nesw-resize;
}
.handle-right::after {
  content: '';
  position: absolute;
  top: 6px;
  right: 6px;
  width: 8px;
  height: 8px;
  border-top: 2px solid #cbd5e1;
  border-right: 2px solid #cbd5e1;
  border-top-right-radius: 4px;
  transition: all 0.2s;
}
.handle-right:hover::after {
  border-color: #6366f1;
}

/* 头部 */
.panel-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3);
}

.header-info {
  display: flex;
  flex-direction: column;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #94a3b8;
}

.status-indicator .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #cbd5e1;
}

.status-indicator.online .dot {
  background: #22c55e;
  box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.2);
}

.status-indicator.online {
  color: #22c55e;
}

.icon-btn {
  background: transparent;
  border: none;
  padding: 8px;
  border-radius: 8px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
}

.icon-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #334155;
}

.close-btn:hover {
  background: #fee2e2;
  color: #ef4444;
}

/* 导航 Tabs */
.nav-tabs {
  display: flex;
  padding: 4px;
  margin: 12px 16px;
  background: #f1f5f9;
  border-radius: 12px;
  position: relative;
  flex-shrink: 0;
}

.tab-item {
  flex: 1;
  position: relative;
  z-index: 2;
  border: none;
  background: transparent;
  padding: 8px;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: color 0.3s;
}

.tab-item.active {
  color: #6366f1;
  font-weight: 600;
}

.tab-indicator {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(33.33% - 8px);
  height: calc(100% - 8px);
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
  width: calc((100% - 8px) / 3);
}

/* 内容区 */
.panel-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
}

.view-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 100%;
}

/* Chat View */
.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
  color: #64748b;
}

.empty-icon {
  width: 80px;
  height: 80px;
  background: #eef2ff;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.empty-state h4 {
  font-size: 18px;
  color: #1e293b;
  margin: 0 0 8px;
}

.quick-actions {
  margin-top: 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.action-chip {
  background: white;
  border: 1px solid #e2e8f0;
  padding: 8px 16px;
  border-radius: 20px;
  color: #475569;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s;
}

.action-chip:hover {
  border-color: #6366f1;
  color: #6366f1;
  background: #eef2ff;
}

/* 消息气泡 */
.message-row {
  display: flex;
  flex-direction: column;
  max-width: 85%;
}

.message-row.user {
  align-self: flex-end;
  align-items: flex-end;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.user .message-bubble {
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  border-bottom-right-radius: 2px;
}

.assistant .message-bubble {
  background: white;
  border: 1px solid #f1f5f9;
  color: #1e293b;
  border-bottom-left-radius: 2px;
}

.timestamp {
  font-size: 10px;
  color: #94a3b8;
  margin-top: 4px;
  padding: 0 4px;
}

/* 输入区 */
.input-section {
  padding: 16px;
  background: rgba(255,255,255,0.8);
  border-top: 1px solid rgba(0,0,0,0.05);
  flex-shrink: 0;
}

.input-wrapper {
  display: flex;
  gap: 8px;
  align-items: flex-end;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 8px;
  transition: all 0.2s;
}

.input-wrapper:focus-within {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.custom-textarea {
  flex: 1;
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 4px !important;
}

.send-btn {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  background: #6366f1;
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.send-btn:hover {
  background: #4f46e5;
  transform: scale(1.05);
}

.send-btn.disabled {
  background: #e2e8f0;
  cursor: not-allowed;
  transform: none;
}

/* Feature View 通用 */
.feature-view {
  padding: 20px;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  margin-left: 4px;
}

.glass-input {
  /* n-input override */
}
.full-height {
  height: 100%;
}
.full-height textarea {
  height: 100% !important;
}

.action-footer {
  margin-top: 8px;
  flex-shrink: 0;
}

.gradient-btn {
  background: linear-gradient(135deg, #6366f1, #8b5cf6) !important;
  border: none !important;
}

/* Result List */
.result-list-container {
  margin-top: auto;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  max-height: 40%;
  overflow: hidden;
}

.list-header {
  padding: 8px 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #64748b;
  border-bottom: 1px solid #e2e8f0;
  background: #f1f5f9;
}

.result-list {
  overflow-y: auto;
  padding: 8px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  border-radius: 8px;
  background: white;
  margin-bottom: 8px;
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.2s;
}

.result-item:hover {
  border-color: #6366f1;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.1);
  transform: translateX(2px);
}

.result-item:last-child {
  margin-bottom: 0;
}

.item-icon-box {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.item-icon-box.lead { background: #e0e7ff; color: #4f46e5; }
.item-icon-box.customer { background: #dbeafe; color: #2563eb; }
.item-icon-box.opportunity { background: #fae8ff; color: #a855f7; }
.item-icon-box.contract { background: #dcfce7; color: #16a34a; }

.item-info {
  flex: 1;
  overflow: hidden;
}

.item-title {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-sub {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.arrow {
  color: #cbd5e1;
}

.result-item:hover .arrow {
  color: #6366f1;
}

/* 悬浮球 */
.ai-floater {
  width: 56px;
  height: 56px;
  border-radius: 20px;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.4);
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.3s;
  position: relative;
  cursor: pointer;
}
.ai-floater.dragging {
  cursor: grabbing;
  transition: none;
}

.ai-floater:hover {
  transform: scale(1.1) rotate(6deg);
  box-shadow: 0 12px 32px rgba(99, 102, 241, 0.5);
}

.ai-floater.active {
  border-radius: 50%;
  transform: rotate(180deg);
  background: #1e293b;
}

.glow-ring {
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border: 2px solid #6366f1;
  border-radius: 24px;
  opacity: 0;
  transition: all 0.3s;
  animation: pulse 2s infinite;
}

.ai-floater:hover .glow-ring {
  opacity: 0.3;
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 0; }
  50% { opacity: 0.3; }
  100% { transform: scale(1.2); opacity: 0; }
}

/* 动效 */
.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.panel-slide-enter-from,
.panel-slide-leave-to {
  opacity: 0;
  transform: translateY(40px) scale(0.9);
}
</style>
