import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { DictData } from '@/types/system'
import { getDictDataByType } from '@/api/system/dict'

/**
 * 字典状态管理
 * 用于缓存字典数据，减少API请求
 */
export const useDictStore = defineStore('dict', () => {
  // 字典数据缓存 Map<dictType, DictData[]>
  const dictCache = ref<Map<string, DictData[]>>(new Map())

  // 加载状态 Map<dictType, boolean>
  const loadingMap = ref<Map<string, boolean>>(new Map())

  /**
   * 获取字典数据
   * @param dictType 字典类型
   * @param forceRefresh 是否强制刷新
   */
  const getDictData = async (dictType: string, forceRefresh = false): Promise<DictData[]> => {
    // 如果缓存中有数据且不强制刷新，直接返回
    if (!forceRefresh && dictCache.value.has(dictType)) {
      return dictCache.value.get(dictType) || []
    }

    // 如果正在加载，等待加载完成
    if (loadingMap.value.get(dictType)) {
      return new Promise((resolve) => {
        const timer = setInterval(() => {
          if (!loadingMap.value.get(dictType)) {
            clearInterval(timer)
            resolve(dictCache.value.get(dictType) || [])
          }
        }, 100)
      })
    }

    // 开始加载
    loadingMap.value.set(dictType, true)

    try {
      const data = await getDictDataByType(dictType)
      dictCache.value.set(dictType, data)
      return data
    } finally {
      loadingMap.value.set(dictType, false)
    }
  }

  /**
   * 根据字典值获取字典标签
   * @param dictType 字典类型
   * @param dictValue 字典值
   */
  const getDictLabel = async (dictType: string, dictValue: string): Promise<string> => {
    const data = await getDictData(dictType)
    const item = data.find((d) => d.dictValue === dictValue)
    return item?.dictLabel || dictValue
  }

  /**
   * 根据字典值获取字典项
   * @param dictType 字典类型
   * @param dictValue 字典值
   */
  const getDictItem = async (dictType: string, dictValue: string): Promise<DictData | undefined> => {
    const data = await getDictData(dictType)
    return data.find((d) => d.dictValue === dictValue)
  }

  /**
   * 刷新字典缓存
   * @param dictType 字典类型，不传则刷新全部
   */
  const refreshDict = async (dictType?: string) => {
    if (dictType) {
      dictCache.value.delete(dictType)
      await getDictData(dictType, true)
    } else {
      dictCache.value.clear()
    }
  }

  /**
   * 清空字典缓存
   */
  const clearDict = () => {
    dictCache.value.clear()
    loadingMap.value.clear()
  }

  return {
    dictCache,
    getDictData,
    getDictLabel,
    getDictItem,
    refreshDict,
    clearDict
  }
})
