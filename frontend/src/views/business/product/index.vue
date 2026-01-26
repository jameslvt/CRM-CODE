<template>
  <div class="product-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">产品目录</h1>
        <p class="page-subtitle">管理产品目录，维护产品信息和定价</p>
      </div>
      <div class="header-actions">
        <n-button type="primary" @click="handleAdd" class="primary-btn">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新增产品
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
            placeholder="搜索产品名称、编码..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="filter-group">
          <n-select
            v-model:value="searchParams.category"
            placeholder="产品分类"
            :options="categoryOptions"
            clearable
            class="filter-select"
          />
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
        :row-key="(row: Product) => row.id"
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
      <product-form
        ref="productFormRef"
        :form-data="currentProduct"
        @submit="handleFormSubmit"
        @cancel="showFormModal = false"
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
  NSwitch,
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
  TrashOutline,
  CubeOutline,
  CheckmarkCircleOutline,
  CloseCircleOutline,
  PricetagOutline
} from '@vicons/ionicons5'
import {
  pageProducts,
  deleteProduct,
  batchDeleteProducts,
  enableProduct,
  disableProduct
} from '@/api/business/product'
import type { Product, ProductQueryParams } from '@/types/business/product'
import { productCategoryOptions, productStatusOptions } from '@/types/business/product'
import ProductForm from '@/components/business/ProductForm.vue'

const message = useMessage()
const dialog = useDialog()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '全部产品', value: '0', icon: markRaw(CubeOutline), class: 'blue' },
  { key: 'enabled', label: '已启用', value: '0', icon: markRaw(CheckmarkCircleOutline), class: 'green' },
  { key: 'disabled', label: '已停用', value: '0', icon: markRaw(CloseCircleOutline), class: 'gray' }
])

// 搜索参数
const searchParams = reactive<ProductQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  category: undefined,
  status: undefined
})

// 分类选项
const categoryOptions = productCategoryOptions

// 状态选项
const statusOptions = productStatusOptions

const loading = ref(false)
const dataList = ref<Product[]>([])
const selectedRowKeys = ref<number[]>([])
const showFormModal = ref(false)
const formTitle = ref('新增产品')
const currentProduct = ref<Partial<Product>>({})
const productFormRef = ref()

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
const formatPrice = (price: number | undefined) => {
  if (!price) return '-'
  return `¥${price.toLocaleString()}`
}

// 获取状态标签
const getStatusTag = (status: number | undefined) => {
  if (status === 1) {
    return h(
      'span',
      {
        class: 'status-tag enabled',
        style: { background: '#dcfce7', color: '#22c55e' }
      },
      '启用'
    )
  }
  return h(
    'span',
    {
      class: 'status-tag disabled',
      style: { background: '#f1f5f9', color: '#64748b' }
    },
    '停用'
  )
}

// 表格列配置
const columns: DataTableColumns<Product> = [
  { type: 'selection', width: 48 },
  {
    title: '产品名称',
    key: 'name',
    width: 220,
    ellipsis: { tooltip: true },
    render: (row) =>
      h('div', { class: 'product-name-cell' }, [
        h('span', { class: 'product-name' }, row.name),
        row.code && h('span', { class: 'product-code' }, row.code)
      ])
  },
  {
    title: '分类',
    key: 'category',
    width: 120
  },
  {
    title: '单价',
    key: 'price',
    width: 120,
    render: (row) => h('span', { class: 'price-text' }, formatPrice(row.price))
  },
  {
    title: '单位',
    key: 'unit',
    width: 80
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) => getStatusTag(row.status)
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
            class: 'action-btn edit',
            onClick: () => handleEdit(row)
          },
          [h(NIcon, { size: 14 }, { default: () => h(CreateOutline) }), '编辑']
        ),
        row.status === 1
          ? h(
              'button',
              {
                class: 'action-btn disable',
                onClick: () => handleToggleStatus(row.id, false)
              },
              '停用'
            )
          : h(
              'button',
              {
                class: 'action-btn enable',
                onClick: () => handleToggleStatus(row.id, true)
              },
              '启用'
            ),
        h(
          NPopconfirm,
          {
            onPositiveClick: () => handleDelete(row.id)
          },
          {
            default: () => '确定要删除该产品吗？',
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
  const enabled = dataList.value.filter(p => p.status === 1).length
  const disabled = dataList.value.filter(p => p.status === 0).length

  miniStats.value[0].value = String(total)
  miniStats.value[1].value = String(enabled)
  miniStats.value[2].value = String(disabled)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const result = await pageProducts(searchParams)
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
  searchParams.category = undefined
  searchParams.status = undefined
  searchParams.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  formTitle.value = '新增产品'
  currentProduct.value = { status: 1 }
  showFormModal.value = true
}

// 编辑
const handleEdit = (row: Product) => {
  formTitle.value = '编辑产品'
  currentProduct.value = { ...row }
  showFormModal.value = true
}

// 删除
const handleDelete = async (id: string) => {
  try {
    await deleteProduct(id)
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
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个产品吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await batchDeleteProducts(selectedRowKeys.value)
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadData()
      } catch (error) {
        message.error('批量删除失败')
      }
    }
  })
}

// 切换状态
const handleToggleStatus = async (id: number, enable: boolean) => {
  try {
    if (enable) {
      await enableProduct(id)
      message.success('产品已启用')
    } else {
      await disableProduct(id)
      message.success('产品已停用')
    }
    loadData()
  } catch (error) {
    message.error('操作失败')
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
   产品管理页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.product-page {
  width: 100%;
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
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

/* 产品名称单元格 */
:deep(.product-name-cell) {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

:deep(.product-name) {
  font-weight: 500;
  color: #0f172a;
}

:deep(.product-code) {
  font-size: 12px;
  color: #94a3b8;
}

/* 价格文本 */
:deep(.price-text) {
  font-weight: 600;
  color: #2563eb;
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

:deep(.action-btn.edit) {
  color: #f59e0b;
}

:deep(.action-btn.edit:hover) {
  background: #fef3c7;
}

:deep(.action-btn.enable) {
  color: #22c55e;
}

:deep(.action-btn.enable:hover) {
  background: #dcfce7;
}

:deep(.action-btn.disable) {
  color: #64748b;
}

:deep(.action-btn.disable:hover) {
  background: #f1f5f9;
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
