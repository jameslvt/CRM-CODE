<template>
  <div class="product-form">
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
          <n-form-item label="产品名称" path="name">
            <n-input
              v-model:value="formData.name"
              placeholder="请输入产品名称"
              maxlength="100"
            />
          </n-form-item>
          <n-form-item label="产品编码" path="code">
            <n-input
              v-model:value="formData.code"
              placeholder="请输入产品编码"
              maxlength="50"
            />
          </n-form-item>
          <n-form-item label="产品分类" path="category">
            <n-select
              v-model:value="formData.category"
              placeholder="请选择产品分类"
              :options="categoryOptions"
              clearable
            />
          </n-form-item>
          <n-form-item label="计量单位" path="unit">
            <n-select
              v-model:value="formData.unit"
              placeholder="请选择计量单位"
              :options="unitOptions"
              clearable
            />
          </n-form-item>
          <n-form-item label="产品单价" path="price">
            <n-input-number
              v-model:value="formData.price"
              placeholder="请输入产品单价"
              :min="0"
              :precision="2"
              :show-button="false"
              class="price-input"
            >
              <template #prefix>¥</template>
            </n-input-number>
          </n-form-item>
          <n-form-item label="状态" path="status">
            <n-switch
              v-model:value="statusEnabled"
              :checked-value="true"
              :unchecked-value="false"
            >
              <template #checked>启用</template>
              <template #unchecked>停用</template>
            </n-switch>
          </n-form-item>
        </div>
      </div>

      <!-- 产品描述 -->
      <div class="form-section">
        <h4 class="section-title">产品描述</h4>
        <n-form-item label="描述" path="description" :label-width="80">
          <n-input
            v-model:value="formData.description"
            type="textarea"
            placeholder="请输入产品描述"
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
        {{ isEdit ? '保存修改' : '创建产品' }}
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
  NSwitch,
  NButton,
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import { createProduct, updateProduct } from '@/api/business/product'
import type { Product, ProductFormData } from '@/types/business/product'
import { productCategoryOptions, productUnitOptions } from '@/types/business/product'

const props = defineProps<{
  formData: Partial<Product>
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
const formData = ref<ProductFormData>({
  name: '',
  code: '',
  category: undefined,
  unit: undefined,
  price: 0,
  description: '',
  status: 1
})

// 状态开关
const statusEnabled = computed({
  get: () => formData.value.status === 1,
  set: (val: boolean) => {
    formData.value.status = val ? 1 : 0
  }
})

// 分类选项
const categoryOptions = productCategoryOptions

// 单位选项
const unitOptions = productUnitOptions

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' },
    { max: 100, message: '产品名称不能超过100个字符', trigger: 'blur' }
  ],
  code: [
    { max: 50, message: '产品编码不能超过50个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, type: 'number', message: '请输入产品单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '产品单价不能为负数', trigger: 'blur' }
  ]
}

// 初始化表单数据
const initFormData = () => {
  if (props.formData) {
    formData.value = {
      id: props.formData.id,
      name: props.formData.name || '',
      code: props.formData.code || '',
      category: props.formData.category,
      unit: props.formData.unit,
      price: props.formData.price || 0,
      description: props.formData.description || '',
      status: props.formData.status ?? 1
    }
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

    const submitData: ProductFormData = {
      name: formData.value.name,
      code: formData.value.code || undefined,
      category: formData.value.category,
      unit: formData.value.unit,
      price: formData.value.price,
      description: formData.value.description || undefined,
      status: formData.value.status
    }

    if (isEdit.value && formData.value.id) {
      await updateProduct(formData.value.id, submitData)
      message.success('产品更新成功')
    } else {
      await createProduct(submitData)
      message.success('产品创建成功')
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
})
</script>

<style scoped>
.product-form {
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

/* 价格输入框 */
.price-input {
  width: 100%;
}

.price-input :deep(.n-input__prefix) {
  color: #64748b;
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
:deep(.n-input-number) {
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
