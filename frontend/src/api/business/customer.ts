/**
 * 客户管理API
 */

import { request } from '../request'
import type { Customer, CustomerQueryParams, CustomerFormData, Customer360 } from '@/types/business/customer'
import type { PageResult, Result } from '@/types/common'

/**
 * 分页查询客户列表
 */
export const pageCustomers = async (params: CustomerQueryParams): Promise<PageResult<Customer>> => {
  const result = await request.get<PageResult<Customer>>('/business/customer/list', { params })
  return result.data
}

/**
 * 根据ID获取客户详情
 */
export const getCustomerById = async (id: string): Promise<Customer> => {
  const result = await request.get<Customer>(`/business/customer/${id}`)
  return result.data
}

/**
 * 获取客户360度视图
 */
export const getCustomer360 = async (id: string): Promise<Customer360> => {
  const result = await request.get<Customer360>(`/business/customer/${id}/360`)
  return result.data
}

/**
 * 创建客户
 */
export const saveCustomer = async (data: CustomerFormData): Promise<string> => {
  const result = await request.post<string>('/business/customer', data)
  return result.data
}

/**
 * 更新客户
 */
export const updateCustomer = async (data: CustomerFormData): Promise<void> => {
  await request.put(`/business/customer/${data.id}`, data)
}

/**
 * 删除客户
 */
export const deleteCustomer = async (id: string): Promise<void> => {
  await request.delete(`/business/customer/${id}`)
}

/**
 * 释放客户到公海
 */
export const releaseToPool = async (id: string, reason?: string): Promise<void> => {
  await request.post(`/business/customer/${id}/release`, { reason })
}

/**
 * 从公海领取客户
 */
export const acquireFromPool = async (id: string): Promise<void> => {
  await request.post(`/business/customer/${id}/acquire`)
}

/**
 * 分配客户
 */
export const assignCustomer = async (id: string, ownerId: string): Promise<void> => {
  await request.post(`/business/customer/${id}/assign`, { ownerId })
}
