import request from '@/api/request'
import type { PageResult } from '@/types/common'
import type {
  Opportunity,
  OpportunityProduct,
  OpportunityQueryParams,
  OpportunityFormData,
  StageAdvanceDTO,
  OpportunityStatistics
} from '@/types/business/opportunity'

/**
 * 分页查询商机列表
 */
export function pageOpportunities(params: OpportunityQueryParams): Promise<PageResult<Opportunity>> {
  return request.get('/api/business/opportunity/page', { params })
}

/**
 * 根据客户ID获取商机列表
 */
export function getOpportunitiesByCustomerId(customerId: number): Promise<Opportunity[]> {
  return request.get(`/api/business/opportunity/customer/${customerId}`)
}

/**
 * 根据ID获取商机详情
 */
export function getOpportunityById(id: number): Promise<Opportunity> {
  return request.get(`/api/business/opportunity/${id}`)
}

/**
 * 创建商机
 */
export function createOpportunity(data: OpportunityFormData): Promise<number> {
  return request.post('/api/business/opportunity', data)
}

/**
 * 更新商机
 */
export function updateOpportunity(id: number, data: OpportunityFormData): Promise<void> {
  return request.put(`/api/business/opportunity/${id}`, data)
}

/**
 * 删除商机
 */
export function deleteOpportunity(id: number): Promise<void> {
  return request.delete(`/api/business/opportunity/${id}`)
}

/**
 * 推进商机阶段
 */
export function advanceStage(data: StageAdvanceDTO): Promise<void> {
  return request.post('/api/business/opportunity/advance-stage', data)
}

/**
 * 获取商机产品列表
 */
export function getOpportunityProducts(opportunityId: number): Promise<OpportunityProduct[]> {
  return request.get(`/api/business/opportunity/${opportunityId}/products`)
}

/**
 * 获取商机统计数据
 */
export function getOpportunityStatistics(ownerId?: number): Promise<OpportunityStatistics> {
  return request.get('/api/business/opportunity/statistics', { params: { ownerId } })
}
