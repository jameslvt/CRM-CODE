<template>
  <div class="product-select">
    <!-- 产品选择头部 -->
    <div class="select-header">
      <h3 class="select-title">选择产品</h3>
      <n-button text size="small" @click="handleAddProduct" class="add-btn">
        <template #icon>
          <n-icon><AddOutline /></n-icon>
        </template>
        添加产品
      </n-button>
    </div>

    <!-- 已选产品列表 -->
    <div v-if="selectedProducts.length > 0" class="selected-products">
      <div
        v-for="(product, index) in selectedProducts"
        :key="index"
        class="product-card"
      >
        <div class="product-info">
          <div class="product-name">{{ getProductName(product.productId) }}</div>
          <div class="product-code">{{ getProductCode(product.productId) }}</div>
        </div>

        <div class="product-fields">
          <div class="field-group">
            <label class="field-label">数量</label>
            <n-input-number
              v-model:value="product.quantity"
              :min="1"
              size="small"
              @update:value="handleQuantityChange(index)"
            />
          </div>

          <div class="field-group">
            <label class="field-label">单价</label>
            <n-input-number
              v-model:value="product.price"
              :min="0"
              :precision="2"
              size="small"
              @update:value="handlePriceChange(index)"
            >
              <template #prefix>¥</template>
            </n-input-number>
          </div>

          <div class="field-group">
            <label class="field-label">折扣</label>
            <n-input-number
              v-model:value="product.discount"
              :min="0"
              :max="100"
              size="small"
              @update:value="handleDiscountChange(index)"
            >
              <template #suffix>%</template>
            </n-input-number>
          </div>

          <div class="field-group amount">
            <label class="field-label">金额</label>
            <span class="amount-value">¥{{ (product.amount || 0).toLocaleString() }}</span>
          </div>
        </div>

        <n-button
          text
          type="error"
          class="remove-btn"
          @click="handleRemoveProduct(index)"
        >
          <n-icon size="18"><CloseOutline /></n-icon>
        </n-button>
      </div>

      <!-- 总计 -->
      <div class="total-row">
        <span class="total-label">产品总计</span>
        <span class="total-value">¥{{ totalAmount.toLocaleString() }}</span>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <n-icon size="48" class="empty-icon"><CubeOutline /></n-icon>
      <p class="empty-text">暂未添加产品</p>
      <n-button type="primary" size="small" @click="handleAddProduct">
        添加产品
      </n-button>
    </div>

    <!-- 产品选择弹窗 -->
    <n-modal
      v-model:show="showProductModal"
      title="选择产品"
      preset="card"
      class="product-modal"
    >
      <div class="product-search">
        <n-input
          v-model:value="searchKeyword"
          placeholder="搜索产品名称或编码..."
          clearable
        >
          <template #prefix>
            <n-icon><SearchOutline /></n-icon>
          </template>
        </n-input>
      </div>

      <div class="product-grid">
        <div
          v-for="product in filteredProducts"
          :key="product.id"
          class="product-option"
          :class="{ selected: isProductSelected(product.id) }"
          @click="handleSelectProduct(product)"
        >
          <div class="option-info">
            <span class="option-name">{{ product.name }}</span>
            <span class="option-code">{{ product.code }}</span>
          </div>
          <div class="option-price">¥{{ product.price.toLocaleString() }}</div>
          <n-icon v-if="isProductSelected(product.id)" class="check-icon">
            <CheckmarkCircleOutline />
          </n-icon>
        </div>
      </div>

      <template #footer>
        <div class="modal-footer">
          <n-button @click="showProductModal = false">关闭</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import {
  NButton,
  NIcon,
  NInputNumber,
  NInput,
  NModal,
  useMessage
} from 'naive-ui'
import {
  AddOutline,
  CloseOutline,
  SearchOutline,
  CubeOutline,
  CheckmarkCircleOutline
} from '@vicons/ionicons5'
import type { OpportunityProduct } from '@/types/business/opportunity'

interface Product {
  id: number
  name: string
  code: string
  price: number
  unit?: string
}

const props = defineProps<{
  products?: OpportunityProduct[]
}>()

const emit = defineEmits<{
  (e: 'update', products: OpportunityProduct[]): void
}>()

const message = useMessage()
const showProductModal = ref(false)
const searchKeyword = ref('')

// 已选产品列表
const selectedProducts = ref<OpportunityProduct[]>([])

// 可选产品列表（模拟数据）
const availableProducts = ref<Product[]>([
  { id: 1, name: 'CRM基础版', code: 'CRM-BASIC', price: 9800, unit: '套' },
  { id: 2, name: 'CRM专业版', code: 'CRM-PRO', price: 19800, unit: '套' },
  { id: 3, name: 'CRM企业版', code: 'CRM-ENT', price: 49800, unit: '套' },
  { id: 4, name: '数据分析模块', code: 'MOD-ANALYTICS', price: 5800, unit: '个' },
  { id: 5, name: 'AI智能助手', code: 'MOD-AI', price: 8800, unit: '个' },
  { id: 6, name: '移动端扩展', code: 'MOD-MOBILE', price: 3800, unit: '个' },
  { id: 7, name: '定制开发服务', code: 'SVC-CUSTOM', price: 1500, unit: '人天' },
  { id: 8, name: '培训服务', code: 'SVC-TRAINING', price: 800, unit: '人天' }
])

// 过滤后的产品列表
const filteredProducts = computed(() => {
  if (!searchKeyword.value) return availableProducts.value
  const keyword = searchKeyword.value.toLowerCase()
  return availableProducts.value.filter(
    p => p.name.toLowerCase().includes(keyword) || p.code.toLowerCase().includes(keyword)
  )
})

// 计算总金额
const totalAmount = computed(() => {
  return selectedProducts.value.reduce((sum, p) => sum + (p.amount || 0), 0)
})

// 监听外部产品变化
watch(
  () => props.products,
  (newVal) => {
    if (newVal) {
      selectedProducts.value = [...newVal]
    }
  },
  { immediate: true, deep: true }
)

// 获取产品名称
const getProductName = (productId: number) => {
  const product = availableProducts.value.find(p => p.id === productId)
  return product?.name || '-'
}

// 获取产品编码
const getProductCode = (productId: number) => {
  const product = availableProducts.value.find(p => p.id === productId)
  return product?.code || ''
}

// 判断产品是否已选
const isProductSelected = (productId: number) => {
  return selectedProducts.value.some(p => p.productId === productId)
}

// 计算产品金额
const calculateAmount = (product: OpportunityProduct) => {
  const quantity = product.quantity || 0
  const price = product.price || 0
  const discount = product.discount || 0
  product.amount = quantity * price * (1 - discount / 100)
}

// 打开产品选择弹窗
const handleAddProduct = () => {
  searchKeyword.value = ''
  showProductModal.value = true
}

// 选择产品
const handleSelectProduct = (product: Product) => {
  if (isProductSelected(product.id)) {
    // 取消选择
    const index = selectedProducts.value.findIndex(p => p.productId === product.id)
    if (index > -1) {
      selectedProducts.value.splice(index, 1)
    }
  } else {
    // 添加产品
    const newProduct: OpportunityProduct = {
      productId: product.id,
      productName: product.name,
      productCode: product.code,
      quantity: 1,
      price: product.price,
      discount: 0,
      amount: product.price,
      unit: product.unit
    }
    selectedProducts.value.push(newProduct)
  }
  emitUpdate()
}

// 移除产品
const handleRemoveProduct = (index: number) => {
  selectedProducts.value.splice(index, 1)
  emitUpdate()
}

// 数量变化
const handleQuantityChange = (index: number) => {
  calculateAmount(selectedProducts.value[index])
  emitUpdate()
}

// 单价变化
const handlePriceChange = (index: number) => {
  calculateAmount(selectedProducts.value[index])
  emitUpdate()
}

// 折扣变化
const handleDiscountChange = (index: number) => {
  calculateAmount(selectedProducts.value[index])
  emitUpdate()
}

// 触发更新事件
const emitUpdate = () => {
  emit('update', [...selectedProducts.value])
}
</script>

<style scoped>
.product-select {
  width: 100%;
}

/* 头部 */
.select-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.select-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.add-btn {
  color: #2563eb;
  font-size: 13px;
}

/* 已选产品列表 */
.selected-products {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  position: relative;
}

.product-info {
  min-width: 140px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.product-code {
  font-size: 12px;
  color: #94a3b8;
}

.product-fields {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.field-label {
  font-size: 11px;
  color: #64748b;
}

.field-group :deep(.n-input-number) {
  width: 100px;
}

.field-group.amount {
  min-width: 100px;
}

.amount-value {
  font-size: 14px;
  font-weight: 600;
  color: #2563eb;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
}

/* 总计行 */
.total-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #dbeafe;
  border-radius: 8px;
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

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: #f8fafc;
  border-radius: 10px;
  border: 1px dashed #e2e8f0;
}

.empty-icon {
  color: #cbd5e1;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
  margin: 0 0 16px 0;
}

/* 产品选择弹窗 */
.product-modal {
  width: 600px;
}

.product-search {
  margin-bottom: 16px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
}

.product-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.product-option:hover {
  border-color: #cbd5e1;
  background: #f1f5f9;
}

.product-option.selected {
  border-color: #2563eb;
  background: #dbeafe;
}

.option-info {
  flex: 1;
}

.option-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
  display: block;
}

.option-code {
  font-size: 12px;
  color: #94a3b8;
}

.option-price {
  font-size: 14px;
  font-weight: 600;
  color: #2563eb;
}

.check-icon {
  position: absolute;
  top: 8px;
  right: 8px;
  color: #2563eb;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
