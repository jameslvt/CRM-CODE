<template>
  <div class="stage-progress">
    <!-- 当前商机信息 -->
    <div class="opportunity-info">
      <div class="info-row">
        <span class="info-label">商机名称</span>
        <span class="info-value">{{ opportunity.name }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">当前阶段</span>
        <span class="stage-badge" :style="{ background: currentStageColor.bg, color: currentStageColor.color }">
          {{ currentStageName }}
        </span>
      </div>
      <div class="info-row">
        <span class="info-label">预计金额</span>
        <span class="info-value amount">¥{{ (opportunity.amount || 0).toLocaleString() }}</span>
      </div>
    </div>

    <!-- 阶段进度条 -->
    <div class="stage-timeline">
      <div
        v-for="(stage, index) in stages"
        :key="stage.code"
        class="stage-item"
        :class="{
          active: stage.code === opportunity.stage,
          completed: isStageCompleted(stage.code),
          disabled: isStageDisabled(stage.code)
        }"
      >
        <div class="stage-dot">
          <n-icon v-if="isStageCompleted(stage.code)" size="14">
            <CheckmarkOutline />
          </n-icon>
          <span v-else>{{ index + 1 }}</span>
        </div>
        <div class="stage-content">
          <span class="stage-name">{{ stage.name }}</span>
          <span class="stage-probability">{{ stage.probability }}%</span>
        </div>
        <div v-if="index < stages.length - 1" class="stage-line" :class="{ completed: isStageCompleted(stages[index + 1].code) }"></div>
      </div>
    </div>

    <!-- 推进表单 -->
    <div class="advance-form">
      <n-form ref="formRef" :model="advanceData" :rules="rules">
        <n-form-item label="目标阶段" path="targetStage">
          <n-select
            v-model:value="advanceData.targetStage"
            placeholder="请选择目标阶段"
            :options="availableStages"
          />
        </n-form-item>

        <n-form-item v-if="advanceData.targetStage === 'LOST'" label="输单原因" path="closeReason">
          <n-select
            v-model:value="advanceData.closeReason"
            placeholder="请选择输单原因"
            :options="lostReasonOptions"
          />
        </n-form-item>

        <n-form-item label="备注" path="remark">
          <n-input
            v-model:value="advanceData.remark"
            type="textarea"
            placeholder="请输入推进备注"
            :rows="3"
            maxlength="500"
          />
        </n-form-item>
      </n-form>
    </div>

    <!-- 操作按钮 -->
    <div class="form-actions">
      <n-button @click="handleCancel">取消</n-button>
      <n-button type="primary" @click="handleAdvance" :loading="submitting">
        确认推进
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import {
  NForm,
  NFormItem,
  NInput,
  NSelect,
  NButton,
  NIcon,
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import { CheckmarkOutline } from '@vicons/ionicons5'
import type { Opportunity, StageAdvanceDTO } from '@/types/business/opportunity'

const props = defineProps<{
  opportunity: Opportunity
}>()

const emit = defineEmits<{
  (e: 'advance', data: StageAdvanceDTO): void
  (e: 'cancel'): void
}>()

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

// 阶段配置
const stages = [
  { code: 'REQUIREMENT', name: '需求确认', probability: 20, color: '#64748b' },
  { code: 'PROPOSAL', name: '方案报价', probability: 40, color: '#2563eb' },
  { code: 'NEGOTIATION', name: '商务谈判', probability: 60, color: '#f59e0b' },
  { code: 'WON', name: '赢单', probability: 100, color: '#22c55e' },
  { code: 'LOST', name: '输单', probability: 0, color: '#ef4444' }
]

// 阶段颜色映射
const stageColorMap: Record<string, { bg: string; color: string }> = {
  REQUIREMENT: { bg: '#f1f5f9', color: '#64748b' },
  PROPOSAL: { bg: '#dbeafe', color: '#2563eb' },
  NEGOTIATION: { bg: '#fef3c7', color: '#f59e0b' },
  WON: { bg: '#dcfce7', color: '#22c55e' },
  LOST: { bg: '#fee2e2', color: '#ef4444' }
}

// 输单原因选项
const lostReasonOptions = [
  { label: '价格因素', value: '价格因素' },
  { label: '竞争对手', value: '竞争对手' },
  { label: '需求变更', value: '需求变更' },
  { label: '预算不足', value: '预算不足' },
  { label: '项目取消', value: '项目取消' },
  { label: '其他原因', value: '其他原因' }
]

// 推进数据
const advanceData = reactive<StageAdvanceDTO>({
  opportunityId: props.opportunity.id,
  targetStage: '',
  remark: '',
  closeReason: ''
})

// 表单验证规则
const rules: FormRules = {
  targetStage: [
    { required: true, message: '请选择目标阶段', trigger: 'change' }
  ],
  closeReason: [
    {
      required: true,
      message: '请选择输单原因',
      trigger: 'change',
      validator: (rule, value) => {
        if (advanceData.targetStage === 'LOST' && !value) {
          return new Error('请选择输单原因')
        }
        return true
      }
    }
  ]
}

// 当前阶段名称
const currentStageName = computed(() => {
  const stage = stages.find(s => s.code === props.opportunity.stage)
  return stage?.name || '-'
})

// 当前阶段颜色
const currentStageColor = computed(() => {
  return stageColorMap[props.opportunity.stage] || stageColorMap.REQUIREMENT
})

// 获取阶段索引
const getStageIndex = (stageCode: string) => {
  return stages.findIndex(s => s.code === stageCode)
}

// 判断阶段是否已完成
const isStageCompleted = (stageCode: string) => {
  const currentIndex = getStageIndex(props.opportunity.stage)
  const targetIndex = getStageIndex(stageCode)
  return targetIndex < currentIndex
}

// 判断阶段是否禁用
const isStageDisabled = (stageCode: string) => {
  // 赢单和输单是终态
  if (props.opportunity.stage === 'WON' || props.opportunity.stage === 'LOST') {
    return true
  }
  return false
}

// 可选的目标阶段
const availableStages = computed(() => {
  const currentIndex = getStageIndex(props.opportunity.stage)
  // 只能推进到下一个阶段或直接输单
  return stages
    .filter((s, index) => {
      // 不能回退
      if (index <= currentIndex) return false
      // 已经是终态不能再推进
      if (props.opportunity.stage === 'WON' || props.opportunity.stage === 'LOST') return false
      return true
    })
    .map(s => ({
      label: s.name,
      value: s.code
    }))
})

// 监听商机变化
watch(
  () => props.opportunity,
  (newVal) => {
    advanceData.opportunityId = newVal.id
    advanceData.targetStage = ''
    advanceData.remark = ''
    advanceData.closeReason = ''
  },
  { immediate: true }
)

// 取消
const handleCancel = () => {
  emit('cancel')
}

// 推进
const handleAdvance = async () => {
  try {
    await formRef.value?.validate()
    submitting.value = true
    emit('advance', { ...advanceData })
  } catch (error: any) {
    if (error?.errorFields) {
      message.error('请检查表单输入')
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.stage-progress {
  padding: 8px 0;
}

/* 商机信息 */
.opportunity-info {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 24px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 13px;
  color: #64748b;
  min-width: 70px;
}

.info-value {
  font-size: 14px;
  color: #0f172a;
  font-weight: 500;
}

.info-value.amount {
  font-size: 16px;
  font-weight: 700;
  color: #2563eb;
}

.stage-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

/* 阶段进度条 */
.stage-timeline {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 0 10px;
}

.stage-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
}

.stage-dot {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e2e8f0;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  z-index: 1;
  transition: all 0.3s ease;
}

.stage-item.active .stage-dot {
  background: #2563eb;
  color: white;
  box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.2);
}

.stage-item.completed .stage-dot {
  background: #22c55e;
  color: white;
}

.stage-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 8px;
}

.stage-name {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.stage-item.active .stage-name {
  color: #2563eb;
  font-weight: 600;
}

.stage-item.completed .stage-name {
  color: #22c55e;
}

.stage-probability {
  font-size: 11px;
  color: #94a3b8;
}

.stage-line {
  position: absolute;
  top: 16px;
  left: calc(50% + 16px);
  width: calc(100% - 32px);
  height: 2px;
  background: #e2e8f0;
}

.stage-line.completed {
  background: #22c55e;
}

.stage-item:last-child .stage-line {
  display: none;
}

/* 推进表单 */
.advance-form {
  margin-bottom: 16px;
}

.advance-form :deep(.n-form-item) {
  margin-bottom: 16px;
}

.advance-form :deep(.n-form-item-label) {
  font-weight: 500;
  color: #475569;
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}
</style>
