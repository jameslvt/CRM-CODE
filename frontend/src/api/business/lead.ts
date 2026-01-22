/**
 * 线索管理API
 */

import request from '../request'
import type { Lead, LeadQueryParams, LeadConvertParams } from '@/types/business'
import type { PageResult, Result } from '@/types/common'

/**
 * 分页查询线索列表
 */
export const pageLeads = (params: LeadQueryParams): Promise<PageResult<Lead>> => {
  return request.get<PageResult<Lead>>('/business/lead/list', { params }).then((res: Result<PageResult<Lead>>) => res.data)
}

/**
 * 根据ID获取线索详情
 */
export const getLeadById = (id: number): Promise<Lead> => {
  return request.get<Lead>(`/business/lead/${id}`).then((res: Result<Lead>) => res.data)
}

/**
 * 创建线索
 */
export const saveLead = (data: Partial<Lead>): Promise<number> => {
  return request.post<number>('/business/lead', data).then((res: Result<number>) => res.data)
}

/**
 * 更新线索
 */
export const updateLead = (data: Lead): Promise<void> => {
  return request.put<void>(`/business/lead/${data.id}`, data).then((res: Result<void>) => res.data)
}

/**
 * 删除线索
 */
export const deleteLead = (id: number): Promise<void> => {
  return request.delete<void>(`/business/lead/${id}`).then((res: Result<void>) => res.data)
}

/**
 * 批量删除线索
 */
export const batchDeleteLeads = (ids: number[]): Promise<void> => {
  return request.delete<void>('/business/lead/batch', { data: ids }).then((res: Result<void>) => res.data)
}

/**
 * 转化线索为客户
 */
export const convertLead = (leadId: number, params: LeadConvertParams): Promise<number> => {
  return request.post<number>('/business/lead/convert', { leadId, ...params }).then((res: Result<number>) => res.data)
}
