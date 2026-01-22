import request from '@/api/request'
import type { PageResult } from '@/types/common'
import type {
  Activity,
  ActivityQueryParams,
  ActivityFormData,
  ActivityStatistics
} from '@/types/business/activity'

// ==================== 跟进记录 ====================

/**
 * 分页查询跟进记录列表
 */
export function pageActivities(params: ActivityQueryParams): Promise<PageResult<Activity>> {
  return request.get('/api/business/activity/page', { params })
}

/**
 * 根据关联对象获取跟进记录列表（时间线）
 */
export function getActivitiesByTarget(targetType: string, targetId: number): Promise<Activity[]> {
  return request.get(`/api/business/activity/target/${targetType}/${targetId}`)
}

/**
 * 根据ID获取跟进记录详情
 */
export function getActivityById(id: number): Promise<Activity> {
  return request.get(`/api/business/activity/${id}`)
}

/**
 * 创建跟进记录
 */
export function createActivity(data: ActivityFormData): Promise<number> {
  return request.post('/api/business/activity', data)
}

/**
 * 更新跟进记录
 */
export function updateActivity(id: number, data: ActivityFormData): Promise<void> {
  return request.put(`/api/business/activity/${id}`, data)
}

/**
 * 删除跟进记录
 */
export function deleteActivity(id: number): Promise<void> {
  return request.delete(`/api/business/activity/${id}`)
}

/**
 * 获取今日待跟进列表
 */
export function getTodayPendingActivities(userId: number): Promise<Activity[]> {
  return request.get('/api/business/activity/today-pending', { params: { userId } })
}

/**
 * 获取跟进统计数据
 */
export function getActivityStatistics(targetType?: string, targetId?: number): Promise<ActivityStatistics> {
  return request.get('/api/business/activity/statistics', { params: { targetType, targetId } })
}
