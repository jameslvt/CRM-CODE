<template>
  <div class="user-management-page">
    <!-- 顶部工具栏 -->
    <n-card class="toolbar-card" :bordered="false">
      <n-space vertical :size="16">
        <n-space :size="12" :wrap="false" class="toolbar-row">
          <!-- 搜索框 -->
          <n-input
            v-model:value="queryParams.keyword"
            placeholder="搜索用户名或真实姓名"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <n-icon :component="SearchOutlined" />
            </template>
          </n-input>

          <!-- 部门筛选 -->
          <n-tree-select
            v-model:value="queryParams.deptId"
            :options="departmentOptions"
            placeholder="选择部门"
            clearable
            filterable
            class="filter-select"
            @update:value="handleSearch"
          />

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

          <!-- 新增用户按钮 -->
          <n-button type="primary" @click="handleAdd">
            <template #icon>
              <n-icon :component="PlusOutlined" />
            </template>
            新增用户
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
        :row-key="(row: User) => row.id"
        :scroll-x="1200"
        striped
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
      />
    </n-card>

    <!-- 新增/编辑用户弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :mask-closable="false"
      preset="card"
      :title="formMode === 'add' ? '新增用户' : '编辑用户'"
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
        <n-form-item label="用户名" path="username">
          <n-input
            v-model:value="formData.username"
            placeholder="请输入用户名（3-50字符）"
            :disabled="formMode === 'edit'"
            maxlength="50"
            show-count
          />
        </n-form-item>

        <n-form-item label="真实姓名" path="realName">
          <n-input
            v-model:value="formData.realName"
            placeholder="请输入真实姓名"
            maxlength="50"
            show-count
          />
        </n-form-item>

        <n-form-item label="手机号" path="phone">
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

        <n-form-item label="部门" path="deptId">
          <n-tree-select
            v-model:value="formData.deptId"
            :options="departmentOptions"
            placeholder="请选择部门"
            clearable
            filterable
          />
        </n-form-item>

        <n-form-item v-if="formMode === 'add'" label="密码" path="password">
          <n-input
            v-model:value="formData.password"
            type="password"
            show-password-on="click"
            placeholder="请输入密码（6-20字符）"
            maxlength="20"
          />
        </n-form-item>

        <n-form-item label="状态" path="status">
          <n-radio-group v-model:value="formData.status">
            <n-radio :value="UserStatus.ENABLED">启用</n-radio>
            <n-radio :value="UserStatus.DISABLED">禁用</n-radio>
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

    <!-- 分配角色弹窗 -->
    <n-modal
      v-model:show="showRoleModal"
      :mask-closable="false"
      preset="card"
      title="分配角色"
      class="role-modal"
      :style="{ width: '500px' }"
    >
      <n-spin :show="roleLoading">
        <n-checkbox-group v-model:value="selectedRoleIds">
          <n-space vertical>
            <n-checkbox
              v-for="role in roleList"
              :key="role.id"
              :value="role.id"
              :label="role.name"
            >
              {{ role.name }}
              <span v-if="role.description" class="role-description">
                ({{ role.description }})
              </span>
            </n-checkbox>
          </n-space>
        </n-checkbox-group>
      </n-spin>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showRoleModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleAssignRoles">
            保存
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 重置密码弹窗 -->
    <n-modal
      v-model:show="showPasswordModal"
      :mask-closable="false"
      preset="card"
      title="重置密码"
      class="password-modal"
      :style="{ width: '450px' }"
    >
      <n-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-placement="left"
        label-width="100"
      >
        <n-form-item label="新密码" path="newPassword">
          <n-input
            v-model:value="passwordForm.newPassword"
            type="password"
            show-password-on="click"
            placeholder="请输入新密码（6-20字符）"
            maxlength="20"
          />
        </n-form-item>

        <n-form-item label="确认密码" path="confirmPassword">
          <n-input
            v-model:value="passwordForm.confirmPassword"
            type="password"
            show-password-on="click"
            placeholder="请再次输入新密码"
            maxlength="20"
          />
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showPasswordModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleResetPassword">
            确认重置
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, computed } from 'vue'
import {
  NCard,
  NSpace,
  NInput,
  NTreeSelect,
  NSelect,
  NButton,
  NIcon,
  NDataTable,
  NModal,
  NForm,
  NFormItem,
  NRadioGroup,
  NRadio,
  NCheckboxGroup,
  NCheckbox,
  NSpin,
  NTag,
  NPopconfirm,
  useMessage,
  type DataTableColumns,
  type FormInst,
  type FormRules,
  type FormItemRule
} from 'naive-ui'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  UserOutlined,
  LockOutlined
} from '@vicons/antd'
import type { User, UserQueryParams, UserFormData, Department, Role } from '@/types/system'
import { UserStatus } from '@/types/system'
import {
  getUserList,
  createUser,
  updateUser,
  deleteUser,
  assignRoles,
  getUserRoles,
  resetPassword,
  getAllRoles,
  getDepartmentTree
} from '@/api/system/user'
import dayjs from 'dayjs'

/**
 * 消息提示实例
 */
const message = useMessage()

/**
 * 表单引用
 */
const formRef = ref<FormInst | null>(null)
const passwordFormRef = ref<FormInst | null>(null)

/**
 * 加载状态
 */
const loading = ref(false)
const submitting = ref(false)
const roleLoading = ref(false)

/**
 * 查询参数
 */
const queryParams = reactive<UserQueryParams>({
  current: 1,
  size: 10,
  keyword: undefined,
  deptId: undefined,
  status: undefined
})

/**
 * 表格数据
 */
const tableData = ref<User[]>([])

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
 * 部门选项
 */
const departmentOptions = ref<any[]>([])

/**
 * 状态选项
 */
const statusOptions = [
  { label: '全部', value: undefined },
  { label: '启用', value: UserStatus.ENABLED },
  { label: '禁用', value: UserStatus.DISABLED }
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
const formData = reactive<UserFormData>({
  username: '',
  realName: '',
  phone: undefined,
  email: undefined,
  password: undefined,
  deptId: undefined,
  status: UserStatus.ENABLED
})

/**
 * 表单验证规则
 */
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_]{3,50}$/,
      message: '用户名为3-50位字母、数字或下划线',
      trigger: 'blur'
    }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '真实姓名长度为2-50个字符', trigger: 'blur' }
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
  password: [
    {
      required: computed(() => formMode.value === 'add'),
      message: '请输入密码',
      trigger: 'blur'
    },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
}

/**
 * 显示角色分配弹窗
 */
const showRoleModal = ref(false)

/**
 * 当前操作的用户 ID
 */
const currentUserId = ref<number>(0)

/**
 * 角色列表
 */
const roleList = ref<Role[]>([])

/**
 * 已选择的角色 ID 列表
 */
const selectedRoleIds = ref<number[]>([])

/**
 * 显示重置密码弹窗
 */
const showPasswordModal = ref(false)

/**
 * 重置密码表单
 */
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

/**
 * 重置密码表单验证规则
 */
const passwordRules: FormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (_rule: FormItemRule, value: string) => {
        return value === passwordForm.newPassword
      },
      message: '两次输入的密码不一致',
      trigger: 'blur'
    }
  ]
}

/**
 * 表格列配置
 */
const columns: DataTableColumns<User> = [
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
    title: '用户名',
    key: 'username',
    width: 150,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '真实姓名',
    key: 'realName',
    width: 120,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '部门',
    key: 'deptName',
    width: 150,
    ellipsis: {
      tooltip: true
    },
    render: (row) => {
      return row.deptName || '-'
    }
  },
  {
    title: '角色',
    key: 'roleNames',
    width: 200,
    ellipsis: {
      tooltip: true
    },
    render: (row) => {
      if (!row.roleNames || row.roleNames.length === 0) {
        return '-'
      }
      return h(
        NSpace,
        { size: 4 },
        {
          default: () =>
            row.roleNames!.map((name) =>
              h(
                NTag,
                { type: 'info', size: 'small' },
                { default: () => name }
              )
            )
        }
      )
    }
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
          type: row.status === UserStatus.ENABLED ? 'success' : 'error',
          size: 'small'
        },
        {
          default: () => (row.status === UserStatus.ENABLED ? '启用' : '禁用')
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
    width: 280,
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
                onClick: () => handleOpenRoleModal(row)
              },
              {
                default: () => '分配角色',
                icon: () => h(NIcon, { component: UserOutlined })
              }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'warning',
                text: true,
                onClick: () => handleOpenPasswordModal(row)
              },
              {
                default: () => '重置密码',
                icon: () => h(NIcon, { component: LockOutlined })
              }
            ),
            h(
              NPopconfirm,
              {
                onPositiveClick: () => handleDelete(row.id)
              },
              {
                default: () => '确定要删除该用户吗？',
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
 * 加载用户列表
 */
async function loadUserList() {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.page = res.data.current
      pagination.pageSize = res.data.size
      pagination.pageCount = res.data.pages
      pagination.itemCount = res.data.total
    }
  } catch (error) {
    message.error('加载用户列表失败')
    console.error('加载用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载部门树
 */
async function loadDepartmentTree() {
  try {
    const res = await getDepartmentTree()
    if (res.code === 200) {
      departmentOptions.value = convertDepartmentTree(res.data)
    }
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

/**
 * 转换部门树为树选择器格式
 */
function convertDepartmentTree(departments: Department[]): any[] {
  return departments.map((dept) => ({
    label: dept.name,
    value: dept.id,
    children: dept.children ? convertDepartmentTree(dept.children) : undefined
  }))
}

/**
 * 搜索
 */
function handleSearch() {
  queryParams.current = 1
  pagination.page = 1
  loadUserList()
}

/**
 * 重置
 */
function handleReset() {
  queryParams.keyword = undefined
  queryParams.deptId = undefined
  queryParams.status = undefined
  handleSearch()
}

/**
 * 页码变化
 */
function handlePageChange(page: number) {
  queryParams.current = page
  pagination.page = page
  loadUserList()
}

/**
 * 每页大小变化
 */
function handlePageSizeChange(pageSize: number) {
  queryParams.size = pageSize
  queryParams.current = 1
  pagination.pageSize = pageSize
  pagination.page = 1
  loadUserList()
}

/**
 * 新增用户
 */
function handleAdd() {
  formMode.value = 'add'
  resetFormData()
  showFormModal.value = true
}

/**
 * 编辑用户
 */
function handleEdit(row: User) {
  formMode.value = 'edit'
  Object.assign(formData, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    phone: row.phone,
    email: row.email,
    deptId: row.deptId,
    status: row.status,
    password: undefined
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
      const res = await createUser(formData)
      if (res.code === 200) {
        message.success('新增用户成功')
        showFormModal.value = false
        loadUserList()
      }
    } else {
      const res = await updateUser(formData.id!, formData)
      if (res.code === 200) {
        message.success('编辑用户成功')
        showFormModal.value = false
        loadUserList()
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 删除用户
 */
async function handleDelete(id: number) {
  try {
    const res = await deleteUser(id)
    if (res.code === 200) {
      message.success('删除用户成功')
      loadUserList()
    }
  } catch (error) {
    message.error('删除用户失败')
    console.error('删除用户失败:', error)
  }
}

/**
 * 打开分配角色弹窗
 */
async function handleOpenRoleModal(row: User) {
  currentUserId.value = row.id
  showRoleModal.value = true
  roleLoading.value = true

  try {
    // 加载所有角色
    const rolesRes = await getAllRoles()
    if (rolesRes.code === 200) {
      roleList.value = rolesRes.data
    }

    // 加载用户已分配的角色
    const userRolesRes = await getUserRoles(row.id)
    if (userRolesRes.code === 200) {
      selectedRoleIds.value = userRolesRes.data
    }
  } catch (error) {
    message.error('加载角色数据失败')
    console.error('加载角色数据失败:', error)
  } finally {
    roleLoading.value = false
  }
}

/**
 * 分配角色
 */
async function handleAssignRoles() {
  submitting.value = true
  try {
    const res = await assignRoles({
      userId: currentUserId.value,
      roleIds: selectedRoleIds.value
    })
    if (res.code === 200) {
      message.success('分配角色成功')
      showRoleModal.value = false
      loadUserList()
    }
  } catch (error) {
    message.error('分配角色失败')
    console.error('分配角色失败:', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 打开重置密码弹窗
 */
function handleOpenPasswordModal(row: User) {
  currentUserId.value = row.id
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  showPasswordModal.value = true
}

/**
 * 重置密码
 */
async function handleResetPassword() {
  try {
    await passwordFormRef.value?.validate()
    submitting.value = true

    const res = await resetPassword({
      userId: currentUserId.value,
      newPassword: passwordForm.newPassword
    })

    if (res.code === 200) {
      message.success('重置密码成功')
      showPasswordModal.value = false
    }
  } catch (error) {
    console.error('表单验证失败:', error)
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
    username: '',
    realName: '',
    phone: undefined,
    email: undefined,
    password: undefined,
    deptId: undefined,
    status: UserStatus.ENABLED
  })
  formRef.value?.restoreValidation()
}

/**
 * 组件挂载时加载数据
 */
onMounted(() => {
  loadUserList()
  loadDepartmentTree()
})
</script>

<style scoped>
/* 页面容器 */
.user-management-page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  background-color: #f5f7fa;
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

/* 角色描述 */
.role-description {
  color: #999;
  font-size: 12px;
  margin-left: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management-page {
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
  .role-modal,
  .password-modal {
    width: 100% !important;
    max-width: 500px;
  }
}

/* 可访问性：焦点状态 */
:deep(.n-button:focus-visible),
:deep(.n-input:focus-within),
:deep(.n-select:focus-within),
:deep(.n-tree-select:focus-within) {
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
</style>
