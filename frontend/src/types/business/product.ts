/**
 * 产品管理类型定义
 */

/**
 * 产品实体
 */
export interface Product {
  id: number
  name: string
  code?: string
  category?: string
  unit?: string
  price: number
  description?: string
  status: number
  statusName?: string
  createTime?: string
  updateTime?: string
}

/**
 * 产品查询参数
 */
export interface ProductQueryParams {
  pageNum?: number
  pageSize?: number
  name?: string
  code?: string
  category?: string
  status?: number
}

/**
 * 产品表单数据
 */
export interface ProductFormData {
  id?: number
  name: string
  code?: string
  category?: string
  unit?: string
  price: number
  description?: string
  status?: number
}

/**
 * 产品分类
 */
export interface ProductCategory {
  value: string
  label: string
}

/**
 * 产品状态枚举
 */
export const ProductStatus = {
  DISABLED: 0,
  ENABLED: 1
} as const

export type ProductStatusType = typeof ProductStatus[keyof typeof ProductStatus]

/**
 * 产品状态选项
 */
export const productStatusOptions = [
  { label: '启用', value: ProductStatus.ENABLED },
  { label: '停用', value: ProductStatus.DISABLED }
]

/**
 * 产品分类选项
 */
export const productCategoryOptions = [
  { label: 'CRM系统', value: 'CRM系统' },
  { label: '功能模块', value: '功能模块' },
  { label: '增值服务', value: '增值服务' },
  { label: '技术服务', value: '技术服务' },
  { label: '其他', value: '其他' }
]

/**
 * 计量单位选项
 */
export const productUnitOptions = [
  { label: '套', value: '套' },
  { label: '个', value: '个' },
  { label: '人天', value: '人天' },
  { label: '年', value: '年' },
  { label: '月', value: '月' }
]
