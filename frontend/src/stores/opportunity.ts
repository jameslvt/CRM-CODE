import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Opportunity, OpportunityQueryParams, OpportunityStatistics } from '@/types/business/opportunity'
import { pageOpportunities, getOpportunityStatistics } from '@/api/business/opportunity'

export const useOpportunityStore = defineStore('opportunity', () => {
  // 商机列表
  const opportunities = ref<Opportunity[]>([])

  // 当前商机
  const currentOpportunity = ref<Opportunity | null>(null)

  // 统计数据
  const statistics = ref<OpportunityStatistics | null>(null)

  // 加载状态
  const loading = ref(false)

  // 分页信息
  const pagination = ref({
    page: 1,
    pageSize: 10,
    total: 0
  })

  // 商机阶段配置
  const stages = ref([
    { code: 'REQUIREMENT', name: '需求确认', probability: 20, color: '#94a3b8' },
    { code: 'PROPOSAL', name: '方案报价', probability: 40, color: '#3b82f6' },
    { code: 'NEGOTIATION', name: '商务谈判', probability: 60, color: '#f59e0b' },
    { code: 'WON', name: '赢单', probability: 100, color: '#22c55e' },
    { code: 'LOST', name: '输单', probability: 0, color: '#ef4444' }
  ])

  /**
   * 加载商机列表
   */
  async function loadOpportunities(params: OpportunityQueryParams) {
    loading.value = true
    try {
      const result = await pageOpportunities(params)
      opportunities.value = result.records
      pagination.value = {
        page: result.current,
        pageSize: result.size,
        total: result.total
      }
      return result
    } finally {
      loading.value = false
    }
  }

  /**
   * 加载统计数据
   */
  async function loadStatistics(ownerId?: number) {
    try {
      statistics.value = await getOpportunityStatistics(ownerId)
      return statistics.value
    } catch (error) {
      console.error('加载商机统计失败', error)
      return null
    }
  }

  /**
   * 设置当前商机
   */
  function setCurrentOpportunity(opportunity: Opportunity | null) {
    currentOpportunity.value = opportunity
  }

  /**
   * 获取阶段配置
   */
  function getStageConfig(stageCode: string) {
    return stages.value.find(s => s.code === stageCode)
  }

  /**
   * 获取阶段颜色
   */
  function getStageColor(stageCode: string) {
    const stage = getStageConfig(stageCode)
    return stage?.color || '#94a3b8'
  }

  /**
   * 获取阶段名称
   */
  function getStageName(stageCode: string) {
    const stage = getStageConfig(stageCode)
    return stage?.name || stageCode
  }

  /**
   * 清空状态
   */
  function clearState() {
    opportunities.value = []
    currentOpportunity.value = null
    statistics.value = null
    pagination.value = {
      page: 1,
      pageSize: 10,
      total: 0
    }
  }

  return {
    opportunities,
    currentOpportunity,
    statistics,
    loading,
    pagination,
    stages,
    loadOpportunities,
    loadStatistics,
    setCurrentOpportunity,
    getStageConfig,
    getStageColor,
    getStageName,
    clearState
  }
})
