<template>
  <div class="user-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户管理</h1>
        <p class="page-subtitle">管理系统用户账号、角色分配和权限控制</p>
      </div>
      <div class="header-actions">
        <n-button type="primary" @click="handleAdd" class="primary-btn">
          <template #icon>
            <n-icon :component="PlusOutlined" />
          </template>
          新增用户
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
            placeholder="搜索用户名、姓名..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="filter-group">
          <n-tree-select
            v-model:value="queryParams.deptId"
            :options="departmentOptions"
            placeholder="选择部门"
            clearable
            filterable
            class="filter-select"
          />
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
        :pagination="pagination as any"
        :row-key="(row: User) => row.id"
        :scroll-x="1200"
        :bordered="false"
        :single-line="false"
        flex-height
        class="data-table"
        @update:page="handlePageChange"
        @update:page-size="handlePageSizeChange"
      />
    </div>

    <!-- 新增/编辑用户弹窗 -->
    <n-modal
      v-model:show="showFormModal"
      :mask-closable="false"
      preset="card"
      :title="formMode === 'add' ? '新增用户' : '编辑用户'"
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
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <n-spin :show="roleLoading">
        <n-checkbox-group v-model:value="selectedRoleIds">
          <n-space vertical>
            <n-checkbox
              v-for="role in roleList"
              :key="role.id"
              :value="role.id"
              :label="role.roleName"
            >
              {{ role.roleName }}
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
      :segmented="{ content: 'soft', footer: 'soft' }"
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
import { ref, reactive, onMounted, h, computed, markRaw } from 'vue'
import {
  NButton,
  NIcon,
  NTreeSelect,
  NSelect,
  NDataTable,
  NModal,
  NForm,
  NFormItem,
  NInput,
  NRadioGroup,
  NRadio,
  NCheckboxGroup,
  NCheckbox,
  NSpin,
  NSpace,
  NPopconfirm,
  useMessage,
  type DataTableColumns,
  type FormInst,
  type FormRules,
  type FormItemRule,
  type PaginationProps
} from 'naive-ui'
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  UserOutlined,
  LockOutlined,
  TeamOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined
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

// 消息提示实例
const message = useMessage()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '全部用户', value: '0', icon: markRaw(TeamOutlined), class: 'blue' },
  { key: 'enabled', label: '已启用', value: '0', icon: markRaw(CheckCircleOutlined), class: 'green' },
  { key: 'disabled', label: '已禁用', value: '0', icon: markRaw(CloseCircleOutlined), class: 'gray' }
])

// 表单引用
const formRef = ref<FormInst | null>(null)
const passwordFormRef = ref<FormInst | null>(null)

// 加载状态
const loading = ref(false)
const submitting = ref(false)
const roleLoading = ref(false)

// 查询参数
const queryParams = reactive<UserQueryParams>({
  current: 1,
  size: 10,
  keyword: undefined,
  deptId: undefined,
  status: undefined
})

// 表格数据
const tableData = ref<User[]>([])

// 分页配置
const pagination = reactive<PaginationProps>({
  page: 1,
  pageSize: 10,
  pageCount: 0,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  prefix: (info) => `共 ${info.itemCount ?? 0} 条`
})

// 部门选项
const departmentOptions = ref<any[]>([])

// 状态选项
const statusOptions = [
  { label: '全部', value: undefined },
  { label: '启用', value: UserStatus.ENABLED },
  { label: '禁用', value: UserStatus.DISABLED }
]

// 表单模式：add-新增，edit-编辑
const formMode = ref<'add' | 'edit'>('add')

// 显示表单弹窗
const showFormModal = ref(false)

// 表单数据
const formData = reactive<UserFormData>({
  username: '',
  realName: '',
  phone: undefined,
  email: undefined,
  password: undefined,
  deptId: undefined,
  status: UserStatus.ENABLED
})

// 表单验证规则
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
      validator: (_rule: FormItemRule, value: string) => {
        if (formMode.value === 'add' && !value) {
          return new Error('请输入密码')
        }
        return true
      },
      trigger: 'blur'
    },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
}

// 显示角色分配弹窗
const showRoleModal = ref(false)

// 当前操作的用户 ID
const currentUserId = ref<number>(0)

// 角色列表
const roleList = ref<Role[]>([])

// 已选择的角色 ID 列表
const selectedRoleIds = ref<number[]>([])

// 显示重置密码弹窗
const showPasswordModal = ref(false)

// 重置密码表单
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

// 重置密码表单验证规则
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

// 状态颜色映射
const statusColorMap: Record<number, { bg: string; color: string; label: string }> = {
  [UserStatus.ENABLED]: { bg: '#dcfce7', color: '#22c55e', label: '启用' },
  [UserStatus.DISABLED]: { bg: '#f1f5f9', color: '#64748b', label: '禁用' }
}

// 获取状态标签
const getStatusTag = (status: number) => {
  const config = statusColorMap[status] || statusColorMap[UserStatus.DISABLED]
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
const columns: DataTableColumns<User> = [
  {
    title: '用户名',
    key: 'username',
    width: 150,
    render: (row) =>
      h('div', { class: 'user-cell' }, [
        h('span', { class: 'user-name' }, row.username),
        h('span', { class: 'user-realname' }, row.realName)
      ])
  },
  {
    title: '部门',
    key: 'deptName',
    width: 150,
    render: (row) => row.deptName || '-'
  },
  {
    title: '角色',
    key: 'roleNames',
    width: 200,
    render: (row) => {
      if (!row.roleNames || row.roleNames.length === 0) return '-'
      return h(
        'div',
        { class: 'role-tags' },
        row.roleNames.map((name) => h('span', { class: 'role-tag' }, name))
      )
    }
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
    width: 240,
    fixed: 'right',
    render: (row) =>
      h('div', { class: 'action-buttons' }, [
        h('button', { class: 'action-btn edit', onClick: () => handleEdit(row) }, [
          h(NIcon, { size: 14 }, { default: () => h(EditOutlined) }), '编辑'
        ]),
        h('button', { class: 'action-btn role', onClick: () => handleOpenRoleModal(row) }, [
          h(NIcon, { size: 14 }, { default: () => h(UserOutlined) }), '角色'
        ]),
        h('button', { class: 'action-btn password', onClick: () => handleOpenPasswordModal(row) }, [
          h(NIcon, { size: 14 }, { default: () => h(LockOutlined) }), '密码'
        ]),
        h(NPopconfirm, { onPositiveClick: () => handleDelete(row.id) }, {
          default: () => '确定要删除该用户吗？',
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
  const enabled = tableData.value.filter(u => u.status === UserStatus.ENABLED).length
  const disabled = tableData.value.filter(u => u.status === UserStatus.DISABLED).length
  miniStats.value[0].value = String(total)
  miniStats.value[1].value = String(enabled)
  miniStats.value[2].value = String(disabled)
}

// 加载用户列表
async function loadUserList() {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.page = Number(res.data.current)
      pagination.pageSize = Number(res.data.size)
      pagination.pageCount = Number(res.data.pages)
      pagination.itemCount = Number(res.data.total)
      updateStats()
    }
  } catch (error) {
    message.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 加载部门树
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

// 转换部门树为树选择器格式
function convertDepartmentTree(departments: Department[]): any[] {
  return departments.map((dept) => ({
    label: dept.name,
    key: dept.id,
    children: dept.children ? convertDepartmentTree(dept.children) : undefined
  }))
}

// 搜索
function handleSearch() {
  queryParams.current = 1
  pagination.page = 1
  loadUserList()
}

// 重置
function handleReset() {
  queryParams.keyword = undefined
  queryParams.deptId = undefined
  queryParams.status = undefined
  handleSearch()
}

// 页码变化
function handlePageChange(page: number) {
  queryParams.current = page
  pagination.page = page
  loadUserList()
}

// 每页大小变化
function handlePageSizeChange(pageSize: number) {
  queryParams.size = pageSize
  queryParams.current = 1
  pagination.pageSize = pageSize
  pagination.page = 1
  loadUserList()
}

// 新增用户
function handleAdd() {
  formMode.value = 'add'
  resetFormData()
  showFormModal.value = true
}

// 编辑用户
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

// 提交表单
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
    // 表单验证失败
  } finally {
    submitting.value = false
  }
}

// 删除用户
async function handleDelete(id: string) {
  try {
    const res = await deleteUser(id)
    if (res.code === 200) {
      message.success('删除用户成功')
      loadUserList()
    }
  } catch (error) {
    message.error('删除用户失败')
  }
}

// 打开分配角色弹窗
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
  } finally {
    roleLoading.value = false
  }
}

// 分配角色
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
  } finally {
    submitting.value = false
  }
}

// 打开重置密码弹窗
function handleOpenPasswordModal(row: User) {
  currentUserId.value = row.id
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  showPasswordModal.value = true
}

// 重置密码
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
    // 表单验证失败
  } finally {
    submitting.value = false
  }
}

// 重置表单数据
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

// 组件挂载时加载数据
onMounted(() => {
  loadUserList()
  loadDepartmentTree()
})
</script>

<style scoped>
/* ========================================
   用户管理页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.user-page {
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

/* 用户单元格 */
:deep(.user-cell) {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

:deep(.user-name) {
  font-weight: 500;
  color: #0f172a;
}

:deep(.user-realname) {
  font-size: 12px;
  color: #94a3b8;
}

/* 角色标签 */
:deep(.role-tags) {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

:deep(.role-tag) {
  display: inline-flex;
  padding: 2px 8px;
  background: #dbeafe;
  color: #2563eb;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
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

:deep(.action-btn.edit) {
  color: #2563eb;
}

:deep(.action-btn.edit:hover) {
  background: #dbeafe;
}

:deep(.action-btn.role) {
  color: #8b5cf6;
}

:deep(.action-btn.role:hover) {
  background: #f3e8ff;
}

:deep(.action-btn.password) {
  color: #f59e0b;
}

:deep(.action-btn.password:hover) {
  background: #fef3c7;
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

.role-modal {
  width: 500px;
}

.password-modal {
  width: 450px;
}

/* 角色描述 */
.role-description {
  color: #94a3b8;
  font-size: 12px;
  margin-left: 4px;
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

  .form-modal,
  .role-modal,
  .password-modal {
    width: 100% !important;
    max-width: 500px;
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
