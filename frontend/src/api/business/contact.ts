/**
 * 联系人管理API
 */

import { request } from '../request'
import type { Contact, ContactFormData, ContactQueryParams } from '@/types/business/contact'
import type { PageResult } from '@/types/common'

/**
 * 分页查询联系人列表（全局）
 */
export const pageAllContacts = async (params: ContactQueryParams): Promise<PageResult<Contact>> => {
  const result = await request.get<PageResult<Contact>>('/business/contact/page', { params })
  return result.data
}

/**
 * 分页查询联系人列表（按客户）
 */
export const pageContacts = async (
  customerId: number,
  name?: string,
  pageNum = 1,
  pageSize = 10
): Promise<PageResult<Contact>> => {
  const result = await request.get<PageResult<Contact>>('/business/contact/list', {
    params: { customerId, name, pageNum, pageSize }
  })
  return result.data
}

/**
 * 获取客户的所有联系人
 */
export const getContactsByCustomerId = async (customerId: number): Promise<Contact[]> => {
  const result = await request.get<Contact[]>(`/business/contact/customer/${customerId}`)
  return result.data
}

/**
 * 根据ID获取联系人详情
 */
export const getContactById = async (id: number): Promise<Contact> => {
  const result = await request.get<Contact>(`/business/contact/${id}`)
  return result.data
}

/**
 * 创建联系人
 */
export const saveContact = async (data: ContactFormData): Promise<number> => {
  const result = await request.post<number>('/business/contact', data)
  return result.data
}

/**
 * 更新联系人
 */
export const updateContact = async (data: ContactFormData): Promise<void> => {
  await request.put(`/business/contact/${data.id}`, data)
}

/**
 * 删除联系人
 */
export const deleteContact = async (id: number): Promise<void> => {
  await request.delete(`/business/contact/${id}`)
}

/**
 * 设置为主要联系人
 */
export const setPrimaryContact = async (id: number): Promise<void> => {
  await request.post(`/business/contact/${id}/set-primary`)
}
