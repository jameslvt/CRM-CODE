<template>
  <div class="lead-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">线索管理</h1>
        <p class="page-subtitle">管理和跟踪所有销售线索，推动业务增长</p>
      </div>
      <div class="header-actions">
        <n-button @click="handleImport" class="secondary-btn">
          <template #icon>
            <n-icon><CloudUploadOutline /></n-icon>
          </template>
          导入
        </n-button>
        <n-button @click="handleExport" class="secondary-btn">
          <template #icon>
            <n-icon><DownloadOutline /></n-icon>
          </template>
          导出
        </n-button>
        <n-button type="primary" @click="handleAdd" class="primary-btn">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新增线索
        </n-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="mini-stat" v-for="stat in miniStats" :key="stat.key">
        <div class="mini-stat-icon" :class="stat.class">
          <n-icon size="18">
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
            v-model="searchParams.keyword"
            type="text"
            placeholder="搜索线索名称、联系人..."
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
        :row-key="(row: Lead) => row.id"
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
      <lead-form
        ref="leadFormRef"
        :form-data="currentLead"
        @submit="handleFormSubmit"
        @cancel="showFormModal = false"
      />
    </n-modal>

    <!-- 转化为客户弹窗 -->
    <n-modal
      v-model:show="showConvertModal"
      title="转化为客户"
      preset="card"
      class="convert-modal"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <lead-convert-dialog
        ref="convertDialogRef"
        :lead-id="convertLeadId"
        @submit="handleConvertSubmit"
        @cancel="showConvertModal = false"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, markRaw } from 'vue'
import {
  NButton,
  NIcon,
  NTag,
  NSpace,
  NPopconfirm,
  NSelect,
  useMessage,
  useDialog,
  type DataTableColumns,
  type PaginationProps
} from 'naive-ui'
import {
  AddOutline,
  SearchOutline,
  TrashOutline,
  DownloadOutline,
  CloudUploadOutline,
  CreateOutline,
  SwapHorizontalOutline,
  EyeOutline,
  PersonOutline,
  CheckmarkCircleOutline,
  TimeOutline,
  CloseCircleOutline
} from '@vicons/ionicons5'
import { useRouter } from 'vue-router'
import { pageLeads, deleteLead, batchDeleteLeads, getLeadStats } from '@/api/business/lead'
import type { Lead, LeadQueryParams } from '@/types/business'
import LeadForm from '@/components/business/LeadForm.vue'
import LeadConvertDialog from '@/components/business/LeadConvertDialog.vue'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '全部线索', value: '0', icon: markRaw(PersonOutline), class: 'blue' },
  { key: 'new', label: '新建', value: '0', icon: markRaw(TimeOutline), class: 'purple' },
  { key: 'following', label: '跟进中', value: '0', icon: markRaw(TimeOutline), class: 'orange' },
  { key: 'converted', label: '已转化', value: '0', icon: markRaw(CheckmarkCircleOutline), class: 'green' },
  { key: 'invalid', label: '已失效', value: '0', icon: markRaw(CloseCircleOutline), class: 'gray' }
])

// 搜索参数 - 字段名与后端 LeadQueryParams 保持一致
const searchParams = reactive<LeadQueryParams>({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: undefined
})

// 状态选项 (后端使用数字: 1-新建, 2-跟进中, 3-已转化, 4-已关闭)
const statusOptions = [
  { label: '新建', value: 1 },
  { label: '跟进中', value: 2 },
  { label: '已转化', value: 3 },
  { label: '已失效', value: 4 }
]

// 评级选项
const ratingOptions = [
  { label: 'A级 - 高', value: 'A' },
  { label: 'B级 - 中', value: 'B' },
  { label: 'C级 - 低', value: 'C' }
]

const loading = ref(false)
const dataList = ref<Lead[]>([])
const selectedRowKeys = ref<number[]>([])
const showFormModal = ref(false)
const showConvertModal = ref(false)
const formTitle = ref('新增线索')
const currentLead = ref<Partial<Lead>>({})
const convertLeadId = ref<number>(0)
const leadFormRef = ref()
const convertDialogRef = ref()

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

// 状态颜色映射 (使用数字键)
const statusColorMap: Record<number, { type: string; bg: string; color: string }> = {
  1: { type: 'info', bg: '#dbeafe', color: '#2563eb' },      // 新建
  2: { type: 'warning', bg: '#fef3c7', color: '#f59e0b' },   // 跟进中
  3: { type: 'success', bg: '#dcfce7', color: '#22c55e' },   // 已转化
  4: { type: 'default', bg: '#f1f5f9', color: '#64748b' }    // 已失效
}

// 评级颜色映射
const ratingColorMap: Record<string, { bg: string; color: string }> = {
  A: { bg: '#fee2e2', color: '#ef4444' },
  B: { bg: '#fef3c7', color: '#f59e0b' },
  C: { bg: '#dbeafe', color: '#3b82f6' }
}

// 获取状态标签
const getStatusTag = (status: number) => {
  const config = statusColorMap[status] || statusColorMap[4]
  const labels: Record<number, string> = {
    1: '新建',
    2: '跟进中',
    3: '已转化',
    4: '已失效'
  }
  return h(
    'span',
    {
      class: 'status-tag',
      style: {
        background: config.bg,
        color: config.color
      }
    },
    labels[status] || String(status)
  )
}

// 获取评级标签
const getRatingTag = (rating: string) => {
  const config = ratingColorMap[rating] || ratingColorMap.C
  const labels: Record<string, string> = { A: 'A级', B: 'B级', C: 'C级' }
  return h(
    'span',
    {
      class: 'rating-tag',
      style: {
        background: config.bg,
        color: config.color
      }
    },
    labels[rating] || rating
  )
}

// 表格列配置
const columns: DataTableColumns<Lead> = [
  { type: 'selection', width: 48 },
  {
    title: '线索名称',
    key: 'name',
    width: 200,
    ellipsis: { tooltip: true },
    render: (row) =>
      h('div', { class: 'lead-name-cell' }, [
        h('span', { class: 'lead-name' }, row.name),
        h('span', { class: 'lead-company' }, row.company)
      ])
  },
  {
    title: '联系电话',
    key: 'phone',
    width: 120
  },
  {
    title: '邮箱',
    key: 'email',
    width: 150,
    ellipsis: { tooltip: true }
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) => getStatusTag(row.status)
  },
  {
    title: '评级',
    key: 'rating',
    width: 80,
    render: (row) => getRatingTag(row.rating || '')
  },
  {
    title: '来源',
    key: 'source',
    width: 100
  },
  {
    title: '负责人',
    key: 'ownerName',
    width: 100,
    render: (row) => row.ownerName || '-'
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
          [h(NIcon, { size: 16 }, { default: () => h(EyeOutline) }), '查看']
        ),
        h(
          'button',
          {
            class: 'action-btn edit',
            onClick: () => handleEdit(row)
          },
          [h(NIcon, { size: 16 }, { default: () => h(CreateOutline) }), '编辑']
        ),
        row.status !== 3 &&
          h(
            'button',
            {
              class: 'action-btn convert',
              onClick: () => handleConvert(row.id)
            },
            [h(NIcon, { size: 16 }, { default: () => h(SwapHorizontalOutline) }), '转化']
          ),
        h(
          NPopconfirm,
          {
            onPositiveClick: () => handleDelete(row.id)
          },
          {
            default: () => '确定要删除该线索吗？',
            trigger: () =>
              h(
                'button',
                { class: 'action-btn delete' },
                [h(NIcon, { size: 16 }, { default: () => h(TrashOutline) }), '删除']
              )
          }
        )
      ])
  }
]

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const [pageResult, statsResult] = await Promise.all([
      pageLeads(searchParams),
      getLeadStats()
    ])
    
    // 更新列表
    dataList.value = pageResult.records
    pagination.itemCount = pageResult.total
    pagination.page = pageResult.current
    
    // 更新统计
    miniStats.value[0].value = statsResult.total?.toString() || '0'
    miniStats.value[1].value = statsResult.new?.toString() || '0'
    miniStats.value[2].value = statsResult.following?.toString() || '0'
    miniStats.value[3].value = statsResult.converted?.toString() || '0'
    miniStats.value[4].value = statsResult.invalid?.toString() || '0'
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
  searchParams.keyword = ''
  searchParams.status = undefined
  searchParams.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  formTitle.value = '新增线索'
  currentLead.value = {}
  showFormModal.value = true
}

// 编辑
const handleEdit = (row: Lead) => {
  formTitle.value = '编辑线索'
  currentLead.value = { ...row }
  showFormModal.value = true
}

// 查看详情
const handleView = (id: string) => {
  router.push(`/business/lead/${id}`)
}

// 删除
const handleDelete = async (id: string) => {
  try {
    await deleteLead(id)
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
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 条线索吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await batchDeleteLeads(selectedRowKeys.value)
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadData()
      } catch (error) {
        message.error('批量删除失败')
      }
    }
  })
}

// 转化为客户
const handleConvert = (id: string) => {
  convertLeadId.value = id
  showConvertModal.value = true
}

// 导出
const handleExport = () => {
  message.info('导出功能开发中...')
}

// 导入
const handleImport = () => {
  message.info('导入功能开发中...')
}

// 表单提交
const handleFormSubmit = () => {
  showFormModal.value = false
  loadData()
}

// 转化提交
const handleConvertSubmit = () => {
  showConvertModal.value = false
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
   线索管理页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.lead-page {
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

.secondary-btn {
  height: 40px;
  padding: 0 16px;
  border-radius: 10px;
  font-weight: 500;
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.3s ease;
}

.secondary-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
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

.mini-stat-icon.purple {
  background: #f3e8ff;
  color: #9333ea;
}

.mini-stat-icon.orange {
  background: #fef3c7;
  color: #f59e0b;
}

.mini-stat-icon.green {
  background: #dcfce7;
  color: #22c55e;
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

/* 线索名称单元格 */
.lead-name-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.lead-name {
  font-weight: 500;
  color: #0f172a;
}

.lead-company {
  font-size: 12px;
  color: #94a3b8;
}

/* 联系人单元格 */
.contact-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.contact-name {
  color: #334155;
}

.contact-phone {
  font-size: 12px;
  color: #94a3b8;
}

/* 状态标签 */
:deep(.status-tag) {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

/* 评级标签 */
:deep(.rating-tag) {
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

:deep(.action-btn.convert) {
  color: #22c55e;
}

:deep(.action-btn.convert:hover) {
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

.convert-modal {
  width: 600px;
}

/* ========================================
   响应式设计
   ======================================== */
@media (max-width: 1280px) {
  .stats-row {
    flex-wrap: wrap;
  }

  .mini-stat {
    flex: 1 1 calc(33.333% - 12px);
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
