<template>
  <div class="permission-management-page">
    <!-- 顶部工具栏 -->
    <n-card class="toolbar-card" :bordered="false">
      <n-space vertical :size="16">
        <n-space :size="12" :wrap="false" class="toolbar-row">
          <!-- 搜索框 -->
          <n-input
            v-model:value="queryParams.keyword"
            placeholder="搜索权限名称或权限标识"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <n-icon><SearchOutlined /></n-icon>
            </template>
          </n-input>

          <!-- 类型筛选 -->
          <n-select
            v-model:value="queryParams.type"
            :options="typeOptions"
            placeholder="选择类型"
            clearable
            class="filter-select"
            @update:value="handleSearch"
          />

          <!-- 搜索按钮 -->
          <n-button type="primary" @click="handleSearch">
            <template #icon>
              <n-icon><SearchOutlined /></n-icon>
            </template>
            搜索
          </n-button>

          <!-- 重置按钮 -->
          <n-button @click="handleReset">
            <template #icon>
              <n-icon><ReloadOutlined /></n-icon>
            </template>
            重置
          </n-button>

          <!-- 展开/收起按钮 -->
          <n-button @click="handleToggleExpand">
            <template #icon>
              <n-icon><component :is="expandAll ? UpOutlined : DownOutlined" /></n-icon>
            </template>
            {{ expandAll ? '收起全部' : '展开全部' }}
          </n-button>

          <!-- 新增权限按钮 -->
          <n-button type="primary" @click="handleAdd">
            <template #icon>
              <n-icon><PlusOutlined /></n-icon>
            </template>
            新增权限
          </n-button>
        </n-space>
      </n-space>
    </n-card>

    <!-- 权限树形表格 -->
    <n-card class="table-card" :bordered="false">
      <n-data-table
        :columns="columns"
        :data="permissionList"
        :loading="loading"
        :row-key="(row: Permission) => row.id"
        :default-expand-all="expandAll"
        class="permission-table"
      />
    </n-card>

    <!-- 新增/编辑权限弹窗 -->
    <n-modal
      v-model:show="showModal"
      :mask-closable="false"
      preset="card"
      :title="modalTitle"
      class="permission-modal"
      style="width: 600px"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-placement="left"
        label-width="100px"
        require-mark-placement="left"
      >
        <n-form-item label="上级权限" path="parentId">
          <n-tree-select
            v-model:value="formData.parentId"
            :options="parentOptions"
            placeholder="选择上级权限（不选则为根权限）"
            clearable
            filterable
          />
        </n-form-item>

        <n-form-item label="权限类型" path="type">
          <n-radio-group v-model:value="formData.type">
            <n-radio :value="PermissionType.DIRECTORY">目录</n-radio>
            <n-radio :value="PermissionType.MENU">菜单</n-radio>
            <n-radio :value="PermissionType.BUTTON">按钮</n-radio>
          </n-radio-group>
        </n-form-item>

        <n-form-item label="权限名称" path="name">
          <n-input
            v-model:value="formData.name"
            placeholder="请输入权限名称"
            maxlength="50"
            show-count
          />
        </n-form-item>

        <n-form-item label="权限标识" path="permissionKey">
          <n-input
            v-model:value="formData.permissionKey"
            placeholder="如: system:user:add"
            :disabled="isEdit"
          />
        </n-form-item>

        <n-form-item
          v-if="formData.type === PermissionType.MENU"
          label="路由路径"
          path="path"
        >
          <n-input
            v-model:value="formData.path"
            placeholder="如: /system/user"
          />
        </n-form-item>

        <n-form-item
          v-if="formData.type === PermissionType.MENU"
          label="组件路径"
          path="component"
        >
          <n-input
            v-model:value="formData.component"
            placeholder="如: system/user/index"
          />
        </n-form-item>

        <n-form-item label="图标" path="icon">
          <n-input
            v-model:value="formData.icon"
            placeholder="图标名称（可选）"
          />
        </n-form-item>

        <n-form-item label="排序" path="sort">
          <n-input-number
            v-model:value="formData.sort"
            :min="0"
            :max="9999"
            placeholder="排序号"
            style="width: 100%"
          />
        </n-form-item>

        <n-form-item label="是否可见" path="visible">
          <n-radio-group v-model:value="formData.visible">
            <n-radio :value="1">显示</n-radio>
            <n-radio :value="0">隐藏</n-radio>
          </n-radio-group>
        </n-form-item>

        <n-form-item label="状态" path="status">
          <n-radio-group v-model:value="formData.status">
            <n-radio :value="PermissionStatus.ENABLED">启用</n-radio>
            <n-radio :value="PermissionStatus.DISABLED">禁用</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">
            保存
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h, onMounted, computed } from 'vue'
import { NButton, NTag, NIcon, NSpace, useMessage, useDialog, type DataTableColumns, type FormRules, type FormInst } from 'naive-ui'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  UpOutlined,
  DownOutlined
} from '@vicons/antd'
import { getPermissionTree, createPermission, updatePermission, deletePermission } from '@/api/system/permission'
import { Permission, PermissionType, PermissionStatus, PermissionQueryParams, PermissionFormData } from '@/types/system'

const message = useMessage()
const dialog = useDialog()

// 数据状态
const loading = ref(false)
const permissionList = ref<Permission[]>([])
const expandAll = ref(true)

// 查询参数
const queryParams = reactive<PermissionQueryParams>({
  keyword: '',
  type: undefined
})

// 类型选项
const typeOptions = [
  { label: '全部', value: undefined },
  { label: '目录', value: PermissionType.DIRECTORY },
  { label: '菜单', value: PermissionType.MENU },
  { label: '按钮', value: PermissionType.BUTTON }
]

// 表格列定义
const columns: DataTableColumns<Permission> = [
  {
    title: '权限名称',
    key: 'name',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: '权限标识',
    key: 'permissionKey',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: '类型',
    key: 'type',
    width: 100,
    align: 'center',
    render: (row) => {
      const typeMap = {
        [PermissionType.DIRECTORY]: { label: '目录', type: 'info' as const },
        [PermissionType.MENU]: { label: '菜单', type: 'success' as const },
        [PermissionType.BUTTON]: { label: '按钮', type: 'warning' as const }
      }
      const config = typeMap[row.type]
      return h(NTag, { type: config.type, size: 'small' }, { default: () => config.label })
    }
  },
  {
    title: '路由路径',
    key: 'path',
    width: 180,
    ellipsis: { tooltip: true }
  },
  {
    title: '组件路径',
    key: 'component',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: '图标',
    key: 'icon',
    width: 100,
    align: 'center'
  },
  {
    title: '排序',
    key: 'sort',
    width: 80,
    align: 'center'
  },
  {
    title: '状态',
    key: 'visible',
    width: 100,
    align: 'center',
    render: (row) => {
      return h(
        NTag,
        { type: row.visible === 1 ? 'success' : 'default', size: 'small' },
        { default: () => (row.visible === 1 ? '显示' : '隐藏') }
      )
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 220,
    align: 'center',
    fixed: 'right',
    render: (row) => {
      return h(
        NSpace,
        { size: 8 },
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
              { default: () => '新增', icon: () => h(NIcon, null, { default: () => h(PlusOutlined) }) }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                text: true,
                onClick: () => handleEdit(row)
              },
              { default: () => '编辑', icon: () => h(NIcon, null, { default: () => h(EditOutlined) }) }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                text: true,
                onClick: () => handleDelete(row)
              },
              { default: () => '删除', icon: () => h(NIcon, null, { default: () => h(DeleteOutlined) }) }
            )
          ]
        }
      )
    }
  }
]

// 弹窗状态
const showModal = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInst | null>(null)

const modalTitle = computed(() => (isEdit.value ? '编辑权限' : '新增权限'))

// 表单数据
const formData = reactive<PermissionFormData>({
  parentId: 0,
  name: '',
  permissionKey: '',
  type: PermissionType.MENU,
  path: '',
  component: '',
  icon: '',
  sort: 0,
  visible: 1,
  status: PermissionStatus.ENABLED
})

// 表单验证规则
const formRules: FormRules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '权限名称长度为2-50个字符', trigger: 'blur' }
  ],
  permissionKey: [
    { required: true, message: '请输入权限标识', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9:_-]+$/,
      message: '权限标识只能包含字母、数字、冒号、下划线和连字符',
      trigger: 'blur'
    }
  ],
  type: [{ required: true, type: 'number', message: '请选择权限类型', trigger: 'change' }],
  path: [
    {
      validator: (_rule, value) => {
        if (formData.type === PermissionType.MENU && !value) {
          return new Error('菜单类型必须填写路由路径')
        }
        return true
      },
      trigger: 'blur'
    }
  ],
  component: [
    {
      validator: (_rule, value) => {
        if (formData.type === PermissionType.MENU && !value) {
          return new Error('菜单类型必须填写组件路径')
        }
        return true
      },
      trigger: 'blur'
    }
  ]
}

// 上级权限选项
const parentOptions = computed(() => {
  return convertToTreeSelect(permissionList.value)
})

// 转换为树选择器格式
function convertToTreeSelect(permissions: Permission[]): any[] {
  return permissions.map((item) => ({
    label: item.name,
    value: item.id,
    children: item.children && item.children.length > 0 ? convertToTreeSelect(item.children) : undefined
  }))
}

// 获取权限树
async function fetchPermissionTree() {
  try {
    loading.value = true
    const res = await getPermissionTree(queryParams)
    permissionList.value = res.data || []
  } catch (error: any) {
    message.error(error.message || '获取权限列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  fetchPermissionTree()
}

// 重置
function handleReset() {
  queryParams.keyword = ''
  queryParams.type = undefined
  fetchPermissionTree()
}

// 展开/收起
function handleToggleExpand() {
  expandAll.value = !expandAll.value
}

// 新增
function handleAdd() {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

// 新增子权限
function handleAddChild(row: Permission) {
  isEdit.value = false
  resetForm()
  formData.parentId = row.id
  showModal.value = true
}

// 编辑
function handleEdit(row: Permission) {
  isEdit.value = true
  Object.assign(formData, {
    id: row.id,
    parentId: row.parentId,
    name: row.name,
    permissionKey: row.permissionKey,
    type: row.type,
    path: row.path || '',
    component: row.component || '',
    icon: row.icon || '',
    sort: row.sort,
    visible: row.visible,
    status: row.status
  })
  showModal.value = true
}

// 删除
function handleDelete(row: Permission) {
  if (row.children && row.children.length > 0) {
    message.warning('该权限下有子权限，无法删除')
    return
  }

  dialog.warning({
    title: '确认删除',
    content: `确定要删除权限"${row.name}"吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deletePermission(row.id)
        message.success('删除成功')
        fetchPermissionTree()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    }
  })
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value?.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updatePermission(formData.id!, formData)
      message.success('编辑成功')
    } else {
      await createPermission(formData)
      message.success('新增成功')
    }

    showModal.value = false
    fetchPermissionTree()
  } catch (error: any) {
    if (error?.errorFields) {
      message.error('请检查表单输入')
    } else {
      message.error(error?.message || '操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
function resetForm() {
  Object.assign(formData, {
    id: undefined,
    parentId: 0,
    name: '',
    permissionKey: '',
    type: PermissionType.MENU,
    path: '',
    component: '',
    icon: '',
    sort: 0,
    visible: 1,
    status: PermissionStatus.ENABLED
  })
  formRef.value?.restoreValidation()
}

// 初始化
onMounted(() => {
  fetchPermissionTree()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;500;600;700&family=Poppins:wght@400;500;600;700&display=swap');

.permission-management-page {
  padding: 16px;
  font-family: 'Open Sans', sans-serif;
}

.toolbar-card {
  margin-bottom: 16px;
  border-radius: 12px;
}

.toolbar-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.search-input {
  width: 280px;
}

.filter-select {
  width: 160px;
}

.table-card {
  border-radius: 12px;
}

.permission-table {
  font-family: 'Open Sans', sans-serif;
}

.permission-modal :deep(.n-card__header) {
  font-family: 'Poppins', sans-serif;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .toolbar-row {
    flex-direction: column;
  }

  .search-input,
  .filter-select {
    width: 100%;
  }

  .permission-modal {
    width: 100% !important;
    max-width: 600px;
  }
}

/* 可访问性 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}

:deep(.n-button) {
  cursor: pointer;
  transition: all 0.2s ease;
}

:deep(.n-button:hover) {
  transform: translateY(-1px);
}

:deep(.n-button:active) {
  transform: translateY(0);
}
</style>
