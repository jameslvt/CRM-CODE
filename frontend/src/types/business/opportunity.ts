/**
 * 商机管理类型定义
 */

/**
 * 商机实体
 */
export interface Opportunity {
  id: string
  name: string
  customerId: string
  customerName?: string
  contactId?: string
  contactName?: string
  amount?: number
  stage: string
  stageName?: string
  probability?: number
  expectedDate?: string
  ownerId?: string
  ownerName?: string
  source?: string
  sourceName?: string
  remark?: string
  closeReason?: string
  products?: OpportunityProduct[]
  createTime?: string
  updateTime?: string
}

/**
 * 商机产品关联
 */
export interface OpportunityProduct {
  id?: string
  opportunityId?: string
  productId: string
  productName?: string
  productCode?: string
  quantity: number
  price: number
  discount?: number
  amount?: number
  unit?: string
}

/**
 * 商机查询参数
 */
export interface OpportunityQueryParams {
  pageNum?: number
  pageSize?: number
  name?: string
  customerId?: string
  customerName?: string
  stage?: string
  ownerId?: string
  source?: string
  expectedDateStart?: string
  expectedDateEnd?: string
  createTimeStart?: string
  createTimeEnd?: string
}

/**
 * 商机表单数据
 */
export interface OpportunityFormData {
  id?: string
  name: string
  customerId: string
  contactId?: string
  amount?: number
  stage: string
  probability?: number
  expectedDate?: string
  ownerId?: string
  source?: string
  remark?: string
  closeReason?: string
  products?: OpportunityProduct[]
}

/**
 * 阶段推进DTO
 */
export interface StageAdvanceDTO {
  opportunityId: string
  targetStage: string
  remark?: string
  closeReason?: string
}

/**
 * 商机阶段配置
 */
export interface OpportunityStage {
  id: string
  code: string
  name: string
  probability: number
  sortOrder: number
  isWon?: boolean
  isLost?: boolean
}

/**
 * 商机统计数据
 */
export interface OpportunityStatistics {
  totalCount: number
  totalAmount: number
  wonCount: number
  wonAmount: number
  lostCount: number
  stageDistribution: Record<string, number>
}
