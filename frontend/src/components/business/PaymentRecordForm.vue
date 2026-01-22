<template>
  <div class="payment-record-form">
    <n-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="80"
      require-mark-placement="right-hanging"
    >
      <n-form-item label="回款金额" path="amount">
        <n-input-number
          v-model:value="formData.amount"
          placeholder="请输入回款金额"
          :min="0.01"
          :precision="2"
          :show-button="false"
          class="full-width"
        >
          <template #prefix>¥</template>
        </n-input-number>
      </n-form-item>

      <n-form-item label="回款日期" path="paymentDate">
        <n-date-picker
          v-model:formatted-value="formData.paymentDate"
          type="date"
          placeholder="请选择回款日期"
          value-format="yyyy-MM-dd"
          class="full-width"
        />
      </n-form-item>

      <n-form-item label="付款方式" path="paymentMethod">
        <n-select
          v-model:value="formData.paymentMethod"
          placeholder="请选择付款方式"
          :options="paymentMethodOptions"
          clearable
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
        确认回款
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
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
import { createPaymentRecord } from '@/api/business/payment'
import type { PaymentRecordFormData } from '@/types/business/payment'
import { paymentMethodOptions } from '@/types/business/payment'

const props = defineProps<{
  planId: number
}>()

const emit = defineEmits<{
  (e: 'submit'): void
  (e: 'cancel'): void
}>()

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

// 表单数据
const formData = ref<PaymentRecordFormData>({
  planId: props.planId,
  amount: 0,
  paymentDate: '',
  paymentMethod: undefined,
  remark: ''
})

// 表单验证规则
const rules: FormRules = {
  amount: [
    { required: true, type: 'number', message: '请输入回款金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '回款金额必须大于0', trigger: 'blur' }
  ],
  paymentDate: [
    { required: true, message: '请选择回款日期', trigger: 'change' }
  ]
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    submitting.value = true

    const submitData: PaymentRecordFormData = {
      planId: props.planId,
      amount: formData.value.amount,
      paymentDate: formData.value.paymentDate,
      paymentMethod: formData.value.paymentMethod,
      remark: formData.value.remark || undefined
    }

    await createPaymentRecord(submitData)
    message.success('回款记录添加成功')

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

// 初始化
onMounted(() => {
  // 默认设置今天的日期
  const today = new Date().toISOString().split('T')[0]
  formData.value.paymentDate = today
})
</script>

<style scoped>
.payment-record-form {
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
  background: #22c55e;
  border: none;
}

.submit-btn:hover {
  background: #16a34a;
}
</style>
