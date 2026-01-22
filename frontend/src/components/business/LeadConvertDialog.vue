<template>
  <div class="lead-convert-dialog">
    <n-alert type="info" style="margin-bottom: 16px">
      转化后将自动创建客户和联系人，线索状态将变更为"已转化"
    </n-alert>

    <n-form
      ref="formRef"
      :model="formModel"
      :rules="rules"
      label-placement="left"
      label-width="120px"
      require-mark-placement="right-hanging"
    >
      <n-form-item label="客户名称" path="customerName">
        <n-input
          v-model:value="formModel.customerName"
          placeholder="请输入客户名称"
          clearable
        />
      </n-form-item>

      <n-form-item label="客户类型" path="customerType">
        <n-select
          v-model:value="formModel.customerType"
          placeholder="请选择客户类型"
          :options="customerTypeOptions"
          clearable
        />
      </n-form-item>

      <n-form-item label="客户级别" path="customerLevel">
        <n-select
          v-model:value="formModel.customerLevel"
          placeholder="请选择客户级别"
          :options="customerLevelOptions"
          clearable
        />
      </n-form-item>

      <n-form-item label="是否创建商机" path="createOpportunity">
        <n-switch v-model:value="formModel.createOpportunity" />
      </n-form-item>

      <template v-if="formModel.createOpportunity">
        <n-divider title-placement="left">商机信息</n-divider>

        <n-form-item label="商机名称" path="opportunityName">
          <n-input
            v-model:value="formModel.opportunityName"
            placeholder="请输入商机名称"
            clearable
          />
        </n-form-item>

        <n-form-item label="预计金额" path="expectedAmount">
          <n-input-number
            v-model:value="formModel.expectedAmount"
            placeholder="请输入预计金额"
            :min="0"
            :precision="2"
            style="width: 100%"
          >
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>

        <n-form-item label="预计成交日期" path="expectedCloseDate">
          <n-date-picker
            v-model:value="formModel.expectedCloseDate"
            type="date"
            placeholder="请选择预计成交日期"
            style="width: 100%"
          />
        </n-form-item>
      </template>

      <n-form-item label="备注" path="remark">
        <n-input
          v-model:value="formModel.remark"
          type="textarea"
          placeholder="请输入备注"
          :rows="3"
          clearable
        />
      </n-form-item>
    </n-form>

    <n-space justify="end" style="margin-top: 24px">
      <n-button @click="handleCancel">取消</n-button>
      <n-button type="primary" :loading="submitting" @click="handleSubmit">
        确认转化
      </n-button>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { useMessage, type FormInst, type FormRules } from 'naive-ui'
import { convertLead, getLeadById } from '@/api/business/lead'
import type { Lead, LeadConvertParams } from '@/types/business'

interface Props {
  leadId: number
}

interface Emits {
  (e: 'submit'): void
  (e: 'cancel'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const message = useMessage()

const formRef = ref<FormInst>()
const submitting = ref(false)
const leadData = ref<Lead>()

// 表单数据（不包含 leadId，因为 leadId 通过 props 传入）
const formModel = reactive<Omit<LeadConvertParams, 'leadId'>>({
  customerName: '',
  customerType: 'ENTERPRISE',
  customerLevel: 'B',
  createOpportunity: false,
  opportunityName: '',
  expectedAmount: undefined,
  expectedCloseDate: undefined,
  remark: ''
})

// 客户类型选项
const customerTypeOptions = [
  { label: '企业客户', value: 'ENTERPRISE' },
  { label: '个人客户', value: 'INDIVIDUAL' }
]

// 客户级别选项
const customerLevelOptions = [
  { label: 'A级 (重要客户)', value: 'A' },
  { label: 'B级 (普通客户)', value: 'B' },
  { label: 'C级 (一般客户)', value: 'C' },
  { label: 'D级 (低优先级)', value: 'D' }
]

// 表单验证规则
const rules: FormRules = {
  customerName: [
    { required: true, message: '请输入客户名称', trigger: 'blur' }
  ],
  customerType: [
    { required: true, message: '请选择客户类型', trigger: 'change' }
  ],
  customerLevel: [
    { required: true, message: '请选择客户级别', trigger: 'change' }
  ],
  opportunityName: [
    {
      required: true,
      message: '请输入商机名称',
      trigger: 'blur',
      validator: (rule, value) => {
        if (formModel.createOpportunity && !value) {
          return new Error('请输入商机名称')
        }
        return true
      }
    }
  ],
  expectedAmount: [
    {
      required: true,
      type: 'number',
      message: '请输入预计金额',
      trigger: 'blur',
      validator: (rule, value) => {
        if (formModel.createOpportunity && !value) {
          return new Error('请输入预计金额')
        }
        return true
      }
    }
  ],
  expectedCloseDate: [
    {
      required: true,
      type: 'number',
      message: '请选择预计成交日期',
      trigger: 'change',
      validator: (rule, value) => {
        if (formModel.createOpportunity && !value) {
          return new Error('请选择预计成交日期')
        }
        return true
      }
    }
  ]
}

// 加载线索数据
const loadLeadData = async () => {
  try {
    leadData.value = await getLeadById(props.leadId)
    // 自动填充客户名称（使用新字段名 company 和 name）
    formModel.customerName = leadData.value.company || leadData.value.name
  } catch (error) {
    message.error('加载线索数据失败')
  }
}

// 监听 leadId 变化
watch(
  () => props.leadId,
  (newVal) => {
    if (newVal) {
      loadLeadData()
    }
  },
  { immediate: true }
)

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    await convertLead(props.leadId, formModel)
    message.success('转化成功')
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
</script>

<style scoped lang="scss">
.lead-convert-dialog {
  // 样式可以根据需要添加
}
</style>
