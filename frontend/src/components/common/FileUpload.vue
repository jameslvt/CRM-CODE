<template>
  <div class="file-upload">
    <!-- 上传区域 -->
    <div
      class="upload-area"
      :class="{ dragging: isDragging, disabled: disabled }"
      @dragover.prevent="handleDragOver"
      @dragleave.prevent="handleDragLeave"
      @drop.prevent="handleDrop"
      @click="triggerUpload"
    >
      <input
        ref="fileInputRef"
        type="file"
        :accept="accept"
        :disabled="disabled"
        class="file-input"
        @change="handleFileChange"
      />

      <div v-if="!uploading && !fileInfo" class="upload-placeholder">
        <n-icon size="48" class="upload-icon">
          <CloudUploadOutline />
        </n-icon>
        <p class="upload-text">点击或拖拽文件到此处上传</p>
        <p class="upload-hint">
          支持格式: {{ acceptText }}，最大 {{ maxSize }}MB
        </p>
      </div>

      <div v-else-if="uploading" class="upload-progress">
        <n-progress
          type="circle"
          :percentage="uploadProgress"
          :stroke-width="6"
          :show-indicator="true"
        />
        <p class="progress-text">上传中...</p>
      </div>

      <div v-else-if="fileInfo" class="file-preview">
        <n-icon size="48" class="file-icon">
          <DocumentOutline />
        </n-icon>
        <div class="file-details">
          <span class="file-name">{{ fileInfo.name }}</span>
          <span class="file-size">{{ formatFileSize(fileInfo.size) }}</span>
        </div>
        <n-button
          text
          type="error"
          class="remove-btn"
          @click.stop="handleRemove"
        >
          <template #icon>
            <n-icon><CloseOutline /></n-icon>
          </template>
        </n-button>
      </div>
    </div>

    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-message">
      <n-icon><AlertCircleOutline /></n-icon>
      {{ errorMessage }}
    </div>

    <!-- 已上传文件URL -->
    <div v-if="modelValue && !fileInfo" class="uploaded-url">
      <n-icon size="16"><LinkOutline /></n-icon>
      <span class="url-text">{{ modelValue }}</span>
      <n-button text type="error" size="small" @click="handleClear">
        清除
      </n-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { getToken } from '@/api/request'
import {
  NIcon,
  NButton,
  NProgress,
  useMessage
} from 'naive-ui'
import {
  CloudUploadOutline,
  DocumentOutline,
  CloseOutline,
  AlertCircleOutline,
  LinkOutline
} from '@vicons/ionicons5'

const props = withDefaults(defineProps<{
  modelValue?: string
  accept?: string
  maxSize?: number
  disabled?: boolean
  uploadUrl?: string
}>(), {
  modelValue: '',
  accept: '.pdf,.doc,.docx,.xls,.xlsx,.jpg,.jpeg,.png',
  maxSize: 10,
  disabled: false,
  uploadUrl: '/api/files/upload'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'success', url: string): void
  (e: 'error', error: Error): void
}>()

const message = useMessage()
const fileInputRef = ref<HTMLInputElement | null>(null)
const isDragging = ref(false)
const uploading = ref(false)
const uploadProgress = ref(0)
const errorMessage = ref('')
const fileInfo = ref<{ name: string; size: number } | null>(null)

// 接受的文件类型文本
const acceptText = computed(() => {
  return props.accept
    .split(',')
    .map(ext => ext.trim().replace('.', '').toUpperCase())
    .join(', ')
})

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 触发文件选择
const triggerUpload = () => {
  if (props.disabled || uploading.value) return
  fileInputRef.value?.click()
}

// 处理拖拽
const handleDragOver = (e: DragEvent) => {
  if (props.disabled) return
  isDragging.value = true
}

const handleDragLeave = (e: DragEvent) => {
  isDragging.value = false
}

const handleDrop = (e: DragEvent) => {
  isDragging.value = false
  if (props.disabled) return

  const files = e.dataTransfer?.files
  if (files && files.length > 0) {
    handleFile(files[0])
  }
}

// 处理文件选择
const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  const files = target.files
  if (files && files.length > 0) {
    handleFile(files[0])
  }
  // 重置 input，允许重复选择同一文件
  target.value = ''
}

// 处理文件
const handleFile = (file: File) => {
  errorMessage.value = ''

  // 验证文件类型
  const fileExt = '.' + file.name.split('.').pop()?.toLowerCase()
  const acceptedTypes = props.accept.split(',').map(t => t.trim().toLowerCase())
  if (!acceptedTypes.includes(fileExt)) {
    errorMessage.value = `不支持的文件格式，请上传 ${acceptText.value} 格式的文件`
    return
  }

  // 验证文件大小
  const maxBytes = props.maxSize * 1024 * 1024
  if (file.size > maxBytes) {
    errorMessage.value = `文件大小不能超过 ${props.maxSize}MB`
    return
  }

  // 设置文件信息
  fileInfo.value = {
    name: file.name,
    size: file.size
  }

  // 上传文件
  uploadFile(file)
}

// 上传文件
const uploadFile = async (file: File) => {
  uploading.value = true
  uploadProgress.value = 0

  try {
    const formData = new FormData()
    formData.append('file', file)

    // 模拟上传进度
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 200)

    // 实际上传请求
    const token = getToken()
    const headers: Record<string, string> = {}
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }

    const response = await fetch(props.uploadUrl, {
      method: 'POST',
      body: formData,
      headers
    })

    clearInterval(progressInterval)

    if (!response.ok) {
      throw new Error('上传失败')
    }

    const result = await response.json()
    uploadProgress.value = 100

    // 假设返回格式为 { code: 0, data: { url: '...' } }
    const url = result.data?.url || result.url || ''

    setTimeout(() => {
      uploading.value = false
      emit('update:modelValue', url)
      emit('success', url)
      message.success('上传成功')
    }, 300)
  } catch (error: any) {
    uploading.value = false
    uploadProgress.value = 0
    fileInfo.value = null
    errorMessage.value = error.message || '上传失败，请重试'
    emit('error', error)
  }
}

// 移除文件
const handleRemove = () => {
  fileInfo.value = null
  errorMessage.value = ''
  emit('update:modelValue', '')
}

// 清除URL
const handleClear = () => {
  emit('update:modelValue', '')
}

// 监听外部值变化
watch(() => props.modelValue, (val) => {
  if (!val) {
    fileInfo.value = null
  }
})
</script>

<style scoped>
.file-upload {
  width: 100%;
}

/* 上传区域 */
.upload-area {
  position: relative;
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.upload-area:hover {
  border-color: #2563eb;
  background: #eff6ff;
}

.upload-area.dragging {
  border-color: #2563eb;
  background: #dbeafe;
}

.upload-area.disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.upload-area.disabled:hover {
  border-color: #e2e8f0;
  background: #f8fafc;
}

.file-input {
  display: none;
}

/* 上传占位符 */
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  color: #94a3b8;
}

.upload-text {
  font-size: 14px;
  color: #475569;
  margin: 0;
}

.upload-hint {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

/* 上传进度 */
.upload-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.progress-text {
  font-size: 14px;
  color: #475569;
  margin: 0;
}

/* 文件预览 */
.file-preview {
  display: flex;
  align-items: center;
  gap: 16px;
}

.file-icon {
  color: #2563eb;
}

.file-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.file-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
  word-break: break-all;
}

.file-size {
  font-size: 12px;
  color: #64748b;
}

.remove-btn {
  flex-shrink: 0;
}

/* 错误提示 */
.error-message {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 13px;
  color: #ef4444;
}

/* 已上传URL */
.uploaded-url {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding: 10px 12px;
  background: #f1f5f9;
  border-radius: 8px;
}

.uploaded-url .n-icon {
  color: #64748b;
  flex-shrink: 0;
}

.url-text {
  flex: 1;
  font-size: 13px;
  color: #475569;
  word-break: break-all;
}
</style>
