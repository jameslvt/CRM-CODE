import request from '../request'
import type { Result, PageResult } from '@/types/common'
import type { DictType, DictData } from '@/types/system'

/**
 * 字典类型查询参数
 */
export interface DictTypeQueryParams {
  pageNum?: number
  pageSize?: number
  dictName?: string
  dictType?: string
  status?: number
}

/**
 * 字典数据查询参数
 */
export interface DictDataQueryParams {
  pageNum?: number
  pageSize?: number
  dictType?: string
  dictLabel?: string
  status?: number
}

/**
 * 分页查询字典类型列表
 */
export function pageDictType(params: DictTypeQueryParams): Promise<PageResult<DictType>> {
  return request.get('/system/dict/type/page', { params })
}

/**
 * 根据ID查询字典类型
 */
export function getDictTypeById(id: string): Promise<DictType> {
  return request.get(`/system/dict/type/${id}`)
}

/**
 * 新增字典类型
 */
export function saveDictType(data: Partial<DictType>): Promise<void> {
  return request.post('/system/dict/type', data)
}

/**
 * 修改字典类型
 */
export function updateDictType(data: DictType): Promise<void> {
  return request.put('/system/dict/type', data)
}

/**
 * 删除字典类型
 */
export function deleteDictType(id: string): Promise<void> {
  return request.delete(`/system/dict/type/${id}`)
}

/**
 * 根据字典类型查询字典数据
 */
export function getDictDataByType(dictType: string): Promise<DictData[]> {
  return request.get(`/system/dict/data/type/${dictType}`)
}

/**
 * 分页查询字典数据列表
 */
export function pageDictData(params: DictDataQueryParams): Promise<PageResult<DictData>> {
  return request.get('/system/dict/data/page', { params })
}

/**
 * 根据ID查询字典数据
 */
export function getDictDataById(id: string): Promise<DictData> {
  return request.get(`/system/dict/data/${id}`)
}

/**
 * 新增字典数据
 */
export function saveDictData(data: Partial<DictData>): Promise<void> {
  return request.post('/system/dict/data', data)
}

/**
 * 修改字典数据
 */
export function updateDictData(data: DictData): Promise<void> {
  return request.put('/system/dict/data', data)
}

/**
 * 删除字典数据
 */
export function deleteDictData(id: string): Promise<void> {
  return request.delete(`/system/dict/data/${id}`)
}

/**
 * 刷新字典缓存
 */
export function refreshDictCache(dictType: string): Promise<void> {
  return request.post(`/system/dict/refresh/${dictType}`)
}
