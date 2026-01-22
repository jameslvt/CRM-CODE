<template>
  <div class="lead-import-export">
    <!-- 导入区域 -->
    <n-card title="批量导入线索" :bordered="false" style="margin-bottom: 16px">
      <n-space vertical>
        <n-alert type="info">
          <template #header>导入说明</template>
          <ul style="margin: 8px 0; padding-left: 20px">
            <li>请先下载导入模板，按照模板格式填写数据</li>
            <li>支持 Excel 格式（.xlsx），最多支持导入 10000 条数据</li>
            <li>必填字段：线索名称、联系人、联系电话、公司名称、来源、评级</li>
            <li>导入过程中如有错误，系统会跳过错误数据继续导入</li>
          </ul>
        </n-alert>

        <n-space>
          <n-button @click="downloadTemplate">
            <template #icon>
              <n-icon><DownloadOutline /></n-icon>
            </template>
            下载模板
          </n-button>

          <n-upload
            ref="uploadRef"
            :action="uploadAction"
            :headers="uploadHeaders"
            :max="1"
            accept=".xlsx,.xls"
            :show-file-list="false"
            @before-upload="handleBeforeUpload"
            @finish="handleUploadFinish"
            @error="handleUploadError"
          >
            <n-button type="primary" :loading="uploading">
              <template #icon>
                <n-icon><CloudUploadOutline /></n-icon>
              </template>
              选择文件导入
            </n-button>
          </n-upload>
        </n-space>

        <n-progress
          v-if="uploading"
          type="line"
          :percentage="uploadProgress"
          :indicator-placement="'inside'"
          processing
        />
      </n-space>
    </n-card>

    <!-- 导出区域 -->
    <n-card title="批量导出线索" :bordered="false">
      <n-space vertical>
        <n-alert type="info">
          <template #header>导出说明</template>
          <ul style="margin: 8px 0; padding-left: 20px">
            <li>可根据当前筛选条件导出线索数据</li>
            <li>导出格式为 Excel（.xlsx），最多导出 10000 条数据</li>
            <li>导出文件名格式：线索数据_yyyyMMddHHmmss.xlsx</li>
          </ul>
        </n-alert>

        <n-button type="primary" :loading="exporting" @click="handleExport">
          <template #icon>
            <n-icon><DownloadOutline /></n-icon>
          </template>
          导出数据
        </n-button>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useMessage, type UploadFileInfo } from 'naive-ui'
import { DownloadOutline, CloudUploadOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'
import type { LeadQueryParams } from '@/types/business'
// 需要安装 xlsx 库：npm install xlsx
import * as XLSX from 'xlsx'

interface Props {
  queryParams?: LeadQueryParams
}

interface Emits {
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()
const message = useMessage()
const userStore = useUserStore()

const uploadRef = ref()
const uploading = ref(false)
const uploadProgress = ref(0)
const exporting = ref(false)

// 上传地址
const uploadAction = computed(() => {
  return `${import.meta.env.VITE_API_BASE_URL}/api/business/lead/import`
})

// 上传请求头
const uploadHeaders = computed(() => {
  return {
    Authorization: `Bearer ${userStore.token}`
  }
})

// 下载模板
const downloadTemplate = () => {
  // 创建模板数据
  const templateData = [
    ['线索名称', '联系人', '联系电话', '邮箱', '公司名称', '职位', '来源', '行业', '评级', '地址', '备注'],
    ['示例线索', '张三', '13800138000', 'zhangsan@example.com', '示例公司', '总经理', '网站', 'IT', 'A', '北京市朝阳区', '这是一条示例数据']
  ]

  // 创建工作簿
  const worksheet = XLSX.utils.aoa_to_sheet(templateData)
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '线索模板')

  // 下载文件
  XLSX.writeFile(workbook, '线索导入模板.xlsx')
  message.success('模板下载成功')
}

// 上传前检查
const handleBeforeUpload = (data: { file: UploadFileInfo; fileList: UploadFileInfo[] }) => {
  const file = data.file.file
  if (!file) {
    return false
  }

  // 检查文件类型
  const fileName = file.name
  const fileExt = fileName.substring(fileName.lastIndexOf('.'))
  if (!['.xlsx', '.xls'].includes(fileExt.toLowerCase())) {
    message.error('只支持 Excel 格式文件（.xlsx, .xls）')
    return false
  }

  // 检查文件大小（最大 10MB）
  const maxSize = 10 * 1024 * 1024
  if (file.size > maxSize) {
    message.error('文件大小不能超过 10MB')
    return false
  }

  uploading.value = true
  uploadProgress.value = 0

  // 模拟进度
  const timer = setInterval(() => {
    if (uploadProgress.value < 90) {
      uploadProgress.value += 10
    } else {
      clearInterval(timer)
    }
  }, 200)

  return true
}

// 上传完成
const handleUploadFinish = ({ file, event }: { file: UploadFileInfo; event?: ProgressEvent }) => {
  uploading.value = false
  uploadProgress.value = 100

  try {
    const response = JSON.parse((event?.target as XMLHttpRequest).response)
    if (response.code === 200) {
      message.success(`导入成功 ${response.data} 条数据`)
      emit('success')
    } else {
      message.error(response.msg || '导入失败')
    }
  } catch (error) {
    message.error('导入失败')
  }

  // 重置进度
  setTimeout(() => {
    uploadProgress.value = 0
  }, 1000)
}

// 上传失败
const handleUploadError = ({ file, event }: { file: UploadFileInfo; event?: ProgressEvent }) => {
  uploading.value = false
  uploadProgress.value = 0
  message.error('上传失败，请重试')
}

// 导出数据
const handleExport = async () => {
  exporting.value = true
  try {
    // 构建查询参数
    const params = new URLSearchParams()
    if (props.queryParams) {
      Object.entries(props.queryParams).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          params.append(key, String(value))
        }
      })
    }

    // 下载文件
    const url = `${import.meta.env.VITE_API_BASE_URL}/api/business/lead/export?${params.toString()}`
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '')
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)

    message.success('导出成功')
  } catch (error) {
    message.error('导出失败')
  } finally {
    exporting.value = false
  }
}
</script>

<style scoped lang="scss">
.lead-import-export {
  ul {
    list-style-type: disc;

    li {
      margin: 4px 0;
      line-height: 1.6;
    }
  }
}
</style>
