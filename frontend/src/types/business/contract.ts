/**
 * 合同管理类型定义
 */

/**
 * 合同实体
 */
export interface Contract {
  id: string
  contractNo?: string
  name: string
  customerId: string
  customerName?: string
  opportunityId?: string
  opportunityName?: string
  amount: number
  startDate?: string
  endDate?: string
  signDate?: string
  status: number
  statusName?: string
  ownerId?: string
  ownerName?: string
  fileUrl?: string
  remark?: string
  paidAmount?: number
  unpaidAmount?: number
  createTime?: string
  updateTime?: string
}

/**
 * 合同查询参数
 */
export interface ContractQueryParams {
  pageNum?: number
  pageSize?: number
  contractNo?: string
  name?: string
  customerId?: string
  customerName?: string
  opportunityId?: string
  status?: number
  ownerId?: string
  signDateStart?: string
  signDateEnd?: string
  createTimeStart?: string
  createTimeEnd?: string
}

/**
 * 合同表单数据
 */
export interface ContractFormData {
  id?: string
  contractNo?: string
  name: string
  customerId: string
  opportunityId?: string
  amount: number
  startDate?: string
  endDate?: string
  signDate?: string
  status?: number
  ownerId?: string
  fileUrl?: string
  remark?: string
}

/**
 * 合同状态枚举
 */
export const ContractStatus = {
  DRAFT: 1,        // 草稿
  PENDING: 2,      // 审批中
  EXECUTING: 3,    // 执行中
  COMPLETED: 4,    // 已完成
  TERMINATED: 5    // 已终止
} as const

export type ContractStatusType = typeof ContractStatus[keyof typeof ContractStatus]

/**
 * 合同状态选项
 */
export const contractStatusOptions = [
  { label: '草稿', value: ContractStatus.DRAFT },
  { label: '审批中', value: ContractStatus.PENDING },
  { label: '执行中', value: ContractStatus.EXECUTING },
  { label: '已完成', value: ContractStatus.COMPLETED },
  { label: '已终止', value: ContractStatus.TERMINATED }
]

/**
 * 合同状态颜色映射
 */
export const contractStatusColorMap: Record<number, { bg: string; color: string }> = {
  [ContractStatus.DRAFT]: { bg: '#f1f5f9', color: '#64748b' },
  [ContractStatus.PENDING]: { bg: '#fef3c7', color: '#f59e0b' },
  [ContractStatus.EXECUTING]: { bg: '#dbeafe', color: '#2563eb' },
  [ContractStatus.COMPLETED]: { bg: '#dcfce7', color: '#22c55e' },
  [ContractStatus.TERMINATED]: { bg: '#fee2e2', color: '#ef4444' }
}

/**
 * 获取合同状态名称
 */
export const getContractStatusName = (status: number): string => {
  const option = contractStatusOptions.find(opt => opt.value === status)
  return option?.label || '未知'
}

/**
 * 获取合同状态颜色
 */
export const getContractStatusColor = (status: number) => {
  return contractStatusColorMap[status] || { bg: '#f1f5f9', color: '#64748b' }
}

/**
 * 合同统计数据
 */
export interface ContractStatistics {
  totalCount: number
  totalAmount: number
  draftCount: number
  pendingCount: number
  executingCount: number
  completedCount: number
  terminatedCount: number
  paidAmount: number
  unpaidAmount: number
}
