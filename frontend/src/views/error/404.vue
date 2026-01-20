<template>
  <div class="error-container">
    <n-result status="404" title="404 页面不存在" description="抱歉，您访问的页面不存在">
      <template #footer>
        <n-button type="primary" @click="router.push('/')">返回首页</n-button>
      </template>
    </n-result>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { NResult, NButton } from 'naive-ui'

const router = useRouter()
</script>

<style scoped>
.error-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
}
</style>
