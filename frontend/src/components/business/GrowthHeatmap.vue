<template>
  <div class="growth-heatmap">
    <div class="card-header">
      <div class="header-title">
        <n-icon :size="18" color="#f59e0b"><FlameOutline /></n-icon>
        <span>增长热力榜</span>
      </div>
      <span class="header-tag">Top 5</span>
    </div>

    <div class="heatmap-list">
      <div 
        v-for="(item, index) in topGrowth" 
        :key="item.region" 
        class="heatmap-item"
      >
        <div class="item-info">
          <span class="rank">{{ index + 1 }}</span>
          <span class="region">{{ item.region }}</span>
          <span class="growth-value">+{{ item.growth }}%</span>
        </div>
        <div class="heat-bar-track">
          <div 
            class="heat-bar" 
            :style="{ 
              width: `${(item.growth / maxGrowth) * 100}%`,
              opacity: 1 - (index * 0.15)
            }"
          ></div>
        </div>
      </div>
    </div>

    <div class="divider"></div>

    <div class="warning-header">
      <n-icon :size="16" color="#ef4444"><WarningOutline /></n-icon>
      <span>同比下降告警</span>
    </div>

    <div class="warning-list">
      <div 
        v-for="item in bottomGrowth" 
        :key="item.region" 
        class="warning-item"
      >
        <span class="region">{{ item.region }}</span>
        <span class="growth-value down">{{ item.growth }}%</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { NIcon } from 'naive-ui'
import { FlameOutline, WarningOutline } from '@vicons/ionicons5'

// 模拟数据
const growthData = ref([
  { region: '晋中-长治大区', growth: 1344 },
  { region: '河南-安徽大区', growth: 336 },
  { region: '吕梁大区', growth: 210 },
  { region: '大同-朔州大区', growth: 97 },
  { region: '太原-临汾-运城', growth: 82 },
  { region: '山东-河北大区', growth: 26 },
  { region: '阳泉大区', growth: -14 },
  { region: '陕西-内蒙大区', growth: -54 },
  { region: '忻州大区', growth: -74 },
  { region: '晋城大区', growth: -95 },
  { region: '西北西南大区', growth: -98 },
  { region: '山西矿监部', growth: -100 }
])

// Top 5 增长
const topGrowth = computed(() => {
  return [...growthData.value]
    .sort((a, b) => b.growth - a.growth)
    .slice(0, 5)
})

// 跌幅榜
const bottomGrowth = computed(() => {
  return [...growthData.value]
    .filter(item => item.growth < 0)
    .sort((a, b) => a.growth - b.growth) // 负数越小排越前
    .slice(0, 3)
})

const maxGrowth = computed(() => topGrowth.value[0]?.growth || 100)
</script>

<style scoped>
.growth-heatmap {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 20px;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

.header-tag {
  font-size: 11px;
  font-weight: 600;
  color: #f59e0b;
  background: #fffbeb;
  padding: 2px 8px;
  border-radius: 10px;
}

.heatmap-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.item-info {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  font-size: 13px;
}

.rank {
  width: 18px;
  height: 18px;
  background: #f1f5f9;
  color: #64748b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 700;
  margin-right: 8px;
}

.heatmap-item:nth-child(1) .rank { background: #fee2e2; color: #ef4444; }
.heatmap-item:nth-child(2) .rank { background: #ffedd5; color: #f97316; }
.heatmap-item:nth-child(3) .rank { background: #fef3c7; color: #d97706; }

.region {
  color: #334155;
  font-weight: 500;
  flex: 1;
}

.growth-value {
  font-family: 'SF Mono', monospace;
  font-weight: 600;
  color: #ef4444; /* 红色代表高增长 */
}

.heat-bar-track {
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  overflow: hidden;
}

.heat-bar {
  height: 100%;
  background: linear-gradient(90deg, #fca5a5 0%, #ef4444 100%);
  border-radius: 3px;
}

.divider {
  height: 1px;
  background: #f1f5f9;
  margin: 20px 0;
}

.warning-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.warning-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  padding: 8px 12px;
  background: #fef2f2;
  border-radius: 8px;
}

.growth-value.down {
  color: #10b981; /* 绿色代表下降/负增长 */
  font-weight: 600;
}
</style>
