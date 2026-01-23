<template>
  <div class="contract-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">合同管理</h1>
        <p class="page-subtitle">管理合同全生命周期，跟踪合同状态和回款进度</p>
      </div>
      <div class="header-actions">
        <n-button type="primary" @click="handleAdd" class="primary-btn">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新增合同
        </n-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="mini-stat" v-for="stat in miniStats" :key="stat.key">
        <div class="mini-stat-icon" :class="stat.class">
          <n-icon size="18" v-if="stat.icon">
            <component :is="stat.icon" />
          </n-icon>
        </div>
        <div class="mini-stat-info">
          <span class="mini-stat-value">{{ stat.value }}</span>
          <span class="mini-stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="search-box">
          <n-icon class="search-icon" size="18">
            <SearchOutline />
          </n-icon>
          <input
            v-model="searchParams.name"
            type="text"
            placeholder="搜索合同名称、编号..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="filter-group">
          <n-select
            v-model:value="searchParams.status"
            placeholder="状态"
            :options="statusOptions"
            clearable
            class="filter-select"
          />
          <n-date-picker
            v-model:value="signDateRange"
            type="daterange"
            clearable
            class="filter-date"
            start-placeholder="签约开始"
            end-placeholder="签约结束"
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
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <n-data-table
        :columns="columns"
        :data="dataList"
        :loading="loading"
        :pagination="pagination"
        :row-key="(row: Contract) => row.id"
        :bordered="false"
        :single-line="false"
        flex-height
        class="data-table"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :title="formTitle"
      preset="card"
      class="form-modal"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <contract-form
        ref="contractFormRef"
        :form-data="currentContract"
        @submit="handleFormSubmit"
        @cancel="showFormModal = false"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, computed, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import {
  NButton,
  NIcon,
  NSelect,
  NDatePicker,
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
  DocumentTextOutline,
  CheckmarkCircleOutline,
  TimeOutline,
  CashOutline,
  EyeOutline
} from '@vicons/ionicons5'
import {
  pageContracts,
  deleteContract,
  submitContractForApproval,
  approveContract,
  rejectContract,
  completeContract,
  terminateContract
} from '@/api/business/contract'
import type { Contract, ContractQueryParams } from '@/types/business/contract'
import {
  contractStatusOptions,
  ContractStatus,
  getContractStatusColor,
  getContractStatusName
} from '@/types/business/contract'
import ContractForm from '@/components/business/ContractForm.vue'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '全部合同', value: '0', icon: markRaw(DocumentTextOutline), class: 'blue' },
  { key: 'executing', label: '执行中', value: '0', icon: markRaw(TimeOutline), class: 'orange' },
  { key: 'completed', label: '已完成', value: '0', icon: markRaw(CheckmarkCircleOutline), class: 'green' },
  { key: 'amount', label: '合同总额', value: '¥0', icon: markRaw(CashOutline), class: 'purple' }
])

// 搜索参数
const searchParams = reactive<ContractQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: undefined
})

// 签约日期范围
const signDateRange = ref<[number, number] | null>(null)

// 状态选项
const statusOptions = contractStatusOptions

const loading = ref(false)
const dataList = ref<Contract[]>([])
const showFormModal = ref(false)
const formTitle = ref('新增合同')
const currentContract = ref<Partial<Contract>>({})
const contractFormRef = ref()

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
  if (!amount) return '¥0'
  return `¥${amount.toLocaleString()}`
}

// 获取状态标签
const getStatusTag = (status: number | undefined) => {
  if (!status) return null
  const color = getContractStatusColor(status)
  const name = getContractStatusName(status)
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
const getPaymentProgress = (contract: Contract) => {
  const total = contract.amount || 0
  const paid = contract.paidAmount || 0
  if (total === 0) return 0
  return Math.round((paid / total) * 100)
}

// 表格列配置
const columns: DataTableColumns<Contract> = [
  {
    title: '合同信息',
    key: 'name',
    width: 260,
    render: (row) =>
      h('div', { class: 'contract-info-cell' }, [
        h('div', { class: 'contract-main' }, [
          h(
            'span',
            {
              class: 'contract-name clickable',
              onClick: () => handleViewDetail(row.id)
            },
            row.name
          ),
          row.contractNo && h('span', { class: 'contract-no' }, row.contractNo)
        ]),
        h('div', { class: 'contract-customer' }, row.customerName || '-')
      ])
  },
  {
    title: '合同金额',
    key: 'amount',
    width: 140,
    render: (row) => h('span', { class: 'amount-text' }, formatAmount(row.amount))
  },
  {
    title: '回款进度',
    key: 'paymentProgress',
    width: 160,
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
          color: progress >= 100 ? '#22c55e' : '#2563eb'
        }),
        h('div', { class: 'progress-info' }, [
          h('span', { class: 'paid' }, formatAmount(row.paidAmount)),
          h('span', { class: 'separator' }, '/'),
          h('span', { class: 'total' }, formatAmount(row.amount))
        ])
      ])
    }
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) => getStatusTag(row.status)
  },
  {
    title: '签约日期',
    key: 'signDate',
    width: 120,
    render: (row) => {
      if (!row.signDate) return '-'
      const date = new Date(row.signDate)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }
  },
  {
    title: '负责人',
    key: 'ownerName',
    width: 100
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
            class: 'action-btn view',
            onClick: () => handleViewDetail(row.id)
          },
          [h(NIcon, { size: 14 }, { default: () => h(EyeOutline) }), '详情']
        ),
        h(
          'button',
          {
            class: 'action-btn edit',
            onClick: () => handleEdit(row)
          },
          [h(NIcon, { size: 14 }, { default: () => h(CreateOutline) }), '编辑']
        ),
        // 状态操作按钮
        ...getStatusActions(row),
        h(
          NPopconfirm,
          {
            onPositiveClick: () => handleDelete(row.id)
          },
          {
            default: () => '确定要删除该合同吗？',
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

// 获取状态操作按钮
const getStatusActions = (row: Contract) => {
  const actions: any[] = []

  if (row.status === ContractStatus.DRAFT) {
    actions.push(
      h(
        'button',
        {
          class: 'action-btn submit',
          onClick: () => handleSubmit(row.id)
        },
        '提交'
      )
    )
  }

  if (row.status === ContractStatus.PENDING) {
    actions.push(
      h(
        'button',
        {
          class: 'action-btn approve',
          onClick: () => handleApprove(row.id)
        },
        '通过'
      )
    )
    actions.push(
      h(
        'button',
        {
          class: 'action-btn reject',
          onClick: () => handleReject(row.id)
        },
        '驳回'
      )
    )
  }

  return actions
}

// 更新统计数据
const updateStats = () => {
  const total = pagination.itemCount || 0
  const executing = dataList.value.filter(c => c.status === ContractStatus.EXECUTING).length
  const completed = dataList.value.filter(c => c.status === ContractStatus.COMPLETED).length
  const totalAmount = dataList.value.reduce((sum, c) => sum + (c.amount || 0), 0)

  miniStats.value[0].value = String(total)
  miniStats.value[1].value = String(executing)
  miniStats.value[2].value = String(completed)
  miniStats.value[3].value = formatAmount(totalAmount)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    // 处理日期范围
    if (signDateRange.value) {
      searchParams.signDateStart = new Date(signDateRange.value[0]).toISOString().split('T')[0]
      searchParams.signDateEnd = new Date(signDateRange.value[1]).toISOString().split('T')[0]
    } else {
      searchParams.signDateStart = undefined
      searchParams.signDateEnd = undefined
    }

    const result = await pageContracts(searchParams)
    dataList.value = result.records
    pagination.itemCount = result.total
    pagination.page = result.current
    updateStats()
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
  searchParams.name = ''
  searchParams.status = undefined
  signDateRange.value = null
  searchParams.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  formTitle.value = '新增合同'
  currentContract.value = { status: ContractStatus.DRAFT }
  showFormModal.value = true
}

// 编辑
const handleEdit = (row: Contract) => {
  formTitle.value = '编辑合同'
  currentContract.value = { ...row }
  showFormModal.value = true
}

// 查看详情
const handleViewDetail = (id: string) => {
  router.push(`/business/contract/${id}`)
}

// 删除
const handleDelete = async (id: string) => {
  try {
    await deleteContract(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    message.error('删除失败')
  }
}

// 提交审批
const handleSubmit = async (id: string) => {
  try {
    await submitContractForApproval(id)
    message.success('提交成功')
    loadData()
  } catch (error) {
    message.error('提交失败')
  }
}

// 审批通过
const handleApprove = async (id: string) => {
  dialog.warning({
    title: '确认审批',
    content: '确定要通过该合同的审批吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await approveContract(id)
        message.success('审批通过')
        loadData()
      } catch (error) {
        message.error('操作失败')
      }
    }
  })
}

// 审批驳回
const handleReject = async (id: string) => {
  dialog.warning({
    title: '确认驳回',
    content: '确定要驳回该合同吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await rejectContract(id)
        message.success('已驳回')
        loadData()
      } catch (error) {
        message.error('操作失败')
      }
    }
  })
}

// 表单提交
const handleFormSubmit = () => {
  showFormModal.value = false
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* ========================================
   合同管理页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.contract-page {
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
   迷你统计卡片
   ======================================== */
.stats-row {
  display: flex;
  gap: 16px;
}

.mini-stat {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.mini-stat:hover {
  border-color: transparent;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
}

.mini-stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mini-stat-icon.blue {
  background: #dbeafe;
  color: #2563eb;
}

.mini-stat-icon.orange {
  background: #fef3c7;
  color: #f59e0b;
}

.mini-stat-icon.green {
  background: #dcfce7;
  color: #22c55e;
}

.mini-stat-icon.purple {
  background: #f3e8ff;
  color: #9333ea;
}

.mini-stat-info {
  display: flex;
  flex-direction: column;
}

.mini-stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.mini-stat-label {
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
  align-items: center;
  gap: 16px;
}

.search-box {
  flex: 1;
  max-width: 400px;
  position: relative;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
}

.search-input {
  width: 100%;
  height: 42px;
  padding: 0 16px 0 44px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  color: #0f172a;
  background: #f8fafc;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #2563eb;
  background: white;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.search-input::placeholder {
  color: #94a3b8;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  width: 120px;
}

.filter-select :deep(.n-base-selection) {
  --n-height: 42px;
  --n-border-radius: 10px;
}

.filter-date {
  width: 260px;
}

.filter-date :deep(.n-input) {
  --n-height: 42px;
  --n-border-radius: 10px;
}

.filter-btn {
  height: 42px;
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
  height: 42px;
  color: #64748b;
}

.reset-btn:hover {
  color: #0f172a;
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
  gap: 4px;
}

:deep(.contract-main) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.contract-name) {
  font-weight: 500;
  color: #0f172a;
}

:deep(.contract-name.clickable) {
  cursor: pointer;
  color: #2563eb;
}

:deep(.contract-name.clickable:hover) {
  text-decoration: underline;
}

:deep(.contract-no) {
  font-size: 12px;
  color: #94a3b8;
}

:deep(.contract-customer) {
  font-size: 12px;
  color: #64748b;
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

:deep(.progress-info .paid) {
  color: #22c55e;
  font-weight: 500;
}

:deep(.progress-info .separator) {
  color: #94a3b8;
}

:deep(.progress-info .total) {
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

:deep(.action-btn.view) {
  color: #2563eb;
}

:deep(.action-btn.view:hover) {
  background: #dbeafe;
}

:deep(.action-btn.edit) {
  color: #f59e0b;
}

:deep(.action-btn.edit:hover) {
  background: #fef3c7;
}

:deep(.action-btn.submit) {
  color: #2563eb;
}

:deep(.action-btn.submit:hover) {
  background: #dbeafe;
}

:deep(.action-btn.approve) {
  color: #22c55e;
}

:deep(.action-btn.approve:hover) {
  background: #dcfce7;
}

:deep(.action-btn.reject) {
  color: #ef4444;
}

:deep(.action-btn.reject:hover) {
  background: #fee2e2;
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
  width: 700px;
}

.form-modal :deep(.n-card-header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
}

.form-modal :deep(.n-card-header__main) {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

/* ========================================
   响应式设计
   ======================================== */
@media (max-width: 1280px) {
  .stats-row {
    flex-wrap: wrap;
  }

  .mini-stat {
    flex: 1 1 calc(50% - 8px);
    min-width: 180px;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .stats-row {
    flex-direction: column;
  }

  .mini-stat {
    flex: none;
    width: 100%;
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box {
    max-width: none;
  }

  .filter-group {
    flex-wrap: wrap;
  }

  .filter-select {
    flex: 1;
    min-width: 100px;
  }

  .filter-date {
    width: 100%;
  }
}

/* ========================================
   减少动画 - 无障碍
   ======================================== */
@media (prefers-reduced-motion: reduce) {
  .mini-stat:hover,
  .primary-btn:hover {
    transform: none;
  }
}
</style>
