/**
 * 系统模块类型定义
 * 包含用户、角色、部门、字典等实体的类型定义
 */

import type { PageParams } from './common'

/**
 * 用户状态枚举
 */
export enum UserStatus {
  /** 启用 */
  ENABLED = 1,
  /** 禁用 */
  DISABLED = 0
}

/**
 * 用户实体
 */
export interface User {
  /** 用户 ID */
  id: number
  /** 用户名 */
  username: string
  /** 真实姓名 */
  realName: string
  /** 手机号 */
  phone?: string
  /** 邮箱 */
  email?: string
  /** 头像 URL */
  avatar?: string
  /** 部门 ID */
  deptId?: number
  /** 部门名称 */
  deptName?: string
  /** 状态：1-启用，0-禁用 */
  status: UserStatus
  /** 角色 ID 列表 */
  roleIds?: number[]
  /** 角色名称列表 */
  roleNames?: string[]
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
  /** 最后登录时间 */
  lastLoginTime?: string
}

/**
 * 用户查询参数
 */
export interface UserQueryParams extends PageParams {
  /** 用户名或真实姓名（模糊查询） */
  keyword?: string
  /** 部门 ID */
  deptId?: number
  /** 状态 */
  status?: UserStatus
  /** 开始时间 */
  startTime?: string
  /** 结束时间 */
  endTime?: string
}

/**
 * 用户表单数据
 */
export interface UserFormData {
  /** 用户 ID（编辑时必填） */
  id?: number
  /** 用户名 */
  username: string
  /** 真实姓名 */
  realName: string
  /** 手机号 */
  phone?: string
  /** 邮箱 */
  email?: string
  /** 密码（新增时必填） */
  password?: string
  /** 部门 ID */
  deptId?: number
  /** 状态 */
  status: UserStatus
}

/**
 * 分配角色参数
 */
export interface AssignRolesParams {
  /** 用户 ID */
  userId: number
  /** 角色 ID 列表 */
  roleIds: number[]
}

/**
 * 重置密码参数
 */
export interface ResetPasswordParams {
  /** 用户 ID */
  userId: number
  /** 新密码 */
  newPassword: string
}

/**
 * 角色状态枚举
 */
export enum RoleStatus {
  /** 启用 */
  ENABLED = 1,
  /** 禁用 */
  DISABLED = 0
}

/**
 * 数据权限范围枚举
 */
export enum DataScope {
  /** 全部数据 */
  ALL = 1,
  /** 本部门及下级 */
  DEPT_AND_CHILD = 2,
  /** 本部门 */
  DEPT = 3,
  /** 仅本人 */
  SELF = 4
}

/**
 * 角色实体
 * 字段与后端 RoleDTO 保持一致
 */
export interface Role {
  /** 角色 ID */
  id: number
  /** 角色编码（后端字段名：roleCode） */
  roleCode: string
  /** 角色名称（后端字段名：roleName） */
  roleName: string
  /** 角色描述 */
  description?: string
  /** 数据权限范围 */
  dataScope?: DataScope
  /** 排序号（后端字段名：sortOrder） */
  sortOrder: number
  /** 状态：1-启用，0-禁用 */
  status: RoleStatus
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
  
  // 兼容旧代码的别名字段
  /** @deprecated 使用 roleCode 代替 */
  code?: string
  /** @deprecated 使用 roleName 代替 */
  name?: string
  /** @deprecated 使用 sortOrder 代替 */
  sort?: number
}

/**
 * 角色查询参数
 */
export interface RoleQueryParams extends PageParams {
  /** 角色名称（模糊查询） */
  roleName?: string
  /** 状态 */
  status?: RoleStatus
  /** @deprecated 使用 roleName 代替 */
  keyword?: string
}

/**
 * 角色表单数据
 * 字段与后端 RoleDTO 保持一致
 */
export interface RoleFormData {
  /** 角色 ID（编辑时必填） */
  id?: number
  /** 角色编码 */
  roleCode: string
  /** 角色名称 */
  roleName: string
  /** 角色描述 */
  description?: string
  /** 数据权限范围 */
  dataScope?: DataScope
  /** 排序号 */
  sortOrder: number
  /** 状态 */
  status: RoleStatus
  /** 备注 */
  remark?: string
  
  // 兼容旧代码的别名字段
  /** @deprecated 使用 roleCode 代替 */
  code?: string
  /** @deprecated 使用 roleName 代替 */
  name?: string
  /** @deprecated 使用 sortOrder 代替 */
  sort?: number
}

/**
 * 分配权限参数
 */
export interface AssignPermissionsParams {
  /** 角色 ID */
  roleId: number
  /** 权限 ID 列表 */
  permissionIds: number[]
}

/**
 * 权限类型枚举
 */
export enum PermissionType {
  /** 目录 */
  DIRECTORY = 1,
  /** 菜单 */
  MENU = 2,
  /** 按钮 */
  BUTTON = 3
}

/**
 * 权限状态枚举
 */
export enum PermissionStatus {
  /** 启用 */
  ENABLED = 1,
  /** 禁用 */
  DISABLED = 0
}

/**
 * 权限可见性枚举
 */
export enum PermissionVisible {
  /** 显示 */
  SHOW = 1,
  /** 隐藏 */
  HIDE = 0
}

/**
 * 权限实体
 */
export interface Permission {
  /** 权限 ID */
  id: number
  /** 权限名称 */
  name: string
  /** 权限标识（如 system:user:add） */
  code: string
  /** 权限标识（别名，兼容旧代码） */
  permissionKey?: string
  /** 权限类型：1-目录，2-菜单，3-按钮 */
  type: PermissionType
  /** 父权限 ID */
  parentId?: number
  /** 路由路径 */
  path?: string
  /** 组件路径 */
  component?: string
  /** 图标 */
  icon?: string
  /** 排序号 */
  sort: number
  /** 是否可见：1-显示，0-隐藏 */
  visible: PermissionVisible
  /** 状态：1-启用，0-禁用 */
  status: PermissionStatus
  /** 子权限 */
  children?: Permission[]
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 权限查询参数
 */
export interface PermissionQueryParams {
  /** 权限名称或权限标识（模糊查询） */
  keyword?: string
  /** 权限类型 */
  type?: PermissionType
  /** 状态 */
  status?: PermissionStatus
}

/**
 * 权限表单数据
 */
export interface PermissionFormData {
  /** 权限 ID（编辑时必填） */
  id?: number
  /** 父权限 ID */
  parentId?: number
  /** 权限类型 */
  type: PermissionType
  /** 权限名称 */
  name: string
  /** 权限标识 */
  code: string
  /** 权限标识（别名，兼容旧代码） */
  permissionKey?: string
  /** 路由路径 */
  path?: string
  /** 组件路径 */
  component?: string
  /** 图标 */
  icon?: string
  /** 排序号 */
  sort: number
  /** 是否可见 */
  visible: PermissionVisible
  /** 状态 */
  status: PermissionStatus
}

/**
 * 部门状态枚举
 */
export enum DepartmentStatus {
  /** 启用 */
  ENABLED = 1,
  /** 禁用 */
  DISABLED = 0
}

/**
 * 部门实体
 */
export interface Department {
  /** 部门 ID */
  id: number
  /** 部门名称 */
  name: string
  /** 父部门 ID */
  parentId?: number
  /** 部门编码 */
  code: string
  /** 负责人 ID */
  leaderId?: number
  /** 负责人姓名 */
  leaderName?: string
  /** 联系电话 */
  phone?: string
  /** 邮箱 */
  email?: string
  /** 排序号 */
  sort: number
  /** 状态：1-启用，0-禁用 */
  status: DepartmentStatus
  /** 子部门 */
  children?: Department[]
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 部门查询参数
 */
export interface DepartmentQueryParams {
  /** 部门名称或部门编码（模糊查询） */
  keyword?: string
  /** 状态 */
  status?: DepartmentStatus
}

/**
 * 部门表单数据
 */
export interface DepartmentFormData {
  /** 部门 ID（编辑时必填） */
  id?: number
  /** 部门名称 */
  name: string
  /** 父部门 ID */
  parentId?: number
  /** 部门编码 */
  code: string
  /** 负责人 ID */
  leaderId?: number
  /** 联系电话 */
  phone?: string
  /** 邮箱 */
  email?: string
  /** 排序号 */
  sort: number
  /** 状态 */
  status: DepartmentStatus
}

/**
 * 字典类型实体
 */
export interface DictType {
  /** 字典类型 ID */
  id: number
  /** 字典名称 */
  dictName: string
  /** 字典类型（唯一标识） */
  dictType: string
  /** 状态：0-停用，1-正常 */
  status: number
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 字典数据实体
 */
export interface DictData {
  /** 字典数据 ID */
  id: number
  /** 字典排序 */
  dictSort: number
  /** 字典标签 */
  dictLabel: string
  /** 字典键值 */
  dictValue: string
  /** 字典类型 */
  dictType: string
  /** 样式属性（如颜色、图标等） */
  cssClass?: string
  /** 表格回显样式 */
  listClass?: string
  /** 是否默认：0-否，1-是 */
  isDefault: number
  /** 状态：0-停用，1-正常 */
  status: number
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
}
