<template>
  <div class="dashboard-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">仪表盘</h1>
        <p class="page-subtitle">欢迎回来，{{ userStore.realName || userStore.username }}！这是您的业务概览。</p>
      </div>
      <div class="header-actions">
        <n-button type="primary" class="action-btn">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新建线索
        </n-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div
        v-for="(stat, index) in statsData"
        :key="stat.key"
        class="stat-card"
        :style="{ animationDelay: `${index * 0.1}s` }"
      >
        <div class="stat-icon" :class="stat.iconClass">
          <component :is="stat.icon" />
        </div>
        <div class="stat-content">
          <span class="stat-label">{{ stat.label }}</span>
          <div class="stat-value-row">
            <span class="stat-value">{{ stat.value }}</span>
            <span v-if="stat.trend" class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
              <n-icon size="14">
                <TrendingUpOutline v-if="stat.trend > 0" />
                <TrendingDownOutline v-else />
              </n-icon>
              {{ Math.abs(stat.trend) }}%
            </span>
          </div>
          <span class="stat-desc">{{ stat.desc }}</span>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 左侧：图表和活动 -->
      <div class="content-left">
        <!-- 销售趋势图表 -->
        <div class="chart-card">
          <div class="card-header">
            <h3 class="card-title">销售趋势</h3>
            <div class="card-actions">
              <n-radio-group v-model:value="chartPeriod" size="small">
                <n-radio-button value="week">本周</n-radio-button>
                <n-radio-button value="month">本月</n-radio-button>
                <n-radio-button value="year">本年</n-radio-button>
              </n-radio-group>
            </div>
          </div>
          <div class="chart-container">
            <div class="chart-placeholder">
              <!-- 简化的图表展示 -->
              <div class="chart-bars">
                <div
                  v-for="(bar, index) in chartData"
                  :key="index"
                  class="chart-bar-wrapper"
                >
                  <div
                    class="chart-bar"
                    :style="{ height: `${bar.value}%`, animationDelay: `${index * 0.05}s` }"
                  ></div>
                  <span class="chart-label">{{ bar.label }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 最近活动 -->
        <div class="activity-card">
          <div class="card-header">
            <h3 class="card-title">最近活动</h3>
            <a href="javascript:;" class="view-all">查看全部</a>
          </div>
          <div class="activity-list">
            <div
              v-for="(activity, index) in recentActivities"
              :key="index"
              class="activity-item"
            >
              <div class="activity-icon" :class="activity.type">
                <n-icon size="16">
                  <component :is="activity.icon" />
                </n-icon>
              </div>
              <div class="activity-content">
                <p class="activity-text">{{ activity.text }}</p>
                <span class="activity-time">{{ activity.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：待办和快捷操作 -->
      <div class="content-right">
        <!-- 销售漏斗 -->
        <div class="funnel-card">
          <div class="card-header">
            <h3 class="card-title">销售漏斗</h3>
          </div>
          <div class="funnel-container">
            <div
              v-for="(stage, index) in funnelData"
              :key="index"
              class="funnel-stage"
              :style="{ width: `${100 - index * 15}%` }"
            >
              <div class="funnel-bar" :class="stage.class">
                <span class="funnel-label">{{ stage.label }}</span>
                <span class="funnel-value">{{ stage.value }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 待办事项 -->
        <div class="todo-card">
          <div class="card-header">
            <h3 class="card-title">待办事项</h3>
            <n-badge :value="todoItems.filter(t => !t.done).length" :max="99">
              <span class="badge-label">待处理</span>
            </n-badge>
          </div>
          <div class="todo-list">
            <div
              v-for="(todo, index) in todoItems"
              :key="index"
              class="todo-item"
              :class="{ done: todo.done }"
            >
              <n-checkbox v-model:checked="todo.done" />
              <div class="todo-content">
                <span class="todo-text">{{ todo.text }}</span>
                <span class="todo-due" :class="{ urgent: todo.urgent }">{{ todo.due }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="quick-actions-card">
          <div class="card-header">
            <h3 class="card-title">快捷操作</h3>
          </div>
          <div class="quick-actions">
            <button
              v-for="action in quickActions"
              :key="action.key"
              class="quick-action-btn"
              @click="handleQuickAction(action.key)"
            >
              <n-icon size="20">
                <component :is="action.icon" />
              </n-icon>
              <span>{{ action.label }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NIcon, NRadioGroup, NRadioButton, NCheckbox, NBadge } from 'naive-ui'
import {
  AddOutline,
  TrendingUpOutline,
  TrendingDownOutline,
  PersonOutline,
  PeopleOutline,
  BriefcaseOutline,
  DocumentTextOutline,
  WalletOutline,
  CallOutline,
  MailOutline,
  CalendarOutline,
  CheckmarkCircleOutline,
  TimeOutline,
  CreateOutline
} from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const chartPeriod = ref('month')

// 统计数据
const statsData = ref([
  {
    key: 'leads',
    label: '线索总数',
    value: '1,234',
    trend: 12.5,
    desc: '较上月增长',
    icon: PersonOutline,
    iconClass: 'blue'
  },
  {
    key: 'customers',
    label: '客户总数',
    value: '567',
    trend: 8.2,
    desc: '较上月增长',
    icon: PeopleOutline,
    iconClass: 'green'
  },
  {
    key: 'opportunities',
    label: '商机总数',
    value: '89',
    trend: -3.1,
    desc: '较上月下降',
    icon: BriefcaseOutline,
    iconClass: 'purple'
  },
  {
    key: 'contracts',
    label: '合同金额',
    value: '¥2.4M',
    trend: 15.8,
    desc: '较上月增长',
    icon: DocumentTextOutline,
    iconClass: 'orange'
  }
])

// 图表数据
const chartData = ref([
  { label: '1月', value: 45 },
  { label: '2月', value: 62 },
  { label: '3月', value: 55 },
  { label: '4月', value: 78 },
  { label: '5月', value: 68 },
  { label: '6月', value: 85 },
  { label: '7月', value: 72 },
  { label: '8月', value: 90 },
  { label: '9月', value: 82 },
  { label: '10月', value: 95 },
  { label: '11月', value: 88 },
  { label: '12月', value: 100 }
])

// 销售漏斗数据
const funnelData = ref([
  { label: '线索', value: 1234, class: 'stage-1' },
  { label: '意向客户', value: 567, class: 'stage-2' },
  { label: '商机', value: 234, class: 'stage-3' },
  { label: '报价', value: 89, class: 'stage-4' },
  { label: '成交', value: 45, class: 'stage-5' }
])

// 最近活动
const recentActivities = ref([
  {
    type: 'lead',
    icon: PersonOutline,
    text: '新增线索：张三 - 科技有限公司',
    time: '10分钟前'
  },
  {
    type: 'call',
    icon: CallOutline,
    text: '完成通话：李四 - 跟进产品需求',
    time: '30分钟前'
  },
  {
    type: 'email',
    icon: MailOutline,
    text: '发送邮件：王五 - 报价单',
    time: '1小时前'
  },
  {
    type: 'contract',
    icon: DocumentTextOutline,
    text: '合同签署：赵六 - ¥50,000',
    time: '2小时前'
  },
  {
    type: 'meeting',
    icon: CalendarOutline,
    text: '预约会议：周一 14:00 - 产品演示',
    time: '3小时前'
  }
])

// 待办事项
const todoItems = ref([
  { text: '跟进科技公司商机', due: '今天', urgent: true, done: false },
  { text: '发送产品报价单', due: '今天', urgent: true, done: false },
  { text: '准备周会汇报材料', due: '明天', urgent: false, done: false },
  { text: '回访老客户', due: '本周', urgent: false, done: true }
])

// 快捷操作
const quickActions = ref([
  { key: 'lead', label: '新建线索', icon: PersonOutline },
  { key: 'customer', label: '新建客户', icon: PeopleOutline },
  { key: 'opportunity', label: '新建商机', icon: BriefcaseOutline },
  { key: 'contract', label: '新建合同', icon: DocumentTextOutline }
])

const handleQuickAction = (key: string) => {
  const routes: Record<string, string> = {
    lead: '/leads',
    customer: '/customers',
    opportunity: '/opportunities',
    contract: '/contracts'
  }
  if (routes[key]) {
    router.push(routes[key])
  }
}
</script>

<style scoped>
/* ========================================
   企业级CRM仪表盘样式
   遵循章程UI/UX设计规范
   ======================================== */

.dashboard-page {
  width: 100%;
  min-height: 100%;
}

/* ========================================
   页面标题
   ======================================== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 4px 0;
  letter-spacing: -0.02em;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.action-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  background: #2563eb;
  border: none;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: #1d4ed8;
  transform: translateY(-2px);
  box-shadow: 0 10px 40px rgba(37, 99, 235, 0.3);
}

/* ========================================
   统计卡片网格
   ======================================== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  gap: 16px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
  animation: slideUp 0.5s ease-out backwards;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
  border-color: transparent;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
}

.stat-icon.blue {
  background: #dbeafe;
  color: #2563eb;
}

.stat-icon.green {
  background: #dcfce7;
  color: #22c55e;
}

.stat-icon.purple {
  background: #f3e8ff;
  color: #9333ea;
}

.stat-icon.orange {
  background: #ffedd5;
  color: #f97316;
}

.stat-content {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 4px;
}

.stat-value-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 6px;
}

.stat-trend.up {
  background: #dcfce7;
  color: #22c55e;
}

.stat-trend.down {
  background: #fee2e2;
  color: #ef4444;
}

.stat-desc {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

/* ========================================
   主要内容区
   ======================================== */
.main-content {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
  align-items: start;
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.content-right {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ========================================
   通用卡片样式
   ======================================== */
.chart-card,
.activity-card,
.funnel-card,
.todo-card,
.quick-actions-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.view-all {
  font-size: 13px;
  color: #2563eb;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
}

.view-all:hover {
  color: #1d4ed8;
}

/* ========================================
   图表卡片
   ======================================== */
.chart-container {
  padding: 24px;
}

.chart-placeholder {
  height: 280px;
  display: flex;
  align-items: flex-end;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  padding-bottom: 30px;
  position: relative;
}

.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  height: 100%;
  justify-content: flex-end;
}

.chart-bar {
  width: 24px;
  background: linear-gradient(180deg, #2563eb 0%, #3b82f6 100%);
  border-radius: 6px 6px 0 0;
  transition: all 0.3s ease;
  animation: growUp 0.8s ease-out backwards;
}

.chart-bar:hover {
  background: linear-gradient(180deg, #1d4ed8 0%, #2563eb 100%);
  transform: scaleY(1.05);
}

@keyframes growUp {
  from {
    height: 0 !important;
  }
}

.chart-label {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 8px;
  position: absolute;
  bottom: 0;
}

/* ========================================
   活动列表
   ======================================== */
.activity-list {
  padding: 8px 0;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 12px 24px;
  transition: background 0.2s ease;
}

.activity-item:hover {
  background: #f8fafc;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.activity-icon.lead {
  background: #dbeafe;
  color: #2563eb;
}

.activity-icon.call {
  background: #dcfce7;
  color: #22c55e;
}

.activity-icon.email {
  background: #fef3c7;
  color: #f59e0b;
}

.activity-icon.contract {
  background: #f3e8ff;
  color: #9333ea;
}

.activity-icon.meeting {
  background: #fce7f3;
  color: #ec4899;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 14px;
  color: #334155;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-time {
  font-size: 12px;
  color: #94a3b8;
}

/* ========================================
   销售漏斗
   ======================================== */
.funnel-container {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.funnel-stage {
  transition: all 0.3s ease;
  width: 100%;
}

.funnel-bar {
  height: 36px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  transition: all 0.3s ease;
}

.funnel-bar:hover {
  transform: scale(1.02);
}

.funnel-bar.stage-1 {
  background: linear-gradient(90deg, #2563eb 0%, #3b82f6 100%);
}

.funnel-bar.stage-2 {
  background: linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%);
}

.funnel-bar.stage-3 {
  background: linear-gradient(90deg, #60a5fa 0%, #93c5fd 100%);
}

.funnel-bar.stage-4 {
  background: linear-gradient(90deg, #93c5fd 0%, #bfdbfe 100%);
}

.funnel-bar.stage-5 {
  background: linear-gradient(90deg, #22c55e 0%, #4ade80 100%);
}

.funnel-label {
  font-size: 13px;
  font-weight: 500;
  color: white;
}

.funnel-value {
  font-size: 14px;
  font-weight: 700;
  color: white;
}

/* ========================================
   待办事项
   ======================================== */
.badge-label {
  font-size: 12px;
  color: #64748b;
}

.todo-list {
  padding: 4px 0;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  transition: all 0.2s ease;
}

.todo-item:hover {
  background: #f8fafc;
}

.todo-item.done {
  opacity: 0.5;
}

.todo-item.done .todo-text {
  text-decoration: line-through;
}

.todo-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-width: 0;
}

.todo-text {
  font-size: 14px;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.todo-due {
  font-size: 12px;
  color: #94a3b8;
  flex-shrink: 0;
  margin-left: 8px;
}

.todo-due.urgent {
  color: #ef4444;
  font-weight: 500;
}

/* ========================================
   快捷操作
   ======================================== */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 20px 24px;
}

.quick-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #475569;
}

.quick-action-btn:hover {
  background: #2563eb;
  border-color: #2563eb;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(37, 99, 235, 0.2);
}

.quick-action-btn span {
  font-size: 13px;
  font-weight: 500;
}

/* ========================================
   响应式设计
   ======================================== */
@media (max-width: 1280px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .main-content {
    grid-template-columns: 1fr;
  }

  .content-right {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
  }

  .funnel-card {
    grid-column: span 2;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .content-right {
    grid-template-columns: 1fr;
  }

  .funnel-card {
    grid-column: span 1;
  }

  .stat-card {
    padding: 20px;
  }

  .stat-value {
    font-size: 24px;
  }
}

/* ========================================
   减少动画 - 无障碍
   ======================================== */
@media (prefers-reduced-motion: reduce) {
  .stat-card,
  .chart-bar {
    animation: none;
  }

  .stat-card:hover,
  .quick-action-btn:hover,
  .action-btn:hover {
    transform: none;
  }
}
</style>
