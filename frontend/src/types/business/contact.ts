/**
 * 联系人模块类型定义
 */

import type { PageParams } from '../common'

/**
 * 性别枚举
 */
export enum Gender {
  /** 男 */
  MALE = 1,
  /** 女 */
  FEMALE = 2
}

/**
 * 联系人实体
 */
export interface Contact {
  /** 联系人ID */
  id: number
  /** 客户ID */
  customerId: number
  /** 客户名称 */
  customerName?: string
  /** 姓名 */
  name: string
  /** 性别 */
  gender?: number
  /** 性别名称 */
  genderName?: string
  /** 职位 */
  position?: string
  /** 部门 */
  department?: string
  /** 电话 */
  phone?: string
  /** 手机 */
  mobile?: string
  /** 邮箱 */
  email?: string
  /** 微信 */
  wechat?: string
  /** 是否主要联系人 */
  isPrimary: number
  /** 生日 */
  birthday?: string
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 联系人查询参数
 */
export interface ContactQueryParams extends PageParams {
  /** 客户ID */
  customerId: number
  /** 姓名 */
  name?: string
}

/**
 * 联系人表单数据
 */
export interface ContactFormData {
  /** 联系人ID（编辑时必填） */
  id?: number
  /** 客户ID */
  customerId: number
  /** 姓名 */
  name: string
  /** 性别 */
  gender?: number
  /** 职位 */
  position?: string
  /** 部门 */
  department?: string
  /** 电话 */
  phone?: string
  /** 手机 */
  mobile?: string
  /** 邮箱 */
  email?: string
  /** 微信 */
  wechat?: string
  /** 是否主要联系人 */
  isPrimary?: boolean
  /** 生日 */
  birthday?: string
  /** 备注 */
  remark?: string
}
