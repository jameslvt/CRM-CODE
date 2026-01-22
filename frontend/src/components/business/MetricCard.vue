<template>
  <div class="metric-card" :class="[`theme-${theme}`, { clickable }]" @click="handleClick">
    <div class="card-header">
      <div class="icon-wrapper" :style="{ background: iconBg }">
        <n-icon :size="iconSize" :style="{ color: iconColor }">
          <slot name="icon">
            <component :is="icon" />
          </slot>
        </n-icon>
      </div>
      <div v-if="trend !== undefined" class="trend-badge" :class="trendClass">
        <n-icon size="12">
          <TrendingUpOutline v-if="trend > 0" />
          <TrendingDownOutline v-else-if="trend < 0" />
          <RemoveOutline v-else />
        </n-icon>
        <span>{{ Math.abs(trend) }}%</span>
      </div>
    </div>

    <div class="card-body">
      <div class="metric-value">
        <span v-if="prefix" class="value-prefix">{{ prefix }}</span>
        <span class="value-number">{{ formattedValue }}</span>
        <span v-if="suffix" class="value-suffix">{{ suffix }}</span>
      </div>
      <div class="metric-label">{{ label }}</div>
    </div>

    <div v-if="subValue !== undefined || $slots.footer" class="card-footer">
      <slot name="footer">
        <span class="sub-label">{{ subLabel }}</span>
        <span class="sub-value">{{ formattedSubValue }}</span>
      </slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { NIcon } from 'naive-ui'
import {
  TrendingUpOutline,
  TrendingDownOutline,
  RemoveOutline,
  StatsChartOutline
} from '@vicons/ionicons5'

const props = withDefaults(defineProps<{
  /** 主要数值 */
  value: number | string
  /** 标签 */
  label: string
  /** 图标组件 */
  icon?: any
  /** 主题色 */
  theme?: 'blue' | 'green' | 'orange' | 'purple' | 'red' | 'gray'
  /** 数值前缀 */
  prefix?: string
  /** 数值后缀 */
  suffix?: string
  /** 趋势百分比 */
  trend?: number
  /** 副数值 */
  subValue?: number | string
  /** 副标签 */
  subLabel?: string
  /** 是否可点击 */
  clickable?: boolean
  /** 图标大小 */
  iconSize?: number
}>(), {
  theme: 'blue',
  icon: StatsChartOutline,
  clickable: false,
  iconSize: 24
})

const emit = defineEmits<{
  (e: 'click'): void
}>()

// 主题颜色映射
const themeColors: Record<string, { bg: string; color: string }> = {
  blue: { bg: '#dbeafe', color: '#2563eb' },
  green: { bg: '#dcfce7', color: '#22c55e' },
  orange: { bg: '#fef3c7', color: '#f59e0b' },
  purple: { bg: '#f3e8ff', color: '#9333ea' },
  red: { bg: '#fee2e2', color: '#ef4444' },
  gray: { bg: '#f1f5f9', color: '#64748b' }
}

// 图标背景色
const iconBg = computed(() => themeColors[props.theme]?.bg || themeColors.blue.bg)

// 图标颜色
const iconColor = computed(() => themeColors[props.theme]?.color || themeColors.blue.color)

// 趋势样式类
const trendClass = computed(() => {
  if (props.trend === undefined) return ''
  if (props.trend > 0) return 'trend-up'
  if (props.trend < 0) return 'trend-down'
  return 'trend-flat'
})

// 格式化主数值
const formattedValue = computed(() => {
  if (typeof props.value === 'string') return props.value
  if (props.value >= 10000) {
    return (props.value / 10000).toFixed(1) + '万'
  }
  return props.value.toLocaleString()
})

// 格式化副数值
const formattedSubValue = computed(() => {
  if (props.subValue === undefined) return ''
  if (typeof props.subValue === 'string') return props.subValue
  return props.subValue.toLocaleString()
})

// 点击处理
const handleClick = () => {
  if (props.clickable) {
    emit('click')
  }
}
</script>

<style scoped>
.metric-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  transition: all 0.3s ease;
}

.metric-card:hover {
  border-color: transparent;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.metric-card.clickable {
  cursor: pointer;
}

.metric-card.clickable:hover {
  transform: translateY(-2px);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 趋势标签 */
.trend-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.trend-badge.trend-up {
  background: #dcfce7;
  color: #22c55e;
}

.trend-badge.trend-down {
  background: #fee2e2;
  color: #ef4444;
}

.trend-badge.trend-flat {
  background: #f1f5f9;
  color: #64748b;
}

/* 卡片主体 */
.card-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-value {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.value-prefix {
  font-size: 16px;
  font-weight: 500;
  color: #64748b;
}

.value-number {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.value-suffix {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  margin-left: 2px;
}

.metric-label {
  font-size: 14px;
  color: #64748b;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.sub-label {
  font-size: 12px;
  color: #94a3b8;
}

.sub-value {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
}

/* 主题变体 */
.metric-card.theme-blue .value-number {
  color: #0f172a;
}

.metric-card.theme-green .value-number {
  color: #0f172a;
}

.metric-card.theme-orange .value-number {
  color: #0f172a;
}

.metric-card.theme-purple .value-number {
  color: #0f172a;
}

.metric-card.theme-red .value-number {
  color: #0f172a;
}
</style>
