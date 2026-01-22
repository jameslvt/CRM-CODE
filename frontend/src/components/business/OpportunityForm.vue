<template>
  <div class="opportunity-form">
    <n-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="100"
      require-mark-placement="right-hanging"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <div class="section-title">基本信息</div>
        <n-grid :cols="2" :x-gap="20">
          <n-gi>
            <n-form-item label="商机名称" path="name">
              <n-input
                v-model:value="formData.name"
                placeholder="请输入商机名称"
                maxlength="200"
              />
            </n-form-item>
          </n-gi>

          <n-gi>
            <n-form-item label="关联客户" path="customerId">
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
          </n-gi>

          <n-gi>
            <n-form-item label="联系人" path="contactId">
              <n-select
                v-model:value="formData.contactId"
                placeholder="请选择联系人"
                :options="contactOptions"
                :disabled="!formData.customerId"
                clearable
              />
            </n-form-item>
          </n-gi>

          <n-gi>
            <n-form-item label="商机阶段" path="stage">
              <n-select
                v-model:value="formData.stage"
                placeholder="请选择阶段"
                :options="stageOptions"
              />
            </n-form-item>
          </n-gi>

          <n-gi>
            <n-form-item label="预计金额" path="amount">
              <n-input-number
                v-model:value="formData.amount"
                placeholder="请输入预计金额"
                :min="0"
                :precision="2"
                style="width: 100%"
              >
                <template #prefix>¥</template>
              </n-input-number>
            </n-form-item>
          </n-gi>

          <n-gi>
            <n-form-item label="赢单概率" path="probability">
              <n-input-number
                v-model:value="formData.probability"
                placeholder="请输入赢单概率"
                :min="0"
                :max="100"
                style="width: 100%"
              >
                <template #suffix>%</template>
              </n-input-number>
            </n-form-item>
          </n-gi>

          <n-gi>
            <n-form-item label="预计成交日期" path="expectedDate">
              <n-date-picker
                v-model:formatted-value="formData.expectedDate"
                type="date"
                placeholder="请选择预计成交日期"
                value-format="yyyy-MM-dd"
                clearable
                style="width: 100%"
              />
            </n-form-item>
          </n-gi>

          <n-gi>
            <n-form-item label="商机来源" path="source">
              <n-select
                v-model:value="formData.source"
                placeholder="请选择来源"
                :options="sourceOptions"
                clearable
              />
            </n-form-item>
          </n-gi>

          <n-gi :span="2">
            <n-form-item label="备注" path="remark">
              <n-input
                v-model:value="formData.remark"
                type="textarea"
                placeholder="请输入备注信息"
                :rows="3"
                maxlength="500"
              />
            </n-form-item>
          </n-gi>
        </n-grid>
      </div>

      <!-- 产品信息 -->
      <div class="form-section">
        <div class="section-header">
          <div class="section-title">产品信息</div>
          <n-button text type="primary" @click="handleAddProduct">
            <template #icon>
              <n-icon><AddOutline /></n-icon>
            </template>
            添加产品
          </n-button>
        </div>

        <div v-if="formData.products && formData.products.length > 0" class="product-list">
          <div
            v-for="(product, index) in formData.products"
            :key="index"
            class="product-item"
          >
            <n-grid :cols="24" :x-gap="12">
              <n-gi :span="6">
                <n-select
                  v-model:value="product.productId"
                  placeholder="选择产品"
                  :options="productOptions"
                  filterable
                  @update:value="(val) => handleProductChange(index, val)"
                />
              </n-gi>
              <n-gi :span="4">
                <n-input-number
                  v-model:value="product.quantity"
                  placeholder="数量"
                  :min="1"
                  @update:value="() => calculateProductAmount(index)"
                />
              </n-gi>
              <n-gi :span="5">
                <n-input-number
                  v-model:value="product.price"
                  placeholder="单价"
                  :min="0"
                  :precision="2"
                  @update:value="() => calculateProductAmount(index)"
                >
                  <template #prefix>¥</template>
                </n-input-number>
              </n-gi>
              <n-gi :span="4">
                <n-input-number
                  v-model:value="product.discount"
                  placeholder="折扣"
                  :min="0"
                  :max="100"
                  @update:value="() => calculateProductAmount(index)"
                >
                  <template #suffix>%</template>
                </n-input-number>
              </n-gi>
              <n-gi :span="4">
                <n-input
                  :value="formatAmount(product.amount)"
                  placeholder="金额"
                  disabled
                />
              </n-gi>
              <n-gi :span="1">
                <n-button
                  text
                  type="error"
                  @click="handleRemoveProduct(index)"
                >
                  <n-icon size="18"><TrashOutline /></n-icon>
                </n-button>
              </n-gi>
            </n-grid>
          </div>

          <div class="product-total">
            <span class="total-label">产品总金额：</span>
            <span class="total-value">¥{{ totalProductAmount.toLocaleString() }}</span>
          </div>
        </div>

        <div v-else class="no-products">
          <span class="no-products-text">暂无产品</span>
          <n-button text size="small" type="primary" @click="handleAddProduct">
            添加产品
          </n-button>
        </div>
      </div>
    </n-form>

    <!-- 表单操作按钮 -->
    <div class="form-actions">
      <n-button @click="handleCancel" class="cancel-btn">取消</n-button>
      <n-button type="primary" @click="handleSubmit" :loading="submitting" class="submit-btn">
        {{ props.formData.id ? '保存修改' : '创建商机' }}
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
  NInputNumber,
  NSelect,
  NGrid,
  NGi,
  NButton,
  NIcon,
  NDatePicker,
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import { AddOutline, TrashOutline } from '@vicons/ionicons5'
import { createOpportunity, updateOpportunity } from '@/api/business/opportunity'
import { pageCustomers } from '@/api/business/customer'
import type { Opportunity, OpportunityFormData, OpportunityProduct } from '@/types/business/opportunity'

const props = defineProps<{
  formData: Partial<Opportunity>
}>()

const emit = defineEmits<{
  (e: 'submit'): void
  (e: 'cancel'): void
}>()

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)
const customerLoading = ref(false)

// 表单数据
const formData = reactive<OpportunityFormData>({
  name: '',
  customerId: 0,
  contactId: undefined,
  amount: undefined,
  stage: 'REQUIREMENT',
  probability: 20,
  expectedDate: undefined,
  source: undefined,
  remark: '',
  products: []
})

// 阶段选项
const stageOptions = [
  { label: '需求确认', value: 'REQUIREMENT' },
  { label: '方案报价', value: 'PROPOSAL' },
  { label: '商务谈判', value: 'NEGOTIATION' },
  { label: '赢单', value: 'WON' },
  { label: '输单', value: 'LOST' }
]

// 来源选项
const sourceOptions = [
  { label: '线索转化', value: 'LEAD' },
  { label: '客户推荐', value: 'REFERRAL' },
  { label: '市场活动', value: 'MARKETING' },
  { label: '官网咨询', value: 'WEBSITE' },
  { label: '其他', value: 'OTHER' }
]

// 客户选项
const customerOptions = ref<{ label: string; value: number }[]>([])

// 联系人选项
const contactOptions = ref<{ label: string; value: number }[]>([])

// 产品选项（模拟数据）
const productOptions = ref([
  { label: 'CRM基础版', value: 1, price: 9800 },
  { label: 'CRM专业版', value: 2, price: 19800 },
  { label: 'CRM企业版', value: 3, price: 49800 },
  { label: '数据分析模块', value: 4, price: 5800 },
  { label: 'AI智能助手', value: 5, price: 8800 }
])

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入商机名称', trigger: 'blur' },
    { min: 2, max: 200, message: '商机名称长度为2-200个字符', trigger: 'blur' }
  ],
  customerId: [
    { required: true, type: 'number', message: '请选择客户', trigger: 'change' }
  ],
  stage: [
    { required: true, message: '请选择商机阶段', trigger: 'change' }
  ]
}

// 计算产品总金额
const totalProductAmount = computed(() => {
  if (!formData.products) return 0
  return formData.products.reduce((sum, p) => sum + (p.amount || 0), 0)
})

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (!amount) return ''
  return `¥${amount.toLocaleString()}`
}

// 监听外部数据变化
watch(
  () => props.formData,
  (newVal) => {
    if (newVal && newVal.id) {
      Object.assign(formData, {
        id: newVal.id,
        name: newVal.name || '',
        customerId: newVal.customerId || 0,
        contactId: newVal.contactId,
        amount: newVal.amount,
        stage: newVal.stage || 'REQUIREMENT',
        probability: newVal.probability || 20,
        expectedDate: newVal.expectedDate,
        source: newVal.source,
        remark: newVal.remark || '',
        products: newVal.products ? [...newVal.products] : []
      })
      // 加载客户选项
      if (newVal.customerId && newVal.customerName) {
        customerOptions.value = [{ label: newVal.customerName, value: newVal.customerId }]
      }
    } else {
      resetForm()
    }
  },
  { immediate: true, deep: true }
)

// 搜索客户
const handleCustomerSearch = async (query: string) => {
  if (!query) return
  customerLoading.value = true
  try {
    const result = await pageCustomers({ name: query, pageNum: 1, pageSize: 20 })
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

// 添加产品
const handleAddProduct = () => {
  if (!formData.products) {
    formData.products = []
  }
  formData.products.push({
    productId: 0,
    quantity: 1,
    price: 0,
    discount: 0,
    amount: 0
  })
}

// 移除产品
const handleRemoveProduct = (index: number) => {
  formData.products?.splice(index, 1)
}

// 产品选择变化
const handleProductChange = (index: number, productId: number) => {
  const product = productOptions.value.find(p => p.value === productId)
  if (product && formData.products) {
    formData.products[index].price = product.price
    calculateProductAmount(index)
  }
}

// 计算产品金额
const calculateProductAmount = (index: number) => {
  if (!formData.products) return
  const product = formData.products[index]
  const quantity = product.quantity || 0
  const price = product.price || 0
  const discount = product.discount || 0
  product.amount = quantity * price * (1 - discount / 100)
}

// 重置表单
const resetForm = () => {
  Object.assign(formData, {
    id: undefined,
    name: '',
    customerId: 0,
    contactId: undefined,
    amount: undefined,
    stage: 'REQUIREMENT',
    probability: 20,
    expectedDate: undefined,
    source: undefined,
    remark: '',
    products: []
  })
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

    // 自动计算总金额
    if (totalProductAmount.value > 0) {
      formData.amount = totalProductAmount.value
    }

    if (formData.id) {
      await updateOpportunity(formData.id, formData)
      message.success('商机信息已更新')
    } else {
      await createOpportunity(formData)
      message.success('商机创建成功')
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
.opportunity-form {
  padding: 8px 0;
}

.form-section {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  padding-left: 12px;
  border-left: 3px solid #2563eb;
  margin-bottom: 16px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.opportunity-form :deep(.n-form-item) {
  margin-bottom: 18px;
}

.opportunity-form :deep(.n-form-item-label) {
  font-weight: 500;
  color: #475569;
}

.opportunity-form :deep(.n-input) {
  --n-border-radius: 8px;
}

.opportunity-form :deep(.n-base-selection) {
  --n-border-radius: 8px;
}

/* 产品列表 */
.product-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-item {
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.product-item :deep(.n-input-number),
.product-item :deep(.n-select),
.product-item :deep(.n-input) {
  width: 100%;
}

.product-total {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #dbeafe;
  border-radius: 8px;
  margin-top: 8px;
}

.total-label {
  font-size: 14px;
  color: #475569;
}

.total-value {
  font-size: 18px;
  font-weight: 700;
  color: #2563eb;
}

.no-products {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 24px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px dashed #e2e8f0;
}

.no-products-text {
  font-size: 14px;
  color: #94a3b8;
}

/* 表单操作按钮 */
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
