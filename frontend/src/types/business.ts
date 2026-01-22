/**
 * 业务模块类型定义
 * 包含线索、客户、商机等实体的类型定义
 */

import type { PageParams } from './common'

/**
 * 线索来源枚举
 */
export enum LeadSource {
  /** 网站 */
  WEBSITE = 1,
  /** 电话 */
  PHONE = 2,
  /** 推荐 */
  REFERRAL = 3,
  /** 展会 */
  EXHIBITION = 4,
  /** 其他 */
  OTHER = 5
}

/**
 * 线索状态枚举
 */
export enum LeadStatus {
  /** 新建 */
  NEW = 1,
  /** 跟进中 */
  FOLLOWING = 2,
  /** 已转化 */
  CONVERTED = 3,
  /** 已失效 */
  INVALID = 4
}

/**
 * 线索评级枚举
 */
export enum LeadRating {
  /** A（高） */
  HIGH = 1,
  /** B（中） */
  MEDIUM = 2,
  /** C（低） */
  LOW = 3
}

/**
 * 线索实体
 * 字段名与后端 LeadDTO 保持一致
 */
export interface Lead {
  /** 线索ID */
  id: number
  /** 线索名称 */
  name: string
  /** 联系电话 */
  phone: string
  /** 邮箱 */
  email?: string
  /** 公司名称 */
  company: string
  /** 职位 */
  position?: string
  /** 线索来源 */
  source: string
  /** 线索来源名称 */
  sourceName?: string
  /** 线索状态 (1-新建, 2-跟进中, 3-已转化, 4-已失效) */
  status: number
  /** 线索状态名称 */
  statusName?: string
  /** 线索评级 */
  rating?: string
  /** 线索评级名称 */
  ratingName?: string
  /** 预计金额（元） */
  estimatedAmount?: number
  /** 负责人ID */
  ownerId?: number
  /** 负责人姓名 */
  ownerName?: string
  /** 地址 */
  address?: string
  /** 行业 */
  industry?: string
  /** 备注 */
  remark?: string
  /** 转化时间 */
  convertTime?: string
  /** 转化后的客户ID */
  customerId?: number
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 线索查询参数
 * 字段名与后端 LeadQueryParams 保持一致
 */
export interface LeadQueryParams extends PageParams {
  /** 关键词（线索名称、公司名称、电话） */
  keyword?: string
  /** 线索来源 */
  source?: string
  /** 线索状态 */
  status?: number
  /** 负责人ID */
  ownerId?: number
  /** 行业 */
  industry?: string
  /** 开始时间 */
  startTime?: string
  /** 结束时间 */
  endTime?: string
}

/**
 * 线索表单数据
 * 字段名与后端 LeadFormData 保持一致
 */
export interface LeadFormData {
  /** 线索ID（编辑时必填） */
  id?: number
  /** 线索名称 */
  name: string
  /** 联系电话 */
  phone?: string
  /** 邮箱 */
  email?: string
  /** 公司名称 */
  company?: string
  /** 职位 */
  position?: string
  /** 线索来源 */
  source?: string
  /** 线索状态 */
  status?: number
  /** 线索评级 */
  rating?: string
  /** 预计金额（元） */
  estimatedAmount?: number
  /** 负责人ID */
  ownerId?: number
  /** 地址 */
  address?: string
  /** 行业 */
  industry?: string
  /** 备注 */
  remark?: string
}

/**
 * 线索转化参数
 */
export interface LeadConvertParams {
  /** 线索ID */
  leadId: number
  /** 客户名称 */
  customerName: string
  /** 客户类型 */
  customerType?: string
  /** 客户级别 */
  customerLevel?: string
  /** 是否创建商机 */
  createOpportunity?: boolean
  /** 商机名称（如果创建商机） */
  opportunityName?: string
  /** 预计金额（如果创建商机） */
  expectedAmount?: number
  /** 预计成交日期（如果创建商机） */
  expectedCloseDate?: string
  /** 备注 */
  remark?: string
}
