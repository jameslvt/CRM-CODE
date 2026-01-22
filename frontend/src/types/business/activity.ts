/**
 * 跟进记录类型定义
 * 对应后端 ActivityDTO
 */

// ==================== 跟进记录 ====================

/**
 * 跟进记录
 */
export interface Activity {
  id: number
  /** 类型: phone、visit、email、meeting、other */
  type: string
  /** 类型名称 */
  typeName?: string
  /** 跟进内容 */
  content: string
  /** 关联对象类型: lead、customer、opportunity */
  targetType: string
  /** 关联对象类型名称 */
  targetTypeName?: string
  /** 关联对象 ID */
  targetId: number
  /** 关联对象名称 */
  targetName?: string
  /** 下次跟进时间 */
  nextTime?: string
  /** 创建人ID */
  createBy?: number
  /** 创建人名称 */
  createByName?: string
  /** 创建时间 */
  createTime?: string
}

/**
 * 跟进记录查询参数
 */
export interface ActivityQueryParams {
  pageNum?: number
  pageSize?: number
  /** 关联对象类型 */
  targetType?: string
  /** 关联对象ID */
  targetId?: number
  /** 跟进类型 */
  type?: string
  /** 创建人ID */
  createBy?: number
}

/**
 * 跟进记录表单数据
 */
export interface ActivityFormData {
  id?: number
  /** 类型 */
  type: string
  /** 跟进内容 */
  content: string
  /** 关联对象类型 */
  targetType: string
  /** 关联对象 ID */
  targetId: number
  /** 下次跟进时间 */
  nextTime?: string
}

// ==================== 跟进类型枚举 ====================

/**
 * 跟进类型
 */
export enum ActivityType {
  /** 电话 */
  PHONE = 'phone',
  /** 拜访 */
  VISIT = 'visit',
  /** 邮件 */
  EMAIL = 'email',
  /** 会议 */
  MEETING = 'meeting',
  /** 其他 */
  OTHER = 'other'
}

/**
 * 跟进类型选项
 */
export const activityTypeOptions = [
  { label: '电话', value: ActivityType.PHONE },
  { label: '拜访', value: ActivityType.VISIT },
  { label: '邮件', value: ActivityType.EMAIL },
  { label: '会议', value: ActivityType.MEETING },
  { label: '其他', value: ActivityType.OTHER }
]

/**
 * 跟进类型颜色映射
 */
export const activityTypeColorMap: Record<string, { bg: string; color: string; icon: string }> = {
  [ActivityType.PHONE]: { bg: '#dbeafe', color: '#2563eb', icon: 'CallOutline' },
  [ActivityType.VISIT]: { bg: '#dcfce7', color: '#22c55e', icon: 'WalkOutline' },
  [ActivityType.EMAIL]: { bg: '#fef3c7', color: '#f59e0b', icon: 'MailOutline' },
  [ActivityType.MEETING]: { bg: '#f3e8ff', color: '#9333ea', icon: 'PeopleOutline' },
  [ActivityType.OTHER]: { bg: '#f1f5f9', color: '#64748b', icon: 'EllipsisHorizontalOutline' }
}

/**
 * 获取跟进类型名称
 */
export function getActivityTypeName(type: string): string {
  const option = activityTypeOptions.find(o => o.value === type)
  return option?.label || '未知'
}

/**
 * 获取跟进类型颜色
 */
export function getActivityTypeColor(type: string): { bg: string; color: string; icon: string } {
  return activityTypeColorMap[type] || activityTypeColorMap[ActivityType.OTHER]
}

// ==================== 关联对象类型 ====================

/**
 * 关联对象类型
 */
export enum TargetType {
  /** 线索 */
  LEAD = 'lead',
  /** 客户 */
  CUSTOMER = 'customer',
  /** 商机 */
  OPPORTUNITY = 'opportunity'
}

/**
 * 关联对象类型选项
 */
export const targetTypeOptions = [
  { label: '线索', value: TargetType.LEAD },
  { label: '客户', value: TargetType.CUSTOMER },
  { label: '商机', value: TargetType.OPPORTUNITY }
]

/**
 * 获取关联对象类型名称
 */
export function getTargetTypeName(type: string): string {
  const option = targetTypeOptions.find(o => o.value === type)
  return option?.label || '未知'
}

// ==================== 跟进统计 ====================

/**
 * 跟进统计数据
 */
export interface ActivityStatistics {
  /** 总跟进次数 */
  totalCount: number
  /** 本周跟进次数 */
  weekCount: number
  /** 本月跟进次数 */
  monthCount: number
  /** 各类型统计 */
  typeStats: Record<string, number>
}
