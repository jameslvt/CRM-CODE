<template>
  <div class="region-performance-table">
    <!-- 卡片头部 -->
    <div class="table-header">
      <div class="header-left">
        <div class="header-icon">
          <n-icon :size="20"><TrendingUpOutline /></n-icon>
        </div>
        <div class="header-text">
          <h3 class="header-title">机构业绩排名</h3>
          <span class="header-subtitle">按完成率排序 · 实时更新</span>
        </div>
      </div>
      <div class="header-right">
        <n-select
          v-model:value="selectedMonth"
          :options="monthOptions"
          size="small"
          style="width: 100px"
        />
      </div>
    </div>

    <!-- 表格区域 -->
    <div class="table-wrapper">
      <table class="performance-table">
        <thead>
          <tr>
            <th class="col-rank">#</th>
            <th class="col-region">机构</th>
            <th class="col-plan">预计划</th>
            <th class="col-actual">月实际</th>
            <th class="col-rate">完成率</th>
            <th class="col-forecast">预测完成</th>
            <th class="col-forecast-rate">预测完成率</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(item, index) in sortedData"
            :key="item.region"
            :class="{ 'row-highlight': item.completionRate >= 100 }"
          >
            <td class="col-rank">
              <span class="rank-badge" :class="getRankClass(index + 1)">
                {{ index + 1 }}
              </span>
            </td>
            <td class="col-region">
              <span class="region-name">{{ item.region }}</span>
            </td>
            <td class="col-plan">
              <span class="value-number">{{ formatNumber(item.plan) }}</span>
            </td>
            <td class="col-actual">
              <span class="value-number highlight">{{ formatNumber(item.actual) }}</span>
            </td>
            <td class="col-rate">
              <div class="rate-cell">
                <div class="progress-bar-wrapper">
                  <div 
                    class="progress-bar" 
                    :class="getProgressClass(item.completionRate)"
                    :style="{ width: `${Math.min(item.completionRate, 100)}%` }"
                  ></div>
                </div>
                <span class="rate-value" :class="getRateClass(item.completionRate)">
                  {{ item.completionRate }}%
                  <n-icon v-if="item.completionRate >= 100" :size="12">
                    <ArrowUpOutline />
                  </n-icon>
                </span>
              </div>
            </td>
            <td class="col-forecast">
              <span class="value-number">{{ formatNumber(item.forecast) }}</span>
            </td>
            <td class="col-forecast-rate">
              <span class="forecast-rate" :class="getForecastRateClass(item.forecastRate)">
                {{ item.forecastRate }}%
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 汇总行 -->
    <div class="summary-row">
      <div class="summary-item">
        <span class="summary-label">母公司合计</span>
        <span class="summary-value">预计划: ¥{{ formatNumber(totalPlan) }}万</span>
      </div>
      <div class="summary-item">
        <span class="summary-value primary">月实际: ¥{{ formatNumber(totalActual) }}万</span>
      </div>
      <div class="summary-item">
        <span class="summary-value" :class="totalCompletionRate >= 70 ? 'success' : 'warning'">
          整体完成率: {{ totalCompletionRate }}%
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { NIcon, NSelect } from 'naive-ui'
import { TrendingUpOutline, ArrowUpOutline } from '@vicons/ionicons5'

// 月份选择
const selectedMonth = ref('01')
const monthOptions = [
  { label: '01月', value: '01' },
  { label: '02月', value: '02' },
  { label: '03月', value: '03' },
  { label: '04月', value: '04' },
  { label: '05月', value: '05' },
  { label: '06月', value: '06' },
  { label: '07月', value: '07' },
  { label: '08月', value: '08' },
  { label: '09月', value: '09' },
  { label: '10月', value: '10' },
  { label: '11月', value: '11' },
  { label: '12月', value: '12' }
]

// 模拟数据 - 基于用户提供的报表
const regionData = ref([
  { region: '大同-朔州大区', plan: 17, actual: 31, completionRate: 180, forecast: 36, forecastRate: 209 },
  { region: '山东-河北大区', plan: 44, actual: 44, completionRate: 100, forecast: 44, forecastRate: 100 },
  { region: '晋城大区', plan: 134, actual: 8, completionRate: 6, forecast: 134, forecastRate: 99 },
  { region: '吕梁大区', plan: 859, actual: 186, completionRate: 22, forecast: 784, forecastRate: 91 },
  { region: '晋中-长治大区', plan: 457, actual: 201, completionRate: 44, forecast: 401, forecastRate: 88 },
  { region: '阳泉大区', plan: 233, actual: 102, completionRate: 44, forecast: 203, forecastRate: 87 },
  { region: '忻州大区', plan: 373, actual: 31, completionRate: 8, forecast: 293, forecastRate: 79 },
  { region: '太原-临汾-运城大区', plan: 700, actual: 163, completionRate: 23, forecast: 465, forecastRate: 66 },
  { region: '河南-安徽大区', plan: 182, actual: 62, completionRate: 34, forecast: 122, forecastRate: 67 },
  { region: '陕西-内蒙大区', plan: 353, actual: 21, completionRate: 6, forecast: 197, forecastRate: 56 },
  { region: '西北西南大区', plan: 162, actual: 1, completionRate: 1, forecast: 82, forecastRate: 51 },
  { region: '东北大区', plan: 80, actual: 0, completionRate: 0, forecast: 40, forecastRate: 50 }
])

// 按完成率排序
const sortedData = computed(() => {
  return [...regionData.value].sort((a, b) => b.completionRate - a.completionRate)
})

// 汇总计算
const totalPlan = computed(() => regionData.value.reduce((sum, item) => sum + item.plan, 0))
const totalActual = computed(() => regionData.value.reduce((sum, item) => sum + item.actual, 0))
const totalCompletionRate = computed(() => {
  if (totalPlan.value === 0) return 0
  return Math.round((totalActual.value / totalPlan.value) * 100)
})

// 格式化数字
const formatNumber = (num: number) => {
  return num.toLocaleString()
}

// 排名样式
const getRankClass = (rank: number) => {
  if (rank === 1) return 'gold'
  if (rank === 2) return 'silver'
  if (rank === 3) return 'bronze'
  return ''
}

// 进度条样式
const getProgressClass = (rate: number) => {
  if (rate >= 100) return 'excellent'
  if (rate >= 50) return 'good'
  if (rate >= 20) return 'normal'
  return 'low'
}

// 完成率文字样式
const getRateClass = (rate: number) => {
  if (rate >= 100) return 'excellent'
  if (rate >= 50) return 'good'
  return 'warning'
}

// 预测完成率样式
const getForecastRateClass = (rate: number) => {
  if (rate >= 100) return 'excellent'
  if (rate >= 80) return 'good'
  if (rate >= 60) return 'normal'
  return 'warning'
}
</script>

<style scoped>
.region-performance-table {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  display: flex; /* 新增 */
  flex-direction: column; /* 新增 */
}

/* 卡片头部 */
.table-header {
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

/* 表格区域 */
.table-wrapper {
  overflow-x: auto;
  flex: 1; /* 填充剩余空间 */
}

.performance-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.performance-table thead {
  background: #f1f5f9;
}

.performance-table th {
  padding: 12px 16px;
  text-align: left;
  font-weight: 600;
  color: #475569;
  white-space: nowrap;
  border-bottom: 2px solid #e2e8f0;
}

.performance-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.2s ease;
}

.performance-table tbody tr:hover {
  background: #f8fafc;
}

.performance-table tbody tr.row-highlight {
  background: linear-gradient(90deg, rgba(34, 197, 94, 0.08) 0%, transparent 100%);
}

/* 列宽控制 */
.col-rank { width: 50px; text-align: center; }
.col-region { min-width: 140px; }
.col-plan, .col-actual, .col-forecast { width: 90px; text-align: right; }
.col-rate { width: 160px; }
.col-forecast-rate { width: 100px; text-align: center; }

/* 排名徽章 */
.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  background: #f1f5f9;
  color: #64748b;
}

.rank-badge.gold {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: white;
}

.rank-badge.silver {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
  color: white;
}

.rank-badge.bronze {
  background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
  color: white;
}

/* 机构名称 */
.region-name {
  font-weight: 500;
  color: #1e293b;
}

/* 数值 */
.value-number {
  font-family: 'SF Mono', 'Monaco', 'Inconsolata', monospace;
  color: #334155;
}

.value-number.highlight {
  color: #2563eb;
  font-weight: 600;
}

/* 完成率单元格 */
.rate-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar-wrapper {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

.progress-bar.excellent {
  background: linear-gradient(90deg, #22c55e 0%, #4ade80 100%);
}

.progress-bar.good {
  background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%);
}

.progress-bar.normal {
  background: linear-gradient(90deg, #f59e0b 0%, #fbbf24 100%);
}

.progress-bar.low {
  background: linear-gradient(90deg, #ef4444 0%, #f87171 100%);
}

/* 完成率数值 */
.rate-value {
  min-width: 50px;
  font-weight: 600;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.rate-value.excellent {
  color: #22c55e;
}

.rate-value.good {
  color: #3b82f6;
}

.rate-value.warning {
  color: #f59e0b;
}

/* 预测完成率 */
.forecast-rate {
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.forecast-rate.excellent {
  background: #dcfce7;
  color: #22c55e;
}

.forecast-rate.good {
  background: #dbeafe;
  color: #2563eb;
}

.forecast-rate.normal {
  background: #fef3c7;
  color: #d97706;
}

.forecast-rate.warning {
  background: #fee2e2;
  color: #ef4444;
}

/* 汇总行 */
.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #f8fafc;
  border-top: 2px solid #e2e8f0;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.summary-value {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.summary-value.primary {
  color: #2563eb;
}

.summary-value.success {
  color: #22c55e;
}

.summary-value.warning {
  color: #f59e0b;
}
</style>
