<template>
  <div class="department-management-page">
    <!-- 顶部工具栏 -->
    <n-card class="toolbar-card" :bordered="false">
      <n-space vertical :size="16">
        <n-space :size="12" :wrap="false" class="toolbar-row">
          <!-- 搜索框 -->
          <n-input
            v-model:value="queryParams.keyword"
            placeholder="搜索部门名称或部门编码"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <n-icon :component="SearchOutlined" />
            </template>
          </n-input>

          <!-- 状态筛选 -->
          <n-select
            v-model:value="queryParams.status"
            :options="statusOptions"
            placeholder="选择状态"
            clearable
            class="filter-select"
            @update:value="handleSearch"
          />

          <!-- 搜索按钮 -->
          <n-button type="primary" @click="handleSearch">
            <template #icon>
              <n-icon :component="SearchOutlined" />
            </template>
            搜索
          </n-button>

          <!-- 重置按钮 -->
          <n-button @click="handleReset">
            <template #icon>
              <n-icon :component="ReloadOutlined" />
            </template>
            重置
          </n-button>

          <!-- 展开/收起按钮 -->
          <n-button @click="handleToggleExpand">
            <template #icon>
              <n-icon :component="expandAll ? UpOutlined : DownOutlined" />
            </template>
            {{ expandAll ? '收起所有' : '展开所有' }}
          </n-button>

          <div class="flex-spacer"></div>

          <!-- 新增部门按钮 -->
          <n-button type="primary" @click="handleAdd">
            <template #icon>
              <n-icon :component="PlusOutlined" />
            </template>
            新增部门
          </n-button>
        </n-space>
      </n-space>
    </n-card>

    <!-- 树形表格 -->
    <n-card class="table-card" :bordered="false">
      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :row-key="(row: Department) => row.id"
        :scroll-x="1400"
        :expanded-row-keys="expandedRowKeys"
        striped
        @update:expanded-row-keys="handleExpandedRowKeysChange"
      />
    </n-card>

    <!-- 新增/编辑部门弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :mask-closable="false"
      preset="card"
      :title="formMode === 'add' ? '新增部门' : '编辑部门'"
      class="form-modal"
      :style="{ width: '600px' }"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-placement="left"
        label-width="100"
        require-mark-placement="right-hanging"
      >
        <n-form-item label="上级部门" path="parentId">
          <n-tree-select
            v-model:value="formData.parentId"
            :options="departmentTreeOptions"
            placeholder="请选择上级部门（不选则为根部门）"
            clearable
            filterable
            :default-expand-all="false"
          />
        </n-form-item>

        <n-form-item label="部门名称" path="name">
          <n-input
            v-model:value="formData.name"
            placeholder="请输入部门名称（2-50字符）"
            maxlength="50"
            show-count
          />
        </n-form-item>

        <n-form-item label="部门编码" path="code">
          <n-input
            v-model:value="formData.code"
            placeholder="请输入部门编码（字母数字下划线）"
            :disabled="formMode === 'edit'"
            maxlength="50"
            show-count
          />
        </n-form-item>

        <n-form-item label="负责人" path="leaderId">
          <n-select
            v-model:value="formData.leaderId"
            :options="userOptions"
            placeholder="请选择负责人"
            clearable
            filterable
          />
        </n-form-item>

        <n-form-item label="联系电话" path="phone">
          <n-input
            v-model:value="formData.phone"
            placeholder="请输入11位手机号"
            maxlength="11"
          />
        </n-form-item>

        <n-form-item label="邮箱" path="email">
          <n-input
            v-model:value="formData.email"
            placeholder="请输入邮箱地址"
            maxlength="100"
          />
        </n-form-item>

        <n-form-item label="排序" path="sort">
          <n-input-number
            v-model:value="formData.sort"
            placeholder="请输入排序号"
            :min="0"
            :max="9999"
            style="width: 100%"
          />
        </n-form-item>

        <n-form-item label="状态" path="status">
          <n-radio-group v-model:value="formData.status">
            <n-radio :value="DepartmentStatus.ENABLED">启用</n-radio>
            <n-radio :value="DepartmentStatus.DISABLED">禁用</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showFormModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleSubmit">
            保存
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 删除确认对话框 -->
    <n-modal
      v-model:show="showDeleteModal"
      preset="dialog"
      title="删除确认"
      type="warning"
      :positive-text="deleteCheckResult?.deletable ? '确定删除' : '知道了'"
      :negative-text="deleteCheckResult?.deletable ? '取消' : undefined"
      :loading="submitting"
      @positive-click="handleConfirmDelete"
    >
      <n-spin :show="deleteChecking">
        <div v-if="deleteCheckResult" class="delete-warning">
          <template v-if="deleteCheckResult.deletable">
            <p>确定要删除该部门吗？</p>
          </template>
          <template v-else>
            <n-alert type="error" title="无法删除" :bordered="false">
              <template v-if="deleteCheckResult.hasChildren">
                <p>该部门下有 <strong>{{ deleteCheckResult.childrenCount }}</strong> 个子部门，请先删除或移动子部门。</p>
              </template>
              <template v-if="deleteCheckResult.hasUsers">
                <p>该部门下有 <strong>{{ deleteCheckResult.usersCount }}</strong> 个用户，请先移动用户到其他部门。</p>
              </template>
            </n-alert>
          </template>
        </div>
      </n-spin>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NCard,
  NSpace,
  NInput,
  NSelect,
  NTreeSelect,
  NButton,
  NIcon,
  NDataTable,
  NModal,
  NForm,
  NFormItem,
  NRadioGroup,
  NRadio,
  NInputNumber,
  NSpin,
  NAlert,
  NTag,
  useMessage,
  type DataTableColumns,
  type FormInst,
  type FormRules
} from 'naive-ui'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  UpOutlined,
  DownOutlined,
  ApartmentOutlined
} from '@vicons/antd'
import type { Department, DepartmentQueryParams, DepartmentFormData, User } from '@/types/system'
import { DepartmentStatus } from '@/types/system'
import {
  getDepartmentTree,
  createDepartment,
  updateDepartment,
  deleteDepartment,
  checkDepartmentDeletable,
  getAllUsers
} from '@/api/system/department'
import dayjs from 'dayjs'

/**
 * 消息提示实例
 */
const message = useMessage()

/**
 * 表单引用
 */
const formRef = ref<FormInst | null>(null)

/**
 * 加载状态
 */
const loading = ref(false)
const submitting = ref(false)
const deleteChecking = ref(false)

/**
 * 查询参数
 */
const queryParams = reactive<DepartmentQueryParams>({
  keyword: undefined,
  status: undefined
})

/**
 * 表格数据
 */
const tableData = ref<Department[]>([])

/**
 * 展开的行键
 */
const expandedRowKeys = ref<number[]>([])

/**
 * 是否展开所有
 */
const expandAll = ref(false)

/**
 * 状态选项
 */
const statusOptions = [
  { label: '全部', value: undefined },
  { label: '启用', value: DepartmentStatus.ENABLED },
  { label: '禁用', value: DepartmentStatus.DISABLED }
]

/**
 * 表单模式：add-新增，edit-编辑
 */
const formMode = ref<'add' | 'edit'>('add')

/**
 * 显示表单弹窗
 */
const showFormModal = ref(false)

/**
 * 表单数据
 */
const formData = reactive<DepartmentFormData>({
  name: '',
  code: '',
  parentId: undefined,
  leaderId: undefined,
  phone: undefined,
  email: undefined,
  sort: 0,
  status: DepartmentStatus.ENABLED
})

/**
 * 表单验证规则
 */
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 50, message: '部门名称长度为2-50个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入部门编码', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]{2,50}$/,
      message: '部门编码为2-50位字母、数字或下划线',
      trigger: 'blur'
    }
  ],
  phone: [
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    }
  ],
  email: [
    {
      pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
      message: '请输入正确的邮箱地址',
      trigger: 'blur'
    }
  ],
  sort: [
    { required: true, message: '请输入排序号', trigger: 'blur', type: 'number' }
  ]
}

/**
 * 部门树选项（用于选择上级部门）
 */
const departmentTreeOptions = ref<any[]>([])

/**
 * 用户选项（用于选择负责人）
 */
const userOptions = ref<any[]>([])

/**
 * 显示删除确认对话框
 */
const showDeleteModal = ref(false)

/**
 * 当前要删除的部门 ID
 */
const currentDeleteId = ref<number>(0)

/**
 * 删除检查结果
 */
const deleteCheckResult = ref<{
  deletable: boolean
  hasChildren: boolean
  hasUsers: boolean
  childrenCount: number
  usersCount: number
} | null>(null)

/**
 * 表格列配置
 */
const columns: DataTableColumns<Department> = [
  {
    title: '部门名称',
    key: 'name',
    width: 250,
    ellipsis: {
      tooltip: true
    },
    render: (row) => {
      return h(
        NSpace,
        { size: 8, align: 'center' },
        {
          default: () => [
            h(NIcon, { component: ApartmentOutlined, size: 16, color: '#2563eb' }),
            h('span', row.name)
          ]
        }
      )
    }
  },
  {
    title: '部门编码',
    key: 'code',
    width: 150,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '负责人',
    key: 'leaderName',
    width: 120,
    align: 'center',
    render: (row) => {
      return row.leaderName || '-'
    }
  },
  {
    title: '联系电话',
    key: 'phone',
    width: 140,
    align: 'center',
    render: (row) => {
      return row.phone || '-'
    }
  },
  {
    title: '排序',
    key: 'sort',
    width: 100,
    align: 'center'
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    align: 'center',
    render: (row) => {
      return h(
        NTag,
        {
          type: row.status === DepartmentStatus.ENABLED ? 'success' : 'error',
          size: 'small'
        },
        {
          default: () => (row.status === DepartmentStatus.ENABLED ? '启用' : '禁用')
        }
      )
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    render: (row) => {
      return dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss')
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 240,
    align: 'center',
    fixed: 'right',
    render: (row) => {
      return h(
        NSpace,
        { size: 8, justify: 'center' },
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                text: true,
                onClick: () => handleAddChild(row)
              },
              {
                default: () => '新增子部门',
                icon: () => h(NIcon, { component: PlusOutlined })
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
                icon: () => h(NIcon, { component: EditOutlined })
              }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                text: true,
                onClick: () => handleDelete(row.id)
              },
              {
                default: () => '删除',
                icon: () => h(NIcon, { component: DeleteOutlined })
              }
            )
          ]
        }
      )
    }
  }
]

/**
 * 加载部门树
 */
async function loadDepartmentTree() {
  loading.value = true
  try {
    const res = await getDepartmentTree(queryParams)
    if (res.code === 200) {
      tableData.value = res.data
      // 如果展开所有，则设置所有行键
      if (expandAll.value) {
        expandedRowKeys.value = getAllDepartmentKeys(res.data)
      }
    }
  } catch (error) {
    message.error('加载部门列表失败')
    console.error('加载部门列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载用户列表（用于选择负责人）
 */
async function loadUserList() {
  try {
    const res = await getAllUsers()
    if (res.code === 200) {
      userOptions.value = res.data.map((user: User) => ({
        label: `${user.realName} (${user.username})`,
        value: user.id
      }))
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

/**
 * 获取所有部门 ID（用于展开所有）
 */
function getAllDepartmentKeys(departments: Department[]): number[] {
  const keys: number[] = []
  function traverse(nodes: Department[]) {
    nodes.forEach(node => {
      keys.push(node.id)
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    })
  }
  traverse(departments)
  return keys
}

/**
 * 转换部门树为树选择器格式（排除当前编辑的部门及其子部门）
 */
function convertDepartmentTree(departments: Department[], excludeId?: number): any[] {
  return departments
    .filter(dept => dept.id !== excludeId)
    .map((dept) => {
      const option: any = {
        label: dept.name,
        value: dept.id
      }
      if (dept.children && dept.children.length > 0) {
        const children = convertDepartmentTree(dept.children, excludeId)
        if (children.length > 0) {
          option.children = children
        }
      }
      return option
    })
}

/**
 * 搜索
 */
function handleSearch() {
  loadDepartmentTree()
}

/**
 * 重置
 */
function handleReset() {
  queryParams.keyword = undefined
  queryParams.status = undefined
  handleSearch()
}

/**
 * 展开/收起所有
 */
function handleToggleExpand() {
  expandAll.value = !expandAll.value
  if (expandAll.value) {
    expandedRowKeys.value = getAllDepartmentKeys(tableData.value)
  } else {
    expandedRowKeys.value = []
  }
}

/**
 * 展开行键变化
 */
function handleExpandedRowKeysChange(keys: number[]) {
  expandedRowKeys.value = keys
  expandAll.value = false
}

/**
 * 新增部门
 */
function handleAdd() {
  formMode.value = 'add'
  resetFormData()
  departmentTreeOptions.value = convertDepartmentTree(tableData.value)
  showFormModal.value = true
}

/**
 * 新增子部门
 */
function handleAddChild(row: Department) {
  formMode.value = 'add'
  resetFormData()
  formData.parentId = row.id
  departmentTreeOptions.value = convertDepartmentTree(tableData.value)
  showFormModal.value = true
}

/**
 * 编辑部门
 */
function handleEdit(row: Department) {
  formMode.value = 'edit'
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    code: row.code,
    parentId: row.parentId,
    leaderId: row.leaderId,
    phone: row.phone,
    email: row.email,
    sort: row.sort,
    status: row.status
  })
  // 排除当前部门及其子部门
  departmentTreeOptions.value = convertDepartmentTree(tableData.value, row.id)
  showFormModal.value = true
}

/**
 * 提交表单
 */
async function handleSubmit() {
  try {
    await formRef.value?.validate()
    submitting.value = true

    if (formMode.value === 'add') {
      const res = await createDepartment(formData)
      if (res.code === 200) {
        message.success('新增部门成功')
        showFormModal.value = false
        loadDepartmentTree()
      }
    } else {
      const res = await updateDepartment(formData.id!, formData)
      if (res.code === 200) {
        message.success('编辑部门成功')
        showFormModal.value = false
        loadDepartmentTree()
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 删除部门
 */
async function handleDelete(id: number) {
  currentDeleteId.value = id
  showDeleteModal.value = true
  deleteChecking.value = true
  deleteCheckResult.value = null

  try {
    const res = await checkDepartmentDeletable(id)
    if (res.code === 200) {
      deleteCheckResult.value = res.data
    }
  } catch (error) {
    message.error('检查部门状态失败')
    console.error('检查部门状态失败:', error)
    showDeleteModal.value = false
  } finally {
    deleteChecking.value = false
  }
}

/**
 * 确认删除
 */
async function handleConfirmDelete() {
  if (!deleteCheckResult.value?.deletable) {
    showDeleteModal.value = false
    return
  }

  submitting.value = true
  try {
    const res = await deleteDepartment(currentDeleteId.value)
    if (res.code === 200) {
      message.success('删除部门成功')
      showDeleteModal.value = false
      loadDepartmentTree()
    }
  } catch (error) {
    message.error('删除部门失败')
    console.error('删除部门失败:', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 重置表单数据
 */
function resetFormData() {
  Object.assign(formData, {
    id: undefined,
    name: '',
    code: '',
    parentId: undefined,
    leaderId: undefined,
    phone: undefined,
    email: undefined,
    sort: 0,
    status: DepartmentStatus.ENABLED
  })
  formRef.value?.restoreValidation()
}

/**
 * 组件挂载时加载数据
 */
onMounted(() => {
  loadDepartmentTree()
  loadUserList()
})
</script>

<style scoped>
/* 页面容器 */
.department-management-page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  background-color: #f5f7fa;
  font-family: 'Open Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

/* 工具栏卡片 */
.toolbar-card {
  flex-shrink: 0;
}

.toolbar-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  width: 280px;
  min-width: 200px;
}

.filter-select {
  width: 180px;
  min-width: 150px;
}

.flex-spacer {
  flex: 1;
  min-width: 16px;
}

/* 表格卡片 */
.table-card {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.table-card :deep(.n-card__content) {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.table-card :deep(.n-data-table) {
  flex: 1;
}

/* 删除警告 */
.delete-warning {
  padding: 8px 0;
}

.delete-warning p {
  margin: 8px 0;
  line-height: 1.6;
}

.delete-warning strong {
  color: #ef4444;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .department-management-page {
    padding: 12px;
    gap: 12px;
  }

  .toolbar-row {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input,
  .filter-select {
    width: 100%;
  }

  .flex-spacer {
    display: none;
  }

  .form-modal {
    width: 100% !important;
    max-width: 600px;
  }
}

/* 可访问性：焦点状态 */
:deep(.n-button:focus-visible),
:deep(.n-input:focus-within),
:deep(.n-select:focus-within),
:deep(.n-tree-select:focus-within),
:deep(.n-input-number:focus-within) {
  outline: 2px solid #2563eb;
  outline-offset: 2px;
}

/* 可访问性：减少动画 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* 悬停效果 */
:deep(.n-button) {
  transition: all 0.2s ease-in-out;
  cursor: pointer;
}

:deep(.n-button:hover) {
  transform: translateY(-1px);
}

:deep(.n-button:active) {
  transform: translateY(0);
}

/* 主按钮样式 - 企业级蓝色 */
:deep(.n-button--primary-type) {
  background-color: #2563eb;
  border-color: #2563eb;
}

:deep(.n-button--primary-type:hover) {
  background-color: #1d4ed8;
  border-color: #1d4ed8;
}

:deep(.n-button--primary-type:active) {
  background-color: #1e40af;
  border-color: #1e40af;
}

/* 加载状态 */
:deep(.n-data-table.n-data-table--loading) {
  opacity: 0.6;
}

/* 自定义滚动条 */
:deep(.n-data-table-wrapper) {
  scrollbar-width: thin;
  scrollbar-color: #d1d5db #f3f4f6;
}

:deep(.n-data-table-wrapper::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(.n-data-table-wrapper::-webkit-scrollbar-track) {
  background: #f3f4f6;
  border-radius: 4px;
}

:deep(.n-data-table-wrapper::-webkit-scrollbar-thumb) {
  background: #d1d5db;
  border-radius: 4px;
}

:deep(.n-data-table-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #9ca3af;
}

/* 标题字体 - Poppins */
:deep(.n-card-header__main),
:deep(.n-modal-card-header),
:deep(.n-data-table th) {
  font-family: 'Poppins', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  font-weight: 600;
}

/* 表格行悬停效果 */
:deep(.n-data-table-tr:hover) {
  background-color: #f9fafb;
}

/* 标签样式优化 */
:deep(.n-tag) {
  font-weight: 500;
  padding: 4px 12px;
}

/* 树形表格缩进优化 */
:deep(.n-data-table-indent) {
  width: 24px;
}

/* 树形表格展开按钮优化 */
:deep(.n-data-table-expand-trigger) {
  color: #2563eb;
  cursor: pointer;
  transition: transform 0.2s ease-in-out;
}

:deep(.n-data-table-expand-trigger:hover) {
  color: #1d4ed8;
}

/* 表单项间距优化 */
:deep(.n-form-item) {
  margin-bottom: 20px;
}

/* 输入框聚焦样式 */
:deep(.n-input:focus-within),
:deep(.n-input-number:focus-within),
:deep(.n-select:focus-within),
:deep(.n-tree-select:focus-within) {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}

/* 警告框样式优化 */
:deep(.n-alert) {
  margin-top: 8px;
}

:deep(.n-alert .n-alert__content) {
  line-height: 1.6;
}
</style>
