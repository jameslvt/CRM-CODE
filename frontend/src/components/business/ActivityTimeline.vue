<template>
  <div class="activity-timeline-container">
    <!-- 头部区域 -->
    <div class="timeline-header">
      <div class="header-left">
        <div class="header-icon">
          <n-icon size="18"><ChatbubblesOutline /></n-icon>
        </div>
        <div class="header-text">
          <h3 class="header-title">跟进记录</h3>
          <span class="header-count">{{ activities.length }} 条记录</span>
        </div>
      </div>
      <n-button
        type="primary"
        size="small"
        @click="handleAdd"
        :style="{ borderRadius: '8px' }"
      >
        <template #icon>
          <n-icon><AddOutline /></n-icon>
        </template>
        添加跟进
      </n-button>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <button
          v-for="(tab, index) in filterTabs"
          :key="tab.value ?? 'all'"
          class="filter-chip"
          :class="{ active: activeFilter === tab.value }"
          :style="{ animationDelay: `${index * 30}ms` }"
          @click="handleFilterChange(tab.value)"
        >
          <span class="chip-icon">
            <n-icon size="13">
              <component :is="tab.icon" />
            </n-icon>
          </span>
          <span class="chip-label">{{ tab.label }}</span>
          <span v-if="getFilterCount(tab.value) > 0" class="chip-count">
            {{ getFilterCount(tab.value) }}
          </span>
        </button>
      </div>
    </div>

    <!-- 时间线内容 -->
    <div class="timeline-content">
      <n-spin :show="loading">
        <template v-if="filteredActivities.length > 0">
          <div class="timeline-list">
            <div
              v-for="(activity, index) in filteredActivities"
              :key="activity.id"
              class="timeline-item"
              :style="{ animationDelay: `${index * 60}ms` }"
            >
              <!-- 左侧时间轴 -->
              <div class="timeline-track">
                <div
                  class="track-node"
                  :class="getTypeClass(activity.type)"
                >
                  <n-icon size="14">
                    <component :is="getTypeIcon(activity.type)" />
                  </n-icon>
                </div>
                <div v-if="index < filteredActivities.length - 1" class="track-line"></div>
              </div>

              <!-- 右侧内容卡片 -->
              <div class="activity-card">
                <!-- 卡片头部 -->
                <div class="card-top">
                  <div class="card-meta">
                    <span
                      class="type-badge"
                      :class="getTypeClass(activity.type)"
                    >
                      {{ activity.typeName || getTypeName(activity.type) }}
                    </span>
                    <span class="time-badge">
                      <n-icon size="12"><TimeOutline /></n-icon>
                      {{ formatTime(activity.createTime) }}
                    </span>
                  </div>
                  <div class="card-actions">
                    <n-tooltip trigger="hover" placement="top">
                      <template #trigger>
                        <button class="action-btn action-edit" @click="handleEdit(activity)">
                          <n-icon size="14"><CreateOutline /></n-icon>
                        </button>
                      </template>
                      编辑
                    </n-tooltip>
                    <n-popconfirm
                      @positive-click="handleDelete(activity.id)"
                      positive-text="确认删除"
                      negative-text="取消"
                    >
                      <template #trigger>
                        <n-tooltip trigger="hover" placement="top">
                          <template #trigger>
                            <button class="action-btn action-delete">
                              <n-icon size="14"><TrashOutline /></n-icon>
                            </button>
                          </template>
                          删除
                        </n-tooltip>
                      </template>
                      <div style="max-width: 200px">
                        确定要删除这条跟进记录吗？此操作不可恢复。
                      </div>
                    </n-popconfirm>
                  </div>
                </div>

                <!-- 卡片内容 -->
                <div class="card-body">
                  <p class="content-text">{{ activity.content }}</p>
                </div>

                <!-- 卡片底部 -->
                <div class="card-bottom">
                  <div class="author-info">
                    <div class="author-avatar">
                      {{ getInitials(activity.createByName) }}
                    </div>
                    <span class="author-name">{{ activity.createByName || '未知' }}</span>
                  </div>
                  <div v-if="activity.nextTime" class="next-follow">
                    <n-icon size="12"><AlarmOutline /></n-icon>
                    <span>{{ formatDate(activity.nextTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <div class="empty-illustration">
            <div class="empty-circle">
              <div class="empty-icon-wrapper">
                <n-icon size="32" color="#94a3b8"><ChatbubblesOutline /></n-icon>
              </div>
            </div>
            <div class="empty-rings">
              <span class="ring ring-1"></span>
              <span class="ring ring-2"></span>
              <span class="ring ring-3"></span>
            </div>
          </div>
          <div class="empty-content">
            <h4 class="empty-title">暂无跟进记录</h4>
            <p class="empty-desc">记录每次客户沟通，让销售过程更透明</p>
            <n-button
              type="primary"
              @click="handleAdd"
              :style="{ borderRadius: '8px', marginTop: '8px' }"
            >
              <template #icon>
                <n-icon><AddOutline /></n-icon>
              </template>
              添加第一条跟进
            </n-button>
          </div>
        </div>
      </n-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import {
  NButton,
  NIcon,
  NSpin,
  NPopconfirm,
  NTooltip,
  useMessage
} from 'naive-ui'
import {
  AddOutline,
  CreateOutline,
  TrashOutline,
  AlarmOutline,
  ChatbubblesOutline,
  CallOutline,
  WalkOutline,
  MailOutline,
  PeopleOutline,
  EllipsisHorizontalOutline,
  ListOutline,
  TimeOutline
} from '@vicons/ionicons5'
import { getActivitiesByTarget, deleteActivity } from '@/api/business/activity'
import type { Activity } from '@/types/business/activity'
import { ActivityType, getActivityTypeName } from '@/types/business/activity'

const props = defineProps<{
  targetType: string
  targetId: number
}>()

const emit = defineEmits<{
  (e: 'add'): void
  (e: 'edit', activity: Activity): void
  (e: 'refresh'): void
}>()

const message = useMessage()
const loading = ref(false)
const activities = ref<Activity[]>([])
const activeFilter = ref<ActivityType | null>(null)

// 筛选标签
const filterTabs: Array<{ label: string; value: ActivityType | null; icon: any }> = [
  { label: '全部', value: null, icon: ListOutline },
  { label: '电话', value: ActivityType.PHONE, icon: CallOutline },
  { label: '拜访', value: ActivityType.VISIT, icon: WalkOutline },
  { label: '邮件', value: ActivityType.EMAIL, icon: MailOutline },
  { label: '会议', value: ActivityType.MEETING, icon: PeopleOutline },
  { label: '其他', value: ActivityType.OTHER, icon: EllipsisHorizontalOutline }
]

// 筛选后的活动列表
const filteredActivities = computed(() => {
  if (!activeFilter.value) return activities.value
  return activities.value.filter(a => a.type === activeFilter.value)
})

// 获取筛选数量
const getFilterCount = (type: ActivityType | null): number => {
  if (type === null) return activities.value.length
  return activities.value.filter(a => a.type === type).length
}

// 获取类型样式类
const getTypeClass = (type: string): string => {
  const classMap: Record<string, string> = {
    [ActivityType.PHONE]: 'type-phone',
    [ActivityType.VISIT]: 'type-visit',
    [ActivityType.EMAIL]: 'type-email',
    [ActivityType.MEETING]: 'type-meeting',
    [ActivityType.OTHER]: 'type-other'
  }
  return classMap[type] || 'type-other'
}

// 获取类型名称
const getTypeName = (type: string) => {
  return getActivityTypeName(type)
}

// 获取类型图标
const getTypeIcon = (type: string) => {
  const iconMap: Record<string, any> = {
    [ActivityType.PHONE]: CallOutline,
    [ActivityType.VISIT]: WalkOutline,
    [ActivityType.EMAIL]: MailOutline,
    [ActivityType.MEETING]: PeopleOutline,
    [ActivityType.OTHER]: EllipsisHorizontalOutline
  }
  return iconMap[type] || EllipsisHorizontalOutline
}

// 获取姓名首字母
const getInitials = (name: string | undefined): string => {
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}

// 格式化时间
const formatTime = (time: string | undefined) => {
  if (!time) return '-'
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60))
      return minutes <= 0 ? '刚刚' : `${minutes}分钟前`
    }
    return `${hours}小时前`
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
  }
}

// 格式化日期
const formatDate = (time: string | undefined) => {
  if (!time) return '-'
  return new Date(time).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

// 加载数据
const loadData = async () => {
  if (!props.targetId) return
  loading.value = true
  try {
    activities.value = await getActivitiesByTarget(props.targetType, props.targetId)
  } catch (error) {
    message.error('加载跟进记录失败')
  } finally {
    loading.value = false
  }
}

// 筛选切换
const handleFilterChange = (value: ActivityType | null) => {
  activeFilter.value = value
}

// 添加跟进
const handleAdd = () => {
  emit('add')
}

// 编辑跟进
const handleEdit = (activity: Activity) => {
  emit('edit', activity)
}

// 删除跟进
const handleDelete = async (id: string) => {
  try {
    await deleteActivity(id)
    message.success('删除成功')
    loadData()
    emit('refresh')
  } catch (error) {
    message.error('删除失败')
  }
}

// 暴露刷新方法
const refresh = () => {
  loadData()
}

defineExpose({ refresh })

// 监听 targetId 变化
watch(() => props.targetId, () => {
  loadData()
})

onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 容器 */
.activity-timeline-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px;
}

/* 头部 */
.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
  line-height: 1.2;
}

.header-count {
  font-size: 12px;
  color: #64748b;
}

/* 筛选区域 */
.filter-section {
  margin-bottom: 20px;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  border-radius: 20px;
  font-size: 12px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  animation: chipFadeIn 0.3s ease forwards;
  opacity: 0;
}

@keyframes chipFadeIn {
  from {
    opacity: 0;
    transform: translateY(4px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.filter-chip:hover {
  border-color: #cbd5e1;
  background: #f8fafc;
}

.filter-chip.active {
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  border-color: transparent;
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
}

.chip-icon {
  display: flex;
  align-items: center;
}

.chip-label {
  font-weight: 500;
}

.chip-count {
  padding: 1px 6px;
  background: rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  font-size: 10px;
  font-weight: 600;
}

.filter-chip.active .chip-count {
  background: rgba(255, 255, 255, 0.25);
}

/* 时间线内容 */
.timeline-content {
  flex: 1;
  overflow-y: auto;
}

.timeline-list {
  display: flex;
  flex-direction: column;
}

/* 时间线项 */
.timeline-item {
  display: flex;
  gap: 16px;
  animation: itemSlideIn 0.4s ease forwards;
  opacity: 0;
}

@keyframes itemSlideIn {
  from {
    opacity: 0;
    transform: translateX(-8px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 时间轴轨道 */
.timeline-track {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 40px;
  flex-shrink: 0;
}

.track-node {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  transition: all 0.2s ease;
}

.track-node.type-phone {
  background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);
  color: #16a34a;
}

.track-node.type-visit {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #2563eb;
}

.track-node.type-email {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #d97706;
}

.track-node.type-meeting {
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  color: #9333ea;
}

.track-node.type-other {
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #64748b;
}

.track-line {
  width: 2px;
  flex: 1;
  min-height: 20px;
  background: linear-gradient(to bottom, #e2e8f0, #f1f5f9);
  margin: 4px 0;
}

/* 活动卡片 */
.activity-card {
  flex: 1;
  background: #f8fafc;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 14px 16px;
  margin-bottom: 12px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.activity-card:hover {
  background: #ffffff;
  border-color: #e2e8f0;
  box-shadow: 0 4px 12px -2px rgba(15, 23, 42, 0.08);
}

/* 卡片头部 */
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.type-badge {
  padding: 3px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 600;
}

.type-badge.type-phone {
  background: #dcfce7;
  color: #16a34a;
}

.type-badge.type-visit {
  background: #dbeafe;
  color: #2563eb;
}

.type-badge.type-email {
  background: #fef3c7;
  color: #d97706;
}

.type-badge.type-meeting {
  background: #f3e8ff;
  color: #9333ea;
}

.type-badge.type-other {
  background: #f1f5f9;
  color: #64748b;
}

.time-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #94a3b8;
}

/* 操作按钮 */
.card-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.activity-card:hover .card-actions {
  opacity: 1;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: #64748b;
}

.action-edit:hover {
  background: #f1f5f9;
  color: #2563eb;
}

.action-delete:hover {
  background: #fee2e2;
  color: #ef4444;
}

/* 卡片内容 */
.card-body {
  margin-bottom: 12px;
}

.content-text {
  font-size: 13px;
  color: #334155;
  line-height: 1.6;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 卡片底部 */
.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: 600;
  color: #475569;
}

.author-name {
  font-size: 12px;
  color: #64748b;
}

.next-follow {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: #fef3c7;
  border-radius: 6px;
  font-size: 11px;
  color: #d97706;
  font-weight: 500;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48px 24px;
}

.empty-illustration {
  position: relative;
  margin-bottom: 24px;
}

.empty-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.empty-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty-rings {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 140px;
  height: 140px;
  pointer-events: none;
}

.ring {
  position: absolute;
  top: 50%;
  left: 50%;
  border: 2px solid #e2e8f0;
  border-radius: 50%;
  animation: ringPulse 3s ease-in-out infinite;
}

.ring-1 {
  width: 100px;
  height: 100px;
  margin: -50px 0 0 -50px;
  animation-delay: 0s;
}

.ring-2 {
  width: 120px;
  height: 120px;
  margin: -60px 0 0 -60px;
  animation-delay: 0.5s;
}

.ring-3 {
  width: 140px;
  height: 140px;
  margin: -70px 0 0 -70px;
  animation-delay: 1s;
}

@keyframes ringPulse {
  0%, 100% {
    opacity: 0.2;
    transform: scale(0.95);
  }
  50% {
    opacity: 0.5;
    transform: scale(1);
  }
}

.empty-content {
  text-align: center;
}

.empty-title {
  font-size: 15px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 6px 0;
}

.empty-desc {
  font-size: 13px;
  color: #94a3b8;
  margin: 0;
  line-height: 1.5;
}

/* 响应式 */
@media (max-width: 640px) {
  .activity-timeline-container {
    padding: 16px;
  }

  .filter-tabs {
    gap: 6px;
  }

  .filter-chip {
    padding: 5px 10px;
    font-size: 11px;
  }

  .timeline-track {
    width: 32px;
  }

  .track-node {
    width: 32px;
    height: 32px;
  }

  .activity-card {
    padding: 12px;
  }

  .card-actions {
    opacity: 1;
  }
}
</style>
