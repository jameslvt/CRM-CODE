/**
 * 客户模块类型定义
 */

import type { PageParams } from '../common'

/**
 * 客户状态枚举
 */
export enum CustomerStatus {
  /** 正常 */
  NORMAL = 1,
  /** 公海 */
  PUBLIC_POOL = 2
}

/**
 * 客户级别枚举
 */
export enum CustomerLevel {
  /** A级 */
  A = 'A',
  /** B级 */
  B = 'B',
  /** C级 */
  C = 'C',
  /** D级 */
  D = 'D'
}

/**
 * 客户规模枚举
 */
export enum CustomerScale {
  /** 小型 */
  SMALL = '小型',
  /** 中型 */
  MEDIUM = '中型',
  /** 大型 */
  LARGE = '大型',
  /** 集团 */
  GROUP = '集团'
}

/**
 * 客户实体
 */
export interface Customer {
  /** 客户ID */
  id: number | string
  /** 客户名称 */
  name: string
  /** 简称 */
  shortName?: string
  /** 客户编码 */
  code?: string
  /** 行业 */
  industry?: string
  /** 规模 */
  scale?: string
  /** 来源 */
  source?: string
  /** 级别 */
  level?: string
  /** 电话 */
  phone?: string
  /** 传真 */
  fax?: string
  /** 网站 */
  website?: string
  /** 地址 */
  address?: string
  /** 负责人ID */
  ownerId?: number | string
  /** 负责人姓名 */
  ownerName?: string
  /** 来源线索ID */
  leadId?: number | string
  /** 状态 */
  status: number
  /** 状态名称 */
  statusName?: string
  /** 备注 */
  remark?: string
  /** 创建人 */
  createBy?: number | string
  /** 更新人 */
  updateBy?: number | string
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 客户查询参数
 */
export interface CustomerQueryParams extends PageParams {
  /** 客户名称 */
  name?: string
  /** 行业 */
  industry?: string
  /** 级别 */
  level?: string
  /** 负责人ID */
  ownerId?: number | string
  /** 状态 */
  status?: number
  /** 是否查询公海客户 */
  isPublicPool?: boolean
}

/**
 * 客户表单数据
 */
export interface CustomerFormData {
  /** 客户ID（编辑时必填） */
  id?: number | string
  /** 客户名称 */
  name: string
  /** 简称 */
  shortName?: string
  /** 行业 */
  industry?: string
  /** 规模 */
  scale?: string
  /** 来源 */
  source?: string
  /** 级别 */
  level?: string
  /** 电话 */
  phone?: string
  /** 传真 */
  fax?: string
  /** 网站 */
  website?: string
  /** 地址 */
  address?: string
  /** 备注 */
  remark?: string
}

/**
 * 客户360度视图
 */
export interface Customer360 {
  /** 基本信息 */
  basicInfo: Customer
  /** 联系人列表 */
  contacts: Contact[]
  /** 商机列表 */
  opportunities: OpportunitySummary[]
  /** 合同列表 */
  contracts: ContractSummary[]
  /** 跟进记录列表 */
  activities: ActivitySummary[]
  /** 统计信息 */
  statistics: CustomerStatistics
}

/**
 * 商机简要信息
 */
export interface OpportunitySummary {
  id: number | string
  name: string
  amount: number
  stage: string
  probability: number
  expectedDate?: string
  createTime: string
}

/**
 * 合同简要信息
 */
export interface ContractSummary {
  id: number | string
  contractNo: string
  name: string
  amount: number
  status: number
  statusName?: string
  signDate?: string
  createTime: string
}

/**
 * 跟进记录简要信息
 */
export interface ActivitySummary {
  id: number | string
  type: string
  content: string
  createByName?: string
  createTime: string
}

/**
 * 客户统计信息
 */
export interface CustomerStatistics {
  /** 商机总金额 */
  totalOpportunityAmount: number
  /** 合同总金额 */
  totalContractAmount: number
  /** 已回款总金额 */
  totalPaymentReceived: number
  /** 商机数量 */
  opportunityCount: number
  /** 合同数量 */
  contractCount: number
  /** 联系人数量 */
  contactCount: number
  /** 跟进记录数量 */
  activityCount: number
}

/**
 * 联系人实体
 */
export interface Contact {
  /** 联系人ID */
  id: number | string
  /** 客户ID */
  customerId: number | string
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
 * 联系人表单数据
 */
export interface ContactFormData {
  /** 联系人ID（编辑时必填） */
  id?: number | string
  /** 客户ID */
  customerId: number | string
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
