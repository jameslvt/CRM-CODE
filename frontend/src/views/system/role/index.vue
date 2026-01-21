<template>
  <div class="role-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">角色管理</h1>
        <p class="page-subtitle">管理系统角色，配置数据权限和功能权限</p>
      </div>
      <div class="header-actions">
        <n-button type="primary" @click="handleAdd" class="primary-btn">
          <template #icon>
            <n-icon :component="PlusOutlined" />
          </template>
          新增角色
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
            placeholder="搜索角色名称、编码..."
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

    <!-- 数据表格 -->
    <div class="table-card">
      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        :row-key="(row: Role) => row.id"
        :scroll-x="1200"
        :bordered="false"
        :single-line="false"
        flex-height
        class="data-table"
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
      />
    </div>

    <!-- 新增/编辑角色弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :mask-closable="false"
      preset="card"
      :title="formMode === 'add' ? '新增角色' : '编辑角色'"
      class="form-modal"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-placement="left"
        label-width="120"
        require-mark-placement="right-hanging"
      >
        <n-form-item label="角色名称" path="name">
          <n-input
            v-model:value="formData.name"
            placeholder="请输入角色名称（2-50字符）"
            maxlength="50"
            show-count
          />
        </n-form-item>

        <n-form-item label="角色编码" path="code">
          <n-input
            v-model:value="formData.code"
            placeholder="请输入角色编码（字母数字下划线）"
            :disabled="formMode === 'edit'"
            maxlength="50"
            show-count
          />
        </n-form-item>

        <n-form-item label="数据权限范围" path="dataScope">
          <n-select
            v-model:value="formData.dataScope"
            :options="dataScopeOptions"
            placeholder="请选择数据权限范围"
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
            <n-radio :value="RoleStatus.ENABLED">启用</n-radio>
            <n-radio :value="RoleStatus.DISABLED">禁用</n-radio>
          </n-radio-group>
        </n-form-item>

        <n-form-item label="备注" path="remark">
          <n-input
            v-model:value="formData.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="3"
            maxlength="200"
            show-count
          />
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

    <!-- 分配权限弹窗 -->
    <n-modal
      v-model:show="showPermissionModal"
      :mask-closable="false"
      preset="card"
      title="分配权限"
      class="permission-modal"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <n-spin :show="permissionLoading">
        <div class="permission-toolbar">
          <n-space>
            <n-button size="small" @click="handleCheckAll">全选</n-button>
            <n-button size="small" @click="handleUncheckAll">全不选</n-button>
            <n-button size="small" @click="handleExpandAll">展开所有</n-button>
            <n-button size="small" @click="handleCollapseAll">收起所有</n-button>
          </n-space>
        </div>
        <n-tree
          ref="permissionTreeRef"
          :data="permissionTreeData"
          :checked-keys="checkedPermissionKeys"
          checkable
          cascade
          expand-on-click
          :default-expanded-keys="defaultExpandedKeys"
          key-field="id"
          label-field="name"
          children-field="children"
          class="permission-tree"
          @update:checked-keys="handlePermissionCheck"
        />
      </n-spin>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showPermissionModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleAssignPermissions">
            保存
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue'
import {
  NButton,
  NIcon,
  NSelect,
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
  NTree,
  NPopconfirm,
  useMessage,
  type DataTableColumns,
  type FormInst,
  type FormRules,
  type TreeInst
} from 'naive-ui'
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SafetyOutlined,
  TeamOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined
} from '@vicons/antd'
import type { Role, RoleQueryParams, RoleFormData, Permission } from '@/types/system'
import { RoleStatus, DataScope } from '@/types/system'
import {
  getRoleList,
  createRole,
  updateRole,
  deleteRole,
  assignPermissions,
  getRolePermissions,
  getPermissionTree
} from '@/api/system/role'
import dayjs from 'dayjs'

// 消息提示实例
const message = useMessage()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '全部角色', value: '0', icon: TeamOutlined, class: 'blue' },
  { key: 'enabled', label: '已启用', value: '0', icon: CheckCircleOutlined, class: 'green' },
  { key: 'disabled', label: '已禁用', value: '0', icon: CloseCircleOutlined, class: 'gray' }
])

// 表单引用
const formRef = ref<FormInst | null>(null)
const permissionTreeRef = ref<TreeInst | null>(null)

// 加载状态
const loading = ref(false)
const submitting = ref(false)
const permissionLoading = ref(false)

// 查询参数
const queryParams = reactive<RoleQueryParams>({
  current: 1,
  size: 10,
  keyword: undefined,
  status: undefined
})

// 表格数据
const tableData = ref<Role[]>([])

// 分页配置
const pagination = reactive({
  page: 1,
  pageSize: 10,
  pageCount: 0,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  prefix: (info: { itemCount: number }) => `共 ${info.itemCount} 条`
})

// 状态选项
const statusOptions = [
  { label: '全部', value: undefined },
  { label: '启用', value: RoleStatus.ENABLED },
  { label: '禁用', value: RoleStatus.DISABLED }
]

// 数据权限范围选项
const dataScopeOptions = [
  { label: '全部数据', value: DataScope.ALL },
  { label: '本部门及下级', value: DataScope.DEPT_AND_CHILD },
  { label: '本部门', value: DataScope.DEPT },
  { label: '仅本人', value: DataScope.SELF }
]

// 数据权限范围标签映射
const dataScopeLabels: Record<DataScope, string> = {
  [DataScope.ALL]: '全部数据',
  [DataScope.DEPT_AND_CHILD]: '本部门及下级',
  [DataScope.DEPT]: '本部门',
  [DataScope.SELF]: '仅本人'
}

// 表单模式：add-新增，edit-编辑
const formMode = ref<'add' | 'edit'>('add')

// 显示表单弹窗
const showFormModal = ref(false)

// 表单数据
const formData = reactive<RoleFormData>({
  name: '',
  code: '',
  dataScope: DataScope.SELF,
  sort: 0,
  status: RoleStatus.ENABLED,
  remark: undefined
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度为2-50个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]{2,50}$/,
      message: '角色编码为2-50位字母、数字或下划线',
      trigger: 'blur'
    }
  ],
  dataScope: [
    { required: true, message: '请选择数据权限范围', trigger: 'change', type: 'number' }
  ],
  sort: [
    { required: true, message: '请输入排序号', trigger: 'blur', type: 'number' }
  ]
}

// 显示权限分配弹窗
const showPermissionModal = ref(false)

// 当前操作的角色 ID
const currentRoleId = ref<number>(0)

// 权限树数据
const permissionTreeData = ref<Permission[]>([])

// 已选中的权限 ID 列表
const checkedPermissionKeys = ref<number[]>([])

// 默认展开的节点
const defaultExpandedKeys = ref<number[]>([])

// 状态颜色映射
const statusColorMap: Record<number, { bg: string; color: string; label: string }> = {
  [RoleStatus.ENABLED]: { bg: '#dcfce7', color: '#22c55e', label: '启用' },
  [RoleStatus.DISABLED]: { bg: '#f1f5f9', color: '#64748b', label: '禁用' }
}

// 获取状态标签
const getStatusTag = (status: number) => {
  const config = statusColorMap[status] || statusColorMap[RoleStatus.DISABLED]
  return h(
    'span',
    {
      class: 'status-tag',
      style: { background: config.bg, color: config.color }
    },
    config.label
  )
}

// 获取数据权限标签
const getDataScopeTag = (dataScope: DataScope) => {
  return h(
    'span',
    {
      class: 'scope-tag',
      style: { background: '#dbeafe', color: '#2563eb' }
    },
    dataScopeLabels[dataScope]
  )
}

// 表格列配置
const columns: DataTableColumns<Role> = [
  {
    title: '角色名称',
    key: 'name',
    width: 150,
    render: (row) =>
      h('div', { class: 'role-cell' }, [
        h('span', { class: 'role-name' }, row.name),
        h('span', { class: 'role-code' }, row.code)
      ])
  },
  {
    title: '数据权限',
    key: 'dataScope',
    width: 140,
    render: (row) => getDataScopeTag(row.dataScope)
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
    width: 200,
    fixed: 'right',
    render: (row) =>
      h('div', { class: 'action-buttons' }, [
        h('button', { class: 'action-btn edit', onClick: () => handleEdit(row) }, [
          h(NIcon, { size: 14 }, { default: () => h(EditOutlined) }), '编辑'
        ]),
        h('button', { class: 'action-btn permission', onClick: () => handleOpenPermissionModal(row) }, [
          h(NIcon, { size: 14 }, { default: () => h(SafetyOutlined) }), '权限'
        ]),
        h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, {
          default: () => '确定要删除该角色吗？',
          trigger: () => h('button', { class: 'action-btn delete' }, [
            h(NIcon, { size: 14 }, { default: () => h(DeleteOutlined) }), '删除'
          ])
        })
      ])
  }
]

// 更新统计数据
function updateStats() {
  const total = pagination.itemCount
  const enabled = tableData.value.filter(r => r.status === RoleStatus.ENABLED).length
  const disabled = tableData.value.filter(r => r.status === RoleStatus.DISABLED).length
  miniStats.value[0].value = String(total)
  miniStats.value[1].value = String(enabled)
  miniStats.value[2].value = String(disabled)
}

// 加载角色列表
async function loadRoleList() {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.page = res.data.current
      pagination.pageSize = res.data.size
      pagination.pageCount = res.data.pages
      pagination.itemCount = res.data.total
      updateStats()
    }
  } catch (error) {
    message.error('加载角色列表失败')
    console.error('加载角色列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function handleSearch() {
  queryParams.current = 1
  pagination.page = 1
  loadRoleList()
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
 * 页码变化
 */
function handlePageChange(page: number) {
  queryParams.current = page
  pagination.page = page
  loadRoleList()
}

/**
 * 每页大小变化
 */
function handlePageSizeChange(pageSize: number) {
  queryParams.size = pageSize
  queryParams.current = 1
  pagination.pageSize = pageSize
  pagination.page = 1
  loadRoleList()
}

/**
 * 新增角色
 */
function handleAdd() {
  formMode.value = 'add'
  resetFormData()
  showFormModal.value = true
}

/**
 * 编辑角色
 */
function handleEdit(row: Role) {
  formMode.value = 'edit'
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    code: row.code,
    dataScope: row.dataScope,
    sort: row.sort,
    status: row.status,
    remark: row.remark
  })
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
      const res = await createRole(formData)
      if (res.code === 200) {
        message.success('新增角色成功')
        showFormModal.value = false
        loadRoleList()
      }
    } else {
      const res = await updateRole(formData.id!, formData)
      if (res.code === 200) {
        message.success('编辑角色成功')
        showFormModal.value = false
        loadRoleList()
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 删除角色
 */
async function handleDelete(id: number) {
  try {
    const res = await deleteRole(id)
    if (res.code === 200) {
      message.success('删除角色成功')
      loadRoleList()
    }
  } catch (error) {
    message.error('删除角色失败')
    console.error('删除角色失败:', error)
  }
}

/**
 * 打开分配权限弹窗
 */
async function handleOpenPermissionModal(row: Role) {
  currentRoleId.value = row.id
  showPermissionModal.value = true
  permissionLoading.value = true

  try {
    // 加载权限树
    const permissionsRes = await getPermissionTree()
    if (permissionsRes.code === 200) {
      permissionTreeData.value = permissionsRes.data
      // 默认展开第一层
      defaultExpandedKeys.value = permissionsRes.data.map(p => p.id)
    }

    // 加载角色已分配的权限
    const rolePermissionsRes = await getRolePermissions(row.id)
    if (rolePermissionsRes.code === 200) {
      checkedPermissionKeys.value = rolePermissionsRes.data
    }
  } catch (error) {
    message.error('加载权限数据失败')
    console.error('加载权限数据失败:', error)
  } finally {
    permissionLoading.value = false
  }
}

/**
 * 权限选择变化
 */
function handlePermissionCheck(keys: number[]) {
  checkedPermissionKeys.value = keys
}

/**
 * 全选
 */
function handleCheckAll() {
  const allKeys = getAllPermissionKeys(permissionTreeData.value)
  checkedPermissionKeys.value = allKeys
}

/**
 * 全不选
 */
function handleUncheckAll() {
  checkedPermissionKeys.value = []
}

/**
 * 展开所有
 */
function handleExpandAll() {
  const allKeys = getAllPermissionKeys(permissionTreeData.value)
  defaultExpandedKeys.value = allKeys
}

/**
 * 收起所有
 */
function handleCollapseAll() {
  defaultExpandedKeys.value = []
}

/**
 * 获取所有权限 ID
 */
function getAllPermissionKeys(permissions: Permission[]): number[] {
  const keys: number[] = []
  function traverse(nodes: Permission[]) {
    nodes.forEach(node => {
      keys.push(node.id)
      if (node.children && node.children.length > 0) {
        traverse(node.children)
      }
    })
  }
  traverse(permissions)
  return keys
}

/**
 * 分配权限
 */
async function handleAssignPermissions() {
  submitting.value = true
  try {
    const res = await assignPermissions({
      roleId: currentRoleId.value,
      permissionIds: checkedPermissionKeys.value
    })
    if (res.code === 200) {
      message.success('分配权限成功')
      showPermissionModal.value = false
      loadRoleList()
    }
  } catch (error) {
    message.error('分配权限失败')
    console.error('分配权限失败:', error)
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
    dataScope: DataScope.SELF,
    sort: 0,
    status: RoleStatus.ENABLED,
    remark: undefined
  })
  formRef.value?.restoreValidation()
}

/**
 * 组件挂载时加载数据
 */
onMounted(() => {
  loadRoleList()
})
</script>

<style scoped>
/* 页面容器 */
.role-page {
  padding: 24px;
  background: #f8fafc;
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 24px;
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
  font-weight: 600;
  border-radius: 8px;
  background: #2563eb;
  border-color: #2563eb;
  transition: all 0.2s ease;
}

.primary-btn:hover {
  background: #1d4ed8;
  border-color: #1d4ed8;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

/* 统计卡片行 */
.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.mini-stat {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  min-width: 160px;
  transition: all 0.2s ease;
}

.mini-stat:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
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
  line-height: 1.2;
}

.mini-stat-label {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

/* 筛选卡片 */
.filter-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  padding: 16px 20px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 400px;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  pointer-events: none;
}

.search-input {
  width: 100%;
  height: 40px;
  padding: 0 16px 0 44px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #0f172a;
  background: #f8fafc;
  transition: all 0.2s ease;
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
  --n-height: 40px;
  --n-border-radius: 8px;
  --n-border: 1px solid #e2e8f0;
  --n-border-hover: 1px solid #cbd5e1;
  --n-border-focus: 1px solid #2563eb;
  --n-box-shadow-focus: 0 0 0 3px rgba(37, 99, 235, 0.1);
  background: #f8fafc;
}

.filter-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 500;
  background: #f1f5f9;
  border-color: #e2e8f0;
  color: #334155;
}

.filter-btn:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.reset-btn {
  height: 40px;
  padding: 0 16px;
  color: #64748b;
  font-weight: 500;
}

.reset-btn:hover {
  color: #334155;
}

/* 表格卡片 */
.table-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.data-table {
  --n-th-color: #f8fafc;
  --n-th-text-color: #475569;
  --n-td-text-color: #334155;
  --n-border-color: #e2e8f0;
  --n-th-font-weight: 600;
}

.data-table :deep(.n-data-table-thead) {
  background: #f8fafc;
}

.data-table :deep(.n-data-table-th) {
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 14px 16px;
}

.data-table :deep(.n-data-table-td) {
  padding: 14px 16px;
}

.data-table :deep(.n-data-table-tr:hover) {
  background: #f8fafc;
}

/* 角色单元格 */
.role-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.role-name {
  font-weight: 600;
  color: #0f172a;
}

.role-code {
  font-size: 12px;
  color: #94a3b8;
  font-family: 'SF Mono', Monaco, monospace;
}

/* 状态标签 */
.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

/* 数据权限标签 */
.scope-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn.edit {
  background: #dbeafe;
  color: #2563eb;
}

.action-btn.edit:hover {
  background: #bfdbfe;
}

.action-btn.permission {
  background: #fef3c7;
  color: #d97706;
}

.action-btn.permission:hover {
  background: #fde68a;
}

.action-btn.delete {
  background: #fee2e2;
  color: #dc2626;
}

.action-btn.delete:hover {
  background: #fecaca;
}

/* 弹窗样式 */
.form-modal,
.permission-modal {
  width: 560px;
}

.form-modal :deep(.n-card-header),
.permission-modal :deep(.n-card-header) {
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.form-modal :deep(.n-card-header__main),
.permission-modal :deep(.n-card-header__main) {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

.form-modal :deep(.n-card__content),
.permission-modal :deep(.n-card__content) {
  padding: 24px;
}

.form-modal :deep(.n-card__footer),
.permission-modal :deep(.n-card__footer) {
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
}

/* 表单样式 */
.form-modal :deep(.n-form-item-label) {
  font-weight: 500;
  color: #334155;
}

.form-modal :deep(.n-input),
.form-modal :deep(.n-input-number),
.form-modal :deep(.n-select) {
  --n-border-radius: 8px;
  --n-height: 40px;
}

/* 权限工具栏 */
.permission-toolbar {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.permission-toolbar :deep(.n-button) {
  border-radius: 6px;
}

/* 权限树 */
.permission-tree {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px 0;
}

.permission-tree :deep(.n-tree-node-content) {
  padding: 8px 12px;
  border-radius: 6px;
}

.permission-tree :deep(.n-tree-node-content:hover) {
  background: #f1f5f9;
}

/* 自定义滚动条 */
.permission-tree::-webkit-scrollbar {
  width: 6px;
}

.permission-tree::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.permission-tree::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.permission-tree::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .stats-row {
    flex-wrap: wrap;
  }

  .mini-stat {
    flex: 1;
    min-width: 140px;
  }
}

@media (max-width: 768px) {
  .role-page {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
  }

  .primary-btn {
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

  .form-modal,
  .permission-modal {
    width: 100% !important;
    max-width: 560px;
  }
}

/* 减少动画 - 无障碍 */
@media (prefers-reduced-motion: reduce) {
  .mini-stat,
  .primary-btn,
  .action-btn,
  .search-input {
    transition: none;
  }

  .primary-btn:hover {
    transform: none;
  }
}
</style>
