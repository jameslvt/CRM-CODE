<template>
  <div class="performance-trend-chart">
    <div class="chart-header">
      <div class="header-left">
        <h3 class="chart-title">业绩趋势</h3>
        <div class="period-selector">
          <button
            v-for="option in periodOptions"
            :key="option.value"
            class="period-btn"
            :class="{ active: periodType === option.value }"
            @click="handlePeriodChange(option.value)"
          >
            {{ option.label }}
          </button>
        </div>
      </div>
      <div class="header-right">
        <div class="metric-selector">
          <button
            v-for="metric in metricOptions"
            :key="metric.value"
            class="metric-btn"
            :class="{ active: activeMetric === metric.value }"
            @click="activeMetric = metric.value"
          >
            {{ metric.label }}
          </button>
        </div>
      </div>
    </div>

    <div class="chart-container">
      <n-spin :show="loading">
        <div v-if="trendPoints.length > 0" class="chart-content">
          <!-- 汇总数据 (移至上方) -->
          <div class="summary-row">
            <div class="summary-card">
              <span class="summary-label">总赢单金额</span>
              <span class="summary-value">¥{{ formatAmount(summary?.totalWonAmount) }}</span>
              <span v-if="summary?.wonAmountGrowthRate" class="summary-trend" :class="getTrendClass(summary.wonAmountGrowthRate)">
                <n-icon size="12">
                  <TrendingUpOutline v-if="summary.wonAmountGrowthRate > 0" />
                  <TrendingDownOutline v-else />
                </n-icon>
                {{ Math.abs(summary.wonAmountGrowthRate).toFixed(1) }}%
              </span>
            </div>
            <div class="summary-card">
              <span class="summary-label">总回款金额</span>
              <span class="summary-value">¥{{ formatAmount(summary?.totalPaymentAmount) }}</span>
              <span v-if="summary?.paymentAmountGrowthRate" class="summary-trend" :class="getTrendClass(summary.paymentAmountGrowthRate)">
                <n-icon size="12">
                  <TrendingUpOutline v-if="summary.paymentAmountGrowthRate > 0" />
                  <TrendingDownOutline v-else />
                </n-icon>
                {{ Math.abs(summary.paymentAmountGrowthRate).toFixed(1) }}%
              </span>
            </div>
            <div class="summary-card">
              <span class="summary-label">新增商机</span>
              <span class="summary-value">{{ summary?.totalNewOpportunityCount || 0 }}</span>
            </div>
            <div class="summary-card">
              <span class="summary-label">赢单数</span>
              <span class="summary-value">{{ summary?.totalWonCount || 0 }}</span>
            </div>
          </div>

          <!-- 简化的柱状图 (移至下方) -->
          <div class="bar-chart">
            <!-- 背景网格线 -->
            <div class="chart-bg-lines">
              <div class="bg-line"></div>
              <div class="bg-line"></div>
              <div class="bg-line"></div>
              <div class="bg-line"></div>
              <div class="bg-line"></div>
            </div>

            <div class="chart-grid">
              <div
                v-for="(point, index) in trendPoints"
                :key="point.period"
                class="bar-item"
              >
                <div class="bar-wrapper">
                  <div
                    class="bar"
                    :style="{
                      height: getBarHeight(point) + '%',
                      background: getBarColor()
                    }"
                  >
                    <div class="bar-tooltip">
                      <span class="tooltip-value">{{ formatValue(getMetricValue(point)) }}</span>
                    </div>
                  </div>
                </div>
                <span class="bar-label">{{ formatPeriod(point.period) }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <n-icon size="48" class="empty-icon"><BarChartOutline /></n-icon>
          <span class="empty-text">暂无趋势数据</span>
        </div>
      </n-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { NIcon, NSpin } from 'naive-ui'
import { TrendingUpOutline, TrendingDownOutline, BarChartOutline } from '@vicons/ionicons5'
import type { TrendPoint, TrendSummary } from '@/types/business/dashboard'

const props = withDefaults(defineProps<{
  trendPoints: TrendPoint[]
  summary?: TrendSummary
  periodType?: string
  loading?: boolean
}>(), {
  trendPoints: () => [],
  periodType: 'month',
  loading: false
})

const emit = defineEmits<{
  (e: 'period-change', value: string): void
}>()

// 当前选中的指标 (默认为商机数量，因为赢单金额可能为0)
const activeMetric = ref<'wonAmount' | 'paymentAmount' | 'opportunityCount'>('opportunityCount')

// 周期选项
const periodOptions = [
  { label: '按月', value: 'month' },
  { label: '按季度', value: 'quarter' }
]

// 指标选项
const metricOptions: Array<{ label: string; value: 'wonAmount' | 'paymentAmount' | 'opportunityCount' }> = [
  { label: '赢单金额', value: 'wonAmount' },
  { label: '回款金额', value: 'paymentAmount' },
  { label: '商机数量', value: 'opportunityCount' }
]

// 获取指标值
const getMetricValue = (point: TrendPoint) => {
  switch (activeMetric.value) {
    case 'wonAmount':
      return Number(point.wonAmount) || 0
    case 'paymentAmount':
      return Number(point.paymentAmount) || 0
    case 'opportunityCount':
      return Number(point.newOpportunityCount) || 0
    default:
      return 0
  }
}

// 获取最大值
const maxValue = computed(() => {
  if (props.trendPoints.length === 0) return 0
  return Math.max(...props.trendPoints.map(p => getMetricValue(p)))
})

// 获取柱状图高度
const getBarHeight = (point: TrendPoint) => {
  if (maxValue.value === 0) return 0
  return (getMetricValue(point) / maxValue.value) * 100
}

// 获取柱状图颜色
const getBarColor = () => {
  switch (activeMetric.value) {
    case 'wonAmount':
      return 'linear-gradient(180deg, #22c55e 0%, #16a34a 100%)'
    case 'paymentAmount':
      return 'linear-gradient(180deg, #3b82f6 0%, #2563eb 100%)'
    case 'opportunityCount':
      return 'linear-gradient(180deg, #f59e0b 0%, #d97706 100%)'
    default:
      return '#3b82f6'
  }
}

// 格式化周期标签
const formatPeriod = (period: string) => {
  if (!period) return ''
  // 假设格式为 2024-01
  const parts = period.split('-')
  if (parts.length === 2) {
    return `${parts[1]}月`
  }
  return period
}

// 格式化数值
const formatValue = (value: number) => {
  if (activeMetric.value === 'opportunityCount') {
    return value.toString()
  }
  return '¥' + formatAmount(value)
}

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (!amount) return '0'
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + '万'
  }
  return amount.toLocaleString()
}

// 获取趋势样式类
const getTrendClass = (rate: number) => {
  return rate > 0 ? 'trend-up' : 'trend-down'
}

// 周期切换
const handlePeriodChange = (value: string) => {
  emit('period-change', value)
}
</script>

<style scoped>
.performance-trend-chart {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 24px;
  height: 100%; /* 撑满父容器 */
  display: flex;
  flex-direction: column;
}

/* 图表头部 */
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

/* 周期选择器 */
.period-selector {
  display: flex;
  gap: 4px;
  padding: 4px;
  background: #f1f5f9;
  border-radius: 8px;
}

.period-btn {
  padding: 6px 12px;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-size: 12px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.period-btn:hover {
  color: #0f172a;
}

.period-btn.active {
  background: white;
  color: #0f172a;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 指标选择器 */
.metric-selector {
  display: flex;
  gap: 8px;
}

.metric-btn {
  padding: 6px 14px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 6px;
  font-size: 12px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.metric-btn:hover {
  border-color: #cbd5e1;
}

.metric-btn.active {
  background: #2563eb;
  border-color: #2563eb;
  color: white;
}

/* 图表容器 */
.chart-container {
  min-height: 300px;
  flex: 1; /* 占据剩余空间 */
  display: flex;
  flex-direction: column;
}

:deep(.n-spin-container),
:deep(.n-spin-content) {
  height: 100% !important;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chart-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
  height: 100%; /* 继承高度 */
  justify-content: space-between; /* 上下分布 */
}

/* 柱状图 */
.bar-chart {
  /* 移除固定高度，改为 flex: 1 */
  flex: 1;
  min-height: 200px; /* 保持最小高度 */
  padding: 0 10px;
  position: relative; /* 为绝对定位的网格线提供参考 */
  display: flex;
  flex-direction: column;
}

.chart-grid {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex: 1; /* 使用 flex: 1 替代 height: 100% 以更稳定地填充 */
  height: 100%; /* 双重保障 */
  gap: 8px;
  position: relative;
  z-index: 2; /* 提高层级，确保在网格线上方 */
  padding-bottom: 4px; /* 微调底部留白 */
}

.bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  max-width: 60px;
  height: 100%; /* 撑满 */
  justify-content: flex-end;
  position: relative; /* 确保 z-index 生效 */
}

.bar-wrapper {
  width: 100%;
  flex: 1;
  height: 100%; /* 显式高度 */
  min-height: 0; /* 防止 flex item 溢出 */
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

/* ... existing bar, hover styles ... */

.bar:hover .bar-tooltip {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(-4px);
}

.bar-tooltip {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  padding: 6px 10px;
  background: #0f172a;
  color: white;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s ease;
  margin-bottom: 4px;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 背景网格线 */
.chart-bg-lines {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 24px; /* 留出标签高度 */
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  pointer-events: none;
  z-index: 0;
}

.bg-line {
  width: 100%;
  height: 1px;
  background: transparent;
  border-top: 1px dashed #e2e8f0;
}

.bg-line:last-child {
  border-top: 1px solid #e2e8f0; /* 底线实线 */
}

.bar-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid transparent;
  border-top-color: #0f172a;
}

.bar-label {
  font-size: 11px;
  color: #94a3b8;
}

/* 汇总数据 */
.summary-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding-bottom: 24px; /* 改为底部间距 */
  /* 移除 border-top */
}

.summary-card {
  display: flex;
  flex-direction: column;
  justify-content: center; /* 垂直居中 */
  gap: 8px;
  padding: 20px; /* 增加内边距 */
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.2s ease;
  height: 100%; /* 确保高度撑满 */
  border: 1px solid transparent;
}

.summary-card:hover {
  background: #f1f5f9;
  border-color: #e2e8f0;
}

.summary-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.summary-value {
  font-size: 24px; /* 增大字体 */
  font-weight: 700;
  color: #0f172a;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.summary-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  font-weight: 500;
}

.summary-trend.trend-up {
  color: #22c55e;
}

.summary-trend.trend-down {
  color: #ef4444;
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
@media (max-width: 1024px) {
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .summary-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .header-left {
    flex-direction: column;
    align-items: flex-start;
  }

  .metric-selector {
    flex-wrap: wrap;
  }

  .summary-row {
    grid-template-columns: 1fr;
  }
}
</style>
