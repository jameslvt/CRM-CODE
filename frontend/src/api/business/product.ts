import { request } from '@/api/request'
import type { PageResult } from '@/types/common'
import type { Product, ProductQueryParams, ProductFormData } from '@/types/business/product'

/**
 * 分页查询产品列表
 */
export async function pageProducts(params: ProductQueryParams): Promise<PageResult<Product>> {
  const result = await request.get<PageResult<Product>>('/business/product/page', { params })
  return result.data
}

/**
 * 获取所有启用的产品列表
 */
export async function getEnabledProducts(): Promise<Product[]> {
  const result = await request.get<Product[]>('/business/product/enabled')
  return result.data
}

/**
 * 根据ID获取产品详情
 */
export async function getProductById(id: string): Promise<Product> {
  const result = await request.get<Product>(`/business/product/${id}`)
  return result.data
}

/**
 * 创建产品
 */
export async function createProduct(data: ProductFormData): Promise<string> {
  const result = await request.post<string>('/business/product', data)
  return result.data
}

/**
 * 更新产品
 */
export async function updateProduct(id: string, data: ProductFormData): Promise<void> {
  await request.put(`/business/product/${id}`, data)
}

/**
 * 删除产品
 */
export async function deleteProduct(id: string): Promise<void> {
  await request.delete(`/business/product/${id}`)
}

/**
 * 批量删除产品
 */
export async function batchDeleteProducts(ids: string[]): Promise<void> {
  await request.delete('/business/product/batch', { data: ids })
}

/**
 * 启用产品
 */
export async function enableProduct(id: string): Promise<void> {
  await request.put(`/business/product/${id}/enable`)
}

/**
 * 停用产品
 */
export async function disableProduct(id: string): Promise<void> {
  await request.put(`/business/product/${id}/disable`)
}
