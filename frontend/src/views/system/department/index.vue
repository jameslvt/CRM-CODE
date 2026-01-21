<template>
  <div class="department-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">部门管理</h1>
        <p class="page-subtitle">管理企业组织架构，设置部门层级关系</p>
      </div>
      <div class="header-actions">
        <n-button @click="handleToggleExpand" class="secondary-btn">
          <template #icon>
            <n-icon :component="expandAll ? UpOutlined : DownOutlined" />
          </template>
          {{ expandAll ? '收起所有' : '展开所有' }}
        </n-button>
        <n-button type="primary" @click="handleAdd" class="primary-btn">
          <template #icon>
            <n-icon :component="PlusOutlined" />
          </template>
          新增部门
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
            <SearchOutlined />
          </n-icon>
          <input
            v-model="queryParams.keyword"
            type="text"
            placeholder="搜索部门名称、编码..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="filter-group">
          <n-select
            v-model:value="queryParams.status"
            :options="statusOptions"
            placeholder="状态"
            clearable
            class="filter-select"
          />
          <n-button @click="handleSearch" class="filter-btn">
            <template #icon>
              <n-icon><SearchOutlined /></n-icon>
            </template>
            搜索
          </n-button>
          <n-button @click="handleReset" quaternary class="reset-btn">
            重置
          </n-button>
        </div>
      </div>
    </div>

    <!-- 树形表格 -->
    <div class="table-card">
      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :row-key="(row: Department) => row.id"
        :scroll-x="1200"
        :expanded-row-keys="expandedRowKeys"
        :bordered="false"
        :single-line="false"
        flex-height
        class="data-table"
        @update:expanded-row-keys="handleExpandedRowKeysChange"
      />
    </div>

    <!-- 新增/编辑部门弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :mask-closable="false"
      preset="card"
      :title="formMode === 'add' ? '新增部门' : '编辑部门'"
      class="form-modal"
      :segmented="{ content: 'soft', footer: 'soft' }"
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
  NButton,
  NIcon,
  NSelect,
  NTreeSelect,
  NDataTable,
  NModal,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NRadioGroup,
  NRadio,
  NSpace,
  NSpin,
  NAlert,
  NPopconfirm,
  useMessage,
  type DataTableColumns,
  type FormInst,
  type FormRules
} from 'naive-ui'
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  UpOutlined,
  DownOutlined,
  ApartmentOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined
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

// 消息提示实例
const message = useMessage()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '全部部门', value: '0', icon: ApartmentOutlined, class: 'blue' },
  { key: 'enabled', label: '已启用', value: '0', icon: CheckCircleOutlined, class: 'green' },
  { key: 'disabled', label: '已禁用', value: '0', icon: CloseCircleOutlined, class: 'gray' }
])

// 表单引用
const formRef = ref<FormInst | null>(null)

// 加载状态
const loading = ref(false)
const submitting = ref(false)
const deleteChecking = ref(false)

// 查询参数
const queryParams = reactive<DepartmentQueryParams>({
  keyword: undefined,
  status: undefined
})

// 表格数据
const tableData = ref<Department[]>([])

// 展开的行键
const expandedRowKeys = ref<number[]>([])

// 是否展开所有
const expandAll = ref(false)

// 状态选项
const statusOptions = [
  { label: '全部', value: undefined },
  { label: '启用', value: DepartmentStatus.ENABLED },
  { label: '禁用', value: DepartmentStatus.DISABLED }
]

// 表单模式：add-新增，edit-编辑
const formMode = ref<'add' | 'edit'>('add')

// 显示表单弹窗
const showFormModal = ref(false)

// 表单数据
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

// 表单验证规则
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

// 部门树选项（用于选择上级部门）
const departmentTreeOptions = ref<any[]>([])

// 用户选项（用于选择负责人）
const userOptions = ref<any[]>([])

// 显示删除确认对话框
const showDeleteModal = ref(false)

// 当前要删除的部门 ID
const currentDeleteId = ref<number>(0)

// 删除检查结果
const deleteCheckResult = ref<{
  deletable: boolean
  hasChildren: boolean
  hasUsers: boolean
  childrenCount: number
  usersCount: number
} | null>(null)

// 状态颜色映射
const statusColorMap: Record<number, { bg: string; color: string; label: string }> = {
  [DepartmentStatus.ENABLED]: { bg: '#dcfce7', color: '#22c55e', label: '启用' },
  [DepartmentStatus.DISABLED]: { bg: '#f1f5f9', color: '#64748b', label: '禁用' }
}

// 获取状态标签
const getStatusTag = (status: number) => {
  const config = statusColorMap[status] || statusColorMap[DepartmentStatus.DISABLED]
  return h(
    'span',
    {
      class: 'status-tag',
      style: { background: config.bg, color: config.color }
    },
    config.label
  )
}

// 表格列配置
const columns: DataTableColumns<Department> = [
  {
    title: '部门名称',
    key: 'name',
    width: 250,
    render: (row) =>
      h('div', { class: 'dept-name-cell' }, [
        h(NIcon, { component: ApartmentOutlined, size: 16, color: '#2563eb' }),
        h('span', { class: 'dept-name' }, row.name)
      ])
  },
  {
    title: '部门编码',
    key: 'code',
    width: 150
  },
  {
    title: '负责人',
    key: 'leaderName',
    width: 120,
    render: (row) => row.leaderName || '-'
  },
  {
    title: '联系电话',
    key: 'phone',
    width: 140,
    render: (row) => row.phone || '-'
  },
  {
    title: '排序',
    key: 'sort',
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
    render: (row) => dayjs(row.createTime).format('YYYY-MM-DD HH:mm')
  },
  {
    title: '操作',
    key: 'actions',
    width: 220,
    fixed: 'right',
    render: (row) =>
      h('div', { class: 'action-buttons' }, [
        h('button', { class: 'action-btn add', onClick: () => handleAddChild(row) }, [
          h(NIcon, { size: 14 }, { default: () => h(PlusOutlined) }), '子部门'
        ]),
        h('button', { class: 'action-btn edit', onClick: () => handleEdit(row) }, [
          h(NIcon, { size: 14 }, { default: () => h(EditOutlined) }), '编辑'
        ]),
        h('button', { class: 'action-btn delete', onClick: () => handleDelete(row.id) }, [
          h(NIcon, { size: 14 }, { default: () => h(DeleteOutlined) }), '删除'
        ])
      ])
  }
]

// 统计部门数量
function countDepartments(departments: Department[]): { total: number; enabled: number; disabled: number } {
  let total = 0, enabled = 0, disabled = 0
  function traverse(nodes: Department[]) {
    nodes.forEach(node => {
      total++
      if (node.status === DepartmentStatus.ENABLED) enabled++
      else disabled++
      if (node.children && node.children.length > 0) traverse(node.children)
    })
  }
  traverse(departments)
  return { total, enabled, disabled }
}

// 更新统计数据
function updateStats() {
  const stats = countDepartments(tableData.value)
  miniStats.value[0].value = String(stats.total)
  miniStats.value[1].value = String(stats.enabled)
  miniStats.value[2].value = String(stats.disabled)
}

// 加载部门树
async function loadDepartmentTree() {
  loading.value = true
  try {
    const res = await getDepartmentTree(queryParams)
    if (res.code === 200) {
      tableData.value = res.data
      updateStats()
      if (expandAll.value) {
        expandedRowKeys.value = getAllDepartmentKeys(res.data)
      }
    }
  } catch (error) {
    message.error('加载部门列表失败')
  } finally {
    loading.value = false
  }
}

// 加载用户列表（用于选择负责人）
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
    // 加载失败
  }
}

// 获取所有部门 ID（用于展开所有）
function getAllDepartmentKeys(departments: Department[]): number[] {
  const keys: number[] = []
  function traverse(nodes: Department[]) {
    nodes.forEach(node => {
      keys.push(node.id)
      if (node.children && node.children.length > 0) traverse(node.children)
    })
  }
  traverse(departments)
  return keys
}

// 转换部门树为树选择器格式（排除当前编辑的部门及其子部门）
function convertDepartmentTree(departments: Department[], excludeId?: number): any[] {
  return departments
    .filter(dept => dept.id !== excludeId)
    .map((dept) => {
      const option: any = { label: dept.name, value: dept.id }
      if (dept.children && dept.children.length > 0) {
        const children = convertDepartmentTree(dept.children, excludeId)
        if (children.length > 0) option.children = children
      }
      return option
    })
}

// 搜索
function handleSearch() {
  loadDepartmentTree()
}

// 重置
function handleReset() {
  queryParams.keyword = undefined
  queryParams.status = undefined
  handleSearch()
}

// 展开/收起所有
function handleToggleExpand() {
  expandAll.value = !expandAll.value
  if (expandAll.value) {
    expandedRowKeys.value = getAllDepartmentKeys(tableData.value)
  } else {
    expandedRowKeys.value = []
  }
}

// 展开行键变化
function handleExpandedRowKeysChange(keys: number[]) {
  expandedRowKeys.value = keys
  expandAll.value = false
}

// 新增部门
function handleAdd() {
  formMode.value = 'add'
  resetFormData()
  departmentTreeOptions.value = convertDepartmentTree(tableData.value)
  showFormModal.value = true
}

// 新增子部门
function handleAddChild(row: Department) {
  formMode.value = 'add'
  resetFormData()
  formData.parentId = row.id
  departmentTreeOptions.value = convertDepartmentTree(tableData.value)
  showFormModal.value = true
}

// 编辑部门
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
  departmentTreeOptions.value = convertDepartmentTree(tableData.value, row.id)
  showFormModal.value = true
}

// 提交表单
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
    // 表单验证失败
  } finally {
    submitting.value = false
  }
}

// 删除部门
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
    showDeleteModal.value = false
  } finally {
    deleteChecking.value = false
  }
}

// 确认删除
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
  } finally {
    submitting.value = false
  }
}

// 重置表单数据
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

// 组件挂载时加载数据
onMounted(() => {
  loadDepartmentTree()
  loadUserList()
})
</script>

<style scoped>
/* ========================================
   部门管理页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.department-page {
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
  max-width: 320px;
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

/* 部门名称单元格 */
:deep(.dept-name-cell) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.dept-name) {
  font-weight: 500;
  color: #0f172a;
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

:deep(.action-btn.add) {
  color: #22c55e;
}

:deep(.action-btn.add:hover) {
  background: #dcfce7;
}

:deep(.action-btn.edit) {
  color: #2563eb;
}

:deep(.action-btn.edit:hover) {
  background: #dbeafe;
}

:deep(.action-btn.delete) {
  color: #ef4444;
}

:deep(.action-btn.delete:hover) {
  background: #fee2e2;
}

/* 树形表格缩进 */
:deep(.n-data-table-indent) {
  width: 24px;
}

:deep(.n-data-table-expand-trigger) {
  color: #2563eb;
  cursor: pointer;
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

/* ========================================
   响应式设计
   ======================================== */
@media (max-width: 1024px) {
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

  .form-modal {
    width: 100% !important;
    max-width: 600px;
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
