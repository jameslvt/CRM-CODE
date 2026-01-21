<template>
  <div class="pool-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">公海池</h1>
        <p class="page-subtitle">未分配的客户资源，可自由领取跟进</p>
      </div>
      <div class="header-actions">
        <n-button @click="handleBack" class="secondary-btn">
          <template #icon>
            <n-icon><ArrowBackOutline /></n-icon>
          </template>
          返回客户列表
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
            v-model="searchParams.name"
            type="text"
            placeholder="搜索客户名称..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="filter-group">
          <n-select
            v-model:value="searchParams.industry"
            placeholder="行业"
            :options="industryOptions"
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
        <n-button size="small" type="primary" @click="handleBatchAcquire">
          <template #icon>
            <n-icon><DownloadOutline /></n-icon>
          </template>
          批量领取
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
        :row-key="(row: Customer) => row.id"
        :checked-row-keys="selectedRowKeys"
        @update:checked-row-keys="handleCheck"
        :bordered="false"
        :single-line="false"
        flex-height
        class="data-table"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NButton,
  NIcon,
  NSelect,
  useMessage,
  useDialog,
  type DataTableColumns,
  type PaginationProps
} from 'naive-ui'
import {
  ArrowBackOutline,
  SearchOutline,
  DownloadOutline,
  EyeOutline,
  GlobeOutline,
  BusinessOutline,
  TimeOutline
} from '@vicons/ionicons5'
import { useRouter } from 'vue-router'
import { pageCustomers, acquireFromPool } from '@/api/business/customer'
import type { Customer, CustomerQueryParams } from '@/types/business/customer'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '公海客户总数', value: '0', icon: GlobeOutline, class: 'blue' },
  { key: 'today', label: '今日新增', value: '0', icon: TimeOutline, class: 'green' },
  { key: 'week', label: '本周新增', value: '0', icon: BusinessOutline, class: 'orange' }
])

// 搜索参数
const searchParams = reactive<CustomerQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  industry: undefined,
  status: 2, // 公海客户状态
  isPublicPool: true
})

// 行业选项
const industryOptions = [
  { label: '互联网/IT', value: '互联网/IT' },
  { label: '金融', value: '金融' },
  { label: '制造业', value: '制造业' },
  { label: '零售', value: '零售' },
  { label: '教育', value: '教育' },
  { label: '医疗', value: '医疗' },
  { label: '其他', value: '其他' }
]

const loading = ref(false)
const dataList = ref<Customer[]>([])
const selectedRowKeys = ref<number[]>([])

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

// 级别颜色映射
const levelColorMap: Record<string, { bg: string; color: string }> = {
  A: { bg: '#fee2e2', color: '#ef4444' },
  B: { bg: '#fef3c7', color: '#f59e0b' },
  C: { bg: '#dbeafe', color: '#3b82f6' },
  D: { bg: '#f1f5f9', color: '#64748b' }
}

// 获取级别标签
const getLevelTag = (level: string | undefined) => {
  if (!level) return h('span', { class: 'level-tag', style: { background: '#f1f5f9', color: '#64748b' } }, '-')
  const config = levelColorMap[level] || levelColorMap.D
  return h(
    'span',
    {
      class: 'level-tag',
      style: {
        background: config.bg,
        color: config.color
      }
    },
    `${level}级`
  )
}

// 表格列配置
const columns: DataTableColumns<Customer> = [
  { type: 'selection', width: 48 },
  {
    title: '客户名称',
    key: 'name',
    width: 220,
    ellipsis: { tooltip: true },
    render: (row) =>
      h('div', { class: 'customer-name-cell' }, [
        h('span', { class: 'customer-name' }, row.name),
        row.code && h('span', { class: 'customer-code' }, row.code)
      ])
  },
  {
    title: '级别',
    key: 'level',
    width: 80,
    render: (row) => getLevelTag(row.level)
  },
  {
    title: '行业',
    key: 'industry',
    width: 120
  },
  {
    title: '规模',
    key: 'scale',
    width: 80
  },
  {
    title: '联系电话',
    key: 'phone',
    width: 140
  },
  {
    title: '释放时间',
    key: 'updateTime',
    width: 160
  },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    fixed: 'right',
    render: (row) =>
      h('div', { class: 'action-buttons' }, [
        h(
          'button',
          {
            class: 'action-btn view',
            onClick: () => handleView(row.id)
          },
          [h(NIcon, { size: 14 }, { default: () => h(EyeOutline) }), '查看']
        ),
        h(
          'button',
          {
            class: 'action-btn acquire',
            onClick: () => handleAcquire(row.id)
          },
          [h(NIcon, { size: 14 }, { default: () => h(DownloadOutline) }), '领取']
        )
      ])
  }
]

// 更新统计数据
const updateStats = () => {
  const total = pagination.itemCount || 0
  miniStats.value[0].value = String(total)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const result = await pageCustomers(searchParams)
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
  searchParams.industry = undefined
  searchParams.pageNum = 1
  loadData()
}

// 返回客户列表
const handleBack = () => {
  router.push('/business/customer')
}

// 查看详情
const handleView = (id: number) => {
  router.push(`/business/customer/${id}`)
}

// 领取客户
const handleAcquire = async (id: number) => {
  try {
    await acquireFromPool(id)
    message.success('领取成功')
    loadData()
  } catch (error) {
    message.error('领取失败')
  }
}

// 批量领取
const handleBatchAcquire = () => {
  dialog.info({
    title: '确认领取',
    content: `确定要领取选中的 ${selectedRowKeys.value.length} 个客户吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        for (const id of selectedRowKeys.value) {
          await acquireFromPool(id)
        }
        message.success('批量领取成功')
        selectedRowKeys.value = []
        loadData()
      } catch (error) {
        message.error('批量领取失败')
      }
    }
  })
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
   公海池页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.pool-page {
  width: 100%;
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 页面标题 */
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

/* 迷你统计卡片 */
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

.mini-stat-icon.orange {
  background: #fef3c7;
  color: #f59e0b;
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

/* 筛选卡片 */
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

/* 数据表格 */
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

/* 客户名称单元格 */
.customer-name-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.customer-name {
  font-weight: 500;
  color: #0f172a;
}

.customer-code {
  font-size: 12px;
  color: #94a3b8;
}

/* 级别标签 */
:deep(.level-tag) {
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

:deep(.action-btn.acquire) {
  color: #22c55e;
}

:deep(.action-btn.acquire:hover) {
  background: #dcfce7;
}

/* 分页样式 */
.data-table :deep(.n-pagination) {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
}

/* 响应式设计 */
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

/* 减少动画 - 无障碍 */
@media (prefers-reduced-motion: reduce) {
  .mini-stat:hover {
    transform: none;
  }
}
</style>
