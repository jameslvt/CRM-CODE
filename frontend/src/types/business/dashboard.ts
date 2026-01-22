/**
 * 仪表盘类型定义
 * 对应后端 DashboardDTO、SalesFunnelDTO、PerformanceTrendDTO
 */

// ==================== 仪表盘汇总数据 ====================

/**
 * 仪表盘数据
 */
export interface DashboardData {
  /** 线索统计 */
  leadStats: LeadStats
  /** 客户统计 */
  customerStats: CustomerStats
  /** 商机统计 */
  opportunityStats: OpportunityStats
  /** 合同统计 */
  contractStats: ContractStats
  /** 回款统计 */
  paymentStats: PaymentStats
}

/**
 * 线索统计
 */
export interface LeadStats {
  /** 总线索数 */
  totalCount: number
  /** 本月新增 */
  monthNewCount: number
  /** 待跟进数 */
  pendingCount: number
  /** 转化数 */
  convertedCount: number
  /** 转化率 */
  conversionRate: number
}

/**
 * 客户统计
 */
export interface CustomerStats {
  /** 总客户数 */
  totalCount: number
  /** 本月新增 */
  monthNewCount: number
  /** 公海池数量 */
  poolCount: number
  /** 活跃客户数 */
  activeCount: number
}

/**
 * 商机统计
 */
export interface OpportunityStats {
  /** 总商机数 */
  totalCount: number
  /** 本月新增 */
  monthNewCount: number
  /** 进行中数量 */
  ongoingCount: number
  /** 赢单数量 */
  wonCount: number
  /** 输单数量 */
  lostCount: number
  /** 赢单率 */
  winRate: number
  /** 总金额 */
  totalAmount: number
  /** 赢单金额 */
  wonAmount: number
}

/**
 * 合同统计
 */
export interface ContractStats {
  /** 总合同数 */
  totalCount: number
  /** 本月新增 */
  monthNewCount: number
  /** 执行中数量 */
  executingCount: number
  /** 总金额 */
  totalAmount: number
  /** 本月签约金额 */
  monthSignedAmount: number
}

/**
 * 回款统计
 */
export interface PaymentStats {
  /** 计划回款总额 */
  totalPlanAmount: number
  /** 实际回款总额 */
  totalActualAmount: number
  /** 本月回款金额 */
  monthActualAmount: number
  /** 待回款金额 */
  pendingAmount: number
  /** 逾期金额 */
  overdueAmount: number
  /** 回款完成率 */
  completionRate: number
}

// ==================== 销售漏斗 ====================

/**
 * 销售漏斗数据
 */
export interface SalesFunnel {
  /** 漏斗阶段列表 */
  stages: FunnelStage[]
  /** 总商机数 */
  totalCount: number
  /** 总金额 */
  totalAmount: number
}

/**
 * 漏斗阶段
 */
export interface FunnelStage {
  /** 阶段名称 */
  stageName: string
  /** 阶段编码 */
  stageCode: string
  /** 商机数量 */
  count: number
  /** 金额 */
  amount: number
  /** 赢单概率 */
  probability: number
  /** 占比（数量） */
  countRatio: number
  /** 占比（金额） */
  amountRatio: number
  /** 转化率（相对上一阶段） */
  conversionRate: number
}

// ==================== 业绩趋势 ====================

/**
 * 业绩趋势数据
 */
export interface PerformanceTrend {
  /** 趋势数据点列表 */
  trendPoints: TrendPoint[]
  /** 统计周期类型 */
  periodType: string
  /** 汇总数据 */
  summary: TrendSummary
}

/**
 * 趋势数据点
 */
export interface TrendPoint {
  /** 时间标签 */
  period: string
  /** 新增线索数 */
  newLeadCount: number
  /** 新增客户数 */
  newCustomerCount: number
  /** 新增商机数 */
  newOpportunityCount: number
  /** 商机金额 */
  opportunityAmount: number
  /** 赢单数 */
  wonCount: number
  /** 赢单金额 */
  wonAmount: number
  /** 签约合同数 */
  signedContractCount: number
  /** 签约金额 */
  signedAmount: number
  /** 回款金额 */
  paymentAmount: number
}

/**
 * 趋势汇总
 */
export interface TrendSummary {
  /** 总新增线索数 */
  totalNewLeadCount: number
  /** 总新增客户数 */
  totalNewCustomerCount: number
  /** 总新增商机数 */
  totalNewOpportunityCount: number
  /** 总商机金额 */
  totalOpportunityAmount: number
  /** 总赢单数 */
  totalWonCount: number
  /** 总赢单金额 */
  totalWonAmount: number
  /** 总签约合同数 */
  totalSignedContractCount: number
  /** 总签约金额 */
  totalSignedAmount: number
  /** 总回款金额 */
  totalPaymentAmount: number
  /** 环比增长率（赢单金额） */
  wonAmountGrowthRate: number
  /** 环比增长率（回款金额） */
  paymentAmountGrowthRate: number
}

// ==================== 查询参数 ====================

/**
 * 仪表盘查询参数
 */
export interface DashboardQueryParams {
  /** 负责人ID */
  ownerId?: number
}

/**
 * 趋势查询参数
 */
export interface TrendQueryParams {
  /** 负责人ID */
  ownerId?: number
  /** 周期类型: month、quarter */
  periodType?: string
  /** 查询月数 */
  months?: number
}

// ==================== 周期类型 ====================

/**
 * 周期类型
 */
export enum PeriodType {
  MONTH = 'month',
  QUARTER = 'quarter'
}

/**
 * 周期类型选项
 */
export const periodTypeOptions = [
  { label: '按月', value: PeriodType.MONTH },
  { label: '按季度', value: PeriodType.QUARTER }
]
