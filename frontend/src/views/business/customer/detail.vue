<template>
  <div class="customer-detail-page">
    <!-- 页面头部 -->
    <n-page-header @back="handleBack">
      <template #title>客户详情</template>
      <template #extra>
        <n-space>
          <n-button @click="handleEdit">
            <template #icon>
              <n-icon><CreateOutline /></n-icon>
            </template>
            编辑
          </n-button>
          <n-popconfirm @positive-click="handleRelease">
            <template #trigger>
              <n-button>
                <template #icon>
                  <n-icon><CloudUploadOutline /></n-icon>
                </template>
                释放到公海
              </n-button>
            </template>
            确定要将该客户释放到公海吗？
          </n-popconfirm>
        </n-space>
      </template>
    </n-page-header>

    <!-- 客户基本标识信息 -->
    <n-card :bordered="false" class="customer-info-card">
      <div class="customer-title">
        <h2 class="title-name">{{ customer360?.basicInfo?.name || '-' }}</h2>
        <div class="title-meta">
          <n-tag v-if="customer360?.basicInfo?.code" size="medium">
            {{ customer360.basicInfo.code }}
          </n-tag>
          <n-tag 
            v-if="customer360?.basicInfo?.level" 
            type="primary" 
            size="medium"
          >
            {{ customer360.basicInfo.level }}级客户
          </n-tag>
        </div>
      </div>
    </n-card>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <n-icon size="24"><BriefcaseOutline /></n-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ customer360?.statistics?.opportunityCount || 0 }}</span>
          <span class="stat-label">商机数量</span>
        </div>
        <div class="stat-amount">
          ¥{{ formatAmount(customer360?.statistics?.totalOpportunityAmount) }}
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <n-icon size="24"><DocumentTextOutline /></n-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ customer360?.statistics?.contractCount || 0 }}</span>
          <span class="stat-label">合同数量</span>
        </div>
        <div class="stat-amount">
          ¥{{ formatAmount(customer360?.statistics?.totalContractAmount) }}
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <n-icon size="24"><WalletOutline /></n-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">¥{{ formatAmount(customer360?.statistics?.totalPaymentReceived) }}</span>
          <span class="stat-label">已回款金额</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">
          <n-icon size="24"><PeopleOutline /></n-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ customer360?.statistics?.contactCount || 0 }}</span>
          <span class="stat-label">联系人数量</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧信息 -->
      <div class="left-panel">
        <!-- 基本信息卡片 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">基本信息</h3>
          </div>
          <div class="card-body">
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">客户名称</span>
                <span class="info-value">{{ customer360?.basicInfo?.name || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">简称</span>
                <span class="info-value">{{ customer360?.basicInfo?.shortName || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">行业</span>
                <span class="info-value">{{ customer360?.basicInfo?.industry || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">规模</span>
                <span class="info-value">{{ customer360?.basicInfo?.scale || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">来源</span>
                <span class="info-value">{{ customer360?.basicInfo?.source || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">负责人</span>
                <span class="info-value">{{ customer360?.basicInfo?.ownerName || '-' }}</span>
              </div>
              <div class="info-item full">
                <span class="info-label">电话</span>
                <span class="info-value">{{ customer360?.basicInfo?.phone || '-' }}</span>
              </div>
              <div class="info-item full">
                <span class="info-label">网站</span>
                <span class="info-value">
                  <a v-if="customer360?.basicInfo?.website" :href="customer360.basicInfo.website" target="_blank" class="link">
                    {{ customer360.basicInfo.website }}
                  </a>
                  <span v-else>-</span>
                </span>
              </div>
              <div class="info-item full">
                <span class="info-label">地址</span>
                <span class="info-value">{{ customer360?.basicInfo?.address || '-' }}</span>
              </div>
              <div class="info-item full">
                <span class="info-label">备注</span>
                <span class="info-value">{{ customer360?.basicInfo?.remark || '-' }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 联系人卡片 -->
        <contact-list
          v-if="customerId"
          ref="contactListRef"
          :customer-id="customerId"
        />
      </div>

      <!-- 右侧标签页 -->
      <div class="right-panel">
        <n-tabs type="line" animated class="detail-tabs">
          <!-- 商机列表 -->
          <n-tab-pane name="opportunities" tab="商机">
            <div class="tab-content">
              <div v-if="customer360?.opportunities?.length" class="record-list">
                <div
                  v-for="opp in customer360.opportunities"
                  :key="opp.id"
                  class="record-item"
                  @click="goToOpportunity(opp.id)"
                >
                  <div class="record-main">
                    <span class="record-name">{{ opp.name }}</span>
                    <span class="record-stage" :class="getStageClass(opp.stage)">{{ opp.stage }}</span>
                  </div>
                  <div class="record-meta">
                    <span class="record-amount">¥{{ formatAmount(opp.amount) }}</span>
                    <span class="record-date">{{ opp.expectedDate || '未设置' }}</span>
                  </div>
                </div>
              </div>
              <div v-else class="empty-tab">
                <n-icon size="40" class="empty-icon"><BriefcaseOutline /></n-icon>
                <p>暂无商机记录</p>
              </div>
            </div>
          </n-tab-pane>

          <!-- 合同列表 -->
          <n-tab-pane name="contracts" tab="合同">
            <div class="tab-content">
              <div v-if="customer360?.contracts?.length" class="record-list">
                <div
                  v-for="contract in customer360.contracts"
                  :key="contract.id"
                  class="record-item"
                  @click="goToContract(contract.id)"
                >
                  <div class="record-main">
                    <span class="record-name">{{ contract.name }}</span>
                    <span class="record-no">{{ contract.contractNo }}</span>
                  </div>
                  <div class="record-meta">
                    <span class="record-amount">¥{{ formatAmount(contract.amount) }}</span>
                    <span class="record-status" :class="getContractStatusClass(contract.status)">
                      {{ contract.statusName }}
                    </span>
                  </div>
                </div>
              </div>
              <div v-else class="empty-tab">
                <n-icon size="40" class="empty-icon"><DocumentTextOutline /></n-icon>
                <p>暂无合同记录</p>
              </div>
            </div>
          </n-tab-pane>

          <!-- 跟进记录 -->
          <n-tab-pane name="activities" tab="跟进记录">
            <div class="tab-content activity-tab">
              <activity-timeline
                v-if="customerId"
                ref="activityTimelineRef"
                :target-type="TargetType.CUSTOMER"
                :target-id="customerId"
                @add="handleAddActivity"
                @edit="handleEditActivity"
                @refresh="handleActivityRefresh"
              />
            </div>
          </n-tab-pane>
        </n-tabs>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <n-modal
      v-model:show="showEditModal"
      title="编辑客户"
      preset="card"
      class="edit-modal"
    >
      <customer-form
        ref="customerFormRef"
        :form-data="customer360?.basicInfo || {}"
        @submit="handleEditSubmit"
        @cancel="showEditModal = false"
      />
    </n-modal>

    <!-- 跟进记录弹窗 -->
    <n-modal
      v-model:show="showActivityModal"
      :title="editingActivity ? '编辑跟进记录' : '添加跟进记录'"
      preset="card"
      class="activity-modal"
    >
      <activity-form
        v-if="customerId"
        :target-type="TargetType.CUSTOMER"
        :target-id="customerId"
        :edit-data="editingActivity"
        @submit="handleActivitySubmit"
        @cancel="showActivityModal = false"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NButton,
  NIcon,
  NTabs,
  NTabPane,
  NPopconfirm,
  NModal,
  useMessage
} from 'naive-ui'
import {
  ArrowBackOutline,
  CreateOutline,
  CloudUploadOutline,
  BriefcaseOutline,
  DocumentTextOutline,
  WalletOutline,
  PeopleOutline,
  TimeOutline,
  CallOutline,
  MailOutline,
  LocationOutline
} from '@vicons/ionicons5'
import { getCustomer360, releaseToPool } from '@/api/business/customer'
import type { Customer360 } from '@/types/business/customer'
import type { Activity } from '@/types/business/activity'
import { TargetType } from '@/types/business/activity'
import ContactList from '@/components/business/ContactList.vue'
import CustomerForm from '@/components/business/CustomerForm.vue'
import ActivityTimeline from '@/components/business/ActivityTimeline.vue'
import ActivityForm from '@/components/business/ActivityForm.vue'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const customerId = computed(() => String(route.params.id))
const customer360 = ref<Customer360 | null>(null)
const loading = ref(false)
const showEditModal = ref(false)
const contactListRef = ref()
const customerFormRef = ref()
const activityTimelineRef = ref()
const showActivityModal = ref(false)
const editingActivity = ref<Activity | null>(null)

// 加载客户360度视图数据
const loadCustomer360 = async () => {
  if (!customerId.value) return

  loading.value = true
  try {
    customer360.value = await getCustomer360(customerId.value)
  } catch (error) {
    message.error('加载客户信息失败')
  } finally {
    loading.value = false
  }
}

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (!amount) return '0'
  return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2 })
}

// 返回列表
const handleBack = () => {
  router.push('/customers')
}

// 编辑客户
const handleEdit = () => {
  showEditModal.value = true
}

// 编辑提交
const handleEditSubmit = () => {
  showEditModal.value = false
  loadCustomer360()
}

// 释放到公海
const handleRelease = async () => {
  try {
    await releaseToPool(customerId.value)
    message.success('已释放到公海')
    router.push('/business/customer')
  } catch (error) {
    message.error('释放失败')
  }
}

// 跳转商机详情
const goToOpportunity = (id: string) => {
  router.push(`/business/opportunity/${id}`)
}

// 跳转合同详情
const goToContract = (id: string) => {
  router.push(`/business/contract/${id}`)
}

// 获取阶段样式类
const getStageClass = (stage: string) => {
  const stageMap: Record<string, string> = {
    '需求确认': 'stage-initial',
    '方案报价': 'stage-proposal',
    '商务谈判': 'stage-negotiation',
    '赢单': 'stage-won',
    '输单': 'stage-lost'
  }
  return stageMap[stage] || 'stage-initial'
}

// 获取合同状态样式类
const getContractStatusClass = (status: number) => {
  const statusMap: Record<number, string> = {
    1: 'status-draft',
    2: 'status-pending',
    3: 'status-active',
    4: 'status-completed',
    5: 'status-terminated'
  }
  return statusMap[status] || 'status-draft'
}

// 获取跟进类型样式类
const getActivityTypeClass = (type: string) => {
  const typeMap: Record<string, string> = {
    '电话': 'type-call',
    '拜访': 'type-visit',
    '邮件': 'type-email',
    '会议': 'type-meeting'
  }
  return typeMap[type] || 'type-other'
}

// 获取跟进类型图标
const getActivityIcon = (type: string) => {
  const iconMap: Record<string, any> = {
    '电话': CallOutline,
    '拜访': LocationOutline,
    '邮件': MailOutline,
    '会议': PeopleOutline
  }
  return iconMap[type] || TimeOutline
}

// 添加跟进记录
const handleAddActivity = () => {
  editingActivity.value = null
  showActivityModal.value = true
}

// 编辑跟进记录
const handleEditActivity = (activity: Activity) => {
  editingActivity.value = activity
  showActivityModal.value = true
}

// 跟进记录提交成功
const handleActivitySubmit = () => {
  showActivityModal.value = false
  editingActivity.value = null
  activityTimelineRef.value?.refresh()
  loadCustomer360()
}

// 跟进记录刷新
const handleActivityRefresh = () => {
  loadCustomer360()
}

onMounted(() => {
  loadCustomer360()
})
</script>

<style scoped>
/* 页面容器 */
.customer-detail-page {
  width: 100%;
  min-height: 100%;
}

/* 客户基本信息卡片 */
.customer-info-card {
  margin-bottom: 16px;
}

.customer-info-card .customer-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.customer-info-card .title-name {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.customer-info-card .title-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}


/* 统计卡片 */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.blue {
  background: #dbeafe;
  color: #2563eb;
}

.stat-icon.green {
  background: #dcfce7;
  color: #22c55e;
}

.stat-icon.orange {
  background: #fef3c7;
  color: #f59e0b;
}

.stat-icon.purple {
  background: #f3e8ff;
  color: #9333ea;
}

.stat-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #0f172a;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
}

.stat-amount {
  font-size: 14px;
  font-weight: 600;
  color: #22c55e;
}

/* 主内容区 */
.main-content {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 20px;
  flex: 1;
}

/* 左侧面板 */
.left-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.card-body {
  padding: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full {
  grid-column: span 2;
}

.info-label {
  font-size: 12px;
  color: #94a3b8;
}

.info-value {
  font-size: 14px;
  color: #334155;
}

.info-value .link {
  color: #2563eb;
  text-decoration: none;
}

.info-value .link:hover {
  text-decoration: underline;
}

/* 右侧面板 */
.right-panel {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.detail-tabs {
  height: 100%;
}

.detail-tabs :deep(.n-tabs-nav) {
  padding: 0 20px;
  background: #f8fafc;
}

.detail-tabs :deep(.n-tabs-tab) {
  padding: 16px 0;
  font-weight: 500;
}

.tab-content {
  padding: 20px;
  min-height: 400px;
}

/* 记录列表 */
.record-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.record-item:hover {
  background: #f1f5f9;
}

.record-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.record-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.record-no {
  font-size: 12px;
  color: #94a3b8;
}

.record-stage {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.record-stage.stage-initial {
  background: #dbeafe;
  color: #2563eb;
}

.record-stage.stage-proposal {
  background: #fef3c7;
  color: #f59e0b;
}

.record-stage.stage-negotiation {
  background: #f3e8ff;
  color: #9333ea;
}

.record-stage.stage-won {
  background: #dcfce7;
  color: #22c55e;
}

.record-stage.stage-lost {
  background: #fee2e2;
  color: #ef4444;
}

.record-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.record-amount {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.record-date {
  font-size: 12px;
  color: #94a3b8;
}

.record-status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.record-status.status-draft {
  background: #f1f5f9;
  color: #64748b;
}

.record-status.status-pending {
  background: #fef3c7;
  color: #f59e0b;
}

.record-status.status-active {
  background: #dbeafe;
  color: #2563eb;
}

.record-status.status-completed {
  background: #dcfce7;
  color: #22c55e;
}

.record-status.status-terminated {
  background: #fee2e2;
  color: #ef4444;
}

/* 跟进记录列表 */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 12px;
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

.activity-icon.type-call {
  background: #dcfce7;
  color: #22c55e;
}

.activity-icon.type-visit {
  background: #dbeafe;
  color: #2563eb;
}

.activity-icon.type-email {
  background: #fef3c7;
  color: #f59e0b;
}

.activity-icon.type-meeting {
  background: #f3e8ff;
  color: #9333ea;
}

.activity-icon.type-other {
  background: #f1f5f9;
  color: #64748b;
}

.activity-content {
  flex: 1;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.activity-item:last-child .activity-content {
  border-bottom: none;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.activity-type {
  font-size: 13px;
  font-weight: 500;
  color: #0f172a;
}

.activity-time {
  font-size: 12px;
  color: #94a3b8;
}

.activity-text {
  font-size: 14px;
  color: #475569;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.activity-author {
  font-size: 12px;
  color: #94a3b8;
}

/* 空状态 */
.empty-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #94a3b8;
}

.empty-icon {
  margin-bottom: 12px;
  color: #cbd5e1;
}

.empty-tab p {
  margin: 0;
  font-size: 14px;
}

/* 编辑弹窗 */
.edit-modal {
  width: 800px;
}

/* 跟进记录弹窗 */
.activity-modal {
  width: 600px;
}

/* 跟进记录 Tab */
.activity-tab {
  padding: 0 !important;
}

/* 响应式 */
@media (max-width: 1280px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .main-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
  }

  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
