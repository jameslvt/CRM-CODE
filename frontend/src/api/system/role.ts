/**
 * 角色管理 API
 * 提供角色的增删改查、权限分配等接口
 */

import { request } from '@/api/request'
import type { Result, PageResult } from '@/types/common'
import type {
  Role,
  RoleQueryParams,
  RoleFormData,
  AssignPermissionsParams,
  Permission
} from '@/types/system'

/**
 * 角色列表查询
 * @param params 查询参数
 * @returns 分页角色列表
 */
export function getRoleList(params: RoleQueryParams): Promise<Result<PageResult<Role>>> {
  return request.get('/system/roles', { params })
}

/**
 * 获取角色详情
 * @param id 角色 ID
 * @returns 角色详情
 */
export function getRoleDetail(id: string): Promise<Result<Role>> {
  return request.get(`/system/roles/${id}`)
}

/**
 * 新增角色
 * @param data 角色表单数据
 * @returns 操作结果
 */
export function createRole(data: RoleFormData): Promise<Result<void>> {
  return request.post('/system/roles', data)
}

/**
 * 编辑角色
 * @param id 角色 ID
 * @param data 角色表单数据
 * @returns 操作结果
 */
export function updateRole(id: string, data: RoleFormData): Promise<Result<void>> {
  return request.put(`/system/roles/${id}`, data)
}

/**
 * 删除角色
 * @param id 角色 ID
 * @returns 操作结果
 */
export function deleteRole(id: string): Promise<Result<void>> {
  return request.delete(`/system/roles/${id}`)
}

/**
 * 批量删除角色
 * @param ids 角色 ID 列表
 * @returns 操作结果
 */
export function batchDeleteRoles(ids: string[]): Promise<Result<void>> {
  return request.post('/system/roles/batch-delete', { ids })
}

/**
 * 分配权限
 * @param params 分配权限参数
 * @returns 操作结果
 */
export function assignPermissions(params: AssignPermissionsParams): Promise<Result<void>> {
  // 后端接口接受 List<Long> 格式，直接发送数组
  return request.post(`/system/roles/${params.roleId}/permissions`, params.permissionIds)
}

/**
 * 获取角色已分配的权限
 * @param roleId 角色 ID
 * @returns 权限 ID 列表
 */
export function getRolePermissions(roleId: string): Promise<Result<string[]>> {
  return request.get(`/system/roles/${roleId}/permissions`)
}

/**
 * 获取权限树（用于分配权限）
 * @returns 权限树
 */
export function getPermissionTree(): Promise<Result<Permission[]>> {
  return request.get('/system/permissions/tree')
}

/**
 * 修改角色状态
 * @param id 角色 ID
 * @param status 状态：1-启用，0-禁用
 * @returns 操作结果
 */
export function updateRoleStatus(id: string, status: number): Promise<Result<void>> {
  return request.put(`/system/roles/${id}/status`, { status })
}

/**
 * 获取所有角色列表（用于下拉选择）
 * @returns 角色列表
 */
export function getAllRoles(): Promise<Result<Role[]>> {
  return request.get('/system/roles/all')
}

/**
 * 检查角色编码是否存在
 * @param code 角色编码
 * @param excludeId 排除的角色 ID（编辑时使用）
 * @returns 是否存在
 */
export function checkRoleCodeExists(code: string, excludeId?: number): Promise<Result<boolean>> {
  return request.get('/system/roles/check-code', {
    params: { code, excludeId }
  })
}
