<template>
  <div class="contract-detail">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <n-button text @click="handleBack" class="back-btn">
          <template #icon>
            <n-icon><ArrowBackOutline /></n-icon>
          </template>
          返回列表
        </n-button>
        <div class="header-content">
          <h1 class="page-title">{{ contract?.name || '合同详情' }}</h1>
          <div class="header-meta">
            <span class="status-badge" :style="{ background: statusColor.bg, color: statusColor.color }">
              {{ statusName }}
            </span>
            <span v-if="contract?.contractNo" class="meta-item">
              <n-icon><DocumentTextOutline /></n-icon>
              {{ contract.contractNo }}
            </span>
            <span class="meta-item">
              <n-icon><PersonOutline /></n-icon>
              {{ contract?.ownerName || '-' }}
            </span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <n-button @click="handleEdit" class="secondary-btn">
          <template #icon>
            <n-icon><CreateOutline /></n-icon>
          </template>
          编辑
        </n-button>
        <!-- 状态操作按钮 -->
        <n-button
          v-if="contract?.status === ContractStatus.DRAFT"
          type="primary"
          @click="handleSubmit"
          class="primary-btn"
        >
          提交审批
        </n-button>
        <n-button
          v-if="contract?.status === ContractStatus.PENDING"
          type="success"
          @click="handleApprove"
          class="success-btn"
        >
          审批通过
        </n-button>
        <n-button
          v-if="contract?.status === ContractStatus.PENDING"
          type="error"
          @click="handleReject"
          class="error-btn"
        >
          驳回
        </n-button>
        <n-button
          v-if="contract?.status === ContractStatus.EXECUTING"
          type="primary"
          @click="handleComplete"
          class="primary-btn"
        >
          完成合同
        </n-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <n-spin size="large" />
    </div>

    <!-- 详情内容 -->
    <div v-else-if="contract" class="detail-content">
      <!-- 核心指标卡片 -->
      <div class="metrics-row">
        <div class="metric-card amount">
          <div class="metric-icon">
            <n-icon size="24"><CashOutline /></n-icon>
          </div>
          <div class="metric-info">
            <span class="metric-value">¥{{ (contract.amount || 0).toLocaleString() }}</span>
            <span class="metric-label">合同金额</span>
          </div>
        </div>
        <div class="metric-card paid">
          <div class="metric-icon">
            <n-icon size="24"><WalletOutline /></n-icon>
          </div>
          <div class="metric-info">
            <span class="metric-value">¥{{ (contract.paidAmount || 0).toLocaleString() }}</span>
            <span class="metric-label">已回款</span>
          </div>
        </div>
        <div class="metric-card unpaid">
          <div class="metric-icon">
            <n-icon size="24"><TimeOutline /></n-icon>
          </div>
          <div class="metric-info">
            <span class="metric-value">¥{{ (contract.unpaidAmount || 0).toLocaleString() }}</span>
            <span class="metric-label">待回款</span>
          </div>
        </div>
        <div class="metric-card progress">
          <div class="metric-icon">
            <n-icon size="24"><StatsChartOutline /></n-icon>
          </div>
          <div class="metric-info">
            <span class="metric-value">{{ paymentProgress }}%</span>
            <span class="metric-label">回款进度</span>
          </div>
        </div>
      </div>

      <!-- 回款进度条 -->
      <div class="progress-card">
        <div class="card-header">
          <h3 class="card-title">回款进度</h3>
          <span class="progress-text">{{ paymentProgress }}%</span>
        </div>
        <n-progress
          type="line"
          :percentage="paymentProgress"
          :height="12"
          :border-radius="6"
          :fill-border-radius="6"
          :color="paymentProgress >= 100 ? '#22c55e' : '#2563eb'"
          :rail-color="'#e2e8f0'"
        />
        <div class="progress-info">
          <span class="info-item">
            <span class="info-label">已回款</span>
            <span class="info-value paid">¥{{ (contract.paidAmount || 0).toLocaleString() }}</span>
          </span>
          <span class="info-item">
            <span class="info-label">待回款</span>
            <span class="info-value unpaid">¥{{ (contract.unpaidAmount || 0).toLocaleString() }}</span>
          </span>
          <span class="info-item">
            <span class="info-label">合同总额</span>
            <span class="info-value total">¥{{ (contract.amount || 0).toLocaleString() }}</span>
          </span>
        </div>
      </div>

      <!-- 详情信息 -->
      <div class="info-grid">
        <!-- 基本信息 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">基本信息</h3>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">合同名称</span>
              <span class="info-value">{{ contract.name }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">合同编号</span>
              <span class="info-value">{{ contract.contractNo || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">关联客户</span>
              <span class="info-value clickable" @click="handleViewCustomer">
                {{ contract.customerName || '-' }}
              </span>
            </div>
            <div class="info-item">
              <span class="info-label">关联商机</span>
              <span
                v-if="contract.opportunityId"
                class="info-value clickable"
                @click="handleViewOpportunity"
              >
                {{ contract.opportunityName || '-' }}
              </span>
              <span v-else class="info-value">-</span>
            </div>
            <div class="info-item">
              <span class="info-label">负责人</span>
              <span class="info-value">{{ contract.ownerName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">合同状态</span>
              <span
                class="status-tag"
                :style="{ background: statusColor.bg, color: statusColor.color }"
              >
                {{ statusName }}
              </span>
            </div>
          </div>
        </div>

        <!-- 日期信息 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">日期信息</h3>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">签约日期</span>
              <span class="info-value">{{ contract.signDate || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">开始日期</span>
              <span class="info-value">{{ contract.startDate || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">结束日期</span>
              <span class="info-value">{{ contract.endDate || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ contract.createTime || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">更新时间</span>
              <span class="info-value">{{ contract.updateTime || '-' }}</span>
            </div>
            <div v-if="contract.remark" class="info-item full">
              <span class="info-label">备注</span>
              <span class="info-value">{{ contract.remark }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 合同文件 -->
      <div class="file-card">
        <div class="card-header">
          <h3 class="card-title">合同文件</h3>
          <n-button size="small" @click="showUploadModal = true">
            <template #icon>
              <n-icon><CloudUploadOutline /></n-icon>
            </template>
            上传文件
          </n-button>
        </div>
        <div v-if="contract.fileUrl" class="file-content">
          <div class="file-item">
            <n-icon size="24" class="file-icon"><DocumentOutline /></n-icon>
            <div class="file-info">
              <span class="file-name">合同文件</span>
              <span class="file-url">{{ contract.fileUrl }}</span>
            </div>
            <n-button text type="primary" @click="handleDownload">
              <template #icon>
                <n-icon><DownloadOutline /></n-icon>
              </template>
              下载
            </n-button>
          </div>
        </div>
        <div v-else class="empty-file">
          <n-icon size="48" class="empty-icon"><DocumentOutline /></n-icon>
          <span class="empty-text">暂无合同文件</span>
          <n-button size="small" @click="showUploadModal = true">上传文件</n-button>
        </div>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <n-modal
      v-model:show="showEditModal"
      title="编辑合同"
      preset="card"
      class="form-modal"
    >
      <contract-form
        v-if="contract"
        :form-data="contract"
        @submit="handleEditSubmit"
        @cancel="showEditModal = false"
      />
    </n-modal>

    <!-- 文件上传弹窗 -->
    <n-modal
      v-model:show="showUploadModal"
      title="上传合同文件"
      preset="card"
      class="upload-modal"
    >
      <file-upload
        v-model="fileUrl"
        accept=".pdf,.doc,.docx"
        :max-size="10"
        @success="handleUploadSuccess"
      />
      <template #footer>
        <div class="modal-footer">
          <n-button @click="showUploadModal = false">取消</n-button>
          <n-button type="primary" @click="handleSaveFile" :disabled="!fileUrl">保存</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NButton,
  NIcon,
  NSpin,
  NProgress,
  useMessage,
  useDialog
} from 'naive-ui'
import {
  ArrowBackOutline,
  CreateOutline,
  CashOutline,
  WalletOutline,
  TimeOutline,
  StatsChartOutline,
  PersonOutline,
  DocumentTextOutline,
  CloudUploadOutline,
  DocumentOutline,
  DownloadOutline
} from '@vicons/ionicons5'
import {
  getContractById,
  submitContractForApproval,
  approveContract,
  rejectContract,
  completeContract,
  updateContractFile
} from '@/api/business/contract'
import type { Contract } from '@/types/business/contract'
import {
  ContractStatus,
  getContractStatusColor,
  getContractStatusName
} from '@/types/business/contract'
import ContractForm from '@/components/business/ContractForm.vue'
import FileUpload from '@/components/common/FileUpload.vue'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const loading = ref(true)
const contract = ref<Contract | null>(null)
const showEditModal = ref(false)
const showUploadModal = ref(false)
const fileUrl = ref('')

// 状态名称
const statusName = computed(() => {
  if (!contract.value) return '-'
  return getContractStatusName(contract.value.status)
})

// 状态颜色
const statusColor = computed(() => {
  if (!contract.value) return { bg: '#f1f5f9', color: '#64748b' }
  return getContractStatusColor(contract.value.status)
})

// 回款进度
const paymentProgress = computed(() => {
  if (!contract.value) return 0
  const total = contract.value.amount || 0
  const paid = contract.value.paidAmount || 0
  if (total === 0) return 0
  return Math.round((paid / total) * 100)
})

// 加载合同详情
const loadContract = async () => {
  const id = String(route.params.id)
  if (!id) {
    message.error('合同ID无效')
    router.push('/contracts')
    return
  }

  loading.value = true
  try {
    contract.value = await getContractById(id)
    fileUrl.value = contract.value.fileUrl || ''
  } catch (error) {
    message.error('加载合同详情失败')
    router.push('/contracts')
  } finally {
    loading.value = false
  }
}

// 返回列表
const handleBack = () => {
  router.push('/contracts')
}

// 编辑
const handleEdit = () => {
  showEditModal.value = true
}

// 编辑提交
const handleEditSubmit = () => {
  showEditModal.value = false
  loadContract()
}

// 提交审批
const handleSubmit = async () => {
  if (!contract.value) return
  try {
    await submitContractForApproval(contract.value.id)
    message.success('提交成功')
    loadContract()
  } catch (error) {
    message.error('提交失败')
  }
}

// 审批通过
const handleApprove = () => {
  if (!contract.value) return
  dialog.warning({
    title: '确认审批',
    content: '确定要通过该合同的审批吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await approveContract(contract.value!.id)
        message.success('审批通过')
        loadContract()
      } catch (error) {
        message.error('操作失败')
      }
    }
  })
}

// 审批驳回
const handleReject = () => {
  if (!contract.value) return
  dialog.warning({
    title: '确认驳回',
    content: '确定要驳回该合同吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await rejectContract(contract.value!.id)
        message.success('已驳回')
        loadContract()
      } catch (error) {
        message.error('操作失败')
      }
    }
  })
}

// 完成合同
const handleComplete = () => {
  if (!contract.value) return
  dialog.warning({
    title: '确认完成',
    content: '确定要将该合同标记为已完成吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await completeContract(contract.value!.id)
        message.success('合同已完成')
        loadContract()
      } catch (error) {
        message.error('操作失败')
      }
    }
  })
}

// 查看客户
const handleViewCustomer = () => {
  if (contract.value?.customerId) {
    router.push(`/business/customer/${contract.value.customerId}`)
  }
}

// 查看商机
const handleViewOpportunity = () => {
  if (contract.value?.opportunityId) {
    router.push(`/business/opportunity/${contract.value.opportunityId}`)
  }
}

// 上传成功
const handleUploadSuccess = (url: string) => {
  fileUrl.value = url
}

// 保存文件
const handleSaveFile = async () => {
  if (!contract.value || !fileUrl.value) return
  try {
    await updateContractFile(contract.value.id, fileUrl.value)
    message.success('文件保存成功')
    showUploadModal.value = false
    loadContract()
  } catch (error) {
    message.error('保存失败')
  }
}

// 下载文件
const handleDownload = () => {
  if (contract.value?.fileUrl) {
    window.open(contract.value.fileUrl, '_blank')
  }
}

onMounted(() => {
  loadContract()
})
</script>

<style scoped>
.contract-detail {
  width: 100%;
  min-height: 100%;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.back-btn {
  color: #64748b;
  font-size: 13px;
  padding: 0;
}

.back-btn:hover {
  color: #2563eb;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
  letter-spacing: -0.02em;
}

.header-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #64748b;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.secondary-btn {
  height: 40px;
  padding: 0 16px;
  border-radius: 10px;
  font-weight: 500;
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
}

.primary-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  background: #2563eb;
  border: none;
}

.success-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  background: #22c55e;
  border: none;
}

.error-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  background: #ef4444;
  border: none;
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

/* 详情内容 */
.detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 核心指标卡片 */
.metrics-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.metric-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.metric-card.amount .metric-icon {
  background: #dcfce7;
  color: #22c55e;
}

.metric-card.paid .metric-icon {
  background: #dbeafe;
  color: #2563eb;
}

.metric-card.unpaid .metric-icon {
  background: #fef3c7;
  color: #f59e0b;
}

.metric-card.progress .metric-icon {
  background: #f3e8ff;
  color: #9333ea;
}

.metric-info {
  display: flex;
  flex-direction: column;
}

.metric-value {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
}

.metric-label {
  font-size: 12px;
  color: #64748b;
}

/* 进度卡片 */
.progress-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.progress-text {
  font-size: 18px;
  font-weight: 700;
  color: #2563eb;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
}

.progress-info .info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.progress-info .info-label {
  font-size: 12px;
  color: #64748b;
}

.progress-info .info-value {
  font-size: 16px;
  font-weight: 600;
}

.progress-info .info-value.paid {
  color: #22c55e;
}

.progress-info .info-value.unpaid {
  color: #f59e0b;
}

.progress-info .info-value.total {
  color: #0f172a;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.info-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 24px;
}

/* 信息列表 */
.info-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full {
  grid-column: span 2;
}

.info-label {
  font-size: 12px;
  color: #64748b;
}

.info-value {
  font-size: 14px;
  color: #0f172a;
  font-weight: 500;
}

.info-value.clickable {
  color: #2563eb;
  cursor: pointer;
}

.info-value.clickable:hover {
  text-decoration: underline;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  width: fit-content;
}

/* 文件卡片 */
.file-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 24px;
}

.file-content {
  margin-top: 16px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
}

.file-icon {
  color: #2563eb;
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.file-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.file-url {
  font-size: 12px;
  color: #64748b;
  word-break: break-all;
}

.empty-file {
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

/* 弹窗样式 */
.form-modal {
  width: 700px;
}

.upload-modal {
  width: 500px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 1280px) {
  .metrics-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .metrics-row {
    grid-template-columns: 1fr;
  }

  .info-list {
    grid-template-columns: 1fr;
  }

  .info-item.full {
    grid-column: span 1;
  }

  .progress-info {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
