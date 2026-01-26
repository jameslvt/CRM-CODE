<template>
  <div class="payment-plan-form">
    <n-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="80"
      require-mark-placement="right-hanging"
    >
      <n-form-item label="关联合同" path="contractId">
        <n-select
          v-model:value="formData.contractId"
          placeholder="请选择合同"
          :options="contractOptions"
          filterable
          remote
          :loading="contractLoading"
          @search="handleContractSearch"
          :disabled="isEdit"
        />
      </n-form-item>

      <n-form-item label="期数" path="period">
        <n-input-number
          v-model:value="formData.period"
          placeholder="请输入期数"
          :min="1"
          :max="100"
          class="full-width"
        />
      </n-form-item>

      <n-form-item label="计划金额" path="planAmount">
        <n-input-number
          v-model:value="formData.planAmount"
          placeholder="请输入计划金额"
          :min="0.01"
          :precision="2"
          :show-button="false"
          class="full-width"
        >
          <template #prefix>¥</template>
        </n-input-number>
      </n-form-item>

      <n-form-item label="计划日期" path="planDate">
        <n-date-picker
          v-model:formatted-value="formData.planDate"
          type="date"
          placeholder="请选择计划回款日期"
          value-format="yyyy-MM-dd"
          class="full-width"
        />
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

    <!-- 底部按钮 -->
    <div class="form-footer">
      <n-button @click="handleCancel" class="cancel-btn">取消</n-button>
      <n-button type="primary" @click="handleSubmit" :loading="submitting" class="submit-btn">
        {{ isEdit ? '保存修改' : '创建计划' }}
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
import { createPaymentPlan, updatePaymentPlan } from '@/api/business/payment'
import { pageContracts } from '@/api/business/contract'
import type { PaymentPlan, PaymentPlanFormData } from '@/types/business/payment'

// 扩展表单数据类型定义，允许 date 为 null
interface LocalPaymentPlanFormData extends Omit<PaymentPlanFormData, 'planDate'> {
  planDate: string | null
}

const props = defineProps<{
  formData: Partial<PaymentPlan>
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
const formData = ref<LocalPaymentPlanFormData>({
  contractId: undefined as unknown as number | string,
  period: 1,
  planAmount: 0,
  planDate: null,
  remark: ''
})

// 合同选项
const contractOptions = ref<{ label: string; value: number | string }[]>([])
const contractLoading = ref(false)

// 表单验证规则
const rules: FormRules = {
  contractId: [
    { required: true, message: '请选择合同', trigger: 'change' }
  ],
  period: [
    { required: true, type: 'number', message: '请输入期数', trigger: 'blur' },
    { type: 'number', min: 1, message: '期数必须大于0', trigger: 'blur' }
  ],
  planAmount: [
    { required: true, type: 'number', message: '请输入计划金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '计划金额必须大于0', trigger: 'blur' }
  ],
  planDate: [
    { required: true, message: '请选择计划回款日期', trigger: 'change' }
  ]
}

// 初始化表单数据
const initFormData = () => {
  if (props.formData) {
    formData.value = {
      id: props.formData.id,
      contractId: props.formData.contractId as (number | string),
      period: props.formData.period || 1,
      planAmount: props.formData.planAmount || 0,
      planDate: props.formData.planDate || null,
      remark: props.formData.remark || ''
    }

    // 如果有合同ID，加载合同信息
    if (props.formData.contractId && props.formData.contractName) {
      contractOptions.value = [{
        label: props.formData.contractName,
        value: props.formData.contractId
      }]
    }
  }
}

// 搜索合同
const handleContractSearch = async (query: string = '') => {
  contractLoading.value = true
  try {
    const result = await pageContracts({ pageNum: 1, pageSize: 20, name: query })
    contractOptions.value = result.records.map(c => ({
      label: `${c.name}${c.contractNo ? ` (${c.contractNo})` : ''}`,
      value: c.id
    }))
  } catch (error) {
    console.error('搜索合同失败', error)
  } finally {
    contractLoading.value = false
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

    const submitData: PaymentPlanFormData = {
      contractId: formData.value.contractId,
      period: formData.value.period,
      planAmount: formData.value.planAmount,
      planDate: formData.value.planDate || '',
      remark: formData.value.remark || undefined
    }

    if (isEdit.value && formData.value.id) {
      await updatePaymentPlan(formData.value.id, submitData)
      message.success('回款计划更新成功')
    } else {
      await createPaymentPlan(submitData)
      message.success('回款计划创建成功')
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
  if (!isEdit.value) {
    handleContractSearch()
  }
})
</script>

<style scoped>
.payment-plan-form {
  padding: 8px 0;
}

.full-width {
  width: 100%;
}

/* 表单项样式 */
:deep(.n-form-item) {
  margin-bottom: 16px;
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
</style>
