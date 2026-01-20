<template>
  <div class="dashboard-container">
    <n-space vertical :size="24">
      <n-card title="欢迎使用 CRM 系统">
        <p>这是仪表盘页面，用于展示系统概览和关键指标。</p>
      </n-card>

      <n-grid :cols="4" :x-gap="16" :y-gap="16" responsive="screen">
        <n-gi>
          <n-card title="线索总数">
            <n-statistic value="1,234" />
          </n-card>
        </n-gi>
        <n-gi>
          <n-card title="客户总数">
            <n-statistic value="567" />
          </n-card>
        </n-gi>
        <n-gi>
          <n-card title="商机总数">
            <n-statistic value="89" />
          </n-card>
        </n-gi>
        <n-gi>
          <n-card title="合同总数">
            <n-statistic value="45" />
          </n-card>
        </n-gi>
      </n-grid>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { NSpace, NCard, NGrid, NGi, NStatistic } from 'naive-ui'
</script>

<style scoped>
.dashboard-container {
  width: 100%;
}
</style>
