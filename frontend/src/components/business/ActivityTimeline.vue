<template>
  <div class="activity-timeline">
    <!-- 头部操作栏 -->
    <div class="timeline-header">
      <div class="header-left">
        <h3 class="section-title">跟进记录</h3>
        <span class="record-count">共 {{ activities.length }} 条</span>
      </div>
      <div class="header-right">
        <n-button type="primary" size="small" @click="handleAdd">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          添加跟进
        </n-button>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <button
        v-for="tab in filterTabs"
        :key="tab.value ?? 'all'"
        class="filter-tab"
        :class="{ active: activeFilter === tab.value }"
        @click="handleFilterChange(tab.value)"
      >
        <n-icon v-if="tab.icon" size="14">
          <component :is="tab.icon" />
        </n-icon>
        {{ tab.label }}
      </button>
    </div>

    <!-- 时间线内容 -->
    <div class="timeline-content">
      <n-spin :show="loading">
        <div v-if="filteredActivities.length > 0" class="timeline-list">
          <div
            v-for="(activity, index) in filteredActivities"
            :key="activity.id"
            class="timeline-item"
          >
            <!-- 时间线节点 -->
            <div class="timeline-node" :style="{ background: getTypeColor(activity.type).bg }">
              <n-icon :size="16" :style="{ color: getTypeColor(activity.type).color }">
                <component :is="getTypeIcon(activity.type)" />
              </n-icon>
            </div>

            <!-- 连接线 -->
            <div v-if="index < filteredActivities.length - 1" class="timeline-line"></div>

            <!-- 内容卡片 -->
            <div class="timeline-card">
              <div class="card-header">
                <div class="header-info">
                  <span class="type-tag" :style="{ background: getTypeColor(activity.type).bg, color: getTypeColor(activity.type).color }">
                    {{ activity.typeName || getTypeName(activity.type) }}
                  </span>
                  <span class="activity-time">{{ formatTime(activity.createTime) }}</span>
                </div>
                <div class="header-actions">
                  <n-button text size="small" @click="handleEdit(activity)">
                    <template #icon>
                      <n-icon><CreateOutline /></n-icon>
                    </template>
                  </n-button>
                  <n-popconfirm @positive-click="handleDelete(activity.id)">
                    <template #trigger>
                      <n-button text size="small" type="error">
                        <template #icon>
                          <n-icon><TrashOutline /></n-icon>
                        </template>
                      </n-button>
                    </template>
                    确定要删除该跟进记录吗？
                  </n-popconfirm>
                </div>
              </div>

              <div class="card-content">
                <p class="activity-content">{{ activity.content }}</p>
              </div>

              <div class="card-footer">
                <div class="footer-item">
                  <n-icon size="12"><PersonOutline /></n-icon>
                  <span>{{ activity.createByName || '-' }}</span>
                </div>
                <div v-if="activity.nextTime" class="footer-item next-time">
                  <n-icon size="12"><AlarmOutline /></n-icon>
                  <span>下次跟进: {{ formatDate(activity.nextTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-state">
          <n-icon size="48" class="empty-icon"><ChatbubblesOutline /></n-icon>
          <span class="empty-text">暂无跟进记录</span>
          <n-button type="primary" size="small" @click="handleAdd">
            添加第一条跟进
          </n-button>
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
  useMessage
} from 'naive-ui'
import {
  AddOutline,
  CreateOutline,
  TrashOutline,
  PersonOutline,
  AlarmOutline,
  ChatbubblesOutline,
  CallOutline,
  WalkOutline,
  MailOutline,
  PeopleOutline,
  EllipsisHorizontalOutline,
  ListOutline
} from '@vicons/ionicons5'
import { getActivitiesByTarget, deleteActivity } from '@/api/business/activity'
import type { Activity } from '@/types/business/activity'
import { ActivityType, getActivityTypeName, getActivityTypeColor } from '@/types/business/activity'

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

// 获取类型颜色
const getTypeColor = (type: string) => {
  return getActivityTypeColor(type)
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
const handleDelete = async (id: number) => {
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
.activity-timeline {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 头部 */
.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.record-count {
  font-size: 12px;
  color: #94a3b8;
  padding: 2px 8px;
  background: #f1f5f9;
  border-radius: 4px;
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: none;
  background: #f1f5f9;
  border-radius: 6px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-tab:hover {
  background: #e2e8f0;
}

.filter-tab.active {
  background: #2563eb;
  color: white;
}

/* 时间线内容 */
.timeline-content {
  min-height: 200px;
}

.timeline-list {
  display: flex;
  flex-direction: column;
}

/* 时间线项 */
.timeline-item {
  position: relative;
  display: flex;
  gap: 16px;
  padding-bottom: 24px;
}

.timeline-item:last-child {
  padding-bottom: 0;
}

/* 时间线节点 */
.timeline-node {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  z-index: 1;
}

/* 连接线 */
.timeline-line {
  position: absolute;
  left: 17px;
  top: 36px;
  bottom: 0;
  width: 2px;
  background: #e2e8f0;
}

/* 内容卡片 */
.timeline-card {
  flex: 1;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.2s ease;
}

.timeline-card:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.type-tag {
  font-size: 11px;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 4px;
}

.activity-time {
  font-size: 12px;
  color: #94a3b8;
}

.header-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.timeline-card:hover .header-actions {
  opacity: 1;
}

.card-content {
  margin-bottom: 12px;
}

.activity-content {
  font-size: 14px;
  color: #334155;
  line-height: 1.6;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 16px;
}

.footer-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #94a3b8;
}

.footer-item.next-time {
  color: #f59e0b;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 48px 20px;
}

.empty-icon {
  color: #cbd5e1;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
}
</style>
