/**
 * AI 模块 API
 * @description AI 智能辅助功能的 API 接口
 */

import { request } from '@/api/request'
import type {
  AiStatusInfo,
  ChatRequest,
  ExtractRequest,
  DuplicateCheckRequest,
  DuplicateCheckResult,
  NLQueryRequest,
  QueryCondition,
  EntityType
} from '@/types/ai'

const BASE_URL = '/ai'

// ==================== AI 服务状态 ====================

/**
 * 获取 AI 服务状态
 */
export async function getAiStatus(): Promise<AiStatusInfo> {
  const result = await request.get<AiStatusInfo>(`${BASE_URL}/status`)
  return result.data
}

// ==================== AI 对话 ====================

/**
 * AI 对话
 * @param data 对话请求
 */
export async function chat(data: ChatRequest): Promise<string> {
  const result = await request.post<string>(`${BASE_URL}/chat`, data)
  return result.data
}

// ==================== 智能提取 ====================

/**
 * 从自然语言提取线索信息
 * @param description 自然语言描述
 */
export async function extractLeadInfo(description: string): Promise<Record<string, any>> {
  const result = await request.post<Record<string, any>>(`${BASE_URL}/extract/lead`, { description })
  return result.data
}

/**
 * 从自然语言提取客户信息
 * @param description 自然语言描述
 */
export async function extractCustomerInfo(description: string): Promise<Record<string, any>> {
  const result = await request.post<Record<string, any>>(`${BASE_URL}/extract/customer`, { description })
  return result.data
}

/**
 * 从自然语言提取商机信息
 * @param description 自然语言描述
 */
export async function extractOpportunityInfo(description: string): Promise<Record<string, any>> {
  const result = await request.post<Record<string, any>>(`${BASE_URL}/extract/opportunity`, { description })
  return result.data
}

/**
 * 从自然语言提取联系人信息
 * @param description 自然语言描述
 */
export async function extractContactInfo(description: string): Promise<Record<string, any>> {
  const result = await request.post<Record<string, any>>(`${BASE_URL}/extract/contact`, { description })
  return result.data
}

// ==================== 智能查重 ====================

/**
 * 线索智能查重
 * @param data 查重请求
 */
export async function checkLeadDuplicate(data: DuplicateCheckRequest): Promise<DuplicateCheckResult> {
  const result = await request.post<DuplicateCheckResult>(`${BASE_URL}/duplicate/lead`, data)
  return result.data
}

/**
 * 客户智能查重
 * @param data 查重请求
 */
export async function checkCustomerDuplicate(data: DuplicateCheckRequest): Promise<DuplicateCheckResult> {
  const result = await request.post<DuplicateCheckResult>(`${BASE_URL}/duplicate/customer`, data)
  return result.data
}

/**
 * 联系人智能查重
 * @param data 查重请求
 */
export async function checkContactDuplicate(data: DuplicateCheckRequest): Promise<DuplicateCheckResult> {
  const result = await request.post<DuplicateCheckResult>(`${BASE_URL}/duplicate/contact`, data)
  return result.data
}

// ==================== 自然语言查询 ====================

/**
 * 解析自然语言查询
 * @param entityType 实体类型
 * @param query 自然语言查询
 */
export async function parseQuery(entityType: EntityType, query: string): Promise<QueryCondition> {
  const result = await request.post<QueryCondition>(`${BASE_URL}/query/parse`, { entityType, query })
  return result.data
}

// ==================== 导出所有 API ====================

export default {
  getAiStatus,
  chat,
  extractLeadInfo,
  extractCustomerInfo,
  extractOpportunityInfo,
  extractContactInfo,
  checkLeadDuplicate,
  checkCustomerDuplicate,
  checkContactDuplicate,
  parseQuery
}
