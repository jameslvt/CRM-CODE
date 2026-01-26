<template>
  <div class="signing-plan-tracker">
    <!-- 卡片头部 -->
    <div class="tracker-header">
      <div class="header-left">
        <div class="header-icon">
          <n-icon :size="20"><CalendarOutline /></n-icon>
        </div>
        <div class="header-text">
          <h3 class="header-title">签单计划追踪</h3>
          <span class="header-subtitle">{{ selectedMonth }}月 · 准毛利</span>
        </div>
      </div>
    </div>

    <!-- 核心指标 -->
    <div class="metrics-section">
      <div class="metric-item">
        <div class="metric-header">
          <div class="metric-icon plan">
            <n-icon :size="16"><FlagOutline /></n-icon>
          </div>
          <span class="metric-label">预计划</span>
        </div>
        <div class="metric-value-wrapper">
          <span class="currency">¥</span>
          <span class="value">{{ planData.plan.toLocaleString() }}</span>
          <span class="unit">万</span>
        </div>
      </div>
      
      <div class="metric-separator"></div>
      
      <div class="metric-item">
        <div class="metric-header">
          <div class="metric-icon actual">
            <n-icon :size="16"><CheckmarkCircleOutline /></n-icon>
          </div>
          <span class="metric-label">月实际</span>
        </div>
        <div class="metric-value-wrapper highlight">
          <span class="currency">¥</span>
          <span class="value">{{ planData.actual.toLocaleString() }}</span>
          <span class="unit">万</span>
        </div>
      </div>
      
      <div class="metric-separator"></div>

      <div class="metric-item">
        <div class="metric-header">
          <div class="metric-icon forecast">
            <n-icon :size="16"><SpeedometerOutline /></n-icon>
          </div>
          <span class="metric-label">预测完成</span>
        </div>
        <div class="metric-value-wrapper success">
          <span class="currency">¥</span>
          <span class="value">{{ planData.forecast.toLocaleString() }}</span>
          <span class="unit">万</span>
        </div>
      </div>
    </div>

    <!-- 双层进度条 -->
    <div class="progress-section">
      <div class="progress-labels">
        <span class="progress-label">完成进度</span>
        <span class="progress-percentage" :class="getCompletionClass(completionRate)">
          {{ completionRate }}%
        </span>
      </div>
      
      <div class="dual-progress">
        <!-- 预测进度（底层） -->
        <div class="progress-track">
          <div 
            class="progress-fill forecast-fill"
            :style="{ width: `${Math.min(forecastRate, 100)}%` }"
          ></div>
          <!-- 实际进度（顶层） -->
          <div 
            class="progress-fill actual-fill"
            :style="{ width: `${Math.min(completionRate, 100)}%` }"
          ></div>
        </div>
        
        <div class="progress-legend">
          <span class="legend-item">
            <span class="legend-dot actual"></span>
            实际 {{ completionRate }}%
          </span>
          <span class="legend-item">
            <span class="legend-dot forecast"></span>
            预测 {{ forecastRate }}%
          </span>
        </div>
      </div>
    </div>

    <!-- 商机状态分布 -->
    <div class="opportunity-section">
      <div class="section-title">
        <n-icon :size="14"><PieChartOutline /></n-icon>
        商机状态分布
      </div>
      
      <div class="opportunity-stats">
        <div class="opportunity-bar">
          <div 
            class="bar-segment confirmed"
            :style="{ width: `${confirmedRatio}%` }"
          >
            <span v-if="confirmedRatio > 15">确定</span>
          </div>
          <div 
            class="bar-segment uncertain"
            :style="{ width: `${uncertainRatio}%` }"
          >
            <span v-if="uncertainRatio > 15">不确定</span>
          </div>
        </div>
        
        <div class="opportunity-details">
          <div class="detail-item">
            <span class="detail-dot confirmed"></span>
            <span class="detail-label">确定(b)</span>
            <span class="detail-value">{{ formatNumber(planData.confirmed) }}万</span>
            <span class="detail-ratio">{{ confirmedRatio }}%</span>
          </div>
          <div class="detail-item">
            <span class="detail-dot uncertain"></span>
            <span class="detail-label">不确定(c)</span>
            <span class="detail-value">{{ formatNumber(planData.uncertain) }}万</span>
            <span class="detail-ratio">{{ uncertainRatio }}%</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 预测公式说明 -->
    <div class="formula-section">
      <n-icon :size="14"><InformationCircleOutline /></n-icon>
      <span>预测完成 = 月实际(a) + 确定(b) + 不确定(c) × 0.5</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { NIcon } from 'naive-ui'
import { 
  CalendarOutline, 
  FlagOutline, 
  CheckmarkCircleOutline, 
  SpeedometerOutline,
  ArrowForwardOutline,
  PieChartOutline,
  InformationCircleOutline
} from '@vicons/ionicons5'

const selectedMonth = ref('01')

// 模拟数据 - 基于用户提供的报表（母公司汇总）
const planData = ref({
  plan: 3596,      // 预计划
  actual: 851,     // 月实际 (a)
  confirmed: 859,  // 确定 (b)
  uncertain: 2186, // 不确定 (c)
  forecast: 2803   // 预测完成 = a + b + c*0.5
})

// 完成率计算
const completionRate = computed(() => {
  if (planData.value.plan === 0) return 0
  return Math.round((planData.value.actual / planData.value.plan) * 100)
})

// 预测完成率
const forecastRate = computed(() => {
  if (planData.value.plan === 0) return 0
  return Math.round((planData.value.forecast / planData.value.plan) * 100)
})

// 确定比例
const confirmedRatio = computed(() => {
  const total = planData.value.confirmed + planData.value.uncertain
  if (total === 0) return 0
  return Math.round((planData.value.confirmed / total) * 100)
})

// 不确定比例
const uncertainRatio = computed(() => {
  return 100 - confirmedRatio.value
})

// 格式化数字
const formatNumber = (num: number) => {
  return num.toLocaleString()
}

// 完成率样式
const getCompletionClass = (rate: number) => {
  if (rate >= 100) return 'excellent'
  if (rate >= 50) return 'good'
  if (rate >= 25) return 'normal'
  return 'warning'
}
</script>

<style scoped>
.signing-plan-tracker {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

/* 卡片头部 */
.tracker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2d4a6f 100%);
  color: white;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #ffffff; /* 确保标题为纯白 */
}

.header-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8); /* 副标题使用高透明度白色 */
}

/* 核心指标 */
.metrics-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  background: white;
}

.metric-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.metric-header {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.metric-icon {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-icon.plan { background: #e0f2fe; color: #0284c7; }
.metric-icon.actual { background: #dbeafe; color: #2563eb; }
.metric-icon.forecast { background: #dcfce7; color: #16a34a; }

.metric-label {
  font-size: 13px;
  color: #64748b;
}

.metric-value-wrapper {
  display: flex;
  align-items: baseline;
  gap: 1px;
  color: #1e293b;
}

.metric-value-wrapper.highlight .value { color: #2563eb; }
.metric-value-wrapper.success .value { color: #16a34a; }

.metric-value-wrapper .currency {
  font-size: 14px;
  color: #94a3b8;
  margin-right: 2px;
}

.metric-value-wrapper .value {
  font-size: 20px;
  font-weight: 700;
  font-family: 'DIN Alternate', 'Monaco', monospace; /* 使用等宽/数字优化字体 */
  letter-spacing: -0.5px;
}

.metric-value-wrapper .unit {
  font-size: 12px;
  color: #64748b;
  margin-left: 2px;
}

.metric-separator {
  width: 1px;
  height: 32px;
  background: #f1f5f9;
}

/* 双层进度条 */
.progress-section {
  padding: 0 24px 20px;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.progress-label {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}

.progress-percentage {
  font-size: 18px;
  font-weight: 700;
}

.progress-percentage.excellent { color: #22c55e; }
.progress-percentage.good { color: #3b82f6; }
.progress-percentage.normal { color: #f59e0b; }
.progress-percentage.warning { color: #ef4444; }

.dual-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-track {
  position: relative;
  height: 12px;
  background: #e2e8f0;
  border-radius: 6px;
  overflow: hidden;
}

.progress-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  border-radius: 6px;
  transition: width 0.8s ease;
}

.progress-fill.forecast-fill {
  background: linear-gradient(90deg, #86efac 0%, #4ade80 100%);
  opacity: 0.5;
}

.progress-fill.actual-fill {
  background: linear-gradient(90deg, #2563eb 0%, #3b82f6 100%);
}

.progress-legend {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #64748b;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.legend-dot.actual {
  background: #2563eb;
}

.legend-dot.forecast {
  background: #4ade80;
  opacity: 0.7;
}

/* 商机状态分布 */
.opportunity-section {
  padding: 0 24px 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
}

.opportunity-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.opportunity-bar {
  display: flex;
  height: 28px;
  border-radius: 8px;
  overflow: hidden;
}

.bar-segment {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  color: white;
  transition: width 0.5s ease;
}

.bar-segment.confirmed {
  background: linear-gradient(90deg, #22c55e 0%, #4ade80 100%);
}

.bar-segment.uncertain {
  background: linear-gradient(90deg, #f59e0b 0%, #fbbf24 100%);
}

.opportunity-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.detail-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.detail-dot.confirmed {
  background: #22c55e;
}

.detail-dot.uncertain {
  background: #f59e0b;
}

.detail-label {
  color: #64748b;
}

.detail-value {
  font-weight: 600;
  color: #1e293b;
  margin-left: auto;
}

.detail-ratio {
  font-size: 12px;
  color: #94a3b8;
  min-width: 36px;
  text-align: right;
}

/* 公式说明 */
.formula-section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: #f1f5f9;
  font-size: 12px;
  color: #64748b;
}
</style>
