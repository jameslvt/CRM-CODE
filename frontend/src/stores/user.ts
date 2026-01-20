/**
 * 用户状态管理
 * 管理用户登录状态、用户信息、权限等
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, LoginResult } from '@/types/common'
import { getToken, setToken, removeToken, setRefreshToken } from '@/api/request'
import { request } from '@/api/request'

/**
 * 登录请求参数
 */
interface LoginParams {
  username: string
  password: string
  captcha?: string
}

/**
 * 用户 Store
 */
export const useUserStore = defineStore('user', () => {
  // ========== 状态 ==========

  /** 用户信息 */
  const userInfo = ref<UserInfo | null>(null)

  /** 用户角色列表 */
  const roles = ref<string[]>([])

  /** 用户权限列表 */
  const permissions = ref<string[]>([])

  // ========== 计算属性 ==========

  /** 是否已登录（只检查 token 是否存在） */
  const isLoggedIn = computed(() => !!getToken())

  /** 用户名 */
  const username = computed(() => userInfo.value?.username || '')

  /** 昵称 */
  const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '')

  /** 头像 */
  const avatar = computed(() => userInfo.value?.avatar || '')

  /** 是否是管理员 */
  const isAdmin = computed(() => roles.value.includes('admin'))

  // ========== 方法 ==========

  /**
   * 登录
   * @param params 登录参数
   */
  async function login(params: LoginParams): Promise<void> {
    try {
      const result = await request.post<LoginResult>('/auth/login', params)

      // 保存 Token
      setToken(result.data.accessToken)
      setRefreshToken(result.data.refreshToken)

      // 保存用户信息
      userInfo.value = result.data.userInfo
      roles.value = result.data.roles || []
      permissions.value = result.data.permissions || []
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  /**
   * 登出
   */
  async function logout(): Promise<void> {
    try {
      // 调用登出接口
      await request.post('/auth/logout')
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      // 清除本地数据
      removeToken()
      userInfo.value = null
      roles.value = []
      permissions.value = []
    }
  }

  /**
   * 获取用户信息
   * 用于页面刷新后恢复用户状态
   */
  async function getUserInfo(): Promise<void> {
    try {
      const result = await request.get<UserInfo>('/auth/current-user')
      userInfo.value = result.data
      // 获取角色和权限
      const [rolesResult, permissionsResult] = await Promise.all([
        request.get<string[]>('/auth/current-roles'),
        request.get<string[]>('/auth/current-permissions')
      ])
      roles.value = rolesResult.data || []
      permissions.value = permissionsResult.data || []
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 获取失败，清除 Token
      removeToken()
      throw error
    }
  }

  /**
   * 更新用户信息
   * @param data 用户信息
   */
  async function updateUserInfo(data: Partial<UserInfo>): Promise<void> {
    try {
      await request.put('/system/user/profile', data)
      // 更新本地用户信息
      if (userInfo.value) {
        userInfo.value = { ...userInfo.value, ...data }
      }
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  /**
   * 修改密码
   * @param oldPassword 旧密码
   * @param newPassword 新密码
   */
  async function changePassword(oldPassword: string, newPassword: string): Promise<void> {
    try {
      await request.put('/system/user/password', {
        oldPassword,
        newPassword
      })
    } catch (error) {
      console.error('修改密码失败:', error)
      throw error
    }
  }

  /**
   * 检查是否有指定权限
   * @param permission 权限标识
   */
  function hasPermission(permission: string): boolean {
    // 管理员拥有所有权限
    if (isAdmin.value) {
      return true
    }
    return permissions.value.includes(permission)
  }

  /**
   * 检查是否有指定角色
   * @param role 角色标识
   */
  function hasRole(role: string): boolean {
    return roles.value.includes(role)
  }

  /**
   * 检查是否有任意一个权限
   * @param perms 权限标识列表
   */
  function hasAnyPermission(perms: string[]): boolean {
    if (isAdmin.value) {
      return true
    }
    return perms.some((perm) => permissions.value.includes(perm))
  }

  /**
   * 检查是否有所有权限
   * @param perms 权限标识列表
   */
  function hasAllPermissions(perms: string[]): boolean {
    if (isAdmin.value) {
      return true
    }
    return perms.every((perm) => permissions.value.includes(perm))
  }

  /**
   * 检查是否有任意一个角色
   * @param roleList 角色标识列表
   */
  function hasAnyRole(roleList: string[]): boolean {
    return roleList.some((role) => roles.value.includes(role))
  }

  /**
   * 检查是否有所有角色
   * @param roleList 角色标识列表
   */
  function hasAllRoles(roleList: string[]): boolean {
    return roleList.every((role) => roles.value.includes(role))
  }

  /**
   * 重置状态
   * 用于测试或特殊场景
   */
  function reset(): void {
    removeToken()
    userInfo.value = null
    roles.value = []
    permissions.value = []
  }

  return {
    // 状态
    userInfo,
    roles,
    permissions,

    // 计算属性
    isLoggedIn,
    username,
    nickname,
    avatar,
    isAdmin,

    // 方法
    login,
    logout,
    getUserInfo,
    updateUserInfo,
    changePassword,
    hasPermission,
    hasRole,
    hasAnyPermission,
    hasAllPermissions,
    hasAnyRole,
    hasAllRoles,
    reset
  }
})
