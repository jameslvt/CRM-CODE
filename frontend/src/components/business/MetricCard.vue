<template>
  <div class="metric-card" :class="[`theme-${theme}`, { clickable }]" @click="handleClick">
    <!-- 左侧：图标 -->
    <div class="card-left">
      <div class="icon-wrapper" :style="{ background: iconBg }">
        <n-icon :size="24" :style="{ color: iconColor }">
          <slot name="icon">
            <component :is="icon" />
          </slot>
        </n-icon>
      </div>
    </div>

    <!-- 右侧：数据 -->
    <div class="card-right">
      <div class="metric-info">
        <div class="metric-label">{{ label }}</div>
        
        <div class="value-row">
          <div class="metric-value">
            <span v-if="prefix" class="value-prefix">{{ prefix }}</span>
            <span class="value-number">{{ formattedValue }}</span>
            <span v-if="suffix" class="value-suffix">{{ suffix }}</span>
          </div>

          <div v-if="trend !== undefined" class="trend-badge" :class="trendClass">
            <n-icon size="10">
              <TrendingUpOutline v-if="trend > 0" />
              <TrendingDownOutline v-else-if="trend < 0" />
              <RemoveOutline v-else />
            </n-icon>
            <span>{{ Math.abs(trend) }}%</span>
          </div>
        </div>
      </div>

      <div class="metric-footer">
        <span class="sub-label">{{ subLabel }}</span>
        <span class="sub-value">{{ formattedSubValue }}</span>
      </div>
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
  blue: { bg: '#eff6ff', color: '#3b82f6' },
  green: { bg: '#f0fdf4', color: '#22c55e' },
  orange: { bg: '#fff7ed', color: '#f97316' },
  purple: { bg: '#faf5ff', color: '#a855f7' },
  red: { bg: '#fef2f2', color: '#ef4444' },
  gray: { bg: '#f8fafc', color: '#64748b' }
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
    if (props.value >= 100000000) {
      return (Number(props.value) / 100000000).toFixed(1) + '亿'
    }
    return (Number(props.value) / 10000).toFixed(1) + '万'
  }
  return Number(props.value).toLocaleString()
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
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  padding: 16px; /* 减小内边距 */
  display: flex; /* 改为 Flex 布局 */
  align-items: flex-start;
  gap: 16px;
  transition: all 0.2s ease;
  height: 100%;
}

.metric-card:hover {
  border-color: #e2e8f0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.metric-card.clickable {
  cursor: pointer;
}

.metric-card.clickable:hover {
  transform: translateY(-1px);
}

/* 左侧图标 */
.card-left {
  flex-shrink: 0;
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 右侧信息 */
.card-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 48px; /* 与图标高度一致 */
}

.metric-info {
  display: flex;
  flex-direction: column;
}

.metric-label {
  font-size: 13px; /* 稍微减小 */
  color: #64748b;
  line-height: 1;
  margin-bottom: 6px;
}

.value-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.metric-value {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.value-prefix {
  font-size: 14px;
  font-weight: 600;
  color: #94a3b8;
}

.value-number {
  font-size: 24px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1;
  letter-spacing: -0.02em;
}

.value-suffix {
  font-size: 12px;
  font-weight: 500;
  color: #94a3b8;
}

/* 趋势标签 */
.trend-badge {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 2px 6px; /* 更小巧 */
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  line-height: 1;
}

.trend-badge.trend-up {
  background: #dcfce7;
  color: #16a34a;
}

.trend-badge.trend-down {
  background: #fee2e2;
  color: #dc2626;
}

.trend-badge.trend-flat {
  background: #f1f5f9;
  color: #64748b;
}

/* 副指标 Footer */
.metric-footer {
  font-size: 12px;
  color: #94a3b8;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: auto; /* 靠底部 */
}

.sub-label {
  color: #94a3b8;
}

.sub-value {
  color: #64748b;
  font-weight: 500;
}
</style>
