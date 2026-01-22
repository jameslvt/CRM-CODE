import { request } from '@/api/request'
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
export async function pageOpportunities(params: OpportunityQueryParams): Promise<PageResult<Opportunity>> {
  const result = await request.get<PageResult<Opportunity>>('/business/opportunity/page', { params })
  return result.data
}

/**
 * 根据客户ID获取商机列表
 */
export async function getOpportunitiesByCustomerId(customerId: string): Promise<Opportunity[]> {
  const result = await request.get<Opportunity[]>(`/business/opportunity/customer/${customerId}`)
  return result.data
}

/**
 * 根据ID获取商机详情
 */
export async function getOpportunityById(id: string): Promise<Opportunity> {
  const result = await request.get<Opportunity>(`/business/opportunity/${id}`)
  return result.data
}

/**
 * 创建商机
 */
export async function createOpportunity(data: OpportunityFormData): Promise<string> {
  const result = await request.post<string>('/business/opportunity', data)
  return result.data
}

/**
 * 更新商机
 */
export async function updateOpportunity(id: string, data: OpportunityFormData): Promise<void> {
  await request.put(`/business/opportunity/${id}`, data)
}

/**
 * 删除商机
 */
export async function deleteOpportunity(id: string): Promise<void> {
  await request.delete(`/business/opportunity/${id}`)
}

/**
 * 推进商机阶段
 */
export async function advanceStage(data: StageAdvanceDTO): Promise<void> {
  await request.post('/business/opportunity/advance-stage', data)
}

/**
 * 获取商机产品列表
 */
export async function getOpportunityProducts(opportunityId: string): Promise<OpportunityProduct[]> {
  const result = await request.get<OpportunityProduct[]>(`/business/opportunity/${opportunityId}/products`)
  return result.data
}

/**
 * 获取商机统计数据
 */
export async function getOpportunityStatistics(ownerId?: string): Promise<OpportunityStatistics> {
  const result = await request.get<OpportunityStatistics>('/business/opportunity/statistics', { params: { ownerId } })
  return result.data
}
