<template>
  <div class="lead-management">
    <!-- 搜索筛选区域 -->
    <n-card :bordered="false" class="search-card">
      <n-form
        ref="searchFormRef"
        :model="searchParams"
        label-placement="left"
        :show-feedback="false"
      >
        <n-grid :cols="24" :x-gap="16">
          <n-form-item-gi :span="6" label="线索名称" path="leadName">
            <n-input
              v-model:value="searchParams.leadName"
              placeholder="请输入线索名称"
              clearable
            />
          </n-form-item-gi>
          <n-form-item-gi :span="6" label="联系人" path="contactName">
            <n-input
              v-model:value="searchParams.contactName"
              placeholder="请输入联系人"
              clearable
            />
          </n-form-item-gi>
          <n-form-item-gi :span="6" label="状态" path="status">
            <n-select
              v-model:value="searchParams.status"
              placeholder="请选择状态"
              :options="statusOptions"
              clearable
            />
          </n-form-item-gi>
          <n-form-item-gi :span="6" label="评级" path="rating">
            <n-select
              v-model:value="searchParams.rating"
              placeholder="请选择评级"
              :options="ratingOptions"
              clearable
            />
          </n-form-item-gi>
        </n-grid>
        <n-space justify="end">
          <n-button @click="handleReset">重置</n-button>
          <n-button type="primary" @click="handleSearch">
            <template #icon>
              <n-icon><SearchOutline /></n-icon>
            </template>
            搜索
          </n-button>
        </n-space>
      </n-form>
    </n-card>

    <!-- 操作栏 -->
    <n-card :bordered="false" class="toolbar-card">
      <n-space>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新增线索
        </n-button>
        <n-button
          type="error"
          :disabled="!selectedRowKeys.length"
          @click="handleBatchDelete"
        >
          <template #icon>
            <n-icon><TrashOutline /></n-icon>
          </template>
          批量删除
        </n-button>
        <n-button @click="handleExport">
          <template #icon>
            <n-icon><DownloadOutline /></n-icon>
          </template>
          导出
        </n-button>
        <n-button @click="handleImport">
          <template #icon>
            <n-icon><CloudUploadOutline /></n-icon>
          </template>
          导入
        </n-button>
      </n-space>
    </n-card>

    <!-- 数据表格 -->
    <n-card :bordered="false" class="table-card">
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
      />
    </n-card>

    <!-- 新增/编辑弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :title="formTitle"
      preset="card"
      style="width: 800px"
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
      style="width: 600px"
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
import { ref, reactive, onMounted, h } from 'vue'
import {
  NButton,
  NIcon,
  NTag,
  NSpace,
  NPopconfirm,
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
  EyeOutline
} from '@vicons/ionicons5'
import { useRouter } from 'vue-router'
import { pageLeads, deleteLead, batchDeleteLeads } from '@/api/business/lead'
import type { Lead, LeadQueryParams } from '@/types/business'
import LeadForm from '@/components/business/LeadForm.vue'
import LeadConvertDialog from '@/components/business/LeadConvertDialog.vue'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

// 搜索参数
const searchParams = reactive<LeadQueryParams>({
  pageNum: 1,
  pageSize: 10,
  leadName: '',
  contactName: '',
  status: undefined,
  rating: undefined
})

// 状态选项
const statusOptions = [
  { label: '新建', value: 'NEW', color: 'info' },
  { label: '跟进中', value: 'FOLLOWING', color: 'warning' },
  { label: '已转化', value: 'CONVERTED', color: 'success' },
  { label: '已失效', value: 'INVALID', color: 'default' }
]

// 评级选项
const ratingOptions = [
  { label: 'A高', value: 'A', color: 'error' },
  { label: 'B中', value: 'B', color: 'warning' },
  { label: 'C低', value: 'C', color: 'info' }
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

// 获取状态标签
const getStatusTag = (status: string) => {
  const option = statusOptions.find((o) => o.value === status)
  return h(
    NTag,
    { type: option?.color as any, size: 'small' },
    { default: () => option?.label || status }
  )
}

// 获取评级标签
const getRatingTag = (rating: string) => {
  const option = ratingOptions.find((o) => o.value === rating)
  return h(
    NTag,
    { type: option?.color as any, size: 'small' },
    { default: () => option?.label || rating }
  )
}

// 表格列配置
const columns: DataTableColumns<Lead> = [
  { type: 'selection' },
  {
    title: '线索名称',
    key: 'leadName',
    width: 180,
    ellipsis: { tooltip: true }
  },
  {
    title: '联系人',
    key: 'contactName',
    width: 120
  },
  {
    title: '联系电话',
    key: 'phone',
    width: 130
  },
  {
    title: '公司名称',
    key: 'companyName',
    width: 180,
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
    render: (row) => getRatingTag(row.rating)
  },
  {
    title: '来源',
    key: 'source',
    width: 100
  },
  {
    title: '负责人',
    key: 'ownerName',
    width: 100
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 160
  },
  {
    title: '操作',
    key: 'actions',
    width: 240,
    fixed: 'right',
    render: (row) =>
      h(
        NSpace,
        { size: 'small' },
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                text: true,
                onClick: () => handleView(row.id)
              },
              {
                default: () => '查看',
                icon: () => h(NIcon, null, { default: () => h(EyeOutline) })
              }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'info',
                text: true,
                onClick: () => handleEdit(row)
              },
              {
                default: () => '编辑',
                icon: () => h(NIcon, null, { default: () => h(CreateOutline) })
              }
            ),
            row.status !== 'CONVERTED' &&
              h(
                NButton,
                {
                  size: 'small',
                  type: 'success',
                  text: true,
                  onClick: () => handleConvert(row.id)
                },
                {
                  default: () => '转化',
                  icon: () => h(NIcon, null, { default: () => h(SwapHorizontalOutline) })
                }
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
                    NButton,
                    {
                      size: 'small',
                      type: 'error',
                      text: true
                    },
                    {
                      default: () => '删除',
                      icon: () => h(NIcon, null, { default: () => h(TrashOutline) })
                    }
                  )
              }
            )
          ]
        }
      )
  }
]

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const result = await pageLeads(searchParams)
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
  searchParams.leadName = ''
  searchParams.contactName = ''
  searchParams.status = undefined
  searchParams.rating = undefined
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
const handleView = (id: number) => {
  router.push(`/business/lead/${id}`)
}

// 删除
const handleDelete = async (id: number) => {
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
const handleConvert = (id: number) => {
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

<style scoped lang="scss">
.lead-management {
  padding: 16px;

  .search-card,
  .toolbar-card,
  .table-card {
    margin-bottom: 16px;
  }

  .search-card {
    :deep(.n-form-item) {
      margin-bottom: 0;
    }
  }
}
</style>
