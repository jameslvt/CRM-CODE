<template>
  <div class="workbench-container">
    <div class="workbench-layout">
      <!-- 左侧：主要工作区 -->
      <div class="main-section">
        <!-- 智能待办 -->
        <div class="section-card todo-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="icon-wrapper bg-blue-100 text-blue-600">
                <n-icon><ListOutline /></n-icon>
              </span>
              智能待办
            </h3>
            <div class="view-all-link">
              <span>查看全部</span>
              <n-icon class="arrow-icon"><ArrowForwardOutline /></n-icon>
            </div>
          </div>
          <div class="card-body">
            <n-tabs type="line" animated>
              <n-tab-pane name="today" tab="今日需跟进 (8)">
                <n-list>
                  <n-list-item v-for="item in todayTodos" :key="item.id">
                    <template #prefix>
                      <n-checkbox v-model:checked="item.done" />
                    </template>
                    <div class="todo-content">
                      <div class="todo-title" :class="{ done: item.done }">{{ item.title }}</div>
                      <div class="todo-meta">
                        <n-tag size="small" :type="item.tagType" :bordered="false">{{ item.tag }}</n-tag>
                        <span class="todo-time">{{ item.time }}</span>
                      </div>
                    </div>
                    <template #suffix>
                      <n-button size="tiny" secondary @click="handleAction(item)">处理</n-button>
                    </template>
                  </n-list-item>
                </n-list>
              </n-tab-pane>
              <n-tab-pane name="urgent" tab="急需处理 (3)">
                <n-list>
                  <n-list-item v-for="item in urgentTodos" :key="item.id">
                    <template #prefix>
                      <n-icon size="20" color="#ef4444"><AlertCircleOutline /></n-icon>
                    </template>
                    <div class="todo-content">
                      <div class="todo-title">{{ item.title }}</div>
                      <div class="todo-meta warning">
                        <span>{{ item.deadline }} 到期</span>
                      </div>
                    </div>
                    <template #suffix>
                      <n-button size="tiny" type="error" ghost>立即处理</n-button>
                    </template>
                  </n-list-item>
                </n-list>
              </n-tab-pane>
              <n-tab-pane name="approval" tab="待审批 (5)">
                <n-empty description="暂无待审批事项" class="py-8" v-if="approvalTodos.length === 0" />
                <n-list v-else>
                   <n-list-item v-for="item in approvalTodos" :key="item.id">
                    <div class="todo-content">
                      <div class="todo-title">{{ item.title }}</div>
                      <div class="todo-meta">申请人: {{ item.applicant }} · {{ item.time }}</div>
                    </div>
                    <template #suffix>
                      <div class="approval-actions">
                        <button class="btn-action pass">
                          <n-icon><CheckmarkCircleOutline /></n-icon>
                          通过
                        </button>
                        <button class="btn-action reject">
                          <n-icon><CloseCircleOutline /></n-icon>
                          驳回
                        </button>
                      </div>
                    </template>
                  </n-list-item>
                </n-list>
              </n-tab-pane>
            </n-tabs>
          </div>
        </div>

        <!-- 最近足迹 -->
        <div class="section-card recent-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="icon-wrapper bg-purple-100 text-purple-600">
                <n-icon><FootstepsOutline /></n-icon>
              </span>
              最近足迹
            </h3>
          </div>
          <div class="card-body">
            <div class="recent-grid">
              <div v-for="item in recentRecords" :key="item.id" class="recent-item" @click="handleNavigate(item)">
                <div class="recent-icon" :class="item.type">
                  <n-icon>
                    <component :is="item.icon" />
                  </n-icon>
                </div>
                <div class="recent-info">
                  <div class="recent-name">{{ item.name }}</div>
                  <div class="recent-desc">{{ item.desc }} · {{ item.time }}</div>
                </div>
                <n-icon class="arrow-icon"><ArrowForwardOutline /></n-icon>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：工具与状态 -->
      <div class="side-section">
        <!-- 个人业绩简报 -->
        <div class="section-card kpi-card">
          <div class="card-header">
            <h3 class="card-title">本月目标</h3>
            <n-button text size="tiny">详情</n-button>
          </div>
          <div class="card-body">
            <div class="kpi-item">
              <div class="kpi-label">
                <span>销售额</span>
                <span class="kpi-value">¥128,000 / ¥200,000</span>
              </div>
              <n-progress
                type="line"
                :percentage="64"
                :color="{ stops: ['#3b82f6', '#2563eb'] }"
                :height="8"
                border-radius="4"
              />
            </div>
             <div class="kpi-item">
              <div class="kpi-label">
                <span>回款额</span>
                <span class="kpi-value">¥45,000 / ¥80,000</span>
              </div>
              <n-progress
                type="line"
                :percentage="56"
                :color="{ stops: ['#10b981', '#059669'] }"
                :height="8"
                border-radius="4"
              />
            </div>
             <div class="kpi-item">
              <div class="kpi-label">
                <span>新客数</span>
                <span class="kpi-value">8 / 15</span>
              </div>
              <n-progress
                type="line"
                :percentage="53"
                :color="{ stops: ['#f59e0b', '#d97706'] }"
                :height="8"
                border-radius="4"
              />
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="section-card quick-card">
          <div class="card-header">
            <h3 class="card-title">快捷操作</h3>
          </div>
          <div class="quick-grid">
            <div class="quick-item" @click="router.push('/leads')">
              <div class="quick-icon bg-blue-50 text-blue-600">
                <n-icon><PersonAddOutline /></n-icon>
              </div>
              <span>新建线索</span>
            </div>
            <div class="quick-item" @click="router.push('/customers')">
              <div class="quick-icon bg-green-50 text-green-600">
                <n-icon><PeopleOutline /></n-icon>
              </div>
              <span>新建客户</span>
            </div>
            <div class="quick-item" @click="router.push('/opportunities')">
              <div class="quick-icon bg-orange-50 text-orange-600">
                <n-icon><TrendingUpOutline /></n-icon>
              </div>
              <span>新建商机</span>
            </div>
            <div class="quick-item" @click="router.push('/contracts')">
              <div class="quick-icon bg-purple-50 text-purple-600">
                <n-icon><DocumentTextOutline /></n-icon>
              </div>
              <span>草拟合同</span>
            </div>
            <div class="quick-item" @click="message.info('开发中...')">
              <div class="quick-icon bg-gray-50 text-gray-600">
                <n-icon><CalendarOutline /></n-icon>
              </div>
              <span>日程安排</span>
            </div>
            <div class="quick-item" @click="message.info('开发中...')">
              <div class="quick-icon bg-gray-50 text-gray-600">
                <n-icon><ChatbubblesOutline /></n-icon>
              </div>
              <span>写跟进</span>
            </div>
          </div>
        </div>

        <!-- 团队公告 -->
        <div class="section-card news-card">
          <div class="card-header">
            <h3 class="card-title">团队动态</h3>
            <n-button text size="tiny">更多</n-button>
          </div>
          <div class="news-feed">
             <div class="feed-item">
               <div class="feed-icon-box win">
                 <n-icon><TrophyOutline /></n-icon>
               </div>
               <div class="feed-content">
                 <div class="feed-text">恭喜 <strong>王小明</strong> 签下 <span class="highlight-gold">¥500,000</span> 大单！</div>
                 <div class="feed-time">10分钟前</div>
               </div>
             </div>
             <div class="feed-item">
               <div class="feed-icon-box notice">
                 <n-icon><MegaphoneOutline /></n-icon>
               </div>
               <div class="feed-content">
                 <div class="feed-text">关于五一放假及调休的通知</div>
                 <div class="feed-time">2小时前</div>
               </div>
             </div>
             <div class="feed-item">
               <div class="feed-icon-box system">
                 <n-icon><ServerOutline /></n-icon>
               </div>
               <div class="feed-content">
                 <div class="feed-text">系统将于周五晚 24:00 进行维护更新</div>
                 <div class="feed-time">昨天</div>
               </div>
             </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { 
  NButton, NIcon, NTabs, NTabPane, NList, NListItem, NCheckbox, NTag,
  NProgress, NEmpty, NButtonGroup, useMessage 
} from 'naive-ui'
import {
  ListOutline,
  AlertCircleOutline,
  FootstepsOutline,
  ArrowForwardOutline,
  PersonAddOutline,
  PeopleOutline,
  TrendingUpOutline,
  DocumentTextOutline,
  CalendarOutline,
  ChatbubblesOutline,
  PersonOutline,
  BriefcaseOutline,
  WalletOutline,
  CheckmarkCircleOutline,
  CloseCircleOutline,
  TrophyOutline,
  MegaphoneOutline,
  ServerOutline
} from '@vicons/ionicons5'

const router = useRouter()
const message = useMessage()

// 模拟数据：今日跟进
const todayTodos = ref([
  { id: 1, title: '回访客户：深圳市科技有限公司', tag: '客户回访', tagType: 'primary', time: '10:00', done: false },
  { id: 2, title: '跟进商机：年度采购项目', tag: '商机跟进', tagType: 'warning', time: '14:30', done: false },
  { id: 3, title: '发送报价单：李总', tag: '发送资料', tagType: 'info', time: '16:00', done: true },
  { id: 4, title: '合同续签确认：王经理', tag: '合同管理', tagType: 'success', time: '17:00', done: false },
])

// 模拟数据：急需处理
const urgentTodos = ref([
  { id: 101, title: '合同即将到期：广州贸易有限公司', deadline: '明天' },
  { id: 102, title: '逾期未回访：张大伟', deadline: '已逾期2天' },
  { id: 103, title: '商机由阶段3退回', deadline: '今天' },
])

// 模拟数据：待审批
const approvalTodos = ref([
  { id: 201, title: '合同审批：2023年度框架协议', applicant: '张三', time: '10分钟前' },
  { id: 202, title: '费用报销：差旅费', applicant: '李四', time: '2小时前' },
  { id: 203, title: '折扣申请：9折优惠特批', applicant: '王五', time: '昨天' },
])

// 模拟数据：最近足迹
const recentRecords = ref([
  { id: 1, type: 'lead', name: '李经理 - 采购意向', desc: '线索', time: '10分钟前', icon: PersonOutline, path: '/business/lead/1' },
  { id: 2, type: 'customer', name: '上海网络科技有限公司', desc: '客户', time: '30分钟前', icon: PeopleOutline, path: '/business/customer/1' },
  { id: 3, type: 'opportunity', name: 'Q1季度服务器采购', desc: '商机', time: '2小时前', icon: BriefcaseOutline, path: '/business/opportunity/1' },
  { id: 4, type: 'contract', name: '年度运维服务合同', desc: '合同', time: '昨天', icon: DocumentTextOutline, path: '/business/contract/1' },
])

const handleAction = (item: any) => {
  message.success(`开始处理：${item.title}`)
}

const handleNavigate = (item: any) => {
  if(item.path) router.push(item.path)
  else message.info('跳转详情页')
}
</script>

<style scoped>
.workbench-container {
  padding: 0;
  min-height: 100%;
}

.workbench-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
  align-items: start;
}

.main-section, .side-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  transition: all 0.3s ease;
}

.section-card:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border-color: transparent;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 8px;
  font-size: 16px;
}

.card-body {
  padding: 20px;
}

/* 待办列表 */
.todo-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.todo-title {
  font-size: 14px;
  color: #1e293b;
  font-weight: 500;
}

.todo-title.done {
  text-decoration: line-through;
  color: #94a3b8;
}

.todo-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #64748b;
}

.todo-meta.warning {
  color: #ef4444;
}

/* 最近足迹 */
.recent-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.recent-item:hover {
  background: #f1f5f9;
  transform: translateX(4px);
}

.recent-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.recent-icon.lead { background: #eff6ff; color: #3b82f6; }
.recent-icon.customer { background: #f0fdf4; color: #22c55e; }
.recent-icon.opportunity { background: #fff7ed; color: #f97316; }
.recent-icon.contract { background: #f3e8ff; color: #a855f7; }

.recent-info {
  flex: 1;
  min-width: 0;
}

.recent-name {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recent-desc {
  font-size: 12px;
  color: #64748b;
}

.arrow-icon {
  color: #cbd5e1;
}

/* KPI 卡片 */
.kpi-item {
  margin-bottom: 16px;
}

.kpi-item:last-child {
  margin-bottom: 0;
}

.kpi-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  font-size: 13px;
  color: #475569;
}

.kpi-value {
  font-weight: 600;
  color: #0f172a;
}

/* 快捷操作 */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-item:hover {
  transform: translateY(-2px);
}

.quick-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  transition: all 0.2s ease;
}

.quick-item:hover .quick-icon {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.quick-item span {
  font-size: 12px;
  color: #475569;
}

/* 团队动态 - Feed 风格 */
.news-feed {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feed-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.feed-icon-box {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.feed-icon-box.win {
  background: #fffbeb;
  color: #f59e0b;
  border: 1px solid #fde68a;
}

.feed-icon-box.notice {
  background: #eff6ff;
  color: #3b82f6;
  border: 1px solid #dbeafe;
}

.feed-icon-box.system {
  background: #f8fafc;
  color: #64748b;
  border: 1px solid #e2e8f0;
}

.feed-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.feed-text {
  font-size: 13px;
  color: #334155;
  line-height: 1.4;
}

.feed-time {
  font-size: 12px;
  color: #94a3b8;
}

.highlight-gold {
  color: #d97706;
  font-weight: 600;
  font-family: monospace; /* 数字等宽更专业 */
}

/* Tailwind 颜色辅助类 (模拟) */
.bg-blue-50 { background-color: #eff6ff; }
.bg-blue-100 { background-color: #dbeafe; }
.text-blue-600 { color: #2563eb; }

.bg-green-50 { background-color: #f0fdf4; }
.text-green-600 { color: #16a34a; }

.bg-orange-50 { background-color: #fff7ed; }
.text-orange-600 { color: #ea580c; }

.bg-purple-50 { background-color: #faf5ff; }
.bg-purple-100 { background-color: #f3e8ff; }
.text-purple-600 { color: #9333ea; }

.bg-gray-50 { background-color: #f9fafb; }
.text-gray-600 { color: #4b5563; }

/* 查看全部链接 - 精致交互 */
.view-all-link {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 4px 8px;
  border-radius: 6px;
}

.view-all-link:hover {
  color: #2563eb;
  background: #eff6ff;
}

.view-all-link .arrow-icon {
  font-size: 14px;
  transition: transform 0.2s ease;
}

.view-all-link:hover .arrow-icon {
  transform: translateX(2px);
}

/* 审批按钮 - Soft UI 风格 */
.approval-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.btn-action {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 6px;
  border: none;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap; /* 强制不换行 */
  flex-shrink: 0; /* 防止被压缩 */
  transition: all 0.2s ease;
}

.btn-action.pass {
  background: #f0fdf4; /* 浅绿色背景 */
  color: #16a34a;      /* 深绿色文字 */
}

.btn-action.pass:hover {
  background: #dcfce7;
  transform: translateY(-1px);
}

.btn-action.reject {
  background: #fef2f2; /* 浅红色背景 */
  color: #dc2626;      /* 深红色文字 */
}

.btn-action.reject:hover {
  background: #fee2e2;
  transform: translateY(-1px);
}
</style>
