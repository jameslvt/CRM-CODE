import request from '../request'
import type { PageResult } from '@/types/common'

/**
 * 操作日志类型定义
 */
export interface OperationLog {
  id: number
  module: string
  operation: string
  method: string
  requestUrl: string
  requestParams: string
  responseResult: string
  userId: number
  username: string
  ip: string
  duration: number
  status: number
  errorMsg: string
  createTime: string
}

/**
 * 操作日志查询参数
 */
export interface OperationLogQuery {
  module?: string
  operation?: string
  username?: string
  status?: number
  startTime?: string
  endTime?: string
  pageNum?: number
  pageSize?: number
}

/**
 * 分页查询操作日志
 */
export function getOperationLogs(params: OperationLogQuery): Promise<PageResult<OperationLog>> {
  return request.get('/system/logs', { params })
}

/**
 * 获取操作日志详情
 */
export function getOperationLogById(id: string): Promise<OperationLog> {
  return request.get(`/system/logs/${id}`)
}

/**
 * 清理历史日志
 */
export function cleanOperationLogs(days: number = 90): Promise<string> {
  return request.delete('/system/logs/clean', { params: { days } })
}
