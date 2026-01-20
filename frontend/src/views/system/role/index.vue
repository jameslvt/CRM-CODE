<template>
  <div class="role-management-page">
    <!-- 顶部工具栏 -->
    <n-card class="toolbar-card" :bordered="false">
      <n-space vertical :size="16">
        <n-space :size="12" :wrap="false" class="toolbar-row">
          <!-- 搜索框 -->
          <n-input
            v-model:value="queryParams.keyword"
            placeholder="搜索角色名称或角色编码"
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

          <div class="flex-spacer"></div>

          <!-- 新增角色按钮 -->
          <n-button type="primary" @click="handleAdd">
            <template #icon>
              <n-icon :component="PlusOutlined" />
            </template>
            新增角色
          </n-button>
        </n-space>
      </n-space>
    </n-card>

    <!-- 数据表格 -->
    <n-card class="table-card" :bordered="false">
      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        :row-key="(row: Role) => row.id"
        :scroll-x="1400"
        striped
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
      />
    </n-card>

    <!-- 新增/编辑角色弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :mask-closable="false"
      preset="card"
      :title="formMode === 'add' ? '新增角色' : '编辑角色'"
      class="form-modal"
      :style="{ width: '600px' }"
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
      :style="{ width: '600px' }"
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
  NCard,
  NSpace,
  NInput,
  NSelect,
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
  NTree,
  NTag,
  NPopconfirm,
  useMessage,
  type DataTableColumns,
  type FormInst,
  type FormRules,
  type TreeInst
} from 'naive-ui'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  SafetyOutlined
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

/**
 * 消息提示实例
 */
const message = useMessage()

/**
 * 表单引用
 */
const formRef = ref<FormInst | null>(null)
const permissionTreeRef = ref<TreeInst | null>(null)

/**
 * 加载状态
 */
const loading = ref(false)
const submitting = ref(false)
const permissionLoading = ref(false)

/**
 * 查询参数
 */
const queryParams = reactive<RoleQueryParams>({
  current: 1,
  size: 10,
  keyword: undefined,
  status: undefined
})

/**
 * 表格数据
 */
const tableData = ref<Role[]>([])

/**
 * 分页配置
 */
const pagination = reactive({
  page: 1,
  pageSize: 10,
  pageCount: 0,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  prefix: (info: { itemCount: number }) => `共 ${info.itemCount} 条`
})

/**
 * 状态选项
 */
const statusOptions = [
  { label: '全部', value: undefined },
  { label: '启用', value: RoleStatus.ENABLED },
  { label: '禁用', value: RoleStatus.DISABLED }
]

/**
 * 数据权限范围选项
 */
const dataScopeOptions = [
  { label: '全部数据', value: DataScope.ALL },
  { label: '本部门及下级', value: DataScope.DEPT_AND_CHILD },
  { label: '本部门', value: DataScope.DEPT },
  { label: '仅本人', value: DataScope.SELF }
]

/**
 * 数据权限范围标签映射
 */
const dataScopeLabels: Record<DataScope, string> = {
  [DataScope.ALL]: '全部数据',
  [DataScope.DEPT_AND_CHILD]: '本部门及下级',
  [DataScope.DEPT]: '本部门',
  [DataScope.SELF]: '仅本人'
}

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
const formData = reactive<RoleFormData>({
  name: '',
  code: '',
  dataScope: DataScope.SELF,
  sort: 0,
  status: RoleStatus.ENABLED,
  remark: undefined
})

/**
 * 表单验证规则
 */
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

/**
 * 显示权限分配弹窗
 */
const showPermissionModal = ref(false)

/**
 * 当前操作的角色 ID
 */
const currentRoleId = ref<number>(0)

/**
 * 权限树数据
 */
const permissionTreeData = ref<Permission[]>([])

/**
 * 已选中的权限 ID 列表
 */
const checkedPermissionKeys = ref<number[]>([])

/**
 * 默认展开的节点
 */
const defaultExpandedKeys = ref<number[]>([])

/**
 * 表格列配置
 */
const columns: DataTableColumns<Role> = [
  {
    title: '序号',
    key: 'index',
    width: 70,
    align: 'center',
    render: (_row, index) => {
      return (pagination.page - 1) * pagination.pageSize + index + 1
    }
  },
  {
    title: '角色名称',
    key: 'name',
    width: 150,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '角色编码',
    key: 'code',
    width: 150,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '数据权限范围',
    key: 'dataScope',
    width: 150,
    align: 'center',
    render: (row) => {
      return h(
        NTag,
        {
          type: 'info',
          size: 'small'
        },
        {
          default: () => dataScopeLabels[row.dataScope]
        }
      )
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
          type: row.status === RoleStatus.ENABLED ? 'success' : 'error',
          size: 'small'
        },
        {
          default: () => (row.status === RoleStatus.ENABLED ? '启用' : '禁用')
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
                type: 'info',
                text: true,
                onClick: () => handleOpenPermissionModal(row)
              },
              {
                default: () => '分配权限',
                icon: () => h(NIcon, { component: SafetyOutlined })
              }
            ),
            h(
              NPopconfirm,
              {
                onPositiveClick: () => handleDelete(row.id)
              },
              {
                default: () => '确定要删除该角色吗？删除后使用该角色的用户将失去相应权限。',
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
                      icon: () => h(NIcon, { component: DeleteOutlined })
                    }
                  )
              }
            )
          ]
        }
      )
    }
  }
]

/**
 * 加载角色列表
 */
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
.role-management-page {
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

/* 权限工具栏 */
.permission-toolbar {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

/* 权限树 */
.permission-tree {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .role-management-page {
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

  .form-modal,
  .permission-modal {
    width: 100% !important;
    max-width: 600px;
  }
}

/* 可访问性：焦点状态 */
:deep(.n-button:focus-visible),
:deep(.n-input:focus-within),
:deep(.n-select:focus-within),
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
:deep(.n-data-table-wrapper),
.permission-tree {
  scrollbar-width: thin;
  scrollbar-color: #d1d5db #f3f4f6;
}

:deep(.n-data-table-wrapper::-webkit-scrollbar),
.permission-tree::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

:deep(.n-data-table-wrapper::-webkit-scrollbar-track),
.permission-tree::-webkit-scrollbar-track {
  background: #f3f4f6;
  border-radius: 4px;
}

:deep(.n-data-table-wrapper::-webkit-scrollbar-thumb),
.permission-tree::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 4px;
}

:deep(.n-data-table-wrapper::-webkit-scrollbar-thumb:hover),
.permission-tree::-webkit-scrollbar-thumb:hover {
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

/* 树形组件样式优化 */
:deep(.n-tree-node-content) {
  padding: 6px 0;
}

:deep(.n-tree-node-content:hover) {
  background-color: #f3f4f6;
}

/* 表单项间距优化 */
:deep(.n-form-item) {
  margin-bottom: 20px;
}

/* 输入框聚焦样式 */
:deep(.n-input:focus-within),
:deep(.n-input-number:focus-within),
:deep(.n-select:focus-within) {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
}
</style>
