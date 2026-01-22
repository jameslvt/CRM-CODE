<template>
  <div class="opportunity-stage-config">
    <n-card title="商机阶段配置" :bordered="false">
      <template #header-extra>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <n-icon><AddOutline /></n-icon>
          </template>
          新增阶段
        </n-button>
      </template>

      <n-data-table
        :columns="columns"
        :data="stageList"
        :loading="loading"
        :bordered="false"
      />
    </n-card>

    <!-- 新增/编辑弹窗 -->
    <n-modal
      v-model:show="showModal"
      :title="modalTitle"
      preset="dialog"
      :positive-text="'确定'"
      :negative-text="'取消'"
      @positive-click="handleSubmit"
    >
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="left"
        label-width="100px"
      >
        <n-form-item label="阶段名称" path="stageName">
          <n-input v-model:value="formData.stageName" placeholder="请输入阶段名称" />
        </n-form-item>
        <n-form-item label="阶段编码" path="stageCode">
          <n-input
            v-model:value="formData.stageCode"
            placeholder="请输入阶段编码"
            :disabled="!!formData.id"
          />
        </n-form-item>
        <n-form-item label="赢单概率" path="winRate">
          <n-input-number
            v-model:value="formData.winRate"
            placeholder="请输入赢单概率"
            :min="0"
            :max="100"
            :precision="2"
            style="width: 100%"
          >
            <template #suffix>%</template>
          </n-input-number>
        </n-form-item>
        <n-form-item label="排序号" path="sort">
          <n-input-number
            v-model:value="formData.sort"
            placeholder="请输入排序号"
            :min="0"
            style="width: 100%"
          />
        </n-form-item>
        <n-form-item label="状态" path="status">
          <n-radio-group v-model:value="formData.status">
            <n-radio :value="1">正常</n-radio>
            <n-radio :value="0">停用</n-radio>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="备注" path="remark">
          <n-input
            v-model:value="formData.remark"
            type="textarea"
            placeholder="请输入备注"
            :rows="3"
          />
        </n-form-item>
      </n-form>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { NButton, NIcon, NTag, NSpace, useMessage, useDialog, type DataTableColumns } from 'naive-ui'
import { AddOutline, CreateOutline, TrashOutline } from '@vicons/ionicons5'
import { listOpportunityStages, saveOpportunityStage, updateOpportunityStage, deleteOpportunityStage } from '@/api/system/opportunity-stage'

interface OpportunityStage {
  id?: number
  stageName: string
  stageCode: string
  winRate: number
  sort: number
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const stageList = ref<OpportunityStage[]>([])
const showModal = ref(false)
const modalTitle = ref('新增阶段')
const formRef = ref()

const formData = ref<OpportunityStage>({
  stageName: '',
  stageCode: '',
  winRate: 0,
  sort: 0,
  status: 1,
  remark: ''
})

const rules = {
  stageName: { required: true, message: '请输入阶段名称', trigger: 'blur' },
  stageCode: { required: true, message: '请输入阶段编码', trigger: 'blur' },
  winRate: { required: true, type: 'number', message: '请输入赢单概率', trigger: 'blur' },
  sort: { required: true, type: 'number', message: '请输入排序号', trigger: 'blur' }
}

const columns: DataTableColumns<OpportunityStage> = [
  { title: '阶段名称', key: 'stageName', width: 150 },
  { title: '阶段编码', key: 'stageCode', width: 150 },
  {
    title: '赢单概率',
    key: 'winRate',
    width: 120,
    render: (row) => `${row.winRate}%`
  },
  { title: '排序号', key: 'sort', width: 100 },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) =>
      h(
        NTag,
        { type: row.status === 1 ? 'success' : 'default' },
        { default: () => (row.status === 1 ? '正常' : '停用') }
      )
  },
  { title: '备注', key: 'remark', ellipsis: { tooltip: true } },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    render: (row) =>
      h(
        NSpace,
        {},
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                text: true,
                onClick: () => handleEdit(row)
              },
              { default: () => '编辑', icon: () => h(NIcon, null, { default: () => h(CreateOutline) }) }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                text: true,
                onClick: () => handleDelete(row.id!)
              },
              { default: () => '删除', icon: () => h(NIcon, null, { default: () => h(TrashOutline) }) }
            )
          ]
        }
      )
  }
]

const loadStages = async () => {
  loading.value = true
  try {
    stageList.value = await listOpportunityStages()
  } catch (error) {
    message.error('加载商机阶段失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  modalTitle.value = '新增阶段'
  formData.value = {
    stageName: '',
    stageCode: '',
    winRate: 0,
    sort: 0,
    status: 1,
    remark: ''
  }
  showModal.value = true
}

const handleEdit = (row: OpportunityStage) => {
  modalTitle.value = '编辑阶段'
  formData.value = { ...row }
  showModal.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    if (formData.value.id) {
      await updateOpportunityStage(formData.value)
      message.success('修改成功')
    } else {
      await saveOpportunityStage(formData.value)
      message.success('新增成功')
    }
    showModal.value = false
    loadStages()
  } catch (error) {
    // 验证失败或API错误
    return false
  }
}

const handleDelete = (id: string) => {
  dialog.warning({
    title: '确认删除',
    content: '确定要删除该商机阶段吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteOpportunityStage(id)
        message.success('删除成功')
        loadStages()
      } catch (error) {
        message.error('删除失败')
      }
    }
  })
}

onMounted(() => {
  loadStages()
})
</script>

<style scoped lang="scss">
.opportunity-stage-config {
  padding: 20px;
}
</style>
