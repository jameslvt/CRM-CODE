/**
 * 权限管理 API
 * 提供权限的增删改查等接口
 */

import { request } from '@/api/request'
import type { Result } from '@/types/common'
import type {
  Permission,
  PermissionQueryParams,
  PermissionFormData
} from '@/types/system'

/**
 * 获取权限树形列表
 * @param params 查询参数
 * @returns 权限树形列表
 */
export function getPermissionTree(params?: PermissionQueryParams): Promise<Result<Permission[]>> {
  return request.get('/system/permission/tree', { params })
}

/**
 * 获取权限详情
 * @param id 权限 ID
 * @returns 权限详情
 */
export function getPermissionDetail(id: string): Promise<Result<Permission>> {
  return request.get(`/system/permission/${id}`)
}

/**
 * 新增权限
 * @param data 权限表单数据
 * @returns 操作结果
 */
export function createPermission(data: PermissionFormData): Promise<Result<void>> {
  return request.post('/system/permission', data)
}

/**
 * 编辑权限
 * @param id 权限 ID
 * @param data 权限表单数据
 * @returns 操作结果
 */
export function updatePermission(id: number, data: PermissionFormData): Promise<Result<void>> {
  return request.put(`/system/permission/${id}`, data)
}

/**
 * 删除权限
 * @param id 权限 ID
 * @returns 操作结果
 */
export function deletePermission(id: string): Promise<Result<void>> {
  return request.delete(`/system/permission/${id}`)
}

/**
 * 检查权限标识是否存在
 * @param code 权限标识
 * @param excludeId 排除的权限 ID（编辑时使用）
 * @returns 是否存在
 */
export function checkPermissionCodeExists(code: string, excludeId?: number): Promise<Result<boolean>> {
  return request.get('/system/permission/check-code', {
    params: { code, excludeId }
  })
}

/**
 * 获取权限树（用于上级权限选择）
 * @returns 权限树
 */
export function getPermissionTreeForSelect(): Promise<Result<Permission[]>> {
  return request.get('/system/permission/tree-select')
}
