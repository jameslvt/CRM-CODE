<template>
  <div class="tag-manager">
    <!-- 标签头部 -->
    <div class="tag-header">
      <h3 class="tag-title">客户标签</h3>
      <n-button text size="small" @click="showTagModal = true" class="add-tag-btn">
        <template #icon>
          <n-icon><AddOutline /></n-icon>
        </template>
        管理标签
      </n-button>
    </div>

    <!-- 已选标签 -->
    <div class="selected-tags" v-if="selectedTags.length > 0">
      <n-tag
        v-for="tag in selectedTags"
        :key="tag.id"
        :color="{ color: tag.color + '20', textColor: tag.color, borderColor: tag.color }"
        closable
        @close="handleRemoveTag(tag.id)"
        class="customer-tag"
      >
        {{ tag.name }}
      </n-tag>
    </div>
    <div v-else class="no-tags">
      <span class="no-tags-text">暂无标签</span>
      <n-button text size="tiny" @click="showTagModal = true">添加标签</n-button>
    </div>

    <!-- 标签管理弹窗 -->
    <n-modal
      v-model:show="showTagModal"
      title="管理客户标签"
      preset="card"
      class="tag-modal"
    >
      <div class="tag-modal-content">
        <!-- 可选标签列表 -->
        <div class="available-tags">
          <div class="section-title">选择标签</div>
          <div class="tag-grid">
            <div
              v-for="tag in availableTags"
              :key="tag.id"
              class="tag-option"
              :class="{ selected: isTagSelected(tag.id) }"
              @click="toggleTag(tag)"
            >
              <span class="tag-color" :style="{ background: tag.color }"></span>
              <span class="tag-name">{{ tag.name }}</span>
              <n-icon v-if="isTagSelected(tag.id)" class="check-icon"><CheckmarkOutline /></n-icon>
            </div>
          </div>
        </div>

        <!-- 创建新标签 -->
        <div class="create-tag">
          <div class="section-title">创建新标签</div>
          <div class="create-form">
            <n-input
              v-model:value="newTagName"
              placeholder="标签名称"
              size="small"
              class="tag-name-input"
            />
            <div class="color-picker">
              <div
                v-for="color in colorOptions"
                :key="color"
                class="color-option"
                :class="{ selected: newTagColor === color }"
                :style="{ background: color }"
                @click="newTagColor = color"
              ></div>
            </div>
            <n-button type="primary" size="small" @click="handleCreateTag" :disabled="!newTagName">
              创建
            </n-button>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="modal-footer">
          <n-button @click="showTagModal = false">关闭</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import {
  NButton,
  NIcon,
  NTag,
  NModal,
  NInput,
  useMessage
} from 'naive-ui'
import {
  AddOutline,
  CheckmarkOutline
} from '@vicons/ionicons5'

interface CustomerTag {
  id: number
  name: string
  color: string
}

const props = defineProps<{
  customerId: number
  tags?: CustomerTag[]
}>()

const emit = defineEmits<{
  (e: 'update', tags: CustomerTag[]): void
}>()

const message = useMessage()

const showTagModal = ref(false)
const selectedTags = ref<CustomerTag[]>([])
const availableTags = ref<CustomerTag[]>([])
const newTagName = ref('')
const newTagColor = ref('#2563eb')

// 颜色选项
const colorOptions = [
  '#2563eb', // Blue
  '#22c55e', // Green
  '#f59e0b', // Orange
  '#ef4444', // Red
  '#9333ea', // Purple
  '#ec4899', // Pink
  '#06b6d4', // Cyan
  '#64748b'  // Gray
]

// 模拟加载可用标签
const loadAvailableTags = async () => {
  // TODO: 从API加载标签列表
  availableTags.value = [
    { id: 1, name: '重点客户', color: '#ef4444' },
    { id: 2, name: '潜力客户', color: '#f59e0b' },
    { id: 3, name: '战略合作', color: '#2563eb' },
    { id: 4, name: '长期合作', color: '#22c55e' },
    { id: 5, name: '新客户', color: '#9333ea' },
    { id: 6, name: '待跟进', color: '#06b6d4' }
  ]
}

// 监听外部标签变化
watch(
  () => props.tags,
  (newTags) => {
    if (newTags) {
      selectedTags.value = [...newTags]
    }
  },
  { immediate: true }
)

// 检查标签是否已选中
const isTagSelected = (tagId: number) => {
  return selectedTags.value.some(t => t.id === tagId)
}

// 切换标签选中状态
const toggleTag = (tag: CustomerTag) => {
  const index = selectedTags.value.findIndex(t => t.id === tag.id)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
  emit('update', selectedTags.value)
}

// 移除标签
const handleRemoveTag = (tagId: number) => {
  const index = selectedTags.value.findIndex(t => t.id === tagId)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
    emit('update', selectedTags.value)
  }
}

// 创建新标签
const handleCreateTag = async () => {
  if (!newTagName.value.trim()) {
    message.warning('请输入标签名称')
    return
  }

  // TODO: 调用API创建标签
  const newTag: CustomerTag = {
    id: Date.now(), // 临时ID
    name: newTagName.value.trim(),
    color: newTagColor.value
  }

  availableTags.value.push(newTag)
  selectedTags.value.push(newTag)
  emit('update', selectedTags.value)

  newTagName.value = ''
  message.success('标签创建成功')
}

onMounted(() => {
  loadAvailableTags()
})
</script>

<style scoped>
.tag-manager {
  width: 100%;
}

.tag-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.tag-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.add-tag-btn {
  color: #2563eb;
  font-size: 13px;
}

/* 已选标签 */
.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.customer-tag {
  border-radius: 6px;
  font-size: 12px;
}

.no-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.no-tags-text {
  font-size: 13px;
  color: #94a3b8;
}

/* 弹窗内容 */
.tag-modal {
  width: 480px;
}

.tag-modal-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
}

/* 可选标签网格 */
.tag-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.tag-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag-option:hover {
  border-color: #cbd5e1;
  background: #f1f5f9;
}

.tag-option.selected {
  border-color: #2563eb;
  background: #dbeafe;
}

.tag-color {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  flex-shrink: 0;
}

.tag-name {
  flex: 1;
  font-size: 13px;
  color: #334155;
}

.check-icon {
  color: #2563eb;
}

/* 创建新标签 */
.create-form {
  display: flex;
  align-items: center;
  gap: 12px;
}

.tag-name-input {
  flex: 1;
}

.color-picker {
  display: flex;
  gap: 6px;
}

.color-option {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 2px solid transparent;
}

.color-option:hover {
  transform: scale(1.1);
}

.color-option.selected {
  border-color: #0f172a;
  box-shadow: 0 0 0 2px white, 0 0 0 4px currentColor;
}

/* 弹窗底部 */
.modal-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
