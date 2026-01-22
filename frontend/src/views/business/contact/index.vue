<template>
  <div class="contact-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">联系人管理</h1>
        <p class="page-subtitle">管理客户联系人信息，维护客户关系</p>
      </div>
      <div class="header-actions">
        <n-button type="primary" @click="handleAdd" class="primary-btn">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新增联系人
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
            placeholder="搜索联系人姓名..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="filter-group">
          <n-input
            v-model:value="searchParams.mobile"
            placeholder="手机号"
            clearable
            class="filter-input"
          />
          <n-select
            v-model:value="searchParams.isPrimary"
            placeholder="联系人类型"
            :options="primaryOptions"
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
    </div>

    <!-- 数据表格 -->
    <div class="table-card">
      <n-data-table
        :columns="columns"
        :data="dataList"
        :loading="loading"
        :pagination="pagination as any"
        :row-key="(row: Contact) => row.id"
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
      <n-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-placement="left"
        label-width="80"
        require-mark-placement="right-hanging"
      >
        <n-form-item label="所属客户" path="customerId">
          <n-select
            v-model:value="formData.customerId"
            placeholder="请选择客户"
            :options="customerOptions"
            filterable
            remote
            :loading="customerLoading"
            @search="handleCustomerSearch"
          />
        </n-form-item>
        <n-form-item label="姓名" path="name">
          <n-input v-model:value="formData.name" placeholder="请输入姓名" />
        </n-form-item>
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="性别" path="gender">
              <n-select
                v-model:value="formData.gender"
                placeholder="请选择性别"
                :options="genderOptions"
                clearable
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="职位" path="position">
              <n-input v-model:value="formData.position" placeholder="请输入职位" />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="部门" path="department">
              <n-input v-model:value="formData.department" placeholder="请输入部门" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="手机" path="mobile">
              <n-input v-model:value="formData.mobile" placeholder="请输入手机号" />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="电话" path="phone">
              <n-input v-model:value="formData.phone" placeholder="请输入电话" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="邮箱" path="email">
              <n-input v-model:value="formData.email" placeholder="请输入邮箱" />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="微信" path="wechat">
              <n-input v-model:value="formData.wechat" placeholder="请输入微信号" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="生日" path="birthday">
              <n-date-picker
                v-model:formatted-value="formData.birthday"
                type="date"
                clearable
                value-format="yyyy-MM-dd"
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-form-item label="主要联系人" path="isPrimary">
          <n-switch v-model:value="formData.isPrimary" />
        </n-form-item>
        <n-form-item label="备注" path="remark">
          <n-input
            v-model:value="formData.remark"
            type="textarea"
            placeholder="请输入备注"
            :rows="3"
          />
        </n-form-item>
      </n-form>
      <template #footer>
        <div class="modal-footer">
          <n-button @click="showFormModal = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import {
  NButton,
  NIcon,
  NSelect,
  NInput,
  NDataTable,
  NModal,
  NForm,
  NFormItem,
  NGrid,
  NGi,
  NDatePicker,
  NSwitch,
  NPopconfirm,
  NTag,
  useMessage,
  type DataTableColumns,
  type PaginationProps,
  type FormInst,
  type FormRules
} from 'naive-ui'
import {
  AddOutline,
  SearchOutline,
  CreateOutline,
  TrashOutline,
  CallOutline,
  PersonOutline,
  StarOutline,
  PeopleOutline
} from '@vicons/ionicons5'
import {
  pageAllContacts,
  saveContact,
  updateContact,
  deleteContact,
  setPrimaryContact
} from '@/api/business/contact'
import { pageCustomers } from '@/api/business/customer'
import type { Contact, ContactQueryParams, ContactFormData } from '@/types/business/contact'
import { genderOptions, getGenderName, getGenderColor } from '@/types/business/contact'

const router = useRouter()
const message = useMessage()

// 迷你统计数据
const miniStats = ref([
  { key: 'total', label: '全部联系人', value: '0', icon: markRaw(PersonOutline), class: 'blue' },
  { key: 'primary', label: '主要联系人', value: '0', icon: markRaw(StarOutline), class: 'orange' },
  { key: 'male', label: '男性', value: '0', icon: markRaw(PersonOutline), class: 'green' },
  { key: 'female', label: '女性', value: '0', icon: markRaw(PersonOutline), class: 'pink' }
])

// 搜索参数
const searchParams = reactive<ContactQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  mobile: '',
  isPrimary: undefined
})

// 主要联系人选项
const primaryOptions = [
  { label: '主要联系人', value: 1 },
  { label: '普通联系人', value: 0 }
]

const loading = ref(false)
const dataList = ref<Contact[]>([])
const showFormModal = ref(false)
const formTitle = ref('新增联系人')
const submitLoading = ref(false)
const formRef = ref<FormInst | null>(null)

// 表单数据
const formData = reactive<ContactFormData>({
  customerId: 0,
  name: '',
  gender: undefined,
  position: '',
  department: '',
  phone: '',
  mobile: '',
  email: '',
  wechat: '',
  isPrimary: false,
  birthday: undefined,
  remark: ''
})

// 客户选项
const customerOptions = ref<{ label: string; value: number }[]>([])
const customerLoading = ref(false)

// 表单验证规则
const formRules: FormRules = {
  customerId: { required: true, type: 'number', message: '请选择客户', trigger: 'change' },
  name: { required: true, message: '请输入姓名', trigger: 'blur' }
}

// 分页配置
const pagination = reactive<PaginationProps>({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100],
  prefix: (info) => `共 ${info.itemCount ?? 0} 条`,
  onChange: (page: number) => {
    if (searchParams.pageNum !== undefined) searchParams.pageNum = page
    loadData()
  },
  onUpdatePageSize: (pageSize: number) => {
    if (searchParams.pageSize !== undefined) searchParams.pageSize = pageSize
    if (searchParams.pageNum !== undefined) searchParams.pageNum = 1
    loadData()
  }
})

// 获取性别标签
const getGenderTag = (gender: number | undefined) => {
  const name = getGenderName(gender)
  const color = getGenderColor(gender)
  return h(
    'span',
    {
      class: 'gender-tag',
      style: { background: color.bg, color: color.color }
    },
    name
  )
}

// 表格列配置
const columns: DataTableColumns<Contact> = [
  {
    title: '联系人信息',
    key: 'name',
    width: 200,
    render: (row) =>
      h('div', { class: 'contact-info-cell' }, [
        h('div', { class: 'contact-main' }, [
          h('span', { class: 'contact-name' }, row.name),
          row.isPrimary === 1 && h('span', { class: 'primary-badge' }, '主要')
        ]),
        h('div', { class: 'contact-position' }, [
          row.position && h('span', {}, row.position),
          row.position && row.department && h('span', { class: 'separator' }, ' · '),
          row.department && h('span', {}, row.department)
        ])
      ])
  },
  {
    title: '所属客户',
    key: 'customerName',
    width: 160,
    render: (row) =>
      h(
        'span',
        {
          class: 'customer-link',
          onClick: () => router.push(`/business/customer/${row.customerId}`)
        },
        row.customerName || '-'
      )
  },
  {
    title: '性别',
    key: 'gender',
    width: 80,
    render: (row) => getGenderTag(row.gender)
  },
  {
    title: '手机',
    key: 'mobile',
    width: 130,
    render: (row) => h('span', { class: 'phone-text' }, row.mobile || '-')
  },
  {
    title: '邮箱',
    key: 'email',
    width: 180,
    ellipsis: { tooltip: true }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 120,
    render: (row) => row.createTime?.split(' ')[0] || '-'
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    fixed: 'right',
    render: (row) =>
      h('div', { class: 'action-buttons' }, [
        row.isPrimary !== 1 &&
          h(
            'button',
            {
              class: 'action-btn primary',
              onClick: () => handleSetPrimary(row.id)
            },
            [h(NIcon, { size: 14 }, { default: () => h(StarOutline) }), '设为主要']
          ),
        h(
          'button',
          {
            class: 'action-btn edit',
            onClick: () => handleEdit(row)
          },
          [h(NIcon, { size: 14 }, { default: () => h(CreateOutline) }), '编辑']
        ),
        h(
          NPopconfirm,
          {
            onPositiveClick: () => handleDelete(row.id)
          },
          {
            default: () => '确定要删除该联系人吗？',
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
  const primary = dataList.value.filter((c) => c.isPrimary === 1).length
  const male = dataList.value.filter((c) => c.gender === 1).length
  const female = dataList.value.filter((c) => c.gender === 2).length

  miniStats.value[0].value = String(total)
  miniStats.value[1].value = String(primary)
  miniStats.value[2].value = String(male)
  miniStats.value[3].value = String(female)
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const result = await pageAllContacts(searchParams)
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

// 搜索客户
const handleCustomerSearch = async (query: string) => {
  if (!query) {
    customerOptions.value = []
    return
  }
  customerLoading.value = true
  try {
    const result = await pageCustomers({ pageNum: 1, pageSize: 20, name: query })
    customerOptions.value = result.records.map((c) => ({
      label: c.name,
      value: c.id
    }))
  } catch (error) {
    console.error('搜索客户失败', error)
  } finally {
    customerLoading.value = false
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
  searchParams.mobile = ''
  searchParams.isPrimary = undefined
  searchParams.pageNum = 1
  loadData()
}

// 新增
const handleAdd = () => {
  formTitle.value = '新增联系人'
  Object.assign(formData, {
    id: undefined,
    customerId: 0,
    name: '',
    gender: undefined,
    position: '',
    department: '',
    phone: '',
    mobile: '',
    email: '',
    wechat: '',
    isPrimary: false,
    birthday: undefined,
    remark: ''
  })
  customerOptions.value = []
  showFormModal.value = true
}

// 编辑
const handleEdit = (row: Contact) => {
  formTitle.value = '编辑联系人'
  Object.assign(formData, {
    id: row.id,
    customerId: row.customerId,
    name: row.name,
    gender: row.gender,
    position: row.position || '',
    department: row.department || '',
    phone: row.phone || '',
    mobile: row.mobile || '',
    email: row.email || '',
    wechat: row.wechat || '',
    isPrimary: row.isPrimary === 1,
    birthday: row.birthday || undefined,
    remark: row.remark || ''
  })
  // 设置客户选项
  if (row.customerName) {
    customerOptions.value = [{ label: row.customerName, value: row.customerId }]
  }
  showFormModal.value = true
}

// 删除
const handleDelete = async (id: string) => {
  try {
    await deleteContact(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    message.error('删除失败')
  }
}

// 设为主要联系人
const handleSetPrimary = async (id: string) => {
  try {
    await setPrimaryContact(id)
    message.success('设置成功')
    loadData()
  } catch (error) {
    message.error('设置失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitLoading.value = true

    const submitData = {
      ...formData,
      isPrimary: formData.isPrimary ? true : false
    }

    if (formData.id) {
      await updateContact(submitData)
      message.success('更新成功')
    } else {
      await saveContact(submitData)
      message.success('创建成功')
    }

    showFormModal.value = false
    loadData()
  } catch (error: any) {
    if (error.message) {
      message.error(error.message)
    }
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* ========================================
   联系人管理页面样式
   遵循章程UI/UX设计规范
   ======================================== */

.contact-page {
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

.mini-stat-icon.orange {
  background: #fef3c7;
  color: #f59e0b;
}

.mini-stat-icon.green {
  background: #dbeafe;
  color: #2563eb;
}

.mini-stat-icon.pink {
  background: #fce7f3;
  color: #ec4899;
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
  max-width: 300px;
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

.filter-input {
  width: 140px;
}

.filter-input :deep(.n-input) {
  --n-height: 42px;
  --n-border-radius: 10px;
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

/* 联系人信息单元格 */
:deep(.contact-info-cell) {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

:deep(.contact-main) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.contact-name) {
  font-weight: 500;
  color: #0f172a;
}

:deep(.primary-badge) {
  padding: 2px 6px;
  background: #fef3c7;
  color: #f59e0b;
  font-size: 11px;
  font-weight: 600;
  border-radius: 4px;
}

:deep(.contact-position) {
  font-size: 12px;
  color: #64748b;
}

:deep(.contact-position .separator) {
  color: #cbd5e1;
}

/* 客户链接 */
:deep(.customer-link) {
  color: #2563eb;
  cursor: pointer;
}

:deep(.customer-link:hover) {
  text-decoration: underline;
}

/* 性别标签 */
:deep(.gender-tag) {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

/* 电话文本 */
:deep(.phone-text) {
  font-family: 'SF Mono', Monaco, monospace;
  color: #475569;
}

/* 操作按钮 */
:deep(.action-buttons) {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
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

:deep(.action-btn.primary) {
  color: #f59e0b;
}

:deep(.action-btn.primary:hover) {
  background: #fef3c7;
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

.form-modal :deep(.n-card__content) {
  padding: 24px;
}

.form-modal :deep(.n-card__footer) {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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

  .filter-input,
  .filter-select {
    flex: 1;
    min-width: 100px;
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
