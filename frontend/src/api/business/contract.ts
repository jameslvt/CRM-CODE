import request from '@/api/request'
import type { PageResult } from '@/types/common'
import type { Contract, ContractQueryParams, ContractFormData, ContractStatistics } from '@/types/business/contract'

/**
 * 分页查询合同列表
 */
export function pageContracts(params: ContractQueryParams): Promise<PageResult<Contract>> {
  return request.get('/api/business/contract/page', { params })
}

/**
 * 根据客户ID获取合同列表
 */
export function getContractsByCustomerId(customerId: number): Promise<Contract[]> {
  return request.get(`/api/business/contract/customer/${customerId}`)
}

/**
 * 根据商机ID获取合同列表
 */
export function getContractsByOpportunityId(opportunityId: number): Promise<Contract[]> {
  return request.get(`/api/business/contract/opportunity/${opportunityId}`)
}

/**
 * 根据ID获取合同详情
 */
export function getContractById(id: number): Promise<Contract> {
  return request.get(`/api/business/contract/${id}`)
}

/**
 * 创建合同
 */
export function createContract(data: ContractFormData): Promise<number> {
  return request.post('/api/business/contract', data)
}

/**
 * 从商机创建合同
 */
export function createContractFromOpportunity(opportunityId: number): Promise<number> {
  return request.post(`/api/business/contract/from-opportunity/${opportunityId}`)
}

/**
 * 更新合同
 */
export function updateContract(id: number, data: ContractFormData): Promise<void> {
  return request.put(`/api/business/contract/${id}`, data)
}

/**
 * 删除合同
 */
export function deleteContract(id: number): Promise<void> {
  return request.delete(`/api/business/contract/${id}`)
}

/**
 * 提交审批
 */
export function submitContractForApproval(id: number): Promise<void> {
  return request.post(`/api/business/contract/${id}/submit`)
}

/**
 * 审批通过
 */
export function approveContract(id: number): Promise<void> {
  return request.post(`/api/business/contract/${id}/approve`)
}

/**
 * 审批驳回
 */
export function rejectContract(id: number): Promise<void> {
  return request.post(`/api/business/contract/${id}/reject`)
}

/**
 * 完成合同
 */
export function completeContract(id: number): Promise<void> {
  return request.post(`/api/business/contract/${id}/complete`)
}

/**
 * 终止合同
 */
export function terminateContract(id: number): Promise<void> {
  return request.post(`/api/business/contract/${id}/terminate`)
}

/**
 * 更新合同文件
 */
export function updateContractFile(id: number, fileUrl: string): Promise<void> {
  return request.put(`/api/business/contract/${id}/file`, null, { params: { fileUrl } })
}

/**
 * 获取合同统计数据
 */
export function getContractStatistics(ownerId?: number): Promise<ContractStatistics> {
  return request.get('/api/business/contract/statistics', { params: { ownerId } })
}
