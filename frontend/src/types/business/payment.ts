/**
 * 回款管理类型定义
 */

/**
 * 回款计划实体
 */
export interface PaymentPlan {
  id: string
  contractId: string
  contractNo?: string
  contractName?: string
  customerId?: string
  customerName?: string
  period: number
  planAmount: number
  planDate: string
  actualAmount?: number
  actualDate?: string
  status: number
  statusName?: string
  remark?: string
  records?: PaymentRecord[]
  createTime?: string
  updateTime?: string
}

/**
 * 回款记录实体
 */
export interface PaymentRecord {
  id: string
  planId: string
  amount: number
  paymentDate: string
  paymentMethod?: string
  paymentMethodName?: string
  remark?: string
  createBy?: string
  createByName?: string
  createTime?: string
}

/**
 * 回款计划查询参数
 */
export interface PaymentPlanQueryParams {
  pageNum?: number
  pageSize?: number
  contractId?: string
  status?: number
}

/**
 * 回款计划表单数据
 */
export interface PaymentPlanFormData {
  id?: string
  contractId: string
  period: number
  planAmount: number
  planDate: string
  remark?: string
}

/**
 * 回款记录表单数据
 */
export interface PaymentRecordFormData {
  id?: string
  planId: string
  amount: number
  paymentDate: string
  paymentMethod?: string
  remark?: string
}

/**
 * 回款计划状态枚举
 */
export const PaymentPlanStatus = {
  PENDING: 1,      // 待回款
  PARTIAL: 2,      // 部分回款
  COMPLETED: 3,    // 已回款
  OVERDUE: 4       // 逾期
} as const

export type PaymentPlanStatusType = typeof PaymentPlanStatus[keyof typeof PaymentPlanStatus]

/**
 * 回款计划状态选项
 */
export const paymentPlanStatusOptions = [
  { label: '待回款', value: PaymentPlanStatus.PENDING },
  { label: '部分回款', value: PaymentPlanStatus.PARTIAL },
  { label: '已回款', value: PaymentPlanStatus.COMPLETED },
  { label: '逾期', value: PaymentPlanStatus.OVERDUE }
]

/**
 * 回款计划状态颜色映射
 */
export const paymentPlanStatusColorMap: Record<number, { bg: string; color: string }> = {
  [PaymentPlanStatus.PENDING]: { bg: '#f1f5f9', color: '#64748b' },
  [PaymentPlanStatus.PARTIAL]: { bg: '#fef3c7', color: '#f59e0b' },
  [PaymentPlanStatus.COMPLETED]: { bg: '#dcfce7', color: '#22c55e' },
  [PaymentPlanStatus.OVERDUE]: { bg: '#fee2e2', color: '#ef4444' }
}

/**
 * 获取回款计划状态名称
 */
export const getPaymentPlanStatusName = (status: number): string => {
  const option = paymentPlanStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}

/**
 * 获取回款计划状态颜色
 */
export const getPaymentPlanStatusColor = (status: number) => {
  return paymentPlanStatusColorMap[status] || { bg: '#f1f5f9', color: '#64748b' }
}

/**
 * 付款方式选项
 */
export const paymentMethodOptions = [
  { label: '银行转账', value: 'BANK_TRANSFER' },
  { label: '支票', value: 'CHECK' },
  { label: '现金', value: 'CASH' },
  { label: '其他', value: 'OTHER' }
]

/**
 * 回款统计数据
 */
export interface PaymentStatistics {
  totalPlanAmount: number
  totalActualAmount: number
  pendingAmount: number
  overdueAmount: number
  pendingCount: number
  partialCount: number
  completedCount: number
  overdueCount: number
}
