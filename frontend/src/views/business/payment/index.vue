<template>
  <div class="payment-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">回款明细</h1>
        <p class="page-subtitle">实时监控回款进度，保障企业现金流健康</p>
      </div>
      <div class="header-actions">
        <n-button type="primary" @click="handleAddPlan" class="primary-btn">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新增回款计划
        </n-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card total">
        <div class="stat-icon">
          <n-icon size="24"><WalletOutline /></n-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">¥{{ formatAmount(statistics.totalPlanAmount) }}</span>
          <span class="stat-label">计划回款总额</span>
        </div>
      </div>
      <div class="stat-card received">
        <div class="stat-icon">
          <n-icon size="24"><CheckmarkCircleOutline /></n-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">¥{{ formatAmount(statistics.totalActualAmount) }}</span>
          <span class="stat-label">已回款金额</span>
        </div>
      </div>
      <div class="stat-card pending">
        <div class="stat-icon">
          <n-icon size="24"><TimeOutline /></n-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">¥{{ formatAmount(statistics.pendingAmount) }}</span>
          <span class="stat-label">待回款金额</span>
        </div>
      </div>
      <div class="stat-card overdue">
        <div class="stat-icon">
          <n-icon size="24"><AlertCircleOutline /></n-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">¥{{ formatAmount(statistics.overdueAmount) }}</span>
          <span class="stat-label">逾期金额</span>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="filter-group">
          <n-select
            v-model:value="searchParams.status"
            placeholder="回款状态"
            :options="statusOptions"
            clearable
            class="filter-select"
          />
          <n-button @click="handleSearch" class="filter-btn">
            <template #icon>
              <n-icon><SearchOutline /></n-icon>
            </template>
            搜索
          </n-button>
          <n-button @click="handleReset" quaternary class="reset-btn">
            重置
          </n-button>
        </div>
        <div class="status-tabs">
          <button
            v-for="tab in statusTabs"
            :key="tab.value ?? 'all'"
            class="status-tab"
            :class="{ active: activeTab === tab.value }"
            @click="handleTabChange(tab.value)"
          >
            {{ tab.label }}
            <span class="tab-count">{{ tab.count }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <n-data-table
        :columns="columns"
        :data="dataList"
        :loading="loading"
        :pagination="pagination"
        :row-key="(row: PaymentPlan) => row.id"
        :bordered="false"
        :single-line="false"
        flex-height
        class="data-table"
      />
    </div>

    <!-- 新增/编辑回款计划弹窗 -->
    <n-modal
      v-model:show="showPlanModal"
      :title="planModalTitle"
      preset="card"
      class="form-modal"
    >
      <payment-plan-form
        :form-data="currentPlan"
        @submit="handlePlanSubmit"
        @cancel="showPlanModal = false"
      />
    </n-modal>

    <!-- 添加回款记录弹窗 -->
    <n-modal
      v-model:show="showRecordModal"
      title="添加回款记录"
      preset="card"
      class="record-modal"
    >
      <payment-record-form
        v-if="currentPlanId"
        :plan-id="currentPlanId"
        @submit="handleRecordSubmit"
        @cancel="showRecordModal = false"
      />
    </n-modal>

    <!-- 回款记录详情弹窗 -->
    <n-modal
      v-model:show="showRecordsModal"
      title="回款记录"
      preset="card"
      class="records-modal"
    >
      <payment-records-list
        v-if="currentPlanId"
        :plan-id="currentPlanId"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, computed } from 'vue'
import {
  NButton,
  NIcon,
  NSelect,
  NPopconfirm,
  NProgress,
  useMessage,
  useDialog,
  type DataTableColumns,
  type PaginationProps
} from 'naive-ui'
import {
  AddOutline,
  SearchOutline,
  CreateOutline,
  TrashOutline,
  WalletOutline,
  CheckmarkCircleOutline,
  TimeOutline,
  AlertCircleOutline,
  CashOutline,
  ListOutline
} from '@vicons/ionicons5'
import {
  pagePaymentPlans,
  deletePaymentPlan,
  getPaymentStatistics
} from '@/api/business/payment'
import type { PaymentPlan, PaymentPlanQueryParams, PaymentStatistics } from '@/types/business/payment'
import {
  paymentPlanStatusOptions,
  PaymentPlanStatus,
  getPaymentPlanStatusColor,
  getPaymentPlanStatusName
} from '@/types/business/payment'
import PaymentPlanForm from '@/components/business/PaymentPlanForm.vue'
import PaymentRecordForm from '@/components/business/PaymentRecordForm.vue'
import PaymentRecordsList from '@/components/business/PaymentRecordsList.vue'

const message = useMessage()
const dialog = useDialog()

// 统计数据
const statistics = ref<PaymentStatistics>({
  totalPlanAmount: 0,
  totalActualAmount: 0,
  pendingAmount: 0,
  overdueAmount: 0,
  pendingCount: 0,
  partialCount: 0,
  completedCount: 0,
  overdueCount: 0
})

// 搜索参数
const searchParams = reactive<PaymentPlanQueryParams>({
  pageNum: 1,
  pageSize: 10,
  status: undefined
})

// 状态选项
const statusOptions = paymentPlanStatusOptions

// 当前激活的标签
const activeTab = ref<number | null>(null)

// 状态标签
const statusTabs = computed(() => [
  { label: '全部', value: null, count: (statistics.value.pendingCount || 0) + (statistics.value.partialCount || 0) + (statistics.value.completedCount || 0) + (statistics.value.overdueCount || 0) },
  { label: '待回款', value: PaymentPlanStatus.PENDING, count: statistics.value.pendingCount || 0 },
  { label: '部分回款', value: PaymentPlanStatus.PARTIAL, count: statistics.value.partialCount || 0 },
  { label: '已回款', value: PaymentPlanStatus.COMPLETED, count: statistics.value.completedCount || 0 },
  { label: '逾期', value: PaymentPlanStatus.OVERDUE, count: statistics.value.overdueCount || 0 }
])

const loading = ref(false)
const dataList = ref<PaymentPlan[]>([])
const showPlanModal = ref(false)
const showRecordModal = ref(false)
const showRecordsModal = ref(false)
const planModalTitle = ref('新增回款计划')
const currentPlan = ref<Partial<PaymentPlan>>({})
const currentPlanId = ref<number | string | null>(null)

// 分页配置
const pagination = reactive<PaginationProps>({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  prefix: ({ itemCount }) => `共 ${itemCount} 条`,
  onChange: (page: number) => {
    searchParams.pageNum = page
    loadData()
  },
  onUpdatePageSize: (pageSize: number) => {
    searchParams.pageSize = pageSize
    searchParams.pageNum = 1
    loadData()
  }
})

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (!amount) return '0'
  return amount.toLocaleString()
}

// 获取状态标签
const getStatusTag = (status: number | undefined) => {
  if (!status) return null
  const color = getPaymentPlanStatusColor(status)
  const name = getPaymentPlanStatusName(status)
  return h(
    'span',
    {
      class: 'status-tag',
      style: { background: color.bg, color: color.color }
    },
    name
  )
}

// 计算回款进度
const getPaymentProgress = (plan: PaymentPlan) => {
  const total = plan.planAmount || 0
  const actual = plan.actualAmount || 0
  if (total === 0) return 0
  return Math.round((actual / total) * 100)
}

// 表格列配置
const columns: DataTableColumns<PaymentPlan> = [
  {
    title: '合同信息',
    key: 'contract',
    width: 220,
    render: (row) =>
      h('div', { class: 'contract-info-cell' }, [
        h('span', { class: 'contract-name' }, row.contractName || '-'),
        row.contractNo && h('span', { class: 'contract-no' }, row.contractNo),
        h('span', { class: 'customer-name' }, row.customerName || '-')
      ])
  },
  {
    title: '期数',
    key: 'period',
    width: 80,
    render: (row) => h('span', { class: 'period-text' }, `第${row.period}期`)
  },
  {
    title: '计划金额',
    key: 'planAmount',
    width: 140,
    render: (row) => h('span', { class: 'amount-text' }, `¥${formatAmount(row.planAmount)}`)
  },
  {
    title: '回款进度',
    key: 'progress',
    width: 180,
    render: (row) => {
      const progress = getPaymentProgress(row)
      return h('div', { class: 'progress-cell' }, [
        h(NProgress, {
          type: 'line',
          percentage: progress,
          indicatorPlacement: 'inside',
          height: 18,
          borderRadius: 4,
          fillBorderRadius: 4,
          color: progress >= 100 ? '#22c55e' : progress > 0 ? '#f59e0b' : '#e2e8f0'
        }),
        h('div', { class: 'progress-info' }, [
          h('span', { class: 'actual' }, `¥${formatAmount(row.actualAmount)}`),
          h('span', { class: 'separator' }, '/'),
          h('span', { class: 'plan' }, `¥${formatAmount(row.planAmount)}`)
        ])
      ])
    }
  },
  {
    title: '计划日期',
    key: 'planDate',
    width: 120
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) => getStatusTag(row.status)
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    fixed: 'right',
    render: (row) =>
      h('div', { class: 'action-buttons' }, [
        h(
          'button',
          {
            class: 'action-btn record',
            onClick: () => handleViewRecords(row.id)
          },
          [h(NIcon, { size: 14 }, { default: () => h(ListOutline) }), '记录']
        ),
        row.status !== PaymentPlanStatus.COMPLETED &&
          h(
            'button',
            {
              class: 'action-btn add-record',
              onClick: () => handleTableAddRecord(row.id)
            },
            [h(NIcon, { size: 14 }, { default: () => h(CashOutline) }), '回款']
          ),
        h(
          'button',
          {
            class: 'action-btn edit',
            onClick: () => handleEditPlan(row)
          },
          [h(NIcon, { size: 14 }, { default: () => h(CreateOutline) }), '编辑']
        ),
        h(
          NPopconfirm,
          {
            onPositiveClick: () => handleDeletePlan(row.id)
          },
          {
            default: () => '确定要删除该回款计划吗？',
            trigger: () =>
              h(
                'button',
                { class: 'action-btn delete' },
                [h(NIcon, { size: 14 }, { default: () => h(TrashOutline) }), '删除']
              )
          }
        )
      ])
  }
]

// 加载统计数据
const loadStatistics = async () => {
  try {
    const data = await getPaymentStatistics()
    statistics.value = data
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const result = await pagePaymentPlans(searchParams)
    dataList.value = result.records
    pagination.itemCount = result.total
    pagination.page = result.current
  } catch (error) {
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  searchParams.pageNum = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchParams.status = undefined
  activeTab.value = null
  searchParams.pageNum = 1
  loadData()
}

// 标签切换
const handleTabChange = (status: number | null) => {
  activeTab.value = status
  searchParams.status = status || undefined
  searchParams.pageNum = 1
  loadData()
}

// 新增回款计划
const handleAddPlan = () => {
  planModalTitle.value = '新增回款计划'
  currentPlan.value = {}
  showPlanModal.value = true
}

// 编辑回款计划
const handleEditPlan = (row: PaymentPlan) => {
  planModalTitle.value = '编辑回款计划'
  currentPlan.value = { ...row }
  showPlanModal.value = true
}

// 删除回款计划
const handleDeletePlan = async (id: string) => {
  try {
    await deletePaymentPlan(id)
    message.success('删除成功')
    loadData()
    loadStatistics()
  } catch (error) {
    message.error('删除失败')
  }
}

// 回款计划提交
const handlePlanSubmit = () => {
  showPlanModal.value = false
  loadData()
  loadStatistics()
}

// 查看回款记录
const handleViewRecords = (planId: number | string) => {
  currentPlanId.value = planId
  showRecordsModal.value = true
}

// 列表中的添加回款记录（需设置 ID）
const handleTableAddRecord = (planId: number | string) => {
  currentPlanId.value = planId
  showRecordModal.value = true
}



// 回款记录提交
const handleRecordSubmit = () => {
  showRecordModal.value = false
  loadData()
  loadStatistics()
}

onMounted(() => {
  loadData()
  loadStatistics()
})
</script>

<style scoped>
/* ========================================
   回款管理页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.payment-page {
  width: 100%;
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ========================================
   页面标题
   ======================================== */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.header-actions {
  display: flex;
  gap: 12px;
}

.primary-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  background: #2563eb;
  border: none;
  transition: all 0.3s ease;
}

.primary-btn:hover {
  background: #1d4ed8;
  transform: translateY(-2px);
  box-shadow: 0 10px 40px rgba(37, 99, 235, 0.3);
}

/* ========================================
   统计卡片
   ======================================== */
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.stat-card:hover {
  border-color: transparent;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card.total .stat-icon {
  background: #dbeafe;
  color: #2563eb;
}

.stat-card.received .stat-icon {
  background: #dcfce7;
  color: #22c55e;
}

.stat-card.pending .stat-icon {
  background: #fef3c7;
  color: #f59e0b;
}

.stat-card.overdue .stat-icon {
  background: #fee2e2;
  color: #ef4444;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
}

/* ========================================
   筛选卡片
   ======================================== */
.filter-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 20px 24px;
}

.filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  width: 140px;
}

.filter-select :deep(.n-base-selection) {
  --n-height: 40px;
  --n-border-radius: 10px;
}

.filter-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  background: #2563eb;
  border: none;
  color: white;
}

.filter-btn:hover {
  background: #1d4ed8;
}

.reset-btn {
  height: 40px;
  color: #64748b;
}

.reset-btn:hover {
  color: #0f172a;
}

/* 状态标签 */
.status-tabs {
  display: flex;
  gap: 8px;
}

.status-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  background: #f1f5f9;
  border-radius: 8px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.status-tab:hover {
  background: #e2e8f0;
}

.status-tab.active {
  background: #2563eb;
  color: white;
}

.tab-count {
  font-size: 11px;
  padding: 2px 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

.status-tab:not(.active) .tab-count {
  background: #e2e8f0;
}

/* ========================================
   数据表格
   ======================================== */
.table-card {
  flex: 1;
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 400px;
}

.data-table {
  flex: 1;
}

.data-table :deep(.n-data-table-thead) {
  background: #f8fafc;
}

.data-table :deep(.n-data-table-th) {
  font-weight: 600;
  color: #475569;
  font-size: 13px;
  padding: 14px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.data-table :deep(.n-data-table-td) {
  padding: 14px 16px;
  font-size: 14px;
  color: #334155;
  border-bottom: 1px solid #f1f5f9;
}

.data-table :deep(.n-data-table-tr:hover .n-data-table-td) {
  background: #f8fafc;
}

/* 合同信息单元格 */
:deep(.contract-info-cell) {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

:deep(.contract-name) {
  font-weight: 500;
  color: #0f172a;
}

:deep(.contract-no) {
  font-size: 12px;
  color: #94a3b8;
}

:deep(.customer-name) {
  font-size: 12px;
  color: #64748b;
}

/* 期数文本 */
:deep(.period-text) {
  font-weight: 500;
  color: #475569;
}

/* 金额文本 */
:deep(.amount-text) {
  font-weight: 600;
  color: #2563eb;
}

/* 进度单元格 */
:deep(.progress-cell) {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

:deep(.progress-info) {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

:deep(.progress-info .actual) {
  color: #22c55e;
  font-weight: 500;
}

:deep(.progress-info .separator) {
  color: #94a3b8;
}

:deep(.progress-info .plan) {
  color: #64748b;
}

/* 状态标签 */
:deep(.status-tag) {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

/* 操作按钮 */
:deep(.action-buttons) {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
}

:deep(.action-btn) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

:deep(.action-btn.record) {
  color: #2563eb;
}

:deep(.action-btn.record:hover) {
  background: #dbeafe;
}

:deep(.action-btn.add-record) {
  color: #22c55e;
}

:deep(.action-btn.add-record:hover) {
  background: #dcfce7;
}

:deep(.action-btn.edit) {
  color: #f59e0b;
}

:deep(.action-btn.edit:hover) {
  background: #fef3c7;
}

:deep(.action-btn.delete) {
  color: #ef4444;
}

:deep(.action-btn.delete:hover) {
  background: #fee2e2;
}

/* 分页样式 */
.data-table :deep(.n-pagination) {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
}

/* ========================================
   弹窗样式
   ======================================== */
.form-modal {
  width: 600px;
}

.record-modal {
  width: 500px;
}

.records-modal {
  width: 700px;
}

/* ========================================
   响应式设计
   ======================================== */
@media (max-width: 1280px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
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

  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }

  .status-tabs {
    flex-wrap: wrap;
  }
}

/* ========================================
   减少动画 - 无障碍
   ======================================== */
@media (prefers-reduced-motion: reduce) {
  .stat-card:hover,
  .primary-btn:hover {
    transform: none;
  }
}
</style>
