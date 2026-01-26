<template>
  <div class="sales-funnel-chart">
    <div class="chart-header">
      <h3 class="chart-title">销售漏斗</h3>
      <div class="chart-summary">
        <span class="summary-item">
          <span class="summary-label">总商机</span>
          <span class="summary-value">{{ totalCount }}</span>
        </span>
        <span class="summary-divider">|</span>
        <span class="summary-item">
          <span class="summary-label">总金额</span>
          <span class="summary-value amount">¥{{ formatAmount(totalAmount) }}</span>
        </span>
      </div>
    </div>

    <!-- 阶段图例 (移至顶部) -->
    <div class="funnel-legend">
      <div
        v-for="(stage, index) in stages"
        :key="stage.stageCode"
        class="legend-item"
      >
        <span class="legend-dot" :style="{ background: getStageColor(index) }"></span>
        <span class="legend-info">
          <span class="legend-name">{{ stage.stageName }}</span>
          <span class="legend-probability">{{ stage.probability }}%</span>
        </span>
      </div>
    </div>

    <div class="funnel-container">
      <n-spin :show="loading">
        <div v-if="stages.length > 0" class="funnel-stages">
          <div
            v-for="(stage, index) in stages"
            :key="stage.stageCode"
            class="funnel-stage"
            :style="{ '--stage-width': getStageWidth(index) + '%' }"
          >
            <div class="stage-bar" :style="{ background: getStageColor(index) }">
              <div class="stage-content">
                <span class="stage-name">{{ stage.stageName }}</span>
                <span class="stage-count">{{ stage.count }} 个</span>
              </div>
            </div>
            <div class="stage-info">
              <div class="info-row">
                <span class="info-label">金额</span>
                <span class="info-value">¥{{ formatAmount(stage.amount) }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">占比</span>
                <span class="info-value">{{ (stage.countRatio || 0).toFixed(1) }}%</span>
              </div>
              <div v-if="index > 0" class="info-row conversion">
                <span class="info-label">转化率</span>
                <span class="info-value">{{ (stage.conversionRate || 0).toFixed(1) }}%</span>
              </div>
            </div>
            <!-- 连接箭头 -->
            <div v-if="index < stages.length - 1" class="stage-arrow">
              <n-icon size="16" color="#cbd5e1"><ChevronDownOutline /></n-icon>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <n-icon size="48" class="empty-icon"><FunnelOutline /></n-icon>
          <span class="empty-text">暂无漏斗数据</span>
        </div>
      </n-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { NIcon, NSpin } from 'naive-ui'
import { ChevronDownOutline, FunnelOutline } from '@vicons/ionicons5'
import type { FunnelStage } from '@/types/business/dashboard'

const props = withDefaults(defineProps<{
  stages: FunnelStage[]
  totalCount?: number
  totalAmount?: number
  loading?: boolean
}>(), {
  stages: () => [],
  totalCount: 0,
  totalAmount: 0,
  loading: false
})

// 阶段颜色
const stageColors = [
  '#3b82f6', // 蓝色
  '#8b5cf6', // 紫色
  '#f59e0b', // 橙色
  '#22c55e', // 绿色
  '#ef4444'  // 红色
]

// 获取阶段颜色
const getStageColor = (index: number) => {
  return stageColors[index % stageColors.length]
}

// 获取阶段宽度（漏斗效果）
const getStageWidth = (index: number) => {
  const baseWidth = 100
  const reduction = 15 // 每层减少的百分比
  return Math.max(40, baseWidth - index * reduction)
}

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (!amount) return '0'
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + '万'
  }
  return amount.toLocaleString()
}
</script>

<style scoped>
.sales-funnel-chart {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

/* 图表头部 */
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.chart-summary {
  display: flex;
  align-items: center;
  gap: 12px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.summary-label {
  font-size: 12px;
  color: #64748b;
}

.summary-value {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.summary-value.amount {
  color: #2563eb;
}

.summary-divider {
  color: #e2e8f0;
}

/* 图例 (移至顶部样式优化) */
.funnel-legend {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 24px;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.legend-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.legend-name {
  font-size: 12px;
  color: #64748b;
  line-height: 1.2;
}

.legend-probability {
  font-size: 10px;
  color: #94a3b8;
  line-height: 1.2;
}

/* 漏斗容器 */
.funnel-container {
  min-height: 300px;
  flex: 1;
}

.funnel-stages {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

/* 漏斗阶段 */
.funnel-stage {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.stage-bar {
  width: var(--stage-width);
  padding: 14px 24px; /* 稍微减小高度 */
  border-radius: 8px;
  color: white;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.stage-bar:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stage-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.stage-name {
  font-size: 14px;
  font-weight: 600;
}

.stage-count {
  font-size: 13px;
  opacity: 0.95;
  font-weight: 500;
}

/* 阶段信息 */
.stage-info {
  display: flex;
  gap: 20px;
  margin-top: 6px;
  padding: 0 16px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 4px;
}

.info-label {
  font-size: 11px;
  color: #94a3b8;
}

.info-value {
  font-size: 12px;
  font-weight: 500;
  color: #475569;
}

.info-row.conversion .info-value {
  color: #22c55e;
}

/* 连接箭头 */
.stage-arrow {
  margin: 2px 0;
  opacity: 0.5;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 60px 20px;
}

.empty-icon {
  color: #cbd5e1;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
}

/* 响应式 */
@media (max-width: 768px) {
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .stage-info {
    flex-wrap: wrap;
    gap: 12px;
  }

  .funnel-legend {
    flex-wrap: wrap;
    gap: 12px;
  }
}
</style>
