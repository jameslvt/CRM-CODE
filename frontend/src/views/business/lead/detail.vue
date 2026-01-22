<template>
  <div class="lead-detail">
    <n-page-header @back="handleBack">
      <template #title>线索详情</template>
      <template #extra>
        <n-space>
          <n-button @click="handleEdit">
            <template #icon>
              <n-icon><CreateOutline /></n-icon>
            </template>
            编辑
          </n-button>
          <n-button
            v-if="leadData?.status !== 3"
            type="primary"
            @click="handleConvert"
          >
            <template #icon>
              <n-icon><SwapHorizontalOutline /></n-icon>
            </template>
            转化为客户
          </n-button>
        </n-space>
      </template>
    </n-page-header>

    <n-spin :show="loading">
      <n-grid :cols="24" :x-gap="16" v-if="leadData">
        <!-- 基本信息 -->
        <n-gi :span="16">
          <n-card title="基本信息" :bordered="false">
            <n-descriptions :column="2" label-placement="left">
              <n-descriptions-item label="线索名称">
                {{ leadData.name }}
              </n-descriptions-item>
              <n-descriptions-item label="状态">
                <n-tag :type="getStatusColor(String(leadData.status))" size="small">
                  {{ getStatusLabel(String(leadData.status)) }}
                </n-tag>
              </n-descriptions-item>
              <n-descriptions-item label="线索名称">
                {{ leadData.name }}
              </n-descriptions-item>
              <n-descriptions-item label="联系电话">
                {{ leadData.phone }}
              </n-descriptions-item>
              <n-descriptions-item label="邮箱">
                {{ leadData.email || '-' }}
              </n-descriptions-item>
              <n-descriptions-item label="评级">
                <n-tag :type="getRatingColor(leadData.rating || '')" size="small">
                  {{ getRatingLabel(leadData.rating || '') }}
                </n-tag>
              </n-descriptions-item>
              <n-descriptions-item label="公司名称">
                {{ leadData.company }}
              </n-descriptions-item>
              <n-descriptions-item label="职位">
                {{ leadData.position || '-' }}
              </n-descriptions-item>
              <n-descriptions-item label="来源">
                {{ leadData.source }}
              </n-descriptions-item>
              <n-descriptions-item label="行业">
                {{ leadData.industry || '-' }}
              </n-descriptions-item>
              <n-descriptions-item label="地址" :span="2">
                {{ leadData.address || '-' }}
              </n-descriptions-item>
              <n-descriptions-item label="备注" :span="2">
                {{ leadData.remark || '-' }}
              </n-descriptions-item>
            </n-descriptions>
          </n-card>
        </n-gi>

        <!-- 侧边信息 -->
        <n-gi :span="8">
          <n-card title="其他信息" :bordered="false">
            <n-descriptions :column="1" label-placement="left">
              <n-descriptions-item label="负责人">
                {{ leadData.ownerName }}
              </n-descriptions-item>
              <n-descriptions-item label="创建时间">
                {{ leadData.createTime }}
              </n-descriptions-item>
              <n-descriptions-item label="更新时间">
                {{ leadData.updateTime || '-' }}
              </n-descriptions-item>
              <n-descriptions-item
                v-if="leadData.status === 3"
                label="转化时间"
              >
                {{ leadData.convertTime || '-' }}
              </n-descriptions-item>
            </n-descriptions>
          </n-card>
        </n-gi>

        <!-- 跟进记录 -->
        <n-gi :span="24">
          <n-card title="跟进记录" :bordered="false">
            <n-empty
              description="暂无跟进记录"
              style="padding: 40px 0"
            />
          </n-card>
        </n-gi>
      </n-grid>
    </n-spin>

    <!-- 编辑弹窗 -->
    <n-modal
      v-model:show="showEditModal"
      title="编辑线索"
      preset="card"
      style="width: 800px"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <lead-form
        ref="leadFormRef"
        :form-data="leadData"
        @submit="handleEditSubmit"
        @cancel="showEditModal = false"
      />
    </n-modal>

    <!-- 转化弹窗 -->
    <n-modal
      v-model:show="showConvertModal"
      title="转化为客户"
      preset="card"
      style="width: 600px"
      :segmented="{ content: 'soft', footer: 'soft' }"
    >
      <lead-convert-dialog
        ref="convertDialogRef"
        :lead-id="leadId"
        @submit="handleConvertSubmit"
        @cancel="showConvertModal = false"
      />
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NIcon, useMessage } from 'naive-ui'
import { CreateOutline, SwapHorizontalOutline } from '@vicons/ionicons5'
import { getLeadById } from '@/api/business/lead'
import type { Lead } from '@/types/business'
import LeadForm from '@/components/business/LeadForm.vue'
import LeadConvertDialog from '@/components/business/LeadConvertDialog.vue'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const leadId = ref(String(route.params.id))
const loading = ref(false)
const leadData = ref<Lead>()
const showEditModal = ref(false)
const showConvertModal = ref(false)
const leadFormRef = ref()
const convertDialogRef = ref()

// 状态映射
const statusMap: Record<string, { label: string; color: string }> = {
  '1': { label: '新建', color: 'info' },
  '2': { label: '跟进中', color: 'warning' },
  '3': { label: '已转化', color: 'success' },
  '4': { label: '已失效', color: 'default' }
}

// 评级映射
const ratingMap: Record<string, { label: string; color: string }> = {
  A: { label: 'A高', color: 'error' },
  B: { label: 'B中', color: 'warning' },
  C: { label: 'C低', color: 'info' }
}

const getStatusLabel = (status: string) => statusMap[status]?.label || status
const getStatusColor = (status: string) => statusMap[status]?.color || 'default'
const getRatingLabel = (rating: string) => ratingMap[rating]?.label || rating
const getRatingColor = (rating: string) => ratingMap[rating]?.color || 'default'

// 加载线索详情
const loadLeadDetail = async () => {
  loading.value = true
  try {
    leadData.value = await getLeadById(leadId.value)
  } catch (error) {
    message.error('加载线索详情失败')
  } finally {
    loading.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

// 编辑
const handleEdit = () => {
  showEditModal.value = true
}

// 编辑提交
const handleEditSubmit = () => {
  showEditModal.value = false
  loadLeadDetail()
}

// 转化
const handleConvert = () => {
  showConvertModal.value = true
}

// 转化提交
const handleConvertSubmit = () => {
  showConvertModal.value = false
  loadLeadDetail()
}

onMounted(() => {
  loadLeadDetail()
})
</script>

<style scoped lang="scss">
.lead-detail {
  width: 100%;
  height: 100%;
  font-family: var(--font-body);

  :deep(.n-page-header) {
    margin-bottom: var(--spacing-base);

    .n-page-header__title {
      font-family: var(--font-heading);
      font-weight: var(--font-weight-semibold);
    }
  }

  :deep(.n-card) {
    margin-bottom: var(--spacing-base);
    border-radius: var(--radius-lg);

    .n-card-header__main {
      font-family: var(--font-heading);
      font-weight: var(--font-weight-semibold);
    }
  }

  :deep(.n-descriptions-item-label) {
    color: var(--text-secondary);
    font-weight: var(--font-weight-medium);
  }

  :deep(.n-descriptions-item-content) {
    color: var(--text-primary);
  }

  :deep(.n-tag) {
    font-weight: var(--font-weight-medium);
    padding: 4px 12px;
    border-radius: var(--radius-md);
  }
}

/* 按钮悬停效果 */
:deep(.n-button) {
  transition: all var(--transition-base);
  cursor: pointer;
  font-family: var(--font-body);
  font-weight: var(--font-weight-medium);
}

:deep(.n-button:hover) {
  transform: translateY(-1px);
}

:deep(.n-button:active) {
  transform: translateY(0);
}

/* 主按钮样式 */
:deep(.n-button--primary-type) {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

:deep(.n-button--primary-type:hover) {
  background-color: var(--primary-hover);
  border-color: var(--primary-hover);
}

:deep(.n-button--primary-type:active) {
  background-color: var(--primary-active);
  border-color: var(--primary-active);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .lead-detail {
    :deep(.n-grid) {
      display: flex;
      flex-direction: column;
    }

    :deep(.n-gi) {
      width: 100% !important;
    }
  }
}
</style>
