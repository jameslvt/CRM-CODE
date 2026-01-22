import { request } from '@/api/request'
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
export async function pageActivities(params: ActivityQueryParams): Promise<PageResult<Activity>> {
  const result = await request.get<PageResult<Activity>>('/business/activity/page', { params })
  return result.data
}

/**
 * 根据关联对象获取跟进记录列表（时间线）
 */
export async function getActivitiesByTarget(targetType: string, targetId: string): Promise<Activity[]> {
  const result = await request.get<Activity[]>(`/business/activity/target/${targetType}/${targetId}`)
  return result.data
}

/**
 * 根据ID获取跟进记录详情
 */
export async function getActivityById(id: string): Promise<Activity> {
  const result = await request.get<Activity>(`/business/activity/${id}`)
  return result.data
}

/**
 * 创建跟进记录
 */
export async function createActivity(data: ActivityFormData): Promise<string> {
  const result = await request.post<string>('/business/activity', data)
  return result.data
}

/**
 * 更新跟进记录
 */
export async function updateActivity(id: string, data: ActivityFormData): Promise<void> {
  await request.put(`/business/activity/${id}`, data)
}

/**
 * 删除跟进记录
 */
export async function deleteActivity(id: string): Promise<void> {
  await request.delete(`/business/activity/${id}`)
}

/**
 * 获取今日待跟进列表
 */
export async function getTodayPendingActivities(userId: string): Promise<Activity[]> {
  const result = await request.get<Activity[]>('/business/activity/today-pending', { params: { userId } })
  return result.data
}

/**
 * 获取跟进统计数据
 */
export async function getActivityStatistics(targetType?: string, targetId?: string): Promise<ActivityStatistics> {
  const result = await request.get<ActivityStatistics>('/business/activity/statistics', { params: { targetType, targetId } })
  return result.data
}
