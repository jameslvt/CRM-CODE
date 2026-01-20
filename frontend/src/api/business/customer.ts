/**
 * 客户管理API
 */

import request from '../request'
import type { Customer, CustomerQueryParams, CustomerFormData, Customer360 } from '@/types/business/customer'
import type { PageResult } from '@/types/common'

/**
 * 分页查询客户列表
 */
export const pageCustomers = (params: CustomerQueryParams): Promise<PageResult<Customer>> => {
  return request.get('/business/customer/list', { params })
}

/**
 * 根据ID获取客户详情
 */
export const getCustomerById = (id: number): Promise<Customer> => {
  return request.get(`/business/customer/${id}`)
}

/**
 * 获取客户360度视图
 */
export const getCustomer360 = (id: number): Promise<Customer360> => {
  return request.get(`/business/customer/${id}/360`)
}

/**
 * 创建客户
 */
export const saveCustomer = (data: CustomerFormData): Promise<number> => {
  return request.post('/business/customer', data)
}

/**
 * 更新客户
 */
export const updateCustomer = (data: CustomerFormData): Promise<void> => {
  return request.put(`/business/customer/${data.id}`, data)
}

/**
 * 删除客户
 */
export const deleteCustomer = (id: number): Promise<void> => {
  return request.delete(`/business/customer/${id}`)
}

/**
 * 释放客户到公海
 */
export const releaseToPool = (id: number, reason?: string): Promise<void> => {
  return request.post(`/business/customer/${id}/release`, { reason })
}

/**
 * 从公海领取客户
 */
export const acquireFromPool = (id: number): Promise<void> => {
  return request.post(`/business/customer/${id}/acquire`)
}

/**
 * 分配客户
 */
export const assignCustomer = (id: number, ownerId: number): Promise<void> => {
  return request.post(`/business/customer/${id}/assign`, { ownerId })
}
