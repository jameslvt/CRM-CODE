/**
 * 线索管理API
 */

import { request } from '../request'
import type { Lead, LeadQueryParams, LeadConvertParams } from '@/types/business'
import type { PageResult } from '@/types/common'

/**
 * 分页查询线索列表
 */
export async function pageLeads(params: LeadQueryParams): Promise<PageResult<Lead>> {
  const result = await request.get<PageResult<Lead>>('/business/lead/list', { params })
  return result.data
}

/**
 * 根据ID获取线索详情
 */
export async function getLeadById(id: string): Promise<Lead> {
  const result = await request.get<Lead>(`/business/lead/${id}`)
  return result.data
}

/**
 * 创建线索
 */
export async function saveLead(data: Partial<Lead>): Promise<string> {
  const result = await request.post<string>('/business/lead', data)
  return result.data
}

/**
 * 更新线索
 */
export async function updateLead(data: Lead): Promise<void> {
  await request.put<void>(`/business/lead/${data.id}`, data)
}

/**
 * 删除线索
 */
export async function deleteLead(id: string): Promise<void> {
  await request.delete<void>(`/business/lead/${id}`)
}

/**
 * 批量删除线索
 */
export async function batchDeleteLeads(ids: string[]): Promise<void> {
  await request.delete<void>('/business/lead/batch', { data: ids })
}

/**
 * 转化线索为客户
 */
export async function convertLead(leadId: string, params: Omit<LeadConvertParams, 'leadId'>): Promise<string> {
  const result = await request.post<string>('/business/lead/convert', { leadId, ...params })
  return result.data
}
/**
 * 获取线索统计
 */
export async function getLeadStats(): Promise<Record<string, number>> {
  const result = await request.get<Record<string, number>>('/business/lead/stats')
  return result.data
}
