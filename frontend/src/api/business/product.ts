import request from '@/api/request'
import type { PageResult } from '@/types/common'
import type { Product, ProductQueryParams, ProductFormData } from '@/types/business/product'

/**
 * 分页查询产品列表
 */
export function pageProducts(params: ProductQueryParams): Promise<PageResult<Product>> {
  return request.get('/api/business/product/page', { params })
}

/**
 * 获取所有启用的产品列表
 */
export function getEnabledProducts(): Promise<Product[]> {
  return request.get('/api/business/product/enabled')
}

/**
 * 根据ID获取产品详情
 */
export function getProductById(id: number): Promise<Product> {
  return request.get(`/api/business/product/${id}`)
}

/**
 * 创建产品
 */
export function createProduct(data: ProductFormData): Promise<number> {
  return request.post('/api/business/product', data)
}

/**
 * 更新产品
 */
export function updateProduct(id: number, data: ProductFormData): Promise<void> {
  return request.put(`/api/business/product/${id}`, data)
}

/**
 * 删除产品
 */
export function deleteProduct(id: number): Promise<void> {
  return request.delete(`/api/business/product/${id}`)
}

/**
 * 批量删除产品
 */
export function batchDeleteProducts(ids: number[]): Promise<void> {
  return request.delete('/api/business/product/batch', { data: ids })
}

/**
 * 启用产品
 */
export function enableProduct(id: number): Promise<void> {
  return request.put(`/api/business/product/${id}/enable`)
}

/**
 * 停用产品
 */
export function disableProduct(id: number): Promise<void> {
  return request.put(`/api/business/product/${id}/disable`)
}
