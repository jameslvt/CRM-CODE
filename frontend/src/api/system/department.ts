/**
 * 部门管理 API
 * 提供部门的增删改查、树形结构查询等接口
 */

import { request } from '@/api/request'
import type { Result } from '@/types/common'
import type {
  Department,
  DepartmentQueryParams,
  DepartmentFormData,
  User
} from '@/types/system'

/**
 * 获取部门树形列表
 * @param params 查询参数
 * @returns 部门树形列表
 */
export function getDepartmentTree(params?: DepartmentQueryParams): Promise<Result<Department[]>> {
  return request.get('/system/departments/tree', { params })
}

/**
 * 获取部门详情
 * @param id 部门 ID
 * @returns 部门详情
 */
export function getDepartmentDetail(id: number): Promise<Result<Department>> {
  return request.get(`/system/departments/${id}`)
}

/**
 * 新增部门
 * @param data 部门表单数据
 * @returns 操作结果
 */
export function createDepartment(data: DepartmentFormData): Promise<Result<void>> {
  return request.post('/system/departments', data)
}

/**
 * 编辑部门
 * @param id 部门 ID
 * @param data 部门表单数据
 * @returns 操作结果
 */
export function updateDepartment(id: number, data: DepartmentFormData): Promise<Result<void>> {
  return request.put(`/system/departments/${id}`, data)
}

/**
 * 删除部门
 * @param id 部门 ID
 * @returns 操作结果
 */
export function deleteDepartment(id: number): Promise<Result<void>> {
  return request.delete(`/system/departments/${id}`)
}

/**
 * 检查部门是否可以删除
 * @param id 部门 ID
 * @returns 检查结果（包含是否有子部门、是否有用户等信息）
 */
export function checkDepartmentDeletable(id: number): Promise<Result<{
  deletable: boolean
  hasChildren: boolean
  hasUsers: boolean
  childrenCount: number
  usersCount: number
}>> {
  return request.get(`/system/departments/${id}/check-deletable`)
}

/**
 * 修改部门状态
 * @param id 部门 ID
 * @param status 状态：1-启用，0-禁用
 * @returns 操作结果
 */
export function updateDepartmentStatus(id: number, status: number): Promise<Result<void>> {
  return request.put(`/system/departments/${id}/status`, { status })
}

/**
 * 获取所有用户列表（用于选择负责人）
 * @returns 用户列表
 */
export function getAllUsers(): Promise<Result<User[]>> {
  return request.get('/system/users/all')
}

/**
 * 检查部门编码是否存在
 * @param code 部门编码
 * @param excludeId 排除的部门 ID（编辑时使用）
 * @returns 是否存在
 */
export function checkDepartmentCodeExists(code: string, excludeId?: number): Promise<Result<boolean>> {
  return request.get('/system/departments/check-code', {
    params: { code, excludeId }
  })
}

/**
 * 获取部门下的所有用户
 * @param deptId 部门 ID
 * @returns 用户列表
 */
export function getDepartmentUsers(deptId: number): Promise<Result<User[]>> {
  return request.get(`/system/departments/${deptId}/users`)
}
