<template>
  <div class="payment-records-list">
    <!-- 计划信息 -->
    <div v-if="planInfo" class="plan-info">
      <div class="info-row">
        <div class="info-item">
          <span class="info-label">计划金额</span>
          <span class="info-value amount">¥{{ formatAmount(planInfo.planAmount) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">已回款</span>
          <span class="info-value received">¥{{ formatAmount(planInfo.actualAmount) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">待回款</span>
          <span class="info-value pending">¥{{ formatAmount(remainingAmount) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">回款进度</span>
          <span class="info-value">{{ paymentProgress }}%</span>
        </div>
      </div>
      <n-progress
        type="line"
        :percentage="paymentProgress"
        :height="8"
        :border-radius="4"
        :fill-border-radius="4"
        :color="paymentProgress >= 100 ? '#22c55e' : '#2563eb'"
        :rail-color="'#e2e8f0'"
      />
    </div>

    <!-- 操作按钮 -->
    <div class="actions-bar">
      <n-button type="primary" size="small" @click="handleAdd" :disabled="isCompleted">
        <template #icon>
          <n-icon><AddOutline /></n-icon>
        </template>
        添加回款记录
      </n-button>
    </div>

    <!-- 记录列表 -->
    <div class="records-content">
      <n-spin :show="loading">
        <div v-if="records.length > 0" class="records-timeline">
          <div
            v-for="(record, index) in records"
            :key="record.id"
            class="record-item"
          >
            <div class="record-dot"></div>
            <div class="record-card">
              <div class="record-header">
                <span class="record-amount">¥{{ formatAmount(record.amount) }}</span>
                <span class="record-date">{{ record.paymentDate }}</span>
              </div>
              <div class="record-body">
                <div class="record-info">
                  <span v-if="record.paymentMethodName" class="method-tag">
                    {{ record.paymentMethodName }}
                  </span>
                  <span v-if="record.remark" class="record-remark">{{ record.remark }}</span>
                </div>
                <div class="record-meta">
                  <span class="meta-item">
                    <n-icon size="12"><PersonOutline /></n-icon>
                    {{ record.createByName || '-' }}
                  </span>
                  <span class="meta-item">
                    <n-icon size="12"><TimeOutline /></n-icon>
                    {{ record.createTime }}
                  </span>
                </div>
              </div>
              <n-popconfirm @positive-click="handleDelete(record.id)">
                <template #trigger>
                  <n-button text type="error" size="small" class="delete-btn">
                    <template #icon>
                      <n-icon><TrashOutline /></n-icon>
                    </template>
                  </n-button>
                </template>
                确定要删除该回款记录吗？
              </n-popconfirm>
            </div>
          </div>
        </div>
        <div v-else class="empty-records">
          <n-icon size="48" class="empty-icon"><WalletOutline /></n-icon>
          <span class="empty-text">暂无回款记录</span>
        </div>
      </n-spin>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  NButton,
  NIcon,
  NProgress,
  NSpin,
  NPopconfirm,
  useMessage
} from 'naive-ui'
import {
  AddOutline,
  PersonOutline,
  TimeOutline,
  TrashOutline,
  WalletOutline
} from '@vicons/ionicons5'
import {
  getPaymentPlanById,
  getPaymentRecordsByPlanId,
  deletePaymentRecord
} from '@/api/business/payment'
import type { PaymentPlan, PaymentRecord } from '@/types/business/payment'
import { PaymentPlanStatus } from '@/types/business/payment'

const props = defineProps<{
  planId: number
}>()

const emit = defineEmits<{
  (e: 'add', planId: number): void
}>()

const message = useMessage()
const loading = ref(false)
const planInfo = ref<PaymentPlan | null>(null)
const records = ref<PaymentRecord[]>([])

// 剩余金额
const remainingAmount = computed(() => {
  if (!planInfo.value) return 0
  return (planInfo.value.planAmount || 0) - (planInfo.value.actualAmount || 0)
})

// 回款进度
const paymentProgress = computed(() => {
  if (!planInfo.value) return 0
  const total = planInfo.value.planAmount || 0
  const actual = planInfo.value.actualAmount || 0
  if (total === 0) return 0
  return Math.min(100, Math.round((actual / total) * 100))
})

// 是否已完成
const isCompleted = computed(() => {
  return planInfo.value?.status === PaymentPlanStatus.COMPLETED
})

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (!amount) return '0'
  return amount.toLocaleString()
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const [plan, recordList] = await Promise.all([
      getPaymentPlanById(props.planId),
      getPaymentRecordsByPlanId(props.planId)
    ])
    planInfo.value = plan
    records.value = recordList
  } catch (error) {
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 添加回款记录
const handleAdd = () => {
  emit('add', props.planId)
}

// 删除回款记录
const handleDelete = async (id: string) => {
  try {
    await deletePaymentRecord(id)
    message.success('删除成功')
    loadData()
  } catch (error) {
    message.error('删除失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.payment-records-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 计划信息 */
.plan-info {
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-label {
  font-size: 12px;
  color: #64748b;
}

.info-value {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}

.info-value.amount {
  color: #2563eb;
}

.info-value.received {
  color: #22c55e;
}

.info-value.pending {
  color: #f59e0b;
}

/* 操作按钮 */
.actions-bar {
  display: flex;
  justify-content: flex-end;
}

/* 记录内容 */
.records-content {
  min-height: 200px;
}

/* 时间线 */
.records-timeline {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-left: 20px;
  position: relative;
}

.records-timeline::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: #e2e8f0;
}

.record-item {
  position: relative;
  display: flex;
  gap: 16px;
}

.record-dot {
  position: absolute;
  left: -20px;
  top: 8px;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #22c55e;
  border: 3px solid white;
  box-shadow: 0 0 0 2px #e2e8f0;
  z-index: 1;
}

.record-card {
  flex: 1;
  padding: 12px 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  position: relative;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.record-amount {
  font-size: 18px;
  font-weight: 700;
  color: #22c55e;
}

.record-date {
  font-size: 13px;
  color: #64748b;
}

.record-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.method-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: #dbeafe;
  color: #2563eb;
  border-radius: 4px;
}

.record-remark {
  font-size: 13px;
  color: #475569;
}

.record-meta {
  display: flex;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #94a3b8;
}

.delete-btn {
  position: absolute;
  top: 12px;
  right: 12px;
}

/* 空状态 */
.empty-records {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 40px 20px;
}

.empty-icon {
  color: #cbd5e1;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
}
</style>
