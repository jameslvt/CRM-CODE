import request from '../request'

/**
 * 商机阶段配置接口
 */
export interface OpportunityStage {
  id?: number
  stageName: string
  stageCode: string
  winRate: number
  sort: number
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/**
 * 查询所有商机阶段
 */
export function listOpportunityStages(): Promise<OpportunityStage[]> {
  return request.get('/system/opportunity-stage/list')
}

/**
 * 根据ID查询商机阶段
 */
export function getOpportunityStageById(id: string): Promise<OpportunityStage> {
  return request.get(`/system/opportunity-stage/${id}`)
}

/**
 * 新增商机阶段
 */
export function saveOpportunityStage(data: Partial<OpportunityStage>): Promise<void> {
  return request.post('/system/opportunity-stage', data)
}

/**
 * 修改商机阶段
 */
export function updateOpportunityStage(data: OpportunityStage): Promise<void> {
  return request.put('/system/opportunity-stage', data)
}

/**
 * 删除商机阶段
 */
export function deleteOpportunityStage(id: string): Promise<void> {
  return request.delete(`/system/opportunity-stage/${id}`)
}

/**
 * 调整阶段排序
 */
export function updateOpportunityStageSort(id: number, sort: number): Promise<void> {
  return request.put(`/system/opportunity-stage/${id}/sort`, null, { params: { sort } })
}
