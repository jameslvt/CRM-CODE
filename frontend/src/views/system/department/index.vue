<template>
  <div class="org-manager">
    <!-- 左侧：组织树导航 -->
    <aside class="org-sidebar">
      <div class="sidebar-header">
        <h2 class="sidebar-title">
          <n-icon :component="ApartmentOutlined" size="20" />
          组织架构
        </h2>
        <n-button quaternary circle size="small" @click="handleAdd" class="add-root-btn">
          <template #icon>
            <n-icon :component="PlusOutlined" />
          </template>
        </n-button>
      </div>
      
      <!-- 搜索框 -->
      <div class="sidebar-search">
        <n-icon :component="SearchOutlined" size="16" class="search-icon" />
        <input
          v-model="queryParams.keyword"
          type="text"
          placeholder="搜索部门..."
          class="search-input"
          @keyup.enter="handleSearch"
        />
      </div>
      
      <!-- 组织树 -->
      <div class="org-tree-wrapper">
        <n-spin :show="loading" size="small">
          <div class="org-tree">
            <OrgTreeNode
              v-for="dept in tableData"
              :key="dept.id"
              :dept="dept"
              :level="0"
              :selected-id="selectedDeptId"
              @select="handleSelectDept"
              @add-child="handleAddChild"
              @edit="handleEdit"
              @delete="handleDelete"
            />
            <div v-if="!loading && tableData.length === 0" class="empty-state">
              <n-icon :component="ApartmentOutlined" size="32" color="#94a3b8" />
              <p>暂无部门数据</p>
              <n-button size="small" type="primary" @click="handleAdd">创建第一个部门</n-button>
            </div>
          </div>
        </n-spin>
      </div>
      
      <!-- 统计信息 -->
      <div class="sidebar-stats">
        <div class="stat-item">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">总部门</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value enabled">{{ stats.enabled }}</span>
          <span class="stat-label">已启用</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value disabled">{{ stats.disabled }}</span>
          <span class="stat-label">已禁用</span>
        </div>
      </div>
    </aside>
    
    <!-- 右侧：部门详情 -->
    <main class="org-content">
      <template v-if="selectedDept">
        <!-- 部门详情卡片 -->
        <div class="detail-card">
          <div class="detail-header">
            <div class="dept-badge" :class="{ disabled: selectedDept.status === 0 }">
              <n-icon :component="ApartmentOutlined" size="24" />
            </div>
            <div class="dept-info">
              <h1 class="dept-title">{{ selectedDept.name }}</h1>
              <div class="dept-meta">
                <span class="dept-code">{{ selectedDept.code }}</span>
                <span class="status-badge" :class="selectedDept.status === 1 ? 'active' : 'inactive'">
                  {{ selectedDept.status === 1 ? '启用' : '禁用' }}
                </span>
              </div>
            </div>
            <div class="detail-actions">
              <n-button @click="handleEdit(selectedDept)" class="action-btn edit-btn">
                <template #icon><n-icon :component="EditOutlined" /></template>
                编辑
              </n-button>
              <n-button @click="handleDelete(selectedDept.id)" class="action-btn delete-btn">
                <template #icon><n-icon :component="DeleteOutlined" /></template>
                删除
              </n-button>
            </div>
          </div>
          
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">负责人</span>
              <span class="detail-value">{{ selectedDept.leaderName || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">排序号</span>
              <span class="detail-value">{{ selectedDept.sort }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ formatTime(selectedDept.createTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">更新时间</span>
              <span class="detail-value">{{ formatTime(selectedDept.updateTime) }}</span>
            </div>
          </div>
          
          <!-- 子部门列表 -->
          <div v-if="selectedDept.children && selectedDept.children.length > 0" class="children-section">
            <h3 class="section-title">
              <n-icon :component="DownOutlined" size="14" />
              下级部门 ({{ selectedDept.children.length }})
            </h3>
            <div class="children-list">
              <div
                v-for="child in selectedDept.children"
                :key="child.id"
                class="child-card"
                @click="handleSelectDept(child)"
              >
                <div class="child-icon">
                  <n-icon :component="ApartmentOutlined" size="16" />
                </div>
                <div class="child-info">
                  <span class="child-name">{{ child.name }}</span>
                  <span class="child-code">{{ child.code }}</span>
                </div>
                <span class="child-status" :class="child.status === 1 ? 'active' : 'inactive'">
                  {{ child.status === 1 ? '启用' : '禁用' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>
      
      <!-- 空状态 -->
      <div v-else class="empty-content">
        <div class="empty-illustration">
          <n-icon :component="ApartmentOutlined" size="64" color="#cbd5e1" />
        </div>
        <h3 class="empty-title">选择一个部门查看详情</h3>
        <p class="empty-desc">在左侧组织架构树中选择部门，或创建新部门</p>
        <n-button type="primary" @click="handleAdd">
          <template #icon><n-icon :component="PlusOutlined" /></template>
          创建部门
        </n-button>
      </div>
    </main>
    
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
        label-width="80"
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
            placeholder="请输入部门编码"
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
            <n-space>
              <n-radio :value="1">启用</n-radio>
              <n-radio :value="0">禁用</n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showFormModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleSubmit">
            {{ formMode === 'add' ? '创建' : '保存' }}
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 删除确认弹窗 -->
    <n-modal
      v-model:show="showDeleteModal"
      preset="dialog"
      title="删除确认"
      type="warning"
      :positive-text="deleteCheckResult.deletable ? '确认删除' : undefined"
      negative-text="取消"
      :loading="deleteChecking"
      @positive-click="confirmDelete"
    >
      <n-spin :show="deleteChecking">
        <div class="delete-warning">
          <template v-if="deleteCheckResult.deletable">
            <p>确定要删除该部门吗？此操作不可恢复。</p>
          </template>
          <template v-else>
            <n-alert type="warning" :bordered="false">
              <template v-if="deleteCheckResult.hasChildren">
                <p>该部门下有 <strong>{{ deleteCheckResult.childrenCount }}</strong> 个子部门，请先删除或移动子部门。</p>
              </template>
              <template v-else-if="deleteCheckResult.hasUsers">
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
import { ref, reactive, computed, onMounted, h, defineComponent } from 'vue'
import {
  NButton,
  NIcon,
  NSelect,
  NTreeSelect,
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
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import {
  SearchOutlined,
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  DownOutlined,
  ApartmentOutlined
} from '@vicons/antd'
import type { Department, DepartmentFormData, User } from '@/types/system'
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

// 组织树节点组件
const OrgTreeNode = defineComponent({
  name: 'OrgTreeNode',
  props: {
    dept: { type: Object as () => Department, required: true },
    level: { type: Number, default: 0 },
    selectedId: { type: [Number, String], default: null }
  },
  emits: ['select', 'add-child', 'edit', 'delete'],
  setup(props, { emit }) {
    const expanded = ref(true)
    const hasChildren = computed(() => props.dept.children && props.dept.children.length > 0)
    const isSelected = computed(() => props.selectedId === props.dept.id)
    
    const toggleExpand = (e: Event) => {
      e.stopPropagation()
      expanded.value = !expanded.value
    }
    
    const handleSelect = () => {
      emit('select', props.dept)
    }
    
    const handleAddChild = (e: Event) => {
      e.stopPropagation()
      emit('add-child', props.dept)
    }
    
    return () => h('div', { class: 'tree-node-container' }, [
      // 连接线
      props.level > 0 ? h('div', {
        class: 'tree-connector',
        style: { left: `${(props.level - 1) * 24 + 12}px` }
      }) : null,
      
      // 节点卡片
      h('div', {
        class: ['tree-node', { selected: isSelected.value, disabled: props.dept.status === 0 }],
        style: { marginLeft: `${props.level * 24}px` },
        onClick: handleSelect
      }, [
        // 展开/收起按钮
        hasChildren.value ? h('button', {
          class: ['expand-btn', { expanded: expanded.value }],
          onClick: toggleExpand
        }, [
          h(NIcon, { size: 12 }, { default: () => h(DownOutlined) })
        ]) : h('div', { class: 'expand-placeholder' }),
        
        // 部门图标
        h('div', { class: 'node-icon' }, [
          h(NIcon, { component: ApartmentOutlined, size: 14 })
        ]),
        
        // 部门名称
        h('span', { class: 'node-name' }, props.dept.name),
        
        // 悬停操作
        h('div', { class: 'node-actions' }, [
          h('button', {
            class: 'node-action add',
            onClick: handleAddChild,
            title: '添加子部门'
          }, [h(NIcon, { size: 12 }, { default: () => h(PlusOutlined) })])
        ])
      ]),
      
      // 子节点
      hasChildren.value && expanded.value ? h('div', { class: 'tree-children' },
        props.dept.children!.map(child =>
          h(OrgTreeNode, {
            key: child.id,
            dept: child,
            level: props.level + 1,
            selectedId: props.selectedId,
            onSelect: (d: Department) => emit('select', d),
            onAddChild: (d: Department) => emit('add-child', d),
            onEdit: (d: Department) => emit('edit', d),
            onDelete: (id: number) => emit('delete', id)
          })
        )
      ) : null
    ])
  }
})

// 消息提示实例
const message = useMessage()

// 状态
const loading = ref(false)
const submitting = ref(false)
const deleteChecking = ref(false)
const tableData = ref<Department[]>([])
const selectedDeptId = ref<number | string | null>(null)
const selectedDept = computed(() => findDeptById(tableData.value, selectedDeptId.value))

// 查询参数
const queryParams = reactive<{ keyword?: string; status?: number }>({
  keyword: undefined,
  status: undefined
})

// 统计
const stats = computed(() => {
  let total = 0, enabled = 0, disabled = 0
  const count = (depts: Department[]) => {
    depts.forEach(d => {
      total++
      d.status === 1 ? enabled++ : disabled++
      if (d.children) count(d.children)
    })
  }
  count(tableData.value)
  return { total, enabled, disabled }
})

// 表单相关
const showFormModal = ref(false)
const formMode = ref<'add' | 'edit'>('add')
const formRef = ref<FormInst | null>(null)
const formData = reactive<DepartmentFormData>({
  id: undefined,
  parentId: undefined,
  name: '',
  code: '',
  leaderId: undefined,
  sort: 0,
  status: DepartmentStatus.ENABLED
})

const formRules: FormRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 50, message: '部门名称长度为2-50个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入部门编码', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序号', trigger: 'blur', type: 'number' }
  ]
}

const departmentTreeOptions = ref<any[]>([])
const userOptions = ref<any[]>([])

// 删除相关
const showDeleteModal = ref(false)
const currentDeleteId = ref<number>(0)
const deleteCheckResult = ref<{
  deletable: boolean
  hasChildren: boolean
  hasUsers: boolean
  childrenCount: number
  usersCount: number
}>({
  deletable: true,
  hasChildren: false,
  hasUsers: false,
  childrenCount: 0,
  usersCount: 0
})

// 工具函数
function findDeptById(depts: Department[], id: number | string | null): Department | null {
  if (!id) return null
  for (const dept of depts) {
    if (dept.id === id) return dept
    if (dept.children) {
      const found = findDeptById(dept.children, id)
      if (found) return found
    }
  }
  return null
}

function formatTime(time?: string): string {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

function convertDepartmentTree(departments: Department[], excludeId?: number): any[] {
  return departments
    .filter(dept => dept.id !== excludeId)
    .map((dept) => {
      const option: any = { label: dept.name, key: dept.id }
      if (dept.children && dept.children.length > 0) {
        const children = convertDepartmentTree(dept.children, excludeId)
        if (children.length > 0) option.children = children
      }
      return option
    })
}

function resetFormData() {
  Object.assign(formData, {
    id: undefined,
    parentId: undefined,
    name: '',
    code: '',
    leaderId: undefined,
    sort: 0,
    status: DepartmentStatus.ENABLED
  })
}

// API 调用
async function loadDepartmentTree() {
  loading.value = true
  try {
    const res = await getDepartmentTree(queryParams)
    if (res.code === 200) {
      tableData.value = res.data
      // 自动选择第一个部门
      if (res.data.length > 0 && !selectedDeptId.value) {
        selectedDeptId.value = res.data[0].id
      }
    }
  } catch (error) {
    console.error('加载部门树失败', error)
  } finally {
    loading.value = false
  }
}

async function loadUserList() {
  try {
    const res = await getAllUsers()
    if (res.code === 200) {
      userOptions.value = res.data.map((user: User) => ({
        label: `${user.nickname || user.username} (${user.username})`,
        value: user.id
      }))
    }
  } catch (error) {
    console.error('加载用户列表失败', error)
  }
}

// 事件处理
function handleSearch() {
  loadDepartmentTree()
}

function handleSelectDept(dept: Department) {
  selectedDeptId.value = dept.id
}

function handleAdd() {
  formMode.value = 'add'
  resetFormData()
  departmentTreeOptions.value = convertDepartmentTree(tableData.value)
  showFormModal.value = true
}

function handleAddChild(row: Department) {
  formMode.value = 'add'
  resetFormData()
  formData.parentId = row.id as number
  departmentTreeOptions.value = convertDepartmentTree(tableData.value)
  showFormModal.value = true
}

function handleEdit(row: Department) {
  formMode.value = 'edit'
  Object.assign(formData, {
    id: row.id,
    name: row.name,
    code: row.code,
    parentId: row.parentId,
    leaderId: row.leaderId,
    sort: row.sort,
    status: row.status
  })
  departmentTreeOptions.value = convertDepartmentTree(tableData.value, row.id as number)
  showFormModal.value = true
}

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

async function handleDelete(id: number | string) {
  currentDeleteId.value = id as number
  showDeleteModal.value = true
  deleteChecking.value = true

  try {
    const res = await checkDepartmentDeletable(id as number)
    if (res.code === 200) {
      deleteCheckResult.value = res.data
    }
  } catch (error) {
    deleteCheckResult.value = {
      deletable: false,
      hasChildren: false,
      hasUsers: false,
      childrenCount: 0,
      usersCount: 0
    }
  } finally {
    deleteChecking.value = false
  }
}

async function confirmDelete() {
  try {
    const res = await deleteDepartment(currentDeleteId.value)
    if (res.code === 200) {
      message.success('删除部门成功')
      if (selectedDeptId.value === currentDeleteId.value) {
        selectedDeptId.value = null
      }
      loadDepartmentTree()
    }
  } catch (error) {
    message.error('删除失败')
  }
}

// 初始化
onMounted(() => {
  loadDepartmentTree()
  loadUserList()
})
</script>

<style scoped>
/* ========================================
   CSS 变量 - 亮色现代风格
   ======================================== */
.org-manager {
  --primary: #2563eb;
  --primary-light: #dbeafe;
  --primary-dark: #1d4ed8;
  --success: #10b981;
  --success-light: #d1fae5;
  --content-bg: #f1f5f9;
  --card-bg: #ffffff;
  --border: #e2e8f0;
  --border-light: #f1f5f9;
  --text: #0f172a;
  --text-secondary: #475569;
  --text-muted: #94a3b8;
  --danger: #ef4444;
  --danger-light: #fee2e2;
  
  display: flex;
  height: 100%;
  gap: 20px;
  padding: 20px;
  background: var(--content-bg);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', sans-serif;
}

/* ========================================
   左侧边栏 - 亮色卡片风格
   ======================================== */
.org-sidebar {
  width: 340px;
  min-width: 300px;
  background: var(--card-bg);
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  border: 1px solid var(--border);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  overflow: hidden;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
  border-bottom: 1px solid var(--border);
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--text);
}

.sidebar-title :deep(.n-icon) {
  color: var(--primary);
}

.add-root-btn {
  color: var(--text-muted);
  transition: all 0.2s;
}

.add-root-btn:hover {
  color: var(--primary);
  background: var(--primary-light);
}

.sidebar-search {
  position: relative;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-light);
}

.sidebar-search .search-icon {
  position: absolute;
  left: 28px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
}

.sidebar-search .search-input {
  width: 100%;
  padding: 10px 12px 10px 36px;
  background: var(--content-bg);
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--text);
  font-size: 13px;
  outline: none;
  transition: all 0.2s;
}

.sidebar-search .search-input::placeholder {
  color: var(--text-muted);
}

.sidebar-search .search-input:focus {
  border-color: var(--primary);
  background: #fff;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

/* 组织树 */
.org-tree-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.org-tree {
  padding: 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 20px;
  text-align: center;
}

.empty-state p {
  margin: 0;
  color: var(--text-muted);
  font-size: 13px;
}

/* 树节点 - 亮色风格 */
:deep(.tree-node-container) {
  position: relative;
}

:deep(.tree-connector) {
  position: absolute;
  top: 0;
  width: 1px;
  height: 20px;
  background: var(--border);
}

:deep(.tree-connector::before) {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 12px;
  height: 1px;
  background: var(--border);
}

:deep(.tree-node) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  margin: 2px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
  border: 1px solid transparent;
}

:deep(.tree-node:hover) {
  background: var(--content-bg);
  border-color: var(--border);
}

:deep(.tree-node.selected) {
  background: var(--primary-light);
  border-color: var(--primary);
}

:deep(.tree-node.selected .node-icon) {
  background: var(--primary);
  color: white;
}

:deep(.tree-node.selected .node-name) {
  color: var(--primary-dark);
  font-weight: 600;
}

:deep(.tree-node.disabled) {
  opacity: 0.5;
}

:deep(.expand-btn) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 4px;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.2s;
}

:deep(.expand-btn:hover) {
  background: var(--border);
  color: var(--text-secondary);
}

:deep(.expand-btn.expanded) {
  transform: rotate(0deg);
}

:deep(.expand-btn:not(.expanded)) {
  transform: rotate(-90deg);
}

:deep(.expand-placeholder) {
  width: 20px;
  height: 20px;
}

:deep(.node-icon) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: var(--primary-light);
  color: var(--primary);
  flex-shrink: 0;
}

:deep(.node-name) {
  flex: 1;
  font-size: 13px;
  font-weight: 500;
  color: var(--text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

:deep(.node-actions) {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.15s;
}

:deep(.tree-node:hover .node-actions) {
  opacity: 1;
}

:deep(.tree-node.selected .node-actions) {
  opacity: 1;
}

:deep(.node-action) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 6px;
  background: var(--card-bg);
  color: var(--primary);
  cursor: pointer;
  transition: all 0.15s;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

:deep(.node-action:hover) {
  background: var(--primary);
  color: white;
  transform: scale(1.05);
}

:deep(.tree-children) {
  position: relative;
}

/* 统计信息 - 亮色风格 */
.sidebar-stats {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 14px 16px;
  border-top: 1px solid var(--border);
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--text);
}

.stat-value.enabled {
  color: var(--success);
}

.stat-value.disabled {
  color: var(--text-muted);
}

.stat-label {
  font-size: 11px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.03em;
}

.stat-divider {
  width: 1px;
  height: 28px;
  background: var(--border);
}

/* ========================================
   右侧内容区
   ======================================== */
.org-content {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 16px;
}

.empty-illustration {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f1f5f9;
}

.empty-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
}

.empty-desc {
  margin: 0;
  font-size: 14px;
  color: var(--text-muted);
}

/* 详情卡片 */
.detail-card {
  background: var(--card-bg);
  border-radius: 16px;
  border: 1px solid var(--border);
  overflow: hidden;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid var(--border);
}

.dept-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
}

.dept-badge.disabled {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
  box-shadow: 0 4px 12px rgba(100, 116, 139, 0.2);
}

.dept-info {
  flex: 1;
}

.dept-title {
  margin: 0 0 6px;
  font-size: 22px;
  font-weight: 700;
  color: var(--text);
  letter-spacing: -0.02em;
}

.dept-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dept-code {
  font-size: 13px;
  color: var(--text-muted);
  font-family: 'JetBrains Mono', monospace;
}

.status-badge {
  display: inline-flex;
  padding: 3px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.active {
  background: var(--primary-light);
  color: #059669;
}

.status-badge.inactive {
  background: #fef3c7;
  color: #d97706;
}

.detail-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  height: 36px;
  padding: 0 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
}

.edit-btn {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: var(--text);
}

.edit-btn:hover {
  background: #e2e8f0;
}

.delete-btn {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: var(--danger);
}

.delete-btn:hover {
  background: #fee2e2;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1px;
  background: var(--border);
  padding: 1px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 20px;
  background: var(--card-bg);
}

.detail-label {
  font-size: 12px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: 14px;
  font-weight: 500;
  color: var(--text);
}

/* 子部门区域 */
.children-section {
  padding: 20px 24px;
  border-top: 1px solid var(--border);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

.children-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}

.child-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  background: #f8fafc;
  border: 1px solid var(--border);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.15s;
}

.child-card:hover {
  background: #f1f5f9;
  border-color: var(--primary);
  transform: translateY(-1px);
}

.child-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: var(--primary-light);
  color: var(--primary);
}

.child-info {
  flex: 1;
  min-width: 0;
}

.child-name {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: var(--text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.child-code {
  font-size: 11px;
  color: var(--text-muted);
  font-family: 'JetBrains Mono', monospace;
}

.child-status {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 100px;
}

.child-status.active {
  background: var(--primary-light);
  color: #059669;
}

.child-status.inactive {
  background: #fef3c7;
  color: #d97706;
}

/* ========================================
   弹窗样式
   ======================================== */
.form-modal {
  width: 560px;
}

.delete-warning {
  padding: 8px 0;
}

.delete-warning p {
  margin: 0;
  line-height: 1.6;
}

.delete-warning strong {
  color: var(--danger);
  font-weight: 600;
}

/* ========================================
   响应式
   ======================================== */
@media (max-width: 1024px) {
  .org-sidebar {
    width: 280px;
    min-width: 240px;
  }
  
  .detail-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .org-manager {
    flex-direction: column;
  }
  
  .org-sidebar {
    width: 100%;
    max-height: 40vh;
  }
  
  .org-content {
    padding: 16px;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .form-modal {
    width: 100% !important;
  }
}
</style>
