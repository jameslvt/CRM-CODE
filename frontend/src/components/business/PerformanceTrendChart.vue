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
          <!-- 简化的柱状图 -->
          <div class="bar-chart">
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

          <!-- 汇总数据 -->
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

// 当前选中的指标
const activeMetric = ref<'wonAmount' | 'paymentAmount' | 'opportunityCount'>('wonAmount')

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
      return point.wonAmount || 0
    case 'paymentAmount':
      return point.paymentAmount || 0
    case 'opportunityCount':
      return point.newOpportunityCount || 0
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
}

.chart-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 柱状图 */
.bar-chart {
  height: 200px;
  padding: 0 10px;
}

.chart-grid {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 100%;
  gap: 8px;
}

.bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  max-width: 60px;
}

.bar-wrapper {
  width: 100%;
  height: 160px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.bar {
  width: 100%;
  max-width: 40px;
  border-radius: 6px 6px 0 0;
  position: relative;
  transition: all 0.3s ease;
  min-height: 4px;
}

.bar:hover {
  transform: scaleY(1.02);
}

.bar:hover .bar-tooltip {
  opacity: 1;
  visibility: visible;
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
  margin-bottom: 8px;
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
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.summary-card {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 10px;
}

.summary-label {
  font-size: 12px;
  color: #64748b;
}

.summary-value {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
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
