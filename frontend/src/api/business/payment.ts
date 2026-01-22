import request from '@/api/request'
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
export function pagePaymentPlans(params: PaymentPlanQueryParams): Promise<PageResult<PaymentPlan>> {
  return request.get('/api/business/payment/plan/page', { params })
}

/**
 * 根据合同ID获取回款计划列表
 */
export function getPaymentPlansByContractId(contractId: number): Promise<PaymentPlan[]> {
  return request.get(`/api/business/payment/plan/contract/${contractId}`)
}

/**
 * 根据ID获取回款计划详情
 */
export function getPaymentPlanById(id: number): Promise<PaymentPlan> {
  return request.get(`/api/business/payment/plan/${id}`)
}

/**
 * 创建回款计划
 */
export function createPaymentPlan(data: PaymentPlanFormData): Promise<number> {
  return request.post('/api/business/payment/plan', data)
}

/**
 * 批量创建回款计划
 */
export function batchCreatePaymentPlans(contractId: number, plans: PaymentPlanFormData[]): Promise<void> {
  return request.post(`/api/business/payment/plan/batch/${contractId}`, plans)
}

/**
 * 更新回款计划
 */
export function updatePaymentPlan(id: number, data: PaymentPlanFormData): Promise<void> {
  return request.put(`/api/business/payment/plan/${id}`, data)
}

/**
 * 删除回款计划
 */
export function deletePaymentPlan(id: number): Promise<void> {
  return request.delete(`/api/business/payment/plan/${id}`)
}

// ==================== 回款记录 ====================

/**
 * 获取回款计划的回款记录列表
 */
export function getPaymentRecordsByPlanId(planId: number): Promise<PaymentRecord[]> {
  return request.get(`/api/business/payment/record/plan/${planId}`)
}

/**
 * 创建回款记录
 */
export function createPaymentRecord(data: PaymentRecordFormData): Promise<number> {
  return request.post('/api/business/payment/record', data)
}

/**
 * 删除回款记录
 */
export function deletePaymentRecord(id: number): Promise<void> {
  return request.delete(`/api/business/payment/record/${id}`)
}

// ==================== 统计 ====================

/**
 * 获取回款统计数据
 */
export function getPaymentStatistics(contractId?: number): Promise<PaymentStatistics> {
  return request.get('/api/business/payment/statistics', { params: { contractId } })
}
