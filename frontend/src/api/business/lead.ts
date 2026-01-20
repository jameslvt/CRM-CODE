/**
 * 线索管理API
 */

import request from '../request'
import type { Lead, LeadQueryParams, LeadConvertParams } from '@/types/business'
import type { PageResult } from '@/types/common'

/**
 * 分页查询线索列表
 */
export const pageLeads = (params: LeadQueryParams): Promise<PageResult<Lead>> => {
  return request.get('/api/business/lead/list', { params })
}

/**
 * 根据ID获取线索详情
 */
export const getLeadById = (id: number): Promise<Lead> => {
  return request.get(`/api/business/lead/${id}`)
}

/**
 * 创建线索
 */
export const saveLead = (data: Partial<Lead>): Promise<number> => {
  return request.post('/api/business/lead', data)
}

/**
 * 更新线索
 */
export const updateLead = (data: Lead): Promise<void> => {
  return request.put(`/api/business/lead/${data.id}`, data)
}

/**
 * 删除线索
 */
export const deleteLead = (id: number): Promise<void> => {
  return request.delete(`/api/business/lead/${id}`)
}

/**
 * 批量删除线索
 */
export const batchDeleteLeads = (ids: number[]): Promise<void> => {
  return request.delete('/api/business/lead/batch', { data: ids })
}

/**
 * 转化线索为客户
 */
export const convertLead = (leadId: number, params: LeadConvertParams): Promise<number> => {
  return request.post('/api/business/lead/convert', { leadId, ...params })
}
