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

      <!-- 产品信息 - Premium Redesign -->
      <div class="product-section">
        <div class="product-section-header">
          <div class="product-section-title">
            <n-icon :component="CubeOutline" class="title-icon" />
            <span>产品明细</span>
          </div>
          <n-button 
            type="primary" 
            size="small"
            class="add-product-btn"
            @click="handleAddProduct"
          >
            <template #icon>
              <n-icon :component="AddCircleOutline" />
            </template>
            添加产品
          </n-button>
        </div>

        <div class="product-section-body">
          <div v-if="formData.products && formData.products.length > 0" class="product-table">
            <!-- 表头 -->
            <div class="product-table-header">
              <div class="col col-product">产品名称</div>
              <div class="col col-qty">数量</div>
              <div class="col col-price">单价</div>
              <div class="col col-discount">折扣</div>
              <div class="col col-subtotal">小计</div>
              <div class="col col-action"></div>
            </div>
            
            <!-- 产品行 -->
            <div class="product-table-body">
              <div
                v-for="(product, index) in formData.products"
                :key="`prod-${index}`"
                class="product-row"
              >
                <div class="col col-product">
                  <n-select
                    v-model:value="product.productId"
                    placeholder="选择产品"
                    :options="productOptions"
                    filterable
                    size="small"
                    @update:value="(val) => handleProductChange(index, val)"
                  />
                </div>
                <div class="col col-qty">
                  <n-input-number
                    v-model:value="product.quantity"
                    placeholder="数量"
                    :min="1"
                    size="small"
                    :show-button="false"
                    @update:value="() => calculateProductAmount(index)"
                  />
                </div>
                <div class="col col-price">
                  <n-input-number
                    v-model:value="product.price"
                    placeholder="单价"
                    :min="0"
                    :precision="2"
                    size="small"
                    :show-button="false"
                    @update:value="() => calculateProductAmount(index)"
                  >
                    <template #prefix>¥</template>
                  </n-input-number>
                </div>
                <div class="col col-discount">
                  <n-input-number
                    v-model:value="product.discount"
                    placeholder="折扣"
                    :min="0"
                    :max="100"
                    size="small"
                    :show-button="false"
                    @update:value="() => calculateProductAmount(index)"
                  >
                    <template #suffix>%</template>
                  </n-input-number>
                </div>
                <div class="col col-subtotal">
                  <span class="subtotal-value">{{ formatAmount(product.amount) }}</span>
                </div>
                <div class="col col-action">
                  <n-button
                    quaternary
                    circle
                    size="small"
                    class="delete-btn"
                    @click="handleRemoveProduct(index)"
                  >
                    <template #icon>
                      <n-icon :component="TrashOutline" />
                    </template>
                  </n-button>
                </div>
              </div>
            </div>

            <!-- 总计 -->
            <div class="product-table-footer">
              <div class="total-row">
                <span class="total-label">产品总金额</span>
                <span class="total-value">¥{{ totalProductAmount.toLocaleString() }}</span>
              </div>
            </div>
          </div>

          <!-- 空状态 - Premium Empty State -->
          <div v-else class="empty-product-state">
            <div class="empty-illustration">
              <div class="empty-icon-wrapper">
                <n-icon :component="LayersOutline" size="36" />
              </div>
              <div class="empty-circles">
                <span class="circle circle-1"></span>
                <span class="circle circle-2"></span>
                <span class="circle circle-3"></span>
              </div>
            </div>
            <p class="empty-title">暂无产品明细</p>
            <p class="empty-desc">添加产品以计算商机预估金额</p>
            <n-button 
              type="primary" 
              ghost 
              size="medium"
              class="empty-add-btn"
              @click="handleAddProduct"
            >
              <template #icon>
                <n-icon :component="AddCircleOutline" />
              </template>
              添加第一个产品
            </n-button>
          </div>
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
import { ref, reactive, watch, computed, onMounted } from 'vue'
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
import { 
  AddOutline, 
  TrashOutline, 
  CubeOutline, 
  AddCircleOutline,
  LayersOutline
} from '@vicons/ionicons5'
import { createOpportunity, updateOpportunity } from '@/api/business/opportunity'
import { pageCustomers } from '@/api/business/customer'
import { pageContacts } from '@/api/business/contact'
import { getEnabledProducts } from '@/api/business/product'
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
const customerOptions = ref<{ label: string; value: number | string }[]>([])

// 联系人选项
const contactOptions = ref<{ label: string; value: number | string }[]>([])

// 产品选项
const productOptions = ref<{ label: string; value: number | string; price: number }[]>([])

// 加载产品列表
const loadProducts = async () => {
  try {
    const products = await getEnabledProducts()
    productOptions.value = products.map(p => ({
      label: p.name,
      value: p.id,
      price: p.price
    }))
  } catch (error) {
    console.error('加载产品列表失败', error)
  }
}

// 在 onMounted 时调用
onMounted(() => {
  loadProducts()
})

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入商机名称', trigger: 'blur' },
    { min: 2, max: 200, message: '商机名称长度为2-200个字符', trigger: 'blur' }
  ],
  customerId: [
    { required: true, message: '请选择客户', trigger: 'change' }
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
  if (!amount) return '¥0'
  return `¥${amount.toLocaleString()}`
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

// 加载联系人列表
const loadContacts = async (customerId: number | string) => {
  if (!customerId) {
    contactOptions.value = []
    formData.contactId = undefined
    return
  }
  
  try {
    const result = await pageContacts(String(customerId), undefined, 1, 100)
    contactOptions.value = result.records.map((c: any) => ({
      label: c.name,
      value: c.id
    }))
  } catch (error) {
    console.error('加载联系人失败', error)
  }
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
      if (newVal.customerId && newVal.customerName) {
        customerOptions.value = [{ label: newVal.customerName, value: newVal.customerId }]
      }
      if (newVal.customerId) {
        loadContacts(newVal.customerId)
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

// 监听客户ID变化
watch(
  () => formData.customerId,
  (newVal) => {
    if (newVal) {
      loadContacts(newVal)
    } else {
      contactOptions.value = []
    }
  }
)

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

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true

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

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  padding-left: 12px;
  border-left: 3px solid #2563eb;
  margin-bottom: 16px;
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

/* ========================================
   产品信息区域 - Premium Design
   ======================================== */
.product-section {
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  margin-bottom: 24px;
}

.product-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e2e8f0;
}

.product-section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.title-icon {
  color: #2563eb;
  font-size: 20px;
}

.add-product-btn {
  height: 32px;
  padding: 0 14px;
  border-radius: 8px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(37, 99, 235, 0.15);
  transition: all 0.2s ease;
}

.add-product-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(37, 99, 235, 0.25);
}

.product-section-body {
  padding: 0;
}

/* 产品表格 */
.product-table {
  width: 100%;
}

.product-table-header {
  display: flex;
  padding: 12px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}

.product-table-body {
  max-height: 280px;
  overflow-y: auto;
}

.product-row {
  display: flex;
  padding: 14px 20px;
  border-bottom: 1px solid #f1f5f9;
  align-items: center;
  transition: background 0.15s ease;
}

.product-row:hover {
  background: #fafbfc;
}

.product-row:last-child {
  border-bottom: none;
}

.col {
  padding: 0 6px;
}

.col-product { flex: 0 0 28%; }
.col-qty { flex: 0 0 14%; }
.col-price { flex: 0 0 18%; }
.col-discount { flex: 0 0 14%; }
.col-subtotal { flex: 0 0 18%; }
.col-action { flex: 0 0 8%; text-align: center; }

.subtotal-value {
  display: inline-block;
  padding: 6px 12px;
  background: #f1f5f9;
  border-radius: 6px;
  font-weight: 600;
  color: #334155;
  font-size: 13px;
}

.delete-btn {
  color: #94a3b8;
  transition: all 0.2s;
}

.delete-btn:hover {
  color: #ef4444;
  background: #fef2f2;
}

/* 总计行 */
.product-table-footer {
  padding: 16px 20px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border-top: 1px solid #bfdbfe;
}

.total-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
}

.total-label {
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

.total-value {
  font-size: 22px;
  font-weight: 700;
  color: #2563eb;
  letter-spacing: -0.02em;
}

/* 空状态 - Premium */
.empty-product-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  text-align: center;
}

.empty-illustration {
  position: relative;
  margin-bottom: 20px;
}

.empty-icon-wrapper {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2563eb;
  position: relative;
  z-index: 2;
}

.empty-circles {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
}

.circle {
  position: absolute;
  border-radius: 50%;
  border: 1px solid #e2e8f0;
  animation: pulse 2s ease-in-out infinite;
}

.circle-1 {
  width: 90px;
  height: 90px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 0s;
}

.circle-2 {
  width: 110px;
  height: 110px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 0.4s;
}

.circle-3 {
  width: 130px;
  height: 130px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 0.8s;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.4;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.1;
    transform: translate(-50%, -50%) scale(1.05);
  }
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 6px 0;
}

.empty-desc {
  font-size: 14px;
  color: #94a3b8;
  margin: 0 0 20px 0;
}

.empty-add-btn {
  height: 38px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.empty-add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
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
