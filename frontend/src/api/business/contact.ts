/**
 * 联系人管理API
 */

import request from '../request'
import type { Contact, ContactFormData } from '@/types/business/customer'
import type { PageResult } from '@/types/common'

/**
 * 分页查询联系人列表
 */
export const pageContacts = (
  customerId: number,
  name?: string,
  pageNum = 1,
  pageSize = 10
): Promise<PageResult<Contact>> => {
  return request.get('/business/contact/list', {
    params: { customerId, name, pageNum, pageSize }
  })
}

/**
 * 获取客户的所有联系人
 */
export const getContactsByCustomerId = (customerId: number): Promise<Contact[]> => {
  return request.get(`/business/contact/customer/${customerId}`)
}

/**
 * 根据ID获取联系人详情
 */
export const getContactById = (id: number): Promise<Contact> => {
  return request.get(`/business/contact/${id}`)
}

/**
 * 创建联系人
 */
export const saveContact = (data: ContactFormData): Promise<number> => {
  return request.post('/business/contact', data)
}

/**
 * 更新联系人
 */
export const updateContact = (data: ContactFormData): Promise<void> => {
  return request.put(`/business/contact/${data.id}`, data)
}

/**
 * 删除联系人
 */
export const deleteContact = (id: number): Promise<void> => {
  return request.delete(`/business/contact/${id}`)
}

/**
 * 设置为主要联系人
 */
export const setPrimaryContact = (id: number): Promise<void> => {
  return request.post(`/business/contact/${id}/set-primary`)
}
