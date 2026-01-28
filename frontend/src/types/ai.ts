/**
 * AI 模块类型定义
 * @description AI 智能辅助功能的类型定义
 */

// ==================== AI 服务状态 ====================

/**
 * AI 服务状态信息
 */
export interface AiStatusInfo {
  /** 是否启用 */
  enabled: boolean
  /** 是否可用 */
  available: boolean
  /** 使用的模型 */
  model: string
  /** API 基础 URL */
  baseUrl: string
}

/**
 * 查询条件
 */
export interface QueryCondition {
  /** 过滤条件列表 */
  filters: QueryFilter[]
  /** 排序字段 */
  orderBy?: string
  /** 排序方向 */
  orderDirection?: 'asc' | 'desc'
}

/**
 * 查询过滤条件
 */
export interface QueryFilter {
  /** 字段名 */
  field: string
  /** 操作符 */
  operator: QueryOperator
  /** 值 */
  value: any
}

/**
 * 查询操作符
 */
export type QueryOperator = 'eq' | 'like' | 'gt' | 'lt' | 'gte' | 'lte' | 'in' | 'between'

// ==================== AI 助手功能类型 ====================

/**
 * AI 助手功能类型
 */
export type AiFeatureType =
  | 'chat'           // 智能对话
  | 'extract'        // 智能提取
  | 'duplicate'      // 智能查重
  | 'query'          // 自然语言查询

/**
 * AI 助手功能配置
 */
export interface AiFeatureConfig {
  /** 功能类型 */
  type: AiFeatureType
  /** 功能名称 */
  name: string
  /** 功能描述 */
  description: string
  /** 图标 */
  icon: string
  /** 是否启用 */
  enabled: boolean
}

/**
 * 快捷提示模板
 */
export interface QuickPrompt {
  /** 模板ID */
  id: string
  /** 模板标题 */
  title: string
  /** 模板内容 */
  content: string
  /** 适用场景 */
  category: 'lead' | 'customer' | 'opportunity' | 'contract' | 'general'
}

// ==================== 对话相关 ====================

/**
 * 对话请求
 */
export interface ChatRequest {
  /** 系统提示词（可选） */
  systemPrompt?: string
  /** 用户消息 */
  message: string
}

/**
 * 对话消息
 */
export interface ChatMessage {
  /** 消息ID */
  id: string
  /** 角色: user/assistant/system */
  role: 'user' | 'assistant' | 'system'
  /** 消息内容 */
  content: string
  /** 时间戳 */
  timestamp: number
  /** 是否正在加载 */
  loading?: boolean
  /** 是否出错 */
  error?: boolean
}

// ==================== 智能提取 ====================

/**
 * 信息提取请求
 */
export interface ExtractRequest {
  /** 自然语言描述 */
  description: string
}

/**
 * 提取的线索信息
 */
export interface ExtractedLeadInfo {
  name?: string
  contactName?: string
  phone?: string
  email?: string
  company?: string
  source?: string
  remark?: string
}

/**
 * 提取的客户信息
 */
export interface ExtractedCustomerInfo {
  name?: string
  industry?: string
  scale?: string
  address?: string
  website?: string
  remark?: string
}

/**
 * 提取的商机信息
 */
export interface ExtractedOpportunityInfo {
  name?: string
  amount?: number
  expectedDate?: string
  stage?: number
  remark?: string
}

/**
 * 提取的联系人信息
 */
export interface ExtractedContactInfo {
  name?: string
  phone?: string
  email?: string
  position?: string
  department?: string
  remark?: string
}

// ==================== 智能查重 ====================

/**
 * 查重请求
 */
export interface DuplicateCheckRequest {
  /** 新数据 */
  newData: Record<string, any>
  /** 现有数据列表 */
  existingData: Record<string, any>[]
}

/**
 * 查重结果
 */
export interface DuplicateCheckResult {
  /** 是否存在重复 */
  hasDuplicate: boolean
  /** 重复项列表 */
  duplicates: DuplicateItem[]
}

/**
 * 重复项
 */
export interface DuplicateItem {
  /** 匹配记录 ID */
  id: string
  /** 相似度 (0-1) */
  similarity: number
  /** 判断原因 */
  reason: string
}

// ==================== 自然语言查询 ====================

/**
 * 自然语言查询请求
 */
export interface NLQueryRequest {
  /** 实体类型: lead, customer, opportunity, contract */
  entityType: EntityType
  /** 自然语言查询 */
  query: string
}

// ... (skip lines)

/**
 * 实体类型
 */
export type EntityType = 'lead' | 'customer' | 'opportunity' | 'contract' | 'contact' | 'product' | 'payment'

// ... (skip lines)

/**
 * 实体类型选项
 */
export const ENTITY_TYPE_OPTIONS = [
  { label: '线索', value: 'lead' },
  { label: '客户', value: 'customer' },
  { label: '商机', value: 'opportunity' },
  { label: '合同', value: 'contract' },
  { label: '联系人', value: 'contact' },
  { label: '产品', value: 'product' },
  { label: '回款', value: 'payment' }
] as const

/**
 * 快捷提示模板列表
 */
export const QUICK_PROMPTS: QuickPrompt[] = [
  {
    id: 'lead-create',
    title: '创建线索',
    content: '帮我创建一个线索：',
    category: 'lead'
  },
  {
    id: 'customer-query',
    title: '查询客户',
    content: '帮我查找',
    category: 'customer'
  },
  {
    id: 'opportunity-analysis',
    title: '商机分析',
    content: '分析一下这个商机的赢单可能性：',
    category: 'opportunity'
  },
  {
    id: 'contract-summary',
    title: '合同摘要',
    content: '帮我总结这份合同的关键信息：',
    category: 'contract'
  },
  {
    id: 'general-help',
    title: '使用帮助',
    content: '请介绍一下你能帮我做什么？',
    category: 'general'
  }
]

/**
 * 相似度等级
 */
export const SIMILARITY_LEVELS = {
  HIGH: { min: 0.9, label: '高度相似', color: '#ef4444' },
  MEDIUM: { min: 0.7, label: '中度相似', color: '#f59e0b' },
  LOW: { min: 0.5, label: '低度相似', color: '#22c55e' }
} as const

/**
 * 获取相似度等级
 */
export function getSimilarityLevel(similarity: number) {
  if (similarity >= SIMILARITY_LEVELS.HIGH.min) {
    return SIMILARITY_LEVELS.HIGH
  }
  if (similarity >= SIMILARITY_LEVELS.MEDIUM.min) {
    return SIMILARITY_LEVELS.MEDIUM
  }
  return SIMILARITY_LEVELS.LOW
}
