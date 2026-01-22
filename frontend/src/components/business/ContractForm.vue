<template>
  <div class="contract-form">
    <n-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="80"
      require-mark-placement="right-hanging"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h4 class="section-title">基本信息</h4>
        <div class="form-grid">
          <n-form-item label="合同名称" path="name">
            <n-input
              v-model:value="formData.name"
              placeholder="请输入合同名称"
              maxlength="200"
            />
          </n-form-item>
          <n-form-item label="合同编号" path="contractNo">
            <n-input
              v-model:value="formData.contractNo"
              placeholder="请输入合同编号（可选）"
              maxlength="50"
            />
          </n-form-item>
          <n-form-item label="关联客户" path="customerId">
            <n-select
              v-model:value="formData.customerId"
              placeholder="请选择客户"
              :options="customerOptions"
              filterable
              remote
              :loading="customerLoading"
              @search="handleCustomerSearch"
              @update:value="handleCustomerChange"
            />
          </n-form-item>
          <n-form-item label="关联商机" path="opportunityId">
            <n-select
              v-model:value="formData.opportunityId"
              placeholder="请选择商机（可选）"
              :options="opportunityOptions"
              :disabled="!formData.customerId"
              clearable
            />
          </n-form-item>
          <n-form-item label="合同金额" path="amount">
            <n-input-number
              v-model:value="formData.amount"
              placeholder="请输入合同金额"
              :min="0"
              :precision="2"
              :show-button="false"
              class="amount-input"
            >
              <template #prefix>¥</template>
            </n-input-number>
          </n-form-item>
          <n-form-item label="负责人" path="ownerId">
            <n-select
              v-model:value="formData.ownerId"
              placeholder="请选择负责人"
              :options="userOptions"
              filterable
            />
          </n-form-item>
        </div>
      </div>

      <!-- 日期信息 -->
      <div class="form-section">
        <h4 class="section-title">日期信息</h4>
        <div class="form-grid">
          <n-form-item label="签约日期" path="signDate">
            <n-date-picker
              v-model:formatted-value="formData.signDate"
              type="date"
              placeholder="请选择签约日期"
              value-format="yyyy-MM-dd"
              class="date-picker"
            />
          </n-form-item>
          <n-form-item label="开始日期" path="startDate">
            <n-date-picker
              v-model:formatted-value="formData.startDate"
              type="date"
              placeholder="请选择开始日期"
              value-format="yyyy-MM-dd"
              class="date-picker"
            />
          </n-form-item>
          <n-form-item label="结束日期" path="endDate">
            <n-date-picker
              v-model:formatted-value="formData.endDate"
              type="date"
              placeholder="请选择结束日期"
              value-format="yyyy-MM-dd"
              class="date-picker"
            />
          </n-form-item>
        </div>
      </div>

      <!-- 备注信息 -->
      <div class="form-section">
        <h4 class="section-title">备注信息</h4>
        <n-form-item label="备注" path="remark" :label-width="80">
          <n-input
            v-model:value="formData.remark"
            type="textarea"
            placeholder="请输入备注信息"
            :rows="4"
            maxlength="500"
            show-count
          />
        </n-form-item>
      </div>
    </n-form>

    <!-- 底部按钮 -->
    <div class="form-footer">
      <n-button @click="handleCancel" class="cancel-btn">取消</n-button>
      <n-button type="primary" @click="handleSubmit" :loading="submitting" class="submit-btn">
        {{ isEdit ? '保存修改' : '创建合同' }}
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import {
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NSelect,
  NDatePicker,
  NButton,
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import { createContract, updateContract } from '@/api/business/contract'
import { pageCustomers } from '@/api/business/customer'
import { pageOpportunities } from '@/api/business/opportunity'
import { pageUsers } from '@/api/system/user'
import type { Contract, ContractFormData } from '@/types/business/contract'
import type { User } from '@/types/system'

// 扩展表单数据类型定义，允许 date 为 null
interface LocalContractFormData extends Omit<ContractFormData, 'startDate' | 'endDate' | 'signDate'> {
  startDate: string | null
  endDate: string | null
  signDate: string | null
}

const props = defineProps<{
  formData: Partial<Contract>
}>()

const emit = defineEmits<{
  (e: 'submit'): void
  (e: 'cancel'): void
}>()

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => !!props.formData?.id)

// 表单数据
const formData = ref<LocalContractFormData>({
  name: '',
  contractNo: '',
  customerId: undefined as unknown as number,
  opportunityId: undefined,
  amount: 0,
  startDate: null,
  endDate: null,
  signDate: null,
  ownerId: undefined,
  remark: ''
})

// 客户选项
const customerOptions = ref<{ label: string; value: number }[]>([])
const customerLoading = ref(false)

// 商机选项
const opportunityOptions = ref<{ label: string; value: number }[]>([])

// 用户选项
const userOptions = ref<{ label: string; value: number }[]>([])

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入合同名称', trigger: 'blur' },
    { min: 2, max: 200, message: '合同名称长度必须在2-200个字符之间', trigger: 'blur' }
  ],
  customerId: [
    { required: true, type: 'number', message: '请选择客户', trigger: 'change' }
  ],
  amount: [
    { required: true, type: 'number', message: '请输入合同金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '合同金额不能为负数', trigger: 'blur' }
  ]
}

// 初始化表单数据
const initFormData = () => {
  if (props.formData) {
    formData.value = {
      id: props.formData.id,
      name: props.formData.name || '',
      contractNo: props.formData.contractNo || '',
      customerId: props.formData.customerId as number,
      opportunityId: props.formData.opportunityId,
      amount: props.formData.amount || 0,
      startDate: props.formData.startDate || null,
      endDate: props.formData.endDate || null,
      signDate: props.formData.signDate || null,
      ownerId: props.formData.ownerId,
      remark: props.formData.remark || ''
    }

    // 如果有客户ID，加载客户信息
    if (props.formData.customerId && props.formData.customerName) {
      customerOptions.value = [{
        label: props.formData.customerName,
        value: props.formData.customerId
      }]
      // 加载该客户的商机
      loadOpportunities(props.formData.customerId)
    }
  }
}

// 搜索客户
const handleCustomerSearch = async (query: string) => {
  if (!query) return
  customerLoading.value = true
  try {
    const result = await pageCustomers({ pageNum: 1, pageSize: 20, name: query })
    customerOptions.value = result.records.map(c => ({
      label: c.name,
      value: c.id
    }))
  } catch (error) {
    console.error('搜索客户失败', error)
  } finally {
    customerLoading.value = false
  }
}

// 客户变更
const handleCustomerChange = (customerId: string) => {
  formData.value.opportunityId = undefined
  if (customerId) {
    loadOpportunities(customerId)
  } else {
    opportunityOptions.value = []
  }
}

// 加载商机列表
const loadOpportunities = async (customerId: string) => {
  try {
    const result = await pageOpportunities({ pageNum: 1, pageSize: 100, customerId })
    opportunityOptions.value = result.records.map(o => ({
      label: o.name,
      value: o.id
    }))
  } catch (error) {
    console.error('加载商机失败', error)
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    const result = await pageUsers({ pageNum: 1, pageSize: 100 })
    userOptions.value = (result.data?.records || []).map((u: User) => ({
      label: u.realName || u.username,
      value: u.id
    }))
  } catch (error) {
    console.error('加载用户失败', error)
  }
}

// 监听 props 变化
watch(() => props.formData, () => {
  initFormData()
}, { immediate: true })

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    submitting.value = true

    const submitData: ContractFormData = {
      name: formData.value.name,
      contractNo: formData.value.contractNo || undefined,
      customerId: formData.value.customerId,
      opportunityId: formData.value.opportunityId,
      amount: formData.value.amount,
      startDate: formData.value.startDate || undefined,
      endDate: formData.value.endDate || undefined,
      signDate: formData.value.signDate || undefined,
      ownerId: formData.value.ownerId,
      remark: formData.value.remark || undefined
    }

    if (isEdit.value && formData.value.id) {
      await updateContract(formData.value.id, submitData)
      message.success('合同更新成功')
    } else {
      await createContract(submitData)
      message.success('合同创建成功')
    }

    emit('submit')
  } catch (error: any) {
    if (error?.message) {
      message.error(error.message)
    }
  } finally {
    submitting.value = false
  }
}

// 取消
const handleCancel = () => {
  emit('cancel')
}

onMounted(() => {
  initFormData()
  loadUsers()
})
</script>

<style scoped>
.contract-form {
  padding: 8px 0;
}

/* 表单分区 */
.form-section {
  margin-bottom: 24px;
}

.form-section:last-of-type {
  margin-bottom: 0;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f5f9;
}

/* 表单网格 */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px 24px;
}

/* 金额输入框 */
.amount-input {
  width: 100%;
}

.amount-input :deep(.n-input__prefix) {
  color: #64748b;
}

/* 日期选择器 */
.date-picker {
  width: 100%;
}

/* 表单项样式 */
:deep(.n-form-item) {
  margin-bottom: 0;
}

:deep(.n-form-item-label) {
  font-size: 13px;
  color: #475569;
}

:deep(.n-input),
:deep(.n-select),
:deep(.n-input-number),
:deep(.n-date-picker) {
  --n-height: 40px;
  --n-border-radius: 8px;
}

:deep(.n-input--textarea) {
  --n-height: auto;
}

/* 底部按钮 */
.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.cancel-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
  font-weight: 500;
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
}

.cancel-btn:hover {
  border-color: #cbd5e1;
  color: #334155;
}

.submit-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
  font-weight: 500;
  background: #2563eb;
  border: none;
}

.submit-btn:hover {
  background: #1d4ed8;
}

/* 响应式设计 */
@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
