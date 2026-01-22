<template>
  <div class="opportunity-detail">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <n-button text @click="handleBack" class="back-btn">
          <template #icon>
            <n-icon><ArrowBackOutline /></n-icon>
          </template>
          返回列表
        </n-button>
        <div class="header-content">
          <h1 class="page-title">{{ opportunity?.name || '商机详情' }}</h1>
          <div class="header-meta">
            <span class="stage-badge" :style="{ background: stageColor.bg, color: stageColor.color }">
              {{ stageName }}
            </span>
            <span class="meta-item">
              <n-icon><PersonOutline /></n-icon>
              {{ opportunity?.ownerName || '-' }}
            </span>
            <span class="meta-item">
              <n-icon><TimeOutline /></n-icon>
              {{ opportunity?.createTime || '-' }}
            </span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <n-button @click="handleEdit" class="secondary-btn">
          <template #icon>
            <n-icon><CreateOutline /></n-icon>
          </template>
          编辑
        </n-button>
        <n-button
          v-if="opportunity?.stage !== 'WON' && opportunity?.stage !== 'LOST'"
          type="primary"
          @click="showStageModal = true"
          class="primary-btn"
        >
          <template #icon>
            <n-icon><ArrowForwardOutline /></n-icon>
          </template>
          推进阶段
        </n-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <n-spin size="large" />
    </div>

    <!-- 详情内容 -->
    <div v-else-if="opportunity" class="detail-content">
      <!-- 核心指标卡片 -->
      <div class="metrics-row">
        <div class="metric-card amount">
          <div class="metric-icon">
            <n-icon size="24"><CashOutline /></n-icon>
          </div>
          <div class="metric-info">
            <span class="metric-value">¥{{ (opportunity.amount || 0).toLocaleString() }}</span>
            <span class="metric-label">预计金额</span>
          </div>
        </div>
        <div class="metric-card probability">
          <div class="metric-icon">
            <n-icon size="24"><TrendingUpOutline /></n-icon>
          </div>
          <div class="metric-info">
            <span class="metric-value">{{ opportunity.probability || 0 }}%</span>
            <span class="metric-label">赢单概率</span>
          </div>
        </div>
        <div class="metric-card date">
          <div class="metric-icon">
            <n-icon size="24"><CalendarOutline /></n-icon>
          </div>
          <div class="metric-info">
            <span class="metric-value">{{ opportunity.expectedDate || '-' }}</span>
            <span class="metric-label">预计成交日期</span>
          </div>
        </div>
        <div class="metric-card customer">
          <div class="metric-icon">
            <n-icon size="24"><BusinessOutline /></n-icon>
          </div>
          <div class="metric-info">
            <span class="metric-value clickable" @click="handleViewCustomer">
              {{ opportunity.customerName || '-' }}
            </span>
            <span class="metric-label">关联客户</span>
          </div>
        </div>
      </div>

      <!-- 阶段进度 -->
      <div class="stage-progress-card">
        <div class="card-header">
          <h3 class="card-title">商机阶段</h3>
        </div>
        <div class="stage-timeline">
          <div
            v-for="(stage, index) in stages"
            :key="stage.code"
            class="stage-item"
            :class="{
              active: stage.code === opportunity.stage,
              completed: isStageCompleted(stage.code),
              won: stage.code === 'WON' && opportunity.stage === 'WON',
              lost: stage.code === 'LOST' && opportunity.stage === 'LOST'
            }"
          >
            <div class="stage-dot">
              <n-icon v-if="isStageCompleted(stage.code)" size="14">
                <CheckmarkOutline />
              </n-icon>
              <n-icon v-else-if="stage.code === 'WON'" size="14">
                <TrophyOutline />
              </n-icon>
              <n-icon v-else-if="stage.code === 'LOST'" size="14">
                <CloseOutline />
              </n-icon>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <div class="stage-content">
              <span class="stage-name">{{ stage.name }}</span>
              <span class="stage-probability">{{ stage.probability }}%</span>
            </div>
            <div v-if="index < stages.length - 1" class="stage-line" :class="{ completed: isStageCompleted(stages[index + 1].code) }"></div>
          </div>
        </div>
      </div>

      <!-- 详情信息 -->
      <div class="info-grid">
        <!-- 基本信息 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">基本信息</h3>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">商机名称</span>
              <span class="info-value">{{ opportunity.name }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">商机来源</span>
              <span class="info-value">{{ opportunity.sourceName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">联系人</span>
              <span class="info-value">{{ opportunity.contactName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">负责人</span>
              <span class="info-value">{{ opportunity.ownerName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ opportunity.createTime || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">更新时间</span>
              <span class="info-value">{{ opportunity.updateTime || '-' }}</span>
            </div>
            <div v-if="opportunity.closeReason" class="info-item full">
              <span class="info-label">关闭原因</span>
              <span class="info-value">{{ opportunity.closeReason }}</span>
            </div>
            <div v-if="opportunity.remark" class="info-item full">
              <span class="info-label">备注</span>
              <span class="info-value">{{ opportunity.remark }}</span>
            </div>
          </div>
        </div>

        <!-- 产品信息 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">产品信息</h3>
            <span class="product-count">{{ (opportunity.products || []).length }} 个产品</span>
          </div>
          <div v-if="opportunity.products && opportunity.products.length > 0" class="product-list">
            <div
              v-for="product in opportunity.products"
              :key="product.id"
              class="product-item"
            >
              <div class="product-info">
                <span class="product-name">{{ product.productName }}</span>
                <span class="product-code">{{ product.productCode }}</span>
              </div>
              <div class="product-detail">
                <span class="detail-item">
                  <span class="detail-label">数量</span>
                  <span class="detail-value">{{ product.quantity }} {{ product.unit || '个' }}</span>
                </span>
                <span class="detail-item">
                  <span class="detail-label">单价</span>
                  <span class="detail-value">¥{{ (product.price || 0).toLocaleString() }}</span>
                </span>
                <span class="detail-item">
                  <span class="detail-label">折扣</span>
                  <span class="detail-value">{{ product.discount || 0 }}%</span>
                </span>
                <span class="detail-item amount">
                  <span class="detail-label">金额</span>
                  <span class="detail-value">¥{{ (product.amount || 0).toLocaleString() }}</span>
                </span>
              </div>
            </div>
            <div class="product-total">
              <span class="total-label">产品总金额</span>
              <span class="total-value">¥{{ productTotalAmount.toLocaleString() }}</span>
            </div>
          </div>
          <div v-else class="empty-products">
            <span class="empty-text">暂无产品信息</span>
          </div>
        </div>
      </div>

      <!-- 跟进记录 -->
      <div class="activity-card">
        <activity-timeline
          v-if="opportunityId"
          ref="activityTimelineRef"
          :target-type="TargetType.OPPORTUNITY"
          :target-id="opportunityId"
          @add="handleAddActivity"
          @edit="handleEditActivity"
          @refresh="handleActivityRefresh"
        />
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <n-modal
      v-model:show="showEditModal"
      title="编辑商机"
      preset="card"
      class="form-modal"
    >
      <opportunity-form
        v-if="opportunity"
        :form-data="opportunity"
        @submit="handleEditSubmit"
        @cancel="showEditModal = false"
      />
    </n-modal>

    <!-- 阶段推进弹窗 -->
    <n-modal
      v-model:show="showStageModal"
      title="推进商机阶段"
      preset="card"
      class="stage-modal"
    >
      <stage-progress
        v-if="opportunity"
        :opportunity="opportunity"
        @advance="handleStageAdvance"
        @cancel="showStageModal = false"
      />
    </n-modal>

    <!-- 跟进记录弹窗 -->
    <n-modal
      v-model:show="showActivityModal"
      :title="editingActivity ? '编辑跟进记录' : '添加跟进记录'"
      preset="card"
      class="activity-modal"
    >
      <activity-form
        v-if="opportunityId"
        :target-type="TargetType.OPPORTUNITY"
        :target-id="opportunityId"
        :edit-data="editingActivity"
        @submit="handleActivitySubmit"
        @cancel="showActivityModal = false"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NButton,
  NIcon,
  NSpin,
  useMessage
} from 'naive-ui'
import {
  ArrowBackOutline,
  CreateOutline,
  ArrowForwardOutline,
  CashOutline,
  TrendingUpOutline,
  CalendarOutline,
  BusinessOutline,
  PersonOutline,
  TimeOutline,
  CheckmarkOutline,
  TrophyOutline,
  CloseOutline
} from '@vicons/ionicons5'
import { getOpportunityById, advanceStage } from '@/api/business/opportunity'
import type { Opportunity, StageAdvanceDTO } from '@/types/business/opportunity'
import type { Activity } from '@/types/business/activity'
import { TargetType } from '@/types/business/activity'
import OpportunityForm from '@/components/business/OpportunityForm.vue'
import StageProgress from '@/components/business/StageProgress.vue'
import ActivityTimeline from '@/components/business/ActivityTimeline.vue'
import ActivityForm from '@/components/business/ActivityForm.vue'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const loading = ref(true)
const opportunity = ref<Opportunity | null>(null)
const showEditModal = ref(false)
const showStageModal = ref(false)
const showActivityModal = ref(false)
const editingActivity = ref<Activity | null>(null)
const activityTimelineRef = ref()

// 商机ID
const opportunityId = computed(() => Number(route.params.id))

// 阶段配置
const stages = [
  { code: 'REQUIREMENT', name: '需求确认', probability: 20 },
  { code: 'PROPOSAL', name: '方案报价', probability: 40 },
  { code: 'NEGOTIATION', name: '商务谈判', probability: 60 },
  { code: 'WON', name: '赢单', probability: 100 },
  { code: 'LOST', name: '输单', probability: 0 }
]

// 阶段颜色映射
const stageColorMap: Record<string, { bg: string; color: string }> = {
  REQUIREMENT: { bg: '#f1f5f9', color: '#64748b' },
  PROPOSAL: { bg: '#dbeafe', color: '#2563eb' },
  NEGOTIATION: { bg: '#fef3c7', color: '#f59e0b' },
  WON: { bg: '#dcfce7', color: '#22c55e' },
  LOST: { bg: '#fee2e2', color: '#ef4444' }
}

// 当前阶段名称
const stageName = computed(() => {
  if (!opportunity.value) return '-'
  const stage = stages.find(s => s.code === opportunity.value?.stage)
  return stage?.name || '-'
})

// 当前阶段颜色
const stageColor = computed(() => {
  if (!opportunity.value) return { bg: '#f1f5f9', color: '#64748b' }
  return stageColorMap[opportunity.value.stage] || stageColorMap.REQUIREMENT
})

// 产品总金额
const productTotalAmount = computed(() => {
  if (!opportunity.value?.products) return 0
  return opportunity.value.products.reduce((sum, p) => sum + (p.amount || 0), 0)
})

// 获取阶段索引
const getStageIndex = (stageCode: string) => {
  return stages.findIndex(s => s.code === stageCode)
}

// 判断阶段是否已完成
const isStageCompleted = (stageCode: string) => {
  if (!opportunity.value) return false
  const currentIndex = getStageIndex(opportunity.value.stage)
  const targetIndex = getStageIndex(stageCode)
  return targetIndex < currentIndex
}

// 加载商机详情
const loadOpportunity = async () => {
  const id = Number(route.params.id)
  if (!id) {
    message.error('商机ID无效')
    router.push('/business/opportunity')
    return
  }

  loading.value = true
  try {
    opportunity.value = await getOpportunityById(id)
  } catch (error) {
    message.error('加载商机详情失败')
    router.push('/business/opportunity')
  } finally {
    loading.value = false
  }
}

// 返回列表
const handleBack = () => {
  router.push('/business/opportunity')
}

// 编辑
const handleEdit = () => {
  showEditModal.value = true
}

// 编辑提交
const handleEditSubmit = () => {
  showEditModal.value = false
  loadOpportunity()
}

// 阶段推进
const handleStageAdvance = async (data: StageAdvanceDTO) => {
  try {
    await advanceStage(data)
    message.success('阶段推进成功')
    showStageModal.value = false
    loadOpportunity()
  } catch (error) {
    message.error('阶段推进失败')
  }
}

// 查看客户
const handleViewCustomer = () => {
  if (opportunity.value?.customerId) {
    router.push(`/business/customer/${opportunity.value.customerId}`)
  }
}

// 添加跟进记录
const handleAddActivity = () => {
  editingActivity.value = null
  showActivityModal.value = true
}

// 编辑跟进记录
const handleEditActivity = (activity: Activity) => {
  editingActivity.value = activity
  showActivityModal.value = true
}

// 跟进记录提交成功
const handleActivitySubmit = () => {
  showActivityModal.value = false
  editingActivity.value = null
  activityTimelineRef.value?.refresh()
}

// 跟进记录刷新
const handleActivityRefresh = () => {
  // 可以在这里刷新商机数据
}

onMounted(() => {
  loadOpportunity()
})
</script>

<style scoped>
.opportunity-detail {
  width: 100%;
  min-height: 100%;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.back-btn {
  color: #64748b;
  font-size: 13px;
  padding: 0;
}

.back-btn:hover {
  color: #2563eb;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
  letter-spacing: -0.02em;
}

.header-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stage-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.secondary-btn {
  height: 40px;
  padding: 0 16px;
  border-radius: 10px;
  font-weight: 500;
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
}

.primary-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  background: #2563eb;
  border: none;
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 核心指标卡片 */
.metrics-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-card.amount .metric-icon {
  background: #dcfce7;
  color: #22c55e;
}

.metric-card.probability .metric-icon {
  background: #dbeafe;
  color: #2563eb;
}

.metric-card.date .metric-icon {
  background: #fef3c7;
  color: #f59e0b;
}

.metric-card.customer .metric-icon {
  background: #f1f5f9;
  color: #64748b;
}

.metric-info {
  display: flex;
  flex-direction: column;
}

.metric-value {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.metric-value.clickable {
  cursor: pointer;
  color: #2563eb;
}

.metric-value.clickable:hover {
  text-decoration: underline;
}

.metric-label {
  font-size: 12px;
  color: #64748b;
}

/* 阶段进度卡片 */
.stage-progress-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 24px;
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.product-count {
  font-size: 13px;
  color: #64748b;
}

/* 阶段时间线 */
.stage-timeline {
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
}

.stage-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
}

.stage-dot {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e2e8f0;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  z-index: 1;
  transition: all 0.3s ease;
}

.stage-item.active .stage-dot {
  background: #2563eb;
  color: white;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.2);
}

.stage-item.completed .stage-dot {
  background: #22c55e;
  color: white;
}

.stage-item.won .stage-dot {
  background: #22c55e;
  color: white;
  box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.2);
}

.stage-item.lost .stage-dot {
  background: #ef4444;
  color: white;
  box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.2);
}

.stage-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 10px;
}

.stage-name {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.stage-item.active .stage-name {
  color: #2563eb;
  font-weight: 600;
}

.stage-item.completed .stage-name {
  color: #22c55e;
}

.stage-probability {
  font-size: 11px;
  color: #94a3b8;
}

.stage-line {
  position: absolute;
  top: 18px;
  left: calc(50% + 18px);
  width: calc(100% - 36px);
  height: 2px;
  background: #e2e8f0;
}

.stage-line.completed {
  background: #22c55e;
}

.stage-item:last-child .stage-line {
  display: none;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.info-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 24px;
}

/* 信息列表 */
.info-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full {
  grid-column: span 2;
}

.info-label {
  font-size: 12px;
  color: #64748b;
}

.info-value {
  font-size: 14px;
  color: #0f172a;
  font-weight: 500;
}

/* 产品列表 */
.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.product-code {
  font-size: 12px;
  color: #94a3b8;
}

.product-detail {
  display: flex;
  gap: 24px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.detail-label {
  font-size: 11px;
  color: #64748b;
}

.detail-value {
  font-size: 13px;
  color: #0f172a;
}

.detail-item.amount .detail-value {
  font-weight: 600;
  color: #2563eb;
}

.product-total {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #dbeafe;
  border-radius: 8px;
}

.total-label {
  font-size: 14px;
  color: #475569;
}

.total-value {
  font-size: 18px;
  font-weight: 700;
  color: #2563eb;
}

.empty-products {
  padding: 40px 20px;
  text-align: center;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
}

/* 弹窗样式 */
.form-modal {
  width: 800px;
}

.stage-modal {
  width: 560px;
}

.activity-modal {
  width: 600px;
}

/* 跟进记录卡片 */
.activity-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 24px;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 1280px) {
  .metrics-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
  }

  .metrics-row {
    grid-template-columns: 1fr;
  }

  .stage-timeline {
    flex-direction: column;
    gap: 16px;
    padding: 0;
  }

  .stage-item {
    flex-direction: row;
    gap: 16px;
  }

  .stage-line {
    display: none;
  }

  .info-list {
    grid-template-columns: 1fr;
  }

  .info-item.full {
    grid-column: span 1;
  }
}
</style>
