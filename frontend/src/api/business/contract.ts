import { request } from '@/api/request'
import type { PageResult } from '@/types/common'
import type { Contract, ContractQueryParams, ContractFormData, ContractStatistics } from '@/types/business/contract'

/**
 * 分页查询合同列表
 */
export async function pageContracts(params: ContractQueryParams): Promise<PageResult<Contract>> {
  const result = await request.get<PageResult<Contract>>('/business/contract/page', { params })
  return result.data
}

/**
 * 根据客户ID获取合同列表
 */
export async function getContractsByCustomerId(customerId: string): Promise<Contract[]> {
  const result = await request.get<Contract[]>(`/business/contract/customer/${customerId}`)
  return result.data
}

/**
 * 根据商机ID获取合同列表
 */
export async function getContractsByOpportunityId(opportunityId: string): Promise<Contract[]> {
  const result = await request.get<Contract[]>(`/business/contract/opportunity/${opportunityId}`)
  return result.data
}

/**
 * 根据ID获取合同详情
 */
export async function getContractById(id: string): Promise<Contract> {
  const result = await request.get<Contract>(`/business/contract/${id}`)
  return result.data
}

/**
 * 创建合同
 */
export async function createContract(data: ContractFormData): Promise<string> {
  const result = await request.post<string>('/business/contract', data)
  return result.data
}

/**
 * 从商机创建合同
 */
export async function createContractFromOpportunity(opportunityId: string): Promise<string> {
  const result = await request.post<string>(`/business/contract/from-opportunity/${opportunityId}`)
  return result.data
}

/**
 * 更新合同
 */
export async function updateContract(id: string, data: ContractFormData): Promise<void> {
  await request.put(`/business/contract/${id}`, data)
}

/**
 * 删除合同
 */
export async function deleteContract(id: string): Promise<void> {
  await request.delete(`/business/contract/${id}`)
}

/**
 * 提交审批
 */
export async function submitContractForApproval(id: string): Promise<void> {
  await request.post(`/business/contract/${id}/submit`)
}

/**
 * 审批通过
 */
export async function approveContract(id: string): Promise<void> {
  await request.post(`/business/contract/${id}/approve`)
}

/**
 * 审批驳回
 */
export async function rejectContract(id: string): Promise<void> {
  await request.post(`/business/contract/${id}/reject`)
}

/**
 * 完成合同
 */
export async function completeContract(id: string): Promise<void> {
  await request.post(`/business/contract/${id}/complete`)
}

/**
 * 终止合同
 */
export async function terminateContract(id: string): Promise<void> {
  await request.post(`/business/contract/${id}/terminate`)
}

/**
 * 更新合同文件
 */
export async function updateContractFile(id: string, fileUrl: string): Promise<void> {
  await request.put(`/business/contract/${id}/file`, null, { params: { fileUrl } })
}

/**
 * 获取合同统计数据
 */
export async function getContractStatistics(ownerId?: string): Promise<ContractStatistics> {
  const result = await request.get<ContractStatistics>('/business/contract/statistics', { params: { ownerId } })
  return result.data
}
