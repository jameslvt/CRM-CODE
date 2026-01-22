import { request } from '@/api/request'
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
export async function getDashboardData(params?: DashboardQueryParams): Promise<DashboardData> {
  const result = await request.get<DashboardData>('/business/dashboard/summary', { params })
  return result.data
}

/**
 * 获取销售漏斗数据
 */
export async function getSalesFunnel(params?: DashboardQueryParams): Promise<SalesFunnel> {
  const result = await request.get<SalesFunnel>('/business/dashboard/funnel', { params })
  return result.data
}

/**
 * 获取业绩趋势数据
 */
export async function getPerformanceTrend(params?: TrendQueryParams): Promise<PerformanceTrend> {
  const result = await request.get<PerformanceTrend>('/business/dashboard/trend', { params })
  return result.data
}
