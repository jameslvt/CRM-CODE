import { request } from '@/api/request'
import type { PageResult } from '@/types/common'
import type {
  PaymentPlan,
  PaymentRecord,
  PaymentPlanQueryParams,
  PaymentPlanFormData,
  PaymentRecordFormData,
  PaymentStatistics
} from '@/types/business/payment'

// ==================== 回款计划 ====================

/**
 * 分页查询回款计划列表
 */
export async function pagePaymentPlans(params: PaymentPlanQueryParams): Promise<PageResult<PaymentPlan>> {
  const result = await request.get<PageResult<PaymentPlan>>('/business/payment/plan/page', { params })
  return result.data
}

/**
 * 根据合同ID获取回款计划列表
 */
export async function getPaymentPlansByContractId(contractId: number): Promise<PaymentPlan[]> {
  const result = await request.get<PaymentPlan[]>(`/business/payment/plan/contract/${contractId}`)
  return result.data
}

/**
 * 根据ID获取回款计划详情
 */
export async function getPaymentPlanById(id: number): Promise<PaymentPlan> {
  const result = await request.get<PaymentPlan>(`/business/payment/plan/${id}`)
  return result.data
}

/**
 * 创建回款计划
 */
export async function createPaymentPlan(data: PaymentPlanFormData): Promise<number> {
  const result = await request.post<number>('/business/payment/plan', data)
  return result.data
}

/**
 * 批量创建回款计划
 */
export async function batchCreatePaymentPlans(contractId: number, plans: PaymentPlanFormData[]): Promise<void> {
  await request.post(`/business/payment/plan/batch/${contractId}`, plans)
}

/**
 * 更新回款计划
 */
export async function updatePaymentPlan(id: number, data: PaymentPlanFormData): Promise<void> {
  await request.put(`/business/payment/plan/${id}`, data)
}

/**
 * 删除回款计划
 */
export async function deletePaymentPlan(id: number): Promise<void> {
  await request.delete(`/business/payment/plan/${id}`)
}

// ==================== 回款记录 ====================

/**
 * 获取回款计划的回款记录列表
 */
export async function getPaymentRecordsByPlanId(planId: number): Promise<PaymentRecord[]> {
  const result = await request.get<PaymentRecord[]>(`/business/payment/record/plan/${planId}`)
  return result.data
}

/**
 * 创建回款记录
 */
export async function createPaymentRecord(data: PaymentRecordFormData): Promise<number> {
  const result = await request.post<number>('/business/payment/record', data)
  return result.data
}

/**
 * 删除回款记录
 */
export async function deletePaymentRecord(id: number): Promise<void> {
  await request.delete(`/business/payment/record/${id}`)
}

// ==================== 统计 ====================

/**
 * 获取回款统计数据
 */
export async function getPaymentStatistics(contractId?: number): Promise<PaymentStatistics> {
  const result = await request.get<PaymentStatistics>('/business/payment/statistics', { params: { contractId } })
  return result.data
}
