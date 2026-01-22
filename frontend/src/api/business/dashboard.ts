import request from '@/api/request'
import type {
  DashboardData,
  SalesFunnel,
  PerformanceTrend,
  DashboardQueryParams,
  TrendQueryParams
} from '@/types/business/dashboard'

/**
 * 获取仪表盘汇总数据
 */
export function getDashboardData(params?: DashboardQueryParams): Promise<DashboardData> {
  return request.get('/api/business/dashboard/summary', { params })
}

/**
 * 获取销售漏斗数据
 */
export function getSalesFunnel(params?: DashboardQueryParams): Promise<SalesFunnel> {
  return request.get('/api/business/dashboard/funnel', { params })
}

/**
 * 获取业绩趋势数据
 */
export function getPerformanceTrend(params?: TrendQueryParams): Promise<PerformanceTrend> {
  return request.get('/api/business/dashboard/trend', { params })
}
