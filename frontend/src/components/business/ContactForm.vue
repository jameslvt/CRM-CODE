<template>
  <div class="contact-form">
    <n-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="80"
      require-mark-placement="right-hanging"
    >
      <n-grid :cols="2" :x-gap="20">
        <n-gi>
          <n-form-item label="姓名" path="name">
            <n-input
              v-model:value="formData.name"
              placeholder="请输入姓名"
              maxlength="50"
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="性别" path="gender">
            <n-radio-group v-model:value="formData.gender">
              <n-space>
                <n-radio :value="1">男</n-radio>
                <n-radio :value="2">女</n-radio>
              </n-space>
            </n-radio-group>
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="职位" path="position">
            <n-input
              v-model:value="formData.position"
              placeholder="请输入职位"
              maxlength="50"
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="部门" path="department">
            <n-input
              v-model:value="formData.department"
              placeholder="请输入部门"
              maxlength="50"
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="手机" path="mobile">
            <n-input
              v-model:value="formData.mobile"
              placeholder="请输入手机号"
              maxlength="20"
            />
          </n-form-item>
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
          <n-form-item label="邮箱" path="email">
            <n-input
              v-model:value="formData.email"
              placeholder="请输入邮箱"
              maxlength="100"
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="微信" path="wechat">
            <n-input
              v-model:value="formData.wechat"
              placeholder="请输入微信号"
              maxlength="50"
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="生日" path="birthday">
            <n-date-picker
              v-model:formatted-value="formData.birthday"
              type="date"
              placeholder="请选择生日"
              value-format="yyyy-MM-dd"
              clearable
              style="width: 100%"
            />
          </n-form-item>
        </n-gi>

        <n-gi>
          <n-form-item label="主要联系人" path="isPrimary">
            <n-switch v-model:value="isPrimarySwitch" />
          </n-form-item>
        </n-gi>

        <n-gi :span="2">
          <n-form-item label="备注" path="remark">
            <n-input
              v-model:value="formData.remark"
              type="textarea"
              placeholder="请输入备注信息"
              :rows="2"
              maxlength="500"
            />
          </n-form-item>
        </n-gi>
      </n-grid>
    </n-form>

    <!-- 表单操作按钮 -->
    <div class="form-actions">
      <n-button @click="handleCancel" class="cancel-btn">取消</n-button>
      <n-button type="primary" @click="handleSubmit" :loading="submitting" class="submit-btn">
        {{ props.formData.id ? '保存修改' : '添加联系人' }}
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed } from 'vue'
import {
  NForm,
  NFormItem,
  NInput,
  NSelect,
  NGrid,
  NGi,
  NButton,
  NRadioGroup,
  NRadio,
  NSpace,
  NSwitch,
  NDatePicker,
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import { saveContact, updateContact } from '@/api/business/contact'
import type { Contact, ContactFormData } from '@/types/business/customer'

const props = defineProps<{
  formData: Partial<Contact>
  customerId: number
}>()

const emit = defineEmits<{
  (e: 'submit'): void
  (e: 'cancel'): void
}>()

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

// 表单数据
const formData = reactive<ContactFormData>({
  customerId: props.customerId,
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

// 主要联系人开关
const isPrimarySwitch = computed({
  get: () => formData.isPrimary === true,
  set: (val: boolean) => {
    formData.isPrimary = val
  }
})

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度为2-50个字符', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
}

// 监听外部数据变化
watch(
  () => props.formData,
  (newVal) => {
    if (newVal && newVal.id) {
      Object.assign(formData, {
        id: newVal.id,
        customerId: newVal.customerId || props.customerId,
        name: newVal.name || '',
        gender: newVal.gender,
        position: newVal.position || '',
        department: newVal.department || '',
        phone: newVal.phone || '',
        mobile: newVal.mobile || '',
        email: newVal.email || '',
        wechat: newVal.wechat || '',
        isPrimary: newVal.isPrimary === 1,
        birthday: newVal.birthday,
        remark: newVal.remark || ''
      })
    } else {
      resetForm()
    }
  },
  { immediate: true, deep: true }
)

// 监听客户ID变化
watch(
  () => props.customerId,
  (newVal) => {
    formData.customerId = newVal
  }
)

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: undefined,
    customerId: props.customerId,
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
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    if (formData.id) {
      await updateContact(formData)
      message.success('联系人信息已更新')
    } else {
      await saveContact(formData)
      message.success('联系人添加成功')
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
.contact-form {
  padding: 8px 0;
}

.contact-form :deep(.n-form-item) {
  margin-bottom: 18px;
}

.contact-form :deep(.n-form-item-label) {
  font-weight: 500;
  color: #475569;
}

.contact-form :deep(.n-input) {
  --n-border-radius: 8px;
}

.contact-form :deep(.n-base-selection) {
  --n-border-radius: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  margin-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.cancel-btn {
  height: 38px;
  padding: 0 20px;
  border-radius: 8px;
}

.submit-btn {
  height: 38px;
  padding: 0 20px;
  border-radius: 8px;
  background: #2563eb;
}

.submit-btn:hover {
  background: #1d4ed8;
}
</style>
