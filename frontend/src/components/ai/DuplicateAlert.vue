<template>
  <div v-if="visible" class="duplicate-alert">
    <n-alert
      :type="alertType"
      :title="alertTitle"
      :closable="closable"
      @close="handleClose"
    >
      <template #icon>
        <n-icon><WarningOutline /></n-icon>
      </template>

      <div class="alert-content">
        <p class="alert-message">{{ alertMessage }}</p>

        <!-- 重复项列表 -->
        <div v-if="duplicates.length > 0" class="duplicates-list">
          <div
            v-for="(item, index) in duplicates"
            :key="index"
            class="duplicate-item"
            @click="handleItemClick(item)"
          >
            <div class="item-header">
              <div class="similarity-badge" :style="{ background: getSimilarityColor(item.similarity) }">
                {{ (item.similarity * 100).toFixed(0) }}% 相似
              </div>
              <span class="item-id">ID: {{ item.id }}</span>
            </div>
            <div class="item-reason">
              <n-icon size="14"><InformationCircleOutline /></n-icon>
              <span>{{ item.reason }}</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="alert-actions">
          <n-button size="small" @click="handleIgnore">
            忽略并继续
          </n-button>
          <n-button
            v-if="duplicates.length > 0"
            type="primary"
            size="small"
            @click="handleViewDuplicate"
          >
            查看重复项
          </n-button>
        </div>
      </div>
    </n-alert>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { NAlert, NButton, NIcon } from 'naive-ui'
import { WarningOutline, InformationCircleOutline } from '@vicons/ionicons5'
import type { DuplicateItem, DuplicateCheckResult } from '@/types/ai'
import { getSimilarityLevel } from '@/types/ai'

const props = withDefaults(defineProps<{
  /** 是否显示 */
  visible: boolean
  /** 查重结果 */
  result?: DuplicateCheckResult | null
  /** 实体类型 */
  entityType?: 'lead' | 'customer' | 'contact'
  /** 是否可关闭 */
  closable?: boolean
}>(), {
  visible: false,
  entityType: 'lead',
  closable: true
})

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'ignore'): void
  (e: 'view', item: DuplicateItem): void
}>()

// 重复项列表
const duplicates = computed(() => props.result?.duplicates || [])

// 是否有重复
const hasDuplicate = computed(() => props.result?.hasDuplicate || false)

// 最高相似度
const maxSimilarity = computed(() => {
  if (duplicates.value.length === 0) return 0
  return Math.max(...duplicates.value.map(d => d.similarity))
})

// 警告类型
const alertType = computed(() => {
  if (!hasDuplicate.value) return 'success'
  if (maxSimilarity.value >= 0.9) return 'error'
  if (maxSimilarity.value >= 0.7) return 'warning'
  return 'info'
})

// 警告标题
const alertTitle = computed(() => {
  const entityLabels: Record<string, string> = {
    lead: '线索',
    customer: '客户',
    contact: '联系人'
  }
  const label = entityLabels[props.entityType] || '数据'

  if (!hasDuplicate.value) {
    return `未发现重复${label}`
  }

  const level = getSimilarityLevel(maxSimilarity.value)
  return `发现${level.label}的${label}记录`
})

// 警告消息
const alertMessage = computed(() => {
  if (!hasDuplicate.value) {
    return '系统未检测到与现有数据重复的记录，可以安全创建。'
  }

  const count = duplicates.value.length
  return `系统检测到 ${count} 条可能重复的记录，请确认是否继续创建。`
})

// 获取相似度颜色
const getSimilarityColor = (similarity: number) => {
  const level = getSimilarityLevel(similarity)
  return level.color
}

// 关闭
const handleClose = () => {
  emit('close')
}

// 忽略
const handleIgnore = () => {
  emit('ignore')
}

// 查看重复项
const handleViewDuplicate = () => {
  if (duplicates.value.length > 0) {
    emit('view', duplicates.value[0])
  }
}

// 点击重复项
const handleItemClick = (item: DuplicateItem) => {
  emit('view', item)
}
</script>

<style scoped>
.duplicate-alert {
  margin-bottom: 16px;
}

.alert-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-message {
  margin: 0;
  font-size: 14px;
  color: #475569;
  line-height: 1.5;
}

/* 重复项列表 */
.duplicates-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.duplicate-item {
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.duplicate-item:hover {
  border-color: #cbd5e1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.similarity-badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.item-id {
  font-size: 12px;
  color: #94a3b8;
}

.item-reason {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 13px;
  color: #64748b;
  line-height: 1.4;
}

.item-reason .n-icon {
  flex-shrink: 0;
  margin-top: 2px;
}

/* 操作按钮 */
.alert-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid #f1f5f9;
}
</style>
