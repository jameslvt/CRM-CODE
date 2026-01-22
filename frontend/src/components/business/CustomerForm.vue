<template>
  <div class="customer-form">
    <n-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="100"
      require-mark-placement="right-hanging"
    >
      <n-grid :cols="2" :x-gap="24">
        <!-- 基本信息 -->
        <n-gi :span="2">
          <div class="form-section-title">基本信息</div>
        </n-gi>

        <n-gi>
          <n-form-item label="客户名称" path="name">
            <n-input
              v-model:value="formData.name"
              placeholder="请输入客户名称"
              maxlength="200"
              show-count
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="简称" path="shortName">
            <n-input
              v-model:value="formData.shortName"
              placeholder="请输入简称"
              maxlength="50"
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="客户级别" path="level">
            <n-select
              v-model:value="formData.level"
              placeholder="请选择客户级别"
              :options="levelOptions"
              clearable
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="行业" path="industry">
            <n-select
              v-model:value="formData.industry"
              placeholder="请选择行业"
              :options="industryOptions"
              clearable
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="规模" path="scale">
            <n-select
              v-model:value="formData.scale"
              placeholder="请选择规模"
              :options="scaleOptions"
              clearable
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="来源" path="source">
            <n-select
              v-model:value="formData.source"
              placeholder="请选择来源"
              :options="sourceOptions"
              clearable
            />
          </n-form-item>
        </n-gi>

        <!-- 联系方式 -->
        <n-gi :span="2">
          <div class="form-section-title">联系方式</div>
        </n-gi>

        <n-gi>
          <n-form-item label="电话" path="phone">
            <n-input
              v-model:value="formData.phone"
              placeholder="请输入电话"
              maxlength="20"
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="传真" path="fax">
            <n-input
              v-model:value="formData.fax"
              placeholder="请输入传真"
              maxlength="20"
            />
          </n-form-item>
        </n-gi>

        <n-gi :span="2">
          <n-form-item label="网站" path="website">
            <n-input
              v-model:value="formData.website"
              placeholder="请输入网站地址"
              maxlength="200"
            />
          </n-form-item>
        </n-gi>

        <n-gi :span="2">
          <n-form-item label="地址" path="address">
            <n-input
              v-model:value="formData.address"
              placeholder="请输入详细地址"
              maxlength="500"
            />
          </n-form-item>
        </n-gi>

        <!-- 其他信息 -->
        <n-gi :span="2">
          <div class="form-section-title">其他信息</div>
        </n-gi>

        <n-gi :span="2">
          <n-form-item label="备注" path="remark">
            <n-input
              v-model:value="formData.remark"
              type="textarea"
              placeholder="请输入备注信息"
              :rows="3"
              maxlength="500"
              show-count
            />
          </n-form-item>
        </n-gi>
      </n-grid>
    </n-form>

    <!-- 表单操作按钮 -->
    <div class="form-actions">
      <n-button @click="handleCancel" class="cancel-btn">取消</n-button>
      <n-button type="primary" @click="handleSubmit" :loading="submitting" class="submit-btn">
        {{ props.formData.id ? '保存修改' : '创建客户' }}
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import {
  NForm,
  NFormItem,
  NInput,
  NSelect,
  NGrid,
  NGi,
  NButton,
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import { saveCustomer, updateCustomer } from '@/api/business/customer'
import type { Customer, CustomerFormData } from '@/types/business/customer'

const props = defineProps<{
  formData: Partial<Customer>
}>()

const emit = defineEmits<{
  (e: 'submit'): void
  (e: 'cancel'): void
}>()

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

// 表单数据
const formData = reactive<CustomerFormData>({
  name: '',
  shortName: '',
  industry: undefined,
  scale: undefined,
  source: undefined,
  level: undefined,
  phone: '',
  fax: '',
  website: '',
  address: '',
  remark: ''
})

// 级别选项
const levelOptions = [
  { label: 'A级 - 重要客户', value: 'A' },
  { label: 'B级 - 普通客户', value: 'B' },
  { label: 'C级 - 一般客户', value: 'C' },
  { label: 'D级 - 低优先级', value: 'D' }
]

// 行业选项
const industryOptions = [
  { label: '互联网/IT', value: '互联网/IT' },
  { label: '金融', value: '金融' },
  { label: '制造业', value: '制造业' },
  { label: '零售', value: '零售' },
  { label: '教育', value: '教育' },
  { label: '医疗', value: '医疗' },
  { label: '房地产', value: '房地产' },
  { label: '物流', value: '物流' },
  { label: '其他', value: '其他' }
]

// 规模选项
const scaleOptions = [
  { label: '小型 (1-50人)', value: '小型' },
  { label: '中型 (51-200人)', value: '中型' },
  { label: '大型 (201-1000人)', value: '大型' },
  { label: '集团 (1000人以上)', value: '集团' }
]

// 来源选项
const sourceOptions = [
  { label: '官网', value: '官网' },
  { label: '广告', value: '广告' },
  { label: '转介绍', value: '转介绍' },
  { label: '展会', value: '展会' },
  { label: '电话营销', value: '电话营销' },
  { label: '线索转化', value: '线索转化' },
  { label: '其他', value: '其他' }
]

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入客户名称', trigger: 'blur' },
    { min: 2, max: 200, message: '客户名称长度为2-200个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^[\d\-\s]+$/, message: '请输入有效的电话号码', trigger: 'blur' }
  ],
  website: [
    { pattern: /^(https?:\/\/)?[\w\-]+(\.[\w\-]+)+/, message: '请输入有效的网站地址', trigger: 'blur' }
  ]
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: undefined,
    name: '',
    shortName: '',
    industry: undefined,
    scale: undefined,
    source: undefined,
    level: undefined,
    phone: '',
    fax: '',
    website: '',
    address: '',
    remark: ''
  })
}

// 监听外部数据变化
watch(
  () => props.formData,
  (newVal) => {
    if (newVal && newVal.id) {
      Object.assign(formData, {
        id: newVal.id,
        name: newVal.name || '',
        shortName: newVal.shortName || '',
        industry: newVal.industry,
        scale: newVal.scale,
        source: newVal.source,
        level: newVal.level,
        phone: newVal.phone || '',
        fax: newVal.fax || '',
        website: newVal.website || '',
        address: newVal.address || '',
        remark: newVal.remark || ''
      })
    } else {
      resetForm()
    }
  },
  { immediate: true, deep: true }
)



// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    if (formData.id) {
      await updateCustomer(formData)
      message.success('客户信息已更新')
    } else {
      await saveCustomer(formData)
      message.success('客户创建成功')
    }

    emit('submit')
  } catch (error: any) {
    if (error?.errorFields) {
      message.error('请检查表单输入')
    } else {
      message.error(error?.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 取消
const handleCancel = () => {
  emit('cancel')
}

// 暴露方法
defineExpose({
  resetForm
})
</script>

<style scoped>
.customer-form {
  padding: 8px 0;
}

.form-section-title {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  padding: 8px 0 16px 0;
  margin-bottom: 8px;
  border-bottom: 1px solid #f1f5f9;
}

.form-section-title:not(:first-child) {
  margin-top: 16px;
}

.customer-form :deep(.n-form-item) {
  margin-bottom: 20px;
}

.customer-form :deep(.n-form-item-label) {
  font-weight: 500;
  color: #475569;
}

.customer-form :deep(.n-input) {
  --n-border-radius: 8px;
}

.customer-form :deep(.n-base-selection) {
  --n-border-radius: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 24px;
  margin-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.cancel-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
}

.submit-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
  background: #2563eb;
}

.submit-btn:hover {
  background: #1d4ed8;
}
</style>
