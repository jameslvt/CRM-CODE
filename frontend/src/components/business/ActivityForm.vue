<template>
  <div class="activity-form">
    <n-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-placement="left"
      label-width="80"
      require-mark-placement="right-hanging"
    >
      <n-form-item label="跟进类型" path="type">
        <div class="type-selector">
          <button
            v-for="option in activityTypeOptions"
            :key="option.value"
            type="button"
            class="type-btn"
            :class="{ active: formData.type === option.value }"
            :style="formData.type === option.value ? { background: getTypeColor(option.value).bg, color: getTypeColor(option.value).color, borderColor: getTypeColor(option.value).color } : {}"
            @click="formData.type = option.value"
          >
            <n-icon size="16">
              <component :is="getTypeIcon(option.value)" />
            </n-icon>
            {{ option.label }}
          </button>
        </div>
      </n-form-item>

      <n-form-item label="跟进内容" path="content">
        <n-input
          v-model:value="formData.content"
          type="textarea"
          placeholder="请输入跟进内容，记录沟通要点..."
          :rows="5"
          maxlength="2000"
          show-count
        />
      </n-form-item>

      <n-form-item label="下次跟进" path="nextTime">
        <n-date-picker
          v-model:formatted-value="formData.nextTime"
          type="datetime"
          placeholder="选择下次跟进时间（可选）"
          value-format="yyyy-MM-dd HH:mm:ss"
          class="full-width"
          clearable
        />
      </n-form-item>
    </n-form>

    <!-- 快捷模板 -->
    <div class="quick-templates">
      <span class="template-label">快捷模板:</span>
      <div class="template-list">
        <button
          v-for="template in quickTemplates"
          :key="template.label"
          type="button"
          class="template-btn"
          @click="applyTemplate(template)"
        >
          {{ template.label }}
        </button>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="form-footer">
      <n-button @click="handleCancel" class="cancel-btn">取消</n-button>
      <n-button type="primary" @click="handleSubmit" :loading="submitting" class="submit-btn">
        {{ isEdit ? '保存修改' : '添加跟进' }}
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
  NDatePicker,
  NButton,
  NIcon,
  useMessage,
  type FormInst,
  type FormRules
} from 'naive-ui'
import {
  CallOutline,
  WalkOutline,
  MailOutline,
  PeopleOutline,
  EllipsisHorizontalOutline
} from '@vicons/ionicons5'
import { createActivity, updateActivity } from '@/api/business/activity'
import type { Activity, ActivityFormData } from '@/types/business/activity'
import { ActivityType, activityTypeOptions, getActivityTypeColor } from '@/types/business/activity'

const props = defineProps<{
  targetType: string
  targetId: number
  editData?: Activity | null
}>()

const emit = defineEmits<{
  (e: 'submit'): void
  (e: 'cancel'): void
}>()

const message = useMessage()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

// 判断是否为编辑模式
const isEdit = computed(() => !!props.editData?.id)

// 表单数据
const formData = ref<ActivityFormData>({
  type: ActivityType.PHONE,
  content: '',
  targetType: props.targetType,
  targetId: props.targetId,
  nextTime: undefined
})

// 表单验证规则
const rules: FormRules = {
  type: [
    { required: true, message: '请选择跟进类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入跟进内容', trigger: 'blur' },
    { min: 1, max: 2000, message: '跟进内容长度不能超过2000个字符', trigger: 'blur' }
  ]
}

// 快捷模板
const quickTemplates = [
  { label: '初次联系', type: ActivityType.PHONE, content: '初次电话联系客户，介绍公司产品和服务。\n\n客户反馈：\n\n下一步计划：' },
  { label: '需求沟通', type: ActivityType.MEETING, content: '与客户进行需求沟通会议。\n\n讨论要点：\n1. \n2. \n3. \n\n客户需求：\n\n下一步计划：' },
  { label: '方案演示', type: ActivityType.VISIT, content: '上门拜访客户，进行产品方案演示。\n\n演示内容：\n\n客户反馈：\n\n下一步计划：' },
  { label: '报价跟进', type: ActivityType.EMAIL, content: '发送报价方案邮件给客户。\n\n报价内容：\n\n客户反馈：\n\n下一步计划：' },
  { label: '合同洽谈', type: ActivityType.MEETING, content: '与客户进行合同条款洽谈。\n\n洽谈要点：\n\n达成共识：\n\n待确认事项：' }
]

// 获取类型颜色
const getTypeColor = (type: string) => {
  return getActivityTypeColor(type)
}

// 获取类型图标
const getTypeIcon = (type: string) => {
  const iconMap: Record<string, any> = {
    [ActivityType.PHONE]: CallOutline,
    [ActivityType.VISIT]: WalkOutline,
    [ActivityType.EMAIL]: MailOutline,
    [ActivityType.MEETING]: PeopleOutline,
    [ActivityType.OTHER]: EllipsisHorizontalOutline
  }
  return iconMap[type] || EllipsisHorizontalOutline
}

// 应用模板
const applyTemplate = (template: { label: string; type: string; content: string }) => {
  formData.value.type = template.type
  formData.value.content = template.content
}

// 初始化表单数据
const initFormData = () => {
  if (props.editData) {
    formData.value = {
      id: props.editData.id,
      type: props.editData.type,
      content: props.editData.content,
      targetType: props.targetType,
      targetId: props.targetId,
      nextTime: props.editData.nextTime
    }
  } else {
    formData.value = {
      type: ActivityType.PHONE,
      content: '',
      targetType: props.targetType,
      targetId: props.targetId,
      nextTime: undefined
    }
  }
}

// 监听 editData 变化
watch(() => props.editData, () => {
  initFormData()
}, { immediate: true })

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate()

    submitting.value = true

    const submitData: ActivityFormData = {
      type: formData.value.type,
      content: formData.value.content,
      targetType: props.targetType,
      targetId: props.targetId,
      nextTime: formData.value.nextTime || undefined
    }

    if (isEdit.value && formData.value.id) {
      await updateActivity(formData.value.id, submitData)
      message.success('跟进记录更新成功')
    } else {
      await createActivity(submitData)
      message.success('跟进记录添加成功')
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
.activity-form {
  padding: 8px 0;
}

.full-width {
  width: 100%;
}

/* 类型选择器 */
.type-selector {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.type-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 8px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.type-btn:hover {
  border-color: #cbd5e1;
  background: #f8fafc;
}

.type-btn.active {
  font-weight: 500;
}

/* 表单项样式 */
:deep(.n-form-item) {
  margin-bottom: 20px;
}

:deep(.n-form-item-label) {
  font-size: 13px;
  color: #475569;
}

:deep(.n-input),
:deep(.n-date-picker) {
  --n-border-radius: 8px;
}

:deep(.n-input--textarea) {
  --n-height: auto;
}

/* 快捷模板 */
.quick-templates {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 10px;
  margin-bottom: 20px;
}

.template-label {
  font-size: 12px;
  color: #64748b;
  white-space: nowrap;
  padding-top: 4px;
}

.template-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.template-btn {
  padding: 4px 10px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 4px;
  font-size: 12px;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s ease;
}

.template-btn:hover {
  border-color: #2563eb;
  color: #2563eb;
  background: #eff6ff;
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
</style>
