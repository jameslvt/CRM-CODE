<template>
  <div class="product-selector">
    <!-- 已选产品列表 -->
    <div v-if="selectedProducts.length > 0" class="selected-products">
      <div class="selected-header">
        <span class="selected-title">已选产品 ({{ selectedProducts.length }})</span>
        <n-button text type="error" size="small" @click="handleClearAll">
          清空
        </n-button>
      </div>
      <div class="selected-list">
        <div
          v-for="product in selectedProducts"
          :key="product.id"
          class="selected-item"
        >
          <div class="product-info">
            <span class="product-name">{{ product.name }}</span>
            <span class="product-price">¥{{ (product.price || 0).toLocaleString() }}</span>
          </div>
          <n-button
            text
            type="error"
            size="small"
            @click="handleRemove(product.id)"
          >
            <template #icon>
              <n-icon><CloseOutline /></n-icon>
            </template>
          </n-button>
        </div>
      </div>
    </div>

    <!-- 添加产品按钮 -->
    <n-button
      dashed
      block
      @click="showModal = true"
      class="add-btn"
    >
      <template #icon>
        <n-icon><AddOutline /></n-icon>
      </template>
      添加产品
    </n-button>

    <!-- 产品选择弹窗 -->
    <n-modal
      v-model:show="showModal"
      title="选择产品"
      preset="card"
      class="selector-modal"
    >
      <div class="modal-content">
        <!-- 搜索框 -->
        <div class="search-box">
          <n-input
            v-model:value="searchKeyword"
            placeholder="搜索产品名称、编码..."
            clearable
            @input="handleSearch"
          >
            <template #prefix>
              <n-icon><SearchOutline /></n-icon>
            </template>
          </n-input>
        </div>

        <!-- 产品列表 -->
        <div class="product-list">
          <n-spin :show="loading">
            <div v-if="productList.length > 0" class="list-content">
              <div
                v-for="product in productList"
                :key="product.id"
                class="product-item"
                :class="{ selected: isSelected(product.id), disabled: product.status !== 1 }"
                @click="handleSelect(product)"
              >
                <div class="item-checkbox">
                  <n-checkbox
                    :checked="isSelected(product.id)"
                    :disabled="product.status !== 1"
                    @update:checked="() => handleSelect(product)"
                  />
                </div>
                <div class="item-info">
                  <div class="item-main">
                    <span class="item-name">{{ product.name }}</span>
                    <span v-if="product.code" class="item-code">{{ product.code }}</span>
                  </div>
                  <div class="item-meta">
                    <span v-if="product.category" class="meta-tag">{{ product.category }}</span>
                    <span class="item-price">¥{{ (product.price || 0).toLocaleString() }}</span>
                    <span v-if="product.unit" class="item-unit">/{{ product.unit }}</span>
                  </div>
                </div>
                <div v-if="product.status !== 1" class="item-status">
                  <span class="status-tag disabled">已停用</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-list">
              <span class="empty-text">暂无产品数据</span>
            </div>
          </n-spin>
        </div>
      </div>

      <template #footer>
        <div class="modal-footer">
          <span class="selected-count">已选择 {{ tempSelected.length }} 个产品</span>
          <div class="footer-actions">
            <n-button @click="handleCancel">取消</n-button>
            <n-button type="primary" @click="handleConfirm">确定</n-button>
          </div>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import {
  NButton,
  NIcon,
  NModal,
  NInput,
  NCheckbox,
  NSpin,
  useMessage
} from 'naive-ui'
import {
  AddOutline,
  CloseOutline,
  SearchOutline
} from '@vicons/ionicons5'
import { getEnabledProducts, pageProducts } from '@/api/business/product'
import type { Product } from '@/types/business/product'

const props = defineProps<{
  modelValue: Product[]
  multiple?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: Product[]): void
}>()

const message = useMessage()

const showModal = ref(false)
const loading = ref(false)
const searchKeyword = ref('')
const productList = ref<Product[]>([])
const selectedProducts = ref<Product[]>([])
const tempSelected = ref<Product[]>([])

// 判断是否已选中
const isSelected = (id: string) => {
  return tempSelected.value.some(p => p.id === id)
}

// 加载产品列表
const loadProducts = async () => {
  loading.value = true
  try {
    if (searchKeyword.value) {
      const result = await pageProducts({
        pageNum: 1,
        pageSize: 50,
        name: searchKeyword.value
      })
      productList.value = result.records
    } else {
      productList.value = await getEnabledProducts()
    }
  } catch (error) {
    message.error('加载产品列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索防抖
let searchTimer: ReturnType<typeof setTimeout> | null = null
const handleSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    loadProducts()
  }, 300)
}

// 选择产品
const handleSelect = (product: Product) => {
  if (product.status !== 1) return

  const index = tempSelected.value.findIndex(p => p.id === product.id)
  if (index > -1) {
    tempSelected.value.splice(index, 1)
  } else {
    if (props.multiple === false) {
      tempSelected.value = [product]
    } else {
      tempSelected.value.push(product)
    }
  }
}

// 移除已选产品
const handleRemove = (id: string) => {
  const index = selectedProducts.value.findIndex(p => p.id === id)
  if (index > -1) {
    selectedProducts.value.splice(index, 1)
    emit('update:modelValue', [...selectedProducts.value])
  }
}

// 清空所有
const handleClearAll = () => {
  selectedProducts.value = []
  emit('update:modelValue', [])
}

// 取消选择
const handleCancel = () => {
  showModal.value = false
  tempSelected.value = [...selectedProducts.value]
}

// 确认选择
const handleConfirm = () => {
  selectedProducts.value = [...tempSelected.value]
  emit('update:modelValue', [...selectedProducts.value])
  showModal.value = false
}

// 监听弹窗打开
watch(showModal, (val) => {
  if (val) {
    tempSelected.value = [...selectedProducts.value]
    searchKeyword.value = ''
    loadProducts()
  }
})

// 监听外部值变化
watch(() => props.modelValue, (val) => {
  selectedProducts.value = val ? [...val] : []
}, { immediate: true, deep: true })

onMounted(() => {
  if (props.modelValue) {
    selectedProducts.value = [...props.modelValue]
  }
})
</script>

<style scoped>
.product-selector {
  width: 100%;
}

/* 已选产品列表 */
.selected-products {
  margin-bottom: 12px;
}

.selected-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.selected-title {
  font-size: 13px;
  font-weight: 500;
  color: #475569;
}

.selected-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.selected-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.product-price {
  font-size: 13px;
  color: #2563eb;
  font-weight: 500;
}

/* 添加按钮 */
.add-btn {
  height: 44px;
  border-radius: 8px;
  border-color: #e2e8f0;
  color: #64748b;
}

.add-btn:hover {
  border-color: #2563eb;
  color: #2563eb;
}

/* 弹窗样式 */
.selector-modal {
  width: 560px;
}

.modal-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 搜索框 */
.search-box :deep(.n-input) {
  --n-height: 40px;
  --n-border-radius: 8px;
}

/* 产品列表 */
.product-list {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.list-content {
  padding: 8px;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.product-item:hover {
  background: #f8fafc;
}

.product-item.selected {
  background: #eff6ff;
  border: 1px solid #bfdbfe;
}

.product-item.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.item-checkbox {
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-main {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.item-code {
  font-size: 12px;
  color: #94a3b8;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-tag {
  font-size: 11px;
  padding: 2px 6px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 4px;
}

.item-price {
  font-size: 13px;
  color: #2563eb;
  font-weight: 500;
}

.item-unit {
  font-size: 12px;
  color: #94a3b8;
}

.item-status {
  flex-shrink: 0;
}

.status-tag.disabled {
  font-size: 11px;
  padding: 2px 8px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 4px;
}

/* 空状态 */
.empty-list {
  padding: 60px 20px;
  text-align: center;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
}

/* 弹窗底部 */
.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-count {
  font-size: 13px;
  color: #64748b;
}

.footer-actions {
  display: flex;
  gap: 12px;
}

.footer-actions :deep(.n-button) {
  height: 36px;
  padding: 0 20px;
  border-radius: 8px;
}
</style>
