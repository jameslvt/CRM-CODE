<template>
  <n-form
    ref="formRef"
    :model="formModel"
    :rules="rules"
    label-placement="left"
    label-width="100px"
    require-mark-placement="right-hanging"
  >
    <n-grid :cols="2" :x-gap="24">
      <n-form-item-gi label="线索名称" path="leadName">
        <n-input
          v-model:value="formModel.leadName"
          placeholder="请输入线索名称"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="联系人" path="contactName">
        <n-input
          v-model:value="formModel.contactName"
          placeholder="请输入联系人"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="联系电话" path="phone">
        <n-input
          v-model:value="formModel.phone"
          placeholder="请输入联系电话"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="邮箱" path="email">
        <n-input
          v-model:value="formModel.email"
          placeholder="请输入邮箱"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="公司名称" path="companyName">
        <n-input
          v-model:value="formModel.companyName"
          placeholder="请输入公司名称"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="职位" path="position">
        <n-input
          v-model:value="formModel.position"
          placeholder="请输入职位"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="来源" path="source">
        <n-select
          v-model:value="formModel.source"
          placeholder="请选择来源"
          :options="sourceOptions"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="行业" path="industry">
        <n-input
          v-model:value="formModel.industry"
          placeholder="请输入行业"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="评级" path="rating">
        <n-select
          v-model:value="formModel.rating"
          placeholder="请选择评级"
          :options="ratingOptions"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi label="状态" path="status">
        <n-select
          v-model:value="formModel.status"
          placeholder="请选择状态"
          :options="statusOptions"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi :span="2" label="地址" path="address">
        <n-input
          v-model:value="formModel.address"
          placeholder="请输入地址"
          clearable
        />
      </n-form-item-gi>

      <n-form-item-gi :span="2" label="备注" path="remark">
        <n-input
          v-model:value="formModel.remark"
          type="textarea"
          placeholder="请输入备注"
          :rows="3"
          clearable
        />
      </n-form-item-gi>
    </n-grid>

    <n-space justify="end" style="margin-top: 24px">
      <n-button @click="handleCancel">取消</n-button>
      <n-button type="primary" :loading="submitting" @click="handleSubmit">
        提交
      </n-button>
    </n-space>
  </n-form>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { useMessage, type FormInst, type FormRules } from 'naive-ui'
import { saveLead, updateLead } from '@/api/business/lead'
import type { Lead } from '@/types/business'

interface Props {
  formData?: Partial<Lead>
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

// 表单数据
const formModel = reactive<Partial<Lead>>({
  leadName: '',
  contactName: '',
  phone: '',
  email: '',
  companyName: '',
  position: '',
  source: '',
  industry: '',
  rating: 'B',
  status: 1, // 默认状态：1-新建
  address: '',
  remark: ''
})

// 来源选项
const sourceOptions = [
  { label: '网站', value: '网站' },
  { label: '电话', value: '电话' },
  { label: '推荐', value: '推荐' },
  { label: '展会', value: '展会' },
  { label: '广告', value: '广告' },
  { label: '其他', value: '其他' }
]

// 评级选项
const ratingOptions = [
  { label: 'A高', value: 'A' },
  { label: 'B中', value: 'B' },
  { label: 'C低', value: 'C' }
]

// 状态选项 (后端使用数字: 1-新建, 2-跟进中, 3-已转化, 4-已关闭)
const statusOptions = [
  { label: '新建', value: 1 },
  { label: '跟进中', value: 2 },
  { label: '已失效', value: 4 }
]

// 表单验证规则
const rules: FormRules = {
  leadName: [
    { required: true, message: '请输入线索名称', trigger: 'blur' }
  ],
  contactName: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
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
  companyName: [
    { required: true, message: '请输入公司名称', trigger: 'blur' }
  ],
  source: [
    { required: true, message: '请选择来源', trigger: 'change' }
  ],
  rating: [
    { required: true, message: '请选择评级', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 监听 props 变化，更新表单数据
watch(
  () => props.formData,
  (newVal) => {
    if (newVal) {
      Object.assign(formModel, newVal)
    }
  },
  { immediate: true, deep: true }
)

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    if (formModel.id) {
      await updateLead(formModel as Lead)
      message.success('修改成功')
    } else {
      await saveLead(formModel)
      message.success('新增成功')
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
</script>

<style scoped lang="scss">
// 样式可以根据需要添加
</style>
