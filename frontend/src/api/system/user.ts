/**
 * 用户管理 API
 * 提供用户的增删改查、角色分配、密码重置等接口
 */

import { request } from '@/api/request'
import type { Result, PageResult } from '@/types/common'
import type {
  User,
  UserQueryParams,
  UserFormData,
  AssignRolesParams,
  ResetPasswordParams,
  Role,
  Department
} from '@/types/system'

/**
 * 用户列表查询
 * @param params 查询参数
 * @returns 分页用户列表
 */
export function getUserList(params: UserQueryParams): Promise<Result<PageResult<User>>> {
  return request.get('/system/user/list', { params })
}

/**
 * 获取用户详情
 * @param id 用户 ID
 * @returns 用户详情
 */
export function getUserDetail(id: number): Promise<Result<User>> {
  return request.get(`/system/user/${id}`)
}

/**
 * 新增用户
 * @param data 用户表单数据
 * @returns 操作结果
 */
export function createUser(data: UserFormData): Promise<Result<void>> {
  return request.post('/system/user', data)
}

/**
 * 编辑用户
 * @param id 用户 ID
 * @param data 用户表单数据
 * @returns 操作结果
 */
export function updateUser(id: number, data: UserFormData): Promise<Result<void>> {
  return request.put(`/system/user/${id}`, data)
}

/**
 * 删除用户
 * @param id 用户 ID
 * @returns 操作结果
 */
export function deleteUser(id: number): Promise<Result<void>> {
  return request.delete(`/system/user/${id}`)
}

/**
 * 批量删除用户
 * @param ids 用户 ID 列表
 * @returns 操作结果
 */
export function batchDeleteUsers(ids: number[]): Promise<Result<void>> {
  return request.post('/system/user/batch-delete', { ids })
}

/**
 * 分配角色
 * @param params 分配角色参数
 * @returns 操作结果
 */
export function assignRoles(params: AssignRolesParams): Promise<Result<void>> {
  return request.post(`/system/user/${params.userId}/roles`, {
    roleIds: params.roleIds
  })
}

/**
 * 获取用户已分配的角色
 * @param userId 用户 ID
 * @returns 角色 ID 列表
 */
export function getUserRoles(userId: number): Promise<Result<number[]>> {
  return request.get(`/system/user/${userId}/roles`)
}

/**
 * 重置密码
 * @param params 重置密码参数
 * @returns 操作结果
 */
export function resetPassword(params: ResetPasswordParams): Promise<Result<void>> {
  return request.post(`/system/user/${params.userId}/reset-password`, {
    newPassword: params.newPassword
  })
}

/**
 * 修改用户状态
 * @param id 用户 ID
 * @param status 状态：1-启用，0-禁用
 * @returns 操作结果
 */
export function updateUserStatus(id: number, status: number): Promise<Result<void>> {
  return request.put(`/system/user/${id}/status`, { status })
}

/**
 * 获取所有角色列表（用于分配角色）
 * @returns 角色列表
 */
export function getAllRoles(): Promise<Result<Role[]>> {
  return request.get('/system/role/all')
}

/**
 * 获取部门树（用于部门选择）
 * @returns 部门树
 */
export function getDepartmentTree(): Promise<Result<Department[]>> {
  return request.get('/system/department/tree')
}
