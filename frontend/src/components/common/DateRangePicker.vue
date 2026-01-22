<template>
  <div class="date-range-picker">
    <!-- 快捷选项 -->
    <div class="quick-options">
      <button
        v-for="option in quickOptions"
        :key="option.value"
        class="quick-btn"
        :class="{ active: activeQuick === option.value }"
        @click="handleQuickSelect(option.value)"
      >
        {{ option.label }}
      </button>
    </div>

    <!-- 自定义日期范围 -->
    <div v-if="showCustom" class="custom-range">
      <n-date-picker
        v-model:value="dateRange"
        type="daterange"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        clearable
        @update:value="handleDateChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { NDatePicker } from 'naive-ui'

const props = withDefaults(defineProps<{
  /** 默认选中的快捷选项 */
  defaultQuick?: string
  /** 是否显示自定义日期选择 */
  showCustomPicker?: boolean
}>(), {
  defaultQuick: 'month',
  showCustomPicker: true
})

const emit = defineEmits<{
  (e: 'change', value: { type: string; startDate?: string; endDate?: string }): void
}>()

// 快捷选项
const quickOptions = [
  { label: '今日', value: 'today' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '本季度', value: 'quarter' },
  { label: '本年', value: 'year' },
  { label: '自定义', value: 'custom' }
]

// 当前选中的快捷选项
const activeQuick = ref(props.defaultQuick)

// 自定义日期范围
const dateRange = ref<[number, number] | null>(null)

// 是否显示自定义日期选择器
const showCustom = computed(() => {
  return props.showCustomPicker && activeQuick.value === 'custom'
})

// 获取日期范围
const getDateRange = (type: string): { startDate: string; endDate: string } => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth()
  const date = now.getDate()
  const day = now.getDay()

  let startDate: Date
  let endDate: Date = now

  switch (type) {
    case 'today':
      startDate = new Date(year, month, date)
      break
    case 'week':
      // 本周一
      const mondayOffset = day === 0 ? -6 : 1 - day
      startDate = new Date(year, month, date + mondayOffset)
      break
    case 'month':
      startDate = new Date(year, month, 1)
      break
    case 'quarter':
      const quarterMonth = Math.floor(month / 3) * 3
      startDate = new Date(year, quarterMonth, 1)
      break
    case 'year':
      startDate = new Date(year, 0, 1)
      break
    default:
      startDate = new Date(year, month, 1)
  }

  return {
    startDate: formatDate(startDate),
    endDate: formatDate(endDate)
  }
}

// 格式化日期
const formatDate = (date: Date): string => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 快捷选项点击
const handleQuickSelect = (value: string) => {
  activeQuick.value = value

  if (value === 'custom') {
    // 自定义模式，等待用户选择日期
    return
  }

  const range = getDateRange(value)
  emit('change', {
    type: value,
    startDate: range.startDate,
    endDate: range.endDate
  })
}

// 自定义日期变化
const handleDateChange = (value: [number, number] | null) => {
  if (!value) return

  const [start, end] = value
  emit('change', {
    type: 'custom',
    startDate: formatDate(new Date(start)),
    endDate: formatDate(new Date(end))
  })
}

// 初始化
watch(() => props.defaultQuick, (val) => {
  if (val && val !== 'custom') {
    handleQuickSelect(val)
  }
}, { immediate: true })
</script>

<style scoped>
.date-range-picker {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 快捷选项 */
.quick-options {
  display: flex;
  gap: 4px;
  padding: 4px;
  background: #f1f5f9;
  border-radius: 10px;
}

.quick-btn {
  padding: 8px 14px;
  border: none;
  background: transparent;
  border-radius: 8px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.quick-btn:hover {
  color: #0f172a;
}

.quick-btn.active {
  background: white;
  color: #0f172a;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 自定义日期范围 */
.custom-range {
  min-width: 280px;
}

.custom-range :deep(.n-date-picker) {
  --n-border-radius: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .date-range-picker {
    flex-direction: column;
    align-items: stretch;
  }

  .quick-options {
    flex-wrap: wrap;
    justify-content: center;
  }

  .custom-range {
    min-width: auto;
  }
}
</style>
