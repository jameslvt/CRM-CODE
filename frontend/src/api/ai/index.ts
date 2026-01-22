/**
 * AI 模块 API
 * @description AI 智能辅助功能的 API 接口
 */

import request from '@/api/request'
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

const BASE_URL = '/api/ai'

// ==================== AI 服务状态 ====================

/**
 * 获取 AI 服务状态
 */
export function getAiStatus(): Promise<AiStatusInfo> {
  return request.get(`${BASE_URL}/status`)
}

// ==================== AI 对话 ====================

/**
 * AI 对话
 * @param data 对话请求
 */
export function chat(data: ChatRequest): Promise<string> {
  return request.post(`${BASE_URL}/chat`, data)
}

// ==================== 智能提取 ====================

/**
 * 从自然语言提取线索信息
 * @param description 自然语言描述
 */
export function extractLeadInfo(description: string): Promise<Record<string, any>> {
  return request.post(`${BASE_URL}/extract/lead`, { description })
}

/**
 * 从自然语言提取客户信息
 * @param description 自然语言描述
 */
export function extractCustomerInfo(description: string): Promise<Record<string, any>> {
  return request.post(`${BASE_URL}/extract/customer`, { description })
}

/**
 * 从自然语言提取商机信息
 * @param description 自然语言描述
 */
export function extractOpportunityInfo(description: string): Promise<Record<string, any>> {
  return request.post(`${BASE_URL}/extract/opportunity`, { description })
}

/**
 * 从自然语言提取联系人信息
 * @param description 自然语言描述
 */
export function extractContactInfo(description: string): Promise<Record<string, any>> {
  return request.post(`${BASE_URL}/extract/contact`, { description })
}

// ==================== 智能查重 ====================

/**
 * 线索智能查重
 * @param data 查重请求
 */
export function checkLeadDuplicate(data: DuplicateCheckRequest): Promise<DuplicateCheckResult> {
  return request.post(`${BASE_URL}/duplicate/lead`, data)
}

/**
 * 客户智能查重
 * @param data 查重请求
 */
export function checkCustomerDuplicate(data: DuplicateCheckRequest): Promise<DuplicateCheckResult> {
  return request.post(`${BASE_URL}/duplicate/customer`, data)
}

/**
 * 联系人智能查重
 * @param data 查重请求
 */
export function checkContactDuplicate(data: DuplicateCheckRequest): Promise<DuplicateCheckResult> {
  return request.post(`${BASE_URL}/duplicate/contact`, data)
}

// ==================== 自然语言查询 ====================

/**
 * 解析自然语言查询
 * @param entityType 实体类型
 * @param query 自然语言查询
 */
export function parseQuery(entityType: EntityType, query: string): Promise<QueryCondition> {
  return request.post(`${BASE_URL}/query/parse`, { entityType, query })
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
