<template>
  <div class="business-dashboard">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">数据分析</h1>
        <p class="page-subtitle">销售业绩概览与数据分析</p>
      </div>
      <div class="header-actions">
        <date-range-picker
          default-quick="month"
          @change="handleDateRangeChange"
        />
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-grid">
      <metric-card
        :value="dashboardData?.leadStats?.totalCount || 0"
        label="线索总数"
        theme="blue"
        :icon="PersonOutline"
        :trend="calculateTrend(dashboardData?.leadStats?.monthNewCount, dashboardData?.leadStats?.totalCount)"
        :sub-value="dashboardData?.leadStats?.monthNewCount || 0"
        sub-label="本月新增"
        clickable
        @click="navigateTo('/business/lead')"
      />
      <metric-card
        :value="dashboardData?.customerStats?.totalCount || 0"
        label="客户总数"
        theme="green"
        :icon="PeopleOutline"
        :sub-value="dashboardData?.customerStats?.activeCount || 0"
        sub-label="活跃客户"
        clickable
        @click="navigateTo('/business/customer')"
      />
      <metric-card
        :value="dashboardData?.opportunityStats?.totalAmount || 0"
        label="商机总金额"
        theme="purple"
        :icon="BriefcaseOutline"
        prefix="¥"
        :sub-value="`赢单率 ${(dashboardData?.opportunityStats?.winRate || 0).toFixed(1)}%`"
        sub-label=""
        clickable
        @click="navigateTo('/business/opportunity')"
      />
      <metric-card
        :value="dashboardData?.contractStats?.totalAmount || 0"
        label="合同总金额"
        theme="orange"
        :icon="DocumentTextOutline"
        prefix="¥"
        :sub-value="dashboardData?.contractStats?.executingCount || 0"
        sub-label="执行中"
        clickable
        @click="navigateTo('/business/contract')"
      />
      <metric-card
        :value="dashboardData?.paymentStats?.totalActualAmount || 0"
        label="已回款金额"
        theme="green"
        :icon="WalletOutline"
        prefix="¥"
        :sub-value="`完成率 ${(dashboardData?.paymentStats?.completionRate || 0).toFixed(1)}%`"
        sub-label=""
        clickable
        @click="navigateTo('/business/payment')"
      />
      <metric-card
        :value="dashboardData?.paymentStats?.pendingAmount || 0"
        label="待回款金额"
        theme="orange"
        :icon="TimeOutline"
        prefix="¥"
        :sub-value="dashboardData?.paymentStats?.overdueAmount || 0"
        sub-label="逾期金额"
      />
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 销售漏斗 -->
      <div class="chart-section funnel-section">
        <sales-funnel-chart
          :stages="funnelData?.stages || []"
          :total-count="funnelData?.totalCount || 0"
          :total-amount="funnelData?.totalAmount || 0"
          :loading="funnelLoading"
        />
      </div>

      <!-- 业绩趋势 -->
      <div class="chart-section trend-section">
        <performance-trend-chart
          :trend-points="trendData?.trendPoints || []"
          :summary="trendData?.summary"
          :period-type="trendParams.periodType"
          :loading="trendLoading"
          @period-change="handlePeriodChange"
        />
      </div>
    </div>

    <!-- 详细统计 -->
    <div class="detail-grid">
      <!-- 线索统计 -->
      <div class="detail-card">
        <div class="detail-header">
          <h3 class="detail-title">线索统计</h3>
          <n-button text type="primary" @click="navigateTo('/business/lead')">
            查看详情
          </n-button>
        </div>
        <div class="detail-content">
          <div class="detail-item">
            <span class="item-label">待跟进</span>
            <span class="item-value">{{ dashboardData?.leadStats?.pendingCount || 0 }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">已转化</span>
            <span class="item-value success">{{ dashboardData?.leadStats?.convertedCount || 0 }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">转化率</span>
            <span class="item-value">{{ (dashboardData?.leadStats?.conversionRate || 0).toFixed(1) }}%</span>
          </div>
        </div>
      </div>

      <!-- 商机统计 -->
      <div class="detail-card">
        <div class="detail-header">
          <h3 class="detail-title">商机统计</h3>
          <n-button text type="primary" @click="navigateTo('/business/opportunity')">
            查看详情
          </n-button>
        </div>
        <div class="detail-content">
          <div class="detail-item">
            <span class="item-label">进行中</span>
            <span class="item-value">{{ dashboardData?.opportunityStats?.ongoingCount || 0 }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">赢单</span>
            <span class="item-value success">{{ dashboardData?.opportunityStats?.wonCount || 0 }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">输单</span>
            <span class="item-value danger">{{ dashboardData?.opportunityStats?.lostCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 合同统计 -->
      <div class="detail-card">
        <div class="detail-header">
          <h3 class="detail-title">合同统计</h3>
          <n-button text type="primary" @click="navigateTo('/business/contract')">
            查看详情
          </n-button>
        </div>
        <div class="detail-content">
          <div class="detail-item">
            <span class="item-label">本月新增</span>
            <span class="item-value">{{ dashboardData?.contractStats?.monthNewCount || 0 }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">本月签约</span>
            <span class="item-value amount">¥{{ formatAmount(dashboardData?.contractStats?.monthSignedAmount) }}</span>
          </div>
        </div>
      </div>

      <!-- 回款统计 -->
      <div class="detail-card">
        <div class="detail-header">
          <h3 class="detail-title">回款统计</h3>
          <n-button text type="primary" @click="navigateTo('/business/payment')">
            查看详情
          </n-button>
        </div>
        <div class="detail-content">
          <div class="detail-item">
            <span class="item-label">本月回款</span>
            <span class="item-value amount">¥{{ formatAmount(dashboardData?.paymentStats?.monthActualAmount) }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">逾期金额</span>
            <span class="item-value danger">¥{{ formatAmount(dashboardData?.paymentStats?.overdueAmount) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, useMessage } from 'naive-ui'
import {
  PersonOutline,
  PeopleOutline,
  BriefcaseOutline,
  DocumentTextOutline,
  WalletOutline,
  TimeOutline
} from '@vicons/ionicons5'
import { getDashboardData, getSalesFunnel, getPerformanceTrend } from '@/api/business/dashboard'
import type { DashboardData, SalesFunnel, PerformanceTrend, TrendQueryParams } from '@/types/business/dashboard'
import MetricCard from '@/components/business/MetricCard.vue'
import SalesFunnelChart from '@/components/business/SalesFunnelChart.vue'
import PerformanceTrendChart from '@/components/business/PerformanceTrendChart.vue'
import DateRangePicker from '@/components/common/DateRangePicker.vue'

const router = useRouter()
const message = useMessage()

// 数据状态
const dashboardData = ref<DashboardData | null>(null)
const funnelData = ref<SalesFunnel | null>(null)
const trendData = ref<PerformanceTrend | null>(null)

// 加载状态
const dashboardLoading = ref(false)
const funnelLoading = ref(false)
const trendLoading = ref(false)

// 趋势查询参数
const trendParams = reactive<TrendQueryParams>({
  periodType: 'month',
  months: 12
})

// 加载仪表盘数据
const loadDashboardData = async () => {
  dashboardLoading.value = true
  try {
    dashboardData.value = await getDashboardData()
  } catch (error) {
    message.error('加载仪表盘数据失败')
  } finally {
    dashboardLoading.value = false
  }
}

// 加载漏斗数据
const loadFunnelData = async () => {
  funnelLoading.value = true
  try {
    funnelData.value = await getSalesFunnel()
  } catch (error) {
    message.error('加载漏斗数据失败')
  } finally {
    funnelLoading.value = false
  }
}

// 加载趋势数据
const loadTrendData = async () => {
  trendLoading.value = true
  try {
    trendData.value = await getPerformanceTrend(trendParams)
  } catch (error) {
    message.error('加载趋势数据失败')
  } finally {
    trendLoading.value = false
  }
}

// 计算趋势百分比
const calculateTrend = (monthNew: number | undefined, total: number | undefined) => {
  if (!monthNew || !total || total === 0) return undefined
  return Math.round((monthNew / total) * 100)
}

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (!amount) return '0'
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + '万'
  }
  return amount.toLocaleString()
}

// 日期范围变化
const handleDateRangeChange = (value: { type: string; startDate?: string; endDate?: string }) => {
  // 可以根据日期范围重新加载数据
  loadDashboardData()
  loadFunnelData()
}

// 周期变化
const handlePeriodChange = (value: string) => {
  trendParams.periodType = value
  loadTrendData()
}

// 导航
const navigateTo = (path: string) => {
  router.push(path)
}

// 初始化
onMounted(() => {
  loadDashboardData()
  loadFunnelData()
  loadTrendData()
})
</script>

<style scoped>
.business-dashboard {
  width: 100%;
  min-height: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 4px 0;
  letter-spacing: -0.02em;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 指标卡片网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

/* 图表网格 */
.charts-grid {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 24px;
}

.chart-section {
  min-height: 400px;
}

/* 详细统计网格 */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.detail-card {
  background: white;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  padding: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.detail-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-label {
  font-size: 13px;
  color: #64748b;
}

.item-value {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}

.item-value.success {
  color: #22c55e;
}

.item-value.danger {
  color: #ef4444;
}

.item-value.amount {
  color: #2563eb;
}

/* 响应式设计 */
@media (max-width: 1536px) {
  .metrics-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1280px) {
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
