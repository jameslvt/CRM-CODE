<template>
  <div class="opportunity-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">商机管理</h1>
        <p class="page-subtitle">跟踪销售机会，推进商机阶段，提升成交转化率</p>
      </div>
      <div class="header-actions">
        <n-button type="primary" @click="handleAdd" class="primary-btn">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新增商机
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
            placeholder="搜索商机名称..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="filter-group">
          <n-select
            v-model:value="searchParams.stage"
            placeholder="商机阶段"
            :options="stageOptions"
            clearable
            class="filter-select"
          />
          <n-select
            v-model:value="searchParams.source"
            placeholder="商机来源"
            :options="sourceOptions"
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
      </div>

      <!-- 批量操作 -->
      <div v-if="selectedRowKeys.length > 0" class="batch-actions">
        <span class="selected-count">已选择 {{ selectedRowKeys.length }} 项</span>
        <n-button size="small" type="error" @click="handleBatchDelete">
          <template #icon>
            <n-icon><TrashOutline /></n-icon>
          </template>
          批量删除
        </n-button>
        <n-button size="small" quaternary @click="selectedRowKeys = []">
          取消选择
        </n-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <n-data-table
        :columns="columns"
        :data="dataList"
        :loading="loading"
        :pagination="pagination"
        :row-key="(row: Opportunity) => row.id"
        :checked-row-keys="selectedRowKeys"
        @update:checked-row-keys="handleCheck"
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
      <opportunity-form
        ref="opportunityFormRef"
        :form-data="currentOpportunity"
        @submit="handleFormSubmit"
        @cancel="showFormModal = false"
      />
    </n-modal>

    <!-- 阶段推进弹窗 -->
    <n-modal
      v-model:show="showStageModal"
      title="推进商机阶段"
      preset="card"
      class="stage-modal"
    >
      <stage-progress
        v-if="stageOpportunity"
        :opportunity="stageOpportunity"
        @advance="handleStageAdvance"
        @cancel="showStageModal = false"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, markRaw } from 'vue'
import {
  NButton,
  NIcon,
  NSelect,
  NPopconfirm,
  useMessage,
  useDialog,
  type DataTableColumns,
  type PaginationProps
} from 'naive-ui'
import {
  AddOutline,
  SearchOutline,
  CreateOutline,
  EyeOutline,
  TrashOutline,
  CashOutline,
  CheckmarkCircleOutline,
  CloseCircleOutline,
  RocketOutline,
  ArrowForwardOutline
} from '@vicons/ionicons5'
import { useRouter } from 'vue-router'
import { pageOpportunities, deleteOpportunity, advanceStage } from '@/api/business/opportunity'
import type { Opportunity, OpportunityQueryParams, StageAdvanceDTO } from '@/types/business/opportunity'
import { useOpportunityStore } from '@/stores/opportunity'
import OpportunityForm from '@/components/business/OpportunityForm.vue'
import StageProgress from '@/components/business/StageProgress.vue'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()
const opportunityStore = useOpportunityStore()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '全部商机', value: '0', icon: markRaw(RocketOutline), class: 'blue' },
  { key: 'amount', label: '预计金额', value: '¥0', icon: markRaw(CashOutline), class: 'green' },
  { key: 'won', label: '已赢单', value: '0', icon: markRaw(CheckmarkCircleOutline), class: 'success' },
  { key: 'lost', label: '已输单', value: '0', icon: markRaw(CloseCircleOutline), class: 'gray' }
])

// 搜索参数
const searchParams = reactive<OpportunityQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  stage: undefined,
  source: undefined
})

// 阶段选项
const stageOptions = [
  { label: '需求确认', value: 'REQUIREMENT' },
  { label: '方案报价', value: 'PROPOSAL' },
  { label: '商务谈判', value: 'NEGOTIATION' },
  { label: '赢单', value: 'WON' },
  { label: '输单', value: 'LOST' }
]

// 来源选项
const sourceOptions = [
  { label: '线索转化', value: 'LEAD' },
  { label: '客户推荐', value: 'REFERRAL' },
  { label: '市场活动', value: 'MARKETING' },
  { label: '官网咨询', value: 'WEBSITE' },
  { label: '其他', value: 'OTHER' }
]

const loading = ref(false)
const dataList = ref<Opportunity[]>([])
const selectedRowKeys = ref<number[]>([])
const showFormModal = ref(false)
const showStageModal = ref(false)
const formTitle = ref('新增商机')
const currentOpportunity = ref<Partial<Opportunity>>({})
const stageOpportunity = ref<Opportunity | null>(null)
const opportunityFormRef = ref()

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

// 阶段颜色映射
const stageColorMap: Record<string, { bg: string; color: string }> = {
  REQUIREMENT: { bg: '#f1f5f9', color: '#64748b' },
  PROPOSAL: { bg: '#dbeafe', color: '#2563eb' },
  NEGOTIATION: { bg: '#fef3c7', color: '#f59e0b' },
  WON: { bg: '#dcfce7', color: '#22c55e' },
  LOST: { bg: '#fee2e2', color: '#ef4444' }
}

// 获取阶段标签
const getStageTag = (stage: string | undefined) => {
  if (!stage) return h('span', { class: 'stage-tag', style: { background: '#f1f5f9', color: '#64748b' } }, '-')
  const config = stageColorMap[stage] || stageColorMap.REQUIREMENT
  const stageName = opportunityStore.getStageName(stage)
  return h(
    'span',
    {
      class: 'stage-tag',
      style: {
        background: config.bg,
        color: config.color
      }
    },
    stageName
  )
}

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (!amount) return '-'
  return `¥${amount.toLocaleString()}`
}

// 表格列配置
const columns: DataTableColumns<Opportunity> = [
  { type: 'selection', width: 48 },
  {
    title: '商机名称',
    key: 'name',
    width: 220,
    ellipsis: { tooltip: true },
    render: (row) =>
      h('div', { class: 'opportunity-name-cell' }, [
        h('span', { class: 'opportunity-name' }, row.name),
        row.customerName && h('span', { class: 'customer-name' }, row.customerName)
      ])
  },
  {
    title: '阶段',
    key: 'stage',
    width: 100,
    render: (row) => getStageTag(row.stage)
  },
  {
    title: '预计金额',
    key: 'amount',
    width: 120,
    render: (row) => h('span', { class: 'amount-text' }, formatAmount(row.amount))
  },
  {
    title: '赢单概率',
    key: 'probability',
    width: 100,
    render: (row) =>
      h('div', { class: 'probability-cell' }, [
        h('div', { class: 'probability-bar' }, [
          h('div', {
            class: 'probability-fill',
            style: { width: `${row.probability || 0}%` }
          })
        ]),
        h('span', { class: 'probability-text' }, `${row.probability || 0}%`)
      ])
  },
  {
    title: '预计成交',
    key: 'expectedDate',
    width: 110,
    render: (row) => {
      if (!row.expectedDate) return '-'
      const date = new Date(row.expectedDate)
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
    title: '创建时间',
    key: 'createTime',
    width: 160,
    render: (row) => {
      if (!row.createTime) return '-'
      const date = new Date(row.createTime)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
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
            onClick: () => handleView(row.id)
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
        row.stage !== 'WON' && row.stage !== 'LOST'
          ? h(
              'button',
              {
                class: 'action-btn advance',
                onClick: () => handleAdvanceStage(row)
              },
              [h(NIcon, { size: 14 }, { default: () => h(ArrowForwardOutline) }), '推进']
            )
          : null,
        h(
          NPopconfirm,
          {
            onPositiveClick: () => handleDelete(row.id)
          },
          {
            default: () => '确定要删除该商机吗？',
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

// 更新统计数据
const updateStats = () => {
  const total = pagination.itemCount || 0
  const totalAmount = dataList.value.reduce((sum, o) => sum + (o.amount || 0), 0)
  const wonCount = dataList.value.filter(o => o.stage === 'WON').length
  const lostCount = dataList.value.filter(o => o.stage === 'LOST').length

  miniStats.value[0].value = String(total)
  miniStats.value[1].value = `¥${totalAmount.toLocaleString()}`
  miniStats.value[2].value = String(wonCount)
  miniStats.value[3].value = String(lostCount)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const result = await pageOpportunities(searchParams)
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
  searchParams.stage = undefined
  searchParams.source = undefined
  searchParams.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  formTitle.value = '新增商机'
  currentOpportunity.value = {}
  showFormModal.value = true
}

// 编辑
const handleEdit = (row: Opportunity) => {
  formTitle.value = '编辑商机'
  currentOpportunity.value = { ...row }
  showFormModal.value = true
}

// 查看详情
const handleView = (id: string) => {
  router.push(`/business/opportunity/${id}`)
}

// 删除
const handleDelete = async (id: string) => {
  try {
    await deleteOpportunity(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    message.error('删除失败')
  }
}

// 批量删除
const handleBatchDelete = () => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个商机吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        for (const id of selectedRowKeys.value) {
          await deleteOpportunity(id)
        }
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadData()
      } catch (error) {
        message.error('批量删除失败')
      }
    }
  })
}

// 推进阶段
const handleAdvanceStage = (row: Opportunity) => {
  stageOpportunity.value = row
  showStageModal.value = true
}

// 阶段推进提交
const handleStageAdvance = async (data: StageAdvanceDTO) => {
  try {
    await advanceStage(data)
    message.success('阶段推进成功')
    showStageModal.value = false
    loadData()
  } catch (error) {
    message.error('阶段推进失败')
  }
}

// 表单提交
const handleFormSubmit = () => {
  showFormModal.value = false
  loadData()
}

// 选择行
const handleCheck = (keys: number[]) => {
  selectedRowKeys.value = keys
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* ========================================
   商机管理页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.opportunity-page {
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

.mini-stat-icon.green {
  background: #dcfce7;
  color: #22c55e;
}

.mini-stat-icon.success {
  background: #d1fae5;
  color: #10b981;
}

.mini-stat-icon.gray {
  background: #f1f5f9;
  color: #64748b;
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
  width: 140px;
}

.filter-select :deep(.n-base-selection) {
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

/* 批量操作 */
.batch-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.selected-count {
  font-size: 14px;
  color: #2563eb;
  font-weight: 500;
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

/* 商机名称单元格 */
:deep(.opportunity-name-cell) {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

:deep(.opportunity-name) {
  font-weight: 500;
  color: #0f172a;
}

:deep(.customer-name) {
  font-size: 12px;
  color: #94a3b8;
}

/* 阶段标签 */
:deep(.stage-tag) {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

/* 金额文本 */
:deep(.amount-text) {
  font-weight: 600;
  color: #0f172a;
}

/* 概率进度条 */
:deep(.probability-cell) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.probability-bar) {
  flex: 1;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

:deep(.probability-fill) {
  height: 100%;
  background: linear-gradient(90deg, #2563eb, #3b82f6);
  border-radius: 3px;
  transition: width 0.3s ease;
}

:deep(.probability-text) {
  font-size: 12px;
  color: #64748b;
  min-width: 36px;
}

/* 操作按钮 */
:deep(.action-buttons) {
  display: flex;
  align-items: center;
  gap: 4px;
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

:deep(.action-btn.advance) {
  color: #22c55e;
}

:deep(.action-btn.advance:hover) {
  background: #dcfce7;
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
  width: 800px;
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

.stage-modal {
  width: 560px;
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
    min-width: 120px;
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
