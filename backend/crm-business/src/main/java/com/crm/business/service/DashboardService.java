package com.crm.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.business.dto.DashboardDTO;
import com.crm.business.dto.PerformanceTrendDTO;
import com.crm.business.dto.SalesFunnelDTO;
import com.crm.business.entity.*;
import com.crm.business.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务
 * 提供销售数据汇总统计、漏斗分析、趋势分析等功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LeadMapper leadMapper;
    private final CustomerMapper customerMapper;
    private final OpportunityMapper opportunityMapper;
    private final ContractMapper contractMapper;
    private final PaymentPlanMapper paymentPlanMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    /**
     * 商机阶段定义（按顺序）
     */
    private static final String[] OPPORTUNITY_STAGES = {
            "需求确认", "方案报价", "商务谈判", "赢单", "输单"
    };

    /**
     * 阶段概率映射
     */
    private static final Map<String, Integer> STAGE_PROBABILITY = new LinkedHashMap<>();
    static {
        STAGE_PROBABILITY.put("需求确认", 10);
        STAGE_PROBABILITY.put("方案报价", 30);
        STAGE_PROBABILITY.put("商务谈判", 60);
        STAGE_PROBABILITY.put("赢单", 100);
        STAGE_PROBABILITY.put("输单", 0);
    }

    /**
     * 获取仪表盘汇总数据
     *
     * @param ownerId 负责人ID（可选，为空则查询全部）
     * @return 仪表盘数据
     */
    public DashboardDTO getDashboardData(Long ownerId) {
        DashboardDTO dashboard = new DashboardDTO();

        // 获取本月时间范围
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

        // 线索统计
        dashboard.setLeadStats(getLeadStats(ownerId, monthStart, monthEnd));

        // 客户统计
        dashboard.setCustomerStats(getCustomerStats(ownerId, monthStart, monthEnd));

        // 商机统计
        dashboard.setOpportunityStats(getOpportunityStats(ownerId, monthStart, monthEnd));

        // 合同统计
        dashboard.setContractStats(getContractStats(ownerId, monthStart, monthEnd));

        // 回款统计
        dashboard.setPaymentStats(getPaymentStats(ownerId, monthStart, monthEnd));

        return dashboard;
    }

    /**
     * 获取线索统计
     */
    private DashboardDTO.LeadStats getLeadStats(Long ownerId, LocalDateTime monthStart, LocalDateTime monthEnd) {
        DashboardDTO.LeadStats stats = new DashboardDTO.LeadStats();

        LambdaQueryWrapper<Lead> baseWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            baseWrapper.eq(Lead::getOwnerId, ownerId);
        }

        // 总线索数
        stats.setTotalCount(leadMapper.selectCount(baseWrapper));

        // 本月新增
        LambdaQueryWrapper<Lead> monthWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            monthWrapper.eq(Lead::getOwnerId, ownerId);
        }
        monthWrapper.ge(Lead::getCreateTime, monthStart)
                    .le(Lead::getCreateTime, monthEnd);
        stats.setMonthNewCount(leadMapper.selectCount(monthWrapper));

        // 待跟进数（状态为新建或跟进中）
        LambdaQueryWrapper<Lead> pendingWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            pendingWrapper.eq(Lead::getOwnerId, ownerId);
        }
        pendingWrapper.in(Lead::getStatus, "new", "following");
        stats.setPendingCount(leadMapper.selectCount(pendingWrapper));

        // 转化数
        LambdaQueryWrapper<Lead> convertedWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            convertedWrapper.eq(Lead::getOwnerId, ownerId);
        }
        convertedWrapper.eq(Lead::getStatus, "converted");
        stats.setConvertedCount(leadMapper.selectCount(convertedWrapper));

        // 转化率
        if (stats.getTotalCount() > 0) {
            BigDecimal rate = new BigDecimal(stats.getConvertedCount())
                    .divide(new BigDecimal(stats.getTotalCount()), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            stats.setConversionRate(rate);
        } else {
            stats.setConversionRate(BigDecimal.ZERO);
        }

        return stats;
    }

    /**
     * 获取客户统计
     */
    private DashboardDTO.CustomerStats getCustomerStats(Long ownerId, LocalDateTime monthStart, LocalDateTime monthEnd) {
        DashboardDTO.CustomerStats stats = new DashboardDTO.CustomerStats();

        LambdaQueryWrapper<Customer> baseWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            baseWrapper.eq(Customer::getOwnerId, ownerId);
        }

        // 总客户数
        stats.setTotalCount(customerMapper.selectCount(baseWrapper));

        // 本月新增
        LambdaQueryWrapper<Customer> monthWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            monthWrapper.eq(Customer::getOwnerId, ownerId);
        }
        monthWrapper.ge(Customer::getCreateTime, monthStart)
                    .le(Customer::getCreateTime, monthEnd);
        stats.setMonthNewCount(customerMapper.selectCount(monthWrapper));

        // 公海池数量
        LambdaQueryWrapper<Customer> poolWrapper = new LambdaQueryWrapper<>();
        poolWrapper.eq(Customer::getStatus, 2); // 公海状态
        stats.setPoolCount(customerMapper.selectCount(poolWrapper));

        // 活跃客户数（有商机的客户）
        LambdaQueryWrapper<Opportunity> oppWrapper = new LambdaQueryWrapper<>();
        oppWrapper.notIn(Opportunity::getStage, "赢单", "输单");
        List<Opportunity> activeOpps = opportunityMapper.selectList(oppWrapper);
        long activeCount = activeOpps.stream()
                .map(Opportunity::getCustomerId)
                .distinct()
                .count();
        stats.setActiveCount(activeCount);

        return stats;
    }

    /**
     * 获取商机统计
     */
    private DashboardDTO.OpportunityStats getOpportunityStats(Long ownerId, LocalDateTime monthStart, LocalDateTime monthEnd) {
        DashboardDTO.OpportunityStats stats = new DashboardDTO.OpportunityStats();

        LambdaQueryWrapper<Opportunity> baseWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            baseWrapper.eq(Opportunity::getOwnerId, ownerId);
        }

        // 总商机数
        stats.setTotalCount(opportunityMapper.selectCount(baseWrapper));

        // 本月新增
        LambdaQueryWrapper<Opportunity> monthWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            monthWrapper.eq(Opportunity::getOwnerId, ownerId);
        }
        monthWrapper.ge(Opportunity::getCreateTime, monthStart)
                    .le(Opportunity::getCreateTime, monthEnd);
        stats.setMonthNewCount(opportunityMapper.selectCount(monthWrapper));

        // 进行中数量
        LambdaQueryWrapper<Opportunity> ongoingWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            ongoingWrapper.eq(Opportunity::getOwnerId, ownerId);
        }
        ongoingWrapper.notIn(Opportunity::getStage, "赢单", "输单");
        stats.setOngoingCount(opportunityMapper.selectCount(ongoingWrapper));

        // 赢单数量
        LambdaQueryWrapper<Opportunity> wonWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wonWrapper.eq(Opportunity::getOwnerId, ownerId);
        }
        wonWrapper.eq(Opportunity::getStage, "赢单");
        stats.setWonCount(opportunityMapper.selectCount(wonWrapper));

        // 输单数量
        LambdaQueryWrapper<Opportunity> lostWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            lostWrapper.eq(Opportunity::getOwnerId, ownerId);
        }
        lostWrapper.eq(Opportunity::getStage, "输单");
        stats.setLostCount(opportunityMapper.selectCount(lostWrapper));

        // 赢单率
        long closedCount = stats.getWonCount() + stats.getLostCount();
        if (closedCount > 0) {
            BigDecimal rate = new BigDecimal(stats.getWonCount())
                    .divide(new BigDecimal(closedCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            stats.setWinRate(rate);
        } else {
            stats.setWinRate(BigDecimal.ZERO);
        }

        // 总金额
        List<Opportunity> allOpps = opportunityMapper.selectList(baseWrapper);
        BigDecimal totalAmount = allOpps.stream()
                .map(Opportunity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAmount(totalAmount);

        // 赢单金额
        List<Opportunity> wonOpps = opportunityMapper.selectList(wonWrapper);
        BigDecimal wonAmount = wonOpps.stream()
                .map(Opportunity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setWonAmount(wonAmount);

        return stats;
    }

    /**
     * 获取合同统计
     */
    private DashboardDTO.ContractStats getContractStats(Long ownerId, LocalDateTime monthStart, LocalDateTime monthEnd) {
        DashboardDTO.ContractStats stats = new DashboardDTO.ContractStats();

        LambdaQueryWrapper<Contract> baseWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            baseWrapper.eq(Contract::getOwnerId, ownerId);
        }

        // 总合同数
        stats.setTotalCount(contractMapper.selectCount(baseWrapper));

        // 本月新增
        LambdaQueryWrapper<Contract> monthWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            monthWrapper.eq(Contract::getOwnerId, ownerId);
        }
        monthWrapper.ge(Contract::getCreateTime, monthStart)
                    .le(Contract::getCreateTime, monthEnd);
        stats.setMonthNewCount(contractMapper.selectCount(monthWrapper));

        // 执行中数量
        LambdaQueryWrapper<Contract> executingWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            executingWrapper.eq(Contract::getOwnerId, ownerId);
        }
        executingWrapper.eq(Contract::getStatus, 3); // 执行中
        stats.setExecutingCount(contractMapper.selectCount(executingWrapper));

        // 总金额
        List<Contract> allContracts = contractMapper.selectList(baseWrapper);
        BigDecimal totalAmount = allContracts.stream()
                .map(Contract::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalAmount(totalAmount);

        // 本月签约金额
        LambdaQueryWrapper<Contract> monthSignedWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            monthSignedWrapper.eq(Contract::getOwnerId, ownerId);
        }
        monthSignedWrapper.ge(Contract::getSignDate, monthStart.toLocalDate())
                          .le(Contract::getSignDate, monthEnd.toLocalDate());
        List<Contract> monthSignedContracts = contractMapper.selectList(monthSignedWrapper);
        BigDecimal monthSignedAmount = monthSignedContracts.stream()
                .map(Contract::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setMonthSignedAmount(monthSignedAmount);

        return stats;
    }

    /**
     * 获取回款统计
     */
    private DashboardDTO.PaymentStats getPaymentStats(Long ownerId, LocalDateTime monthStart, LocalDateTime monthEnd) {
        DashboardDTO.PaymentStats stats = new DashboardDTO.PaymentStats();

        // 获取所有回款计划
        List<PaymentPlan> allPlans = paymentPlanMapper.selectList(null);

        // 计划回款总额
        BigDecimal totalPlanAmount = allPlans.stream()
                .map(PaymentPlan::getPlanAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalPlanAmount(totalPlanAmount);

        // 实际回款总额
        BigDecimal totalActualAmount = allPlans.stream()
                .map(PaymentPlan::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalActualAmount(totalActualAmount);

        // 本月回款金额
        LambdaQueryWrapper<PaymentRecord> monthRecordWrapper = new LambdaQueryWrapper<>();
        monthRecordWrapper.ge(PaymentRecord::getPaymentDate, monthStart.toLocalDate())
                          .le(PaymentRecord::getPaymentDate, monthEnd.toLocalDate());
        List<PaymentRecord> monthRecords = paymentRecordMapper.selectList(monthRecordWrapper);
        BigDecimal monthActualAmount = monthRecords.stream()
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setMonthActualAmount(monthActualAmount);

        // 待回款金额
        stats.setPendingAmount(totalPlanAmount.subtract(totalActualAmount));

        // 逾期金额
        LambdaQueryWrapper<PaymentPlan> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(PaymentPlan::getStatus, 4); // 逾期状态
        List<PaymentPlan> overduePlans = paymentPlanMapper.selectList(overdueWrapper);
        BigDecimal overdueAmount = overduePlans.stream()
                .map(p -> p.getPlanAmount().subtract(p.getActualAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setOverdueAmount(overdueAmount);

        // 回款完成率
        if (totalPlanAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = totalActualAmount
                    .divide(totalPlanAmount, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            stats.setCompletionRate(rate);
        } else {
            stats.setCompletionRate(BigDecimal.ZERO);
        }

        return stats;
    }

    /**
     * 获取销售漏斗数据
     *
     * @param ownerId 负责人ID（可选）
     * @return 销售漏斗数据
     */
    public SalesFunnelDTO getSalesFunnel(Long ownerId) {
        SalesFunnelDTO funnel = new SalesFunnelDTO();
        List<SalesFunnelDTO.FunnelStage> stages = new ArrayList<>();

        LambdaQueryWrapper<Opportunity> baseWrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            baseWrapper.eq(Opportunity::getOwnerId, ownerId);
        }

        // 获取所有商机
        List<Opportunity> allOpps = opportunityMapper.selectList(baseWrapper);
        long totalCount = allOpps.size();
        BigDecimal totalAmount = allOpps.stream()
                .map(Opportunity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        funnel.setTotalCount(totalCount);
        funnel.setTotalAmount(totalAmount);

        Long previousCount = null;

        // 按阶段统计（排除输单）
        for (String stageName : OPPORTUNITY_STAGES) {
            if ("输单".equals(stageName)) {
                continue; // 漏斗不显示输单
            }

            SalesFunnelDTO.FunnelStage stage = new SalesFunnelDTO.FunnelStage();
            stage.setStageName(stageName);
            stage.setStageCode(stageName);
            stage.setProbability(STAGE_PROBABILITY.get(stageName));

            // 统计该阶段的商机
            long stageCount = allOpps.stream()
                    .filter(o -> stageName.equals(o.getStage()))
                    .count();
            BigDecimal stageAmount = allOpps.stream()
                    .filter(o -> stageName.equals(o.getStage()))
                    .map(Opportunity::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            stage.setCount(stageCount);
            stage.setAmount(stageAmount);

            // 计算占比
            if (totalCount > 0) {
                stage.setCountRatio(new BigDecimal(stageCount)
                        .divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")));
            } else {
                stage.setCountRatio(BigDecimal.ZERO);
            }

            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                stage.setAmountRatio(stageAmount
                        .divide(totalAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")));
            } else {
                stage.setAmountRatio(BigDecimal.ZERO);
            }

            // 计算转化率
            if (previousCount != null && previousCount > 0) {
                stage.setConversionRate(new BigDecimal(stageCount)
                        .divide(new BigDecimal(previousCount), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")));
            } else {
                stage.setConversionRate(new BigDecimal("100"));
            }

            previousCount = stageCount;
            stages.add(stage);
        }

        funnel.setStages(stages);
        return funnel;
    }

    /**
     * 获取业绩趋势数据
     *
     * @param ownerId 负责人ID（可选）
     * @param periodType 周期类型: month、quarter
     * @param months 查询月数
     * @return 业绩趋势数据
     */
    public PerformanceTrendDTO getPerformanceTrend(Long ownerId, String periodType, int months) {
        PerformanceTrendDTO trend = new PerformanceTrendDTO();
        trend.setPeriodType(periodType);

        List<PerformanceTrendDTO.TrendPoint> trendPoints = new ArrayList<>();
        PerformanceTrendDTO.TrendSummary summary = new PerformanceTrendDTO.TrendSummary();

        // 初始化汇总数据
        summary.setTotalNewLeadCount(0L);
        summary.setTotalNewCustomerCount(0L);
        summary.setTotalNewOpportunityCount(0L);
        summary.setTotalOpportunityAmount(BigDecimal.ZERO);
        summary.setTotalWonCount(0L);
        summary.setTotalWonAmount(BigDecimal.ZERO);
        summary.setTotalSignedContractCount(0L);
        summary.setTotalSignedAmount(BigDecimal.ZERO);
        summary.setTotalPaymentAmount(BigDecimal.ZERO);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = months - 1; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            LocalDateTime monthStart = monthDate.withDayOfMonth(1).atStartOfDay();
            LocalDateTime monthEnd = monthDate.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);

            PerformanceTrendDTO.TrendPoint point = new PerformanceTrendDTO.TrendPoint();
            point.setPeriod(monthDate.format(formatter));

            // 新增线索数
            LambdaQueryWrapper<Lead> leadWrapper = new LambdaQueryWrapper<>();
            if (ownerId != null) {
                leadWrapper.eq(Lead::getOwnerId, ownerId);
            }
            leadWrapper.ge(Lead::getCreateTime, monthStart).le(Lead::getCreateTime, monthEnd);
            long newLeadCount = leadMapper.selectCount(leadWrapper);
            point.setNewLeadCount(newLeadCount);
            summary.setTotalNewLeadCount(summary.getTotalNewLeadCount() + newLeadCount);

            // 新增客户数
            LambdaQueryWrapper<Customer> customerWrapper = new LambdaQueryWrapper<>();
            if (ownerId != null) {
                customerWrapper.eq(Customer::getOwnerId, ownerId);
            }
            customerWrapper.ge(Customer::getCreateTime, monthStart).le(Customer::getCreateTime, monthEnd);
            long newCustomerCount = customerMapper.selectCount(customerWrapper);
            point.setNewCustomerCount(newCustomerCount);
            summary.setTotalNewCustomerCount(summary.getTotalNewCustomerCount() + newCustomerCount);

            // 新增商机数和金额
            LambdaQueryWrapper<Opportunity> oppWrapper = new LambdaQueryWrapper<>();
            if (ownerId != null) {
                oppWrapper.eq(Opportunity::getOwnerId, ownerId);
            }
            oppWrapper.ge(Opportunity::getCreateTime, monthStart).le(Opportunity::getCreateTime, monthEnd);
            List<Opportunity> newOpps = opportunityMapper.selectList(oppWrapper);
            point.setNewOpportunityCount((long) newOpps.size());
            BigDecimal oppAmount = newOpps.stream().map(Opportunity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            point.setOpportunityAmount(oppAmount);
            summary.setTotalNewOpportunityCount(summary.getTotalNewOpportunityCount() + newOpps.size());
            summary.setTotalOpportunityAmount(summary.getTotalOpportunityAmount().add(oppAmount));

            // 赢单数和金额（按更新时间判断）
            LambdaQueryWrapper<Opportunity> wonWrapper = new LambdaQueryWrapper<>();
            if (ownerId != null) {
                wonWrapper.eq(Opportunity::getOwnerId, ownerId);
            }
            wonWrapper.eq(Opportunity::getStage, "赢单")
                      .ge(Opportunity::getUpdateTime, monthStart)
                      .le(Opportunity::getUpdateTime, monthEnd);
            List<Opportunity> wonOpps = opportunityMapper.selectList(wonWrapper);
            point.setWonCount((long) wonOpps.size());
            BigDecimal wonAmount = wonOpps.stream().map(Opportunity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            point.setWonAmount(wonAmount);
            summary.setTotalWonCount(summary.getTotalWonCount() + wonOpps.size());
            summary.setTotalWonAmount(summary.getTotalWonAmount().add(wonAmount));

            // 签约合同数和金额
            LambdaQueryWrapper<Contract> contractWrapper = new LambdaQueryWrapper<>();
            if (ownerId != null) {
                contractWrapper.eq(Contract::getOwnerId, ownerId);
            }
            contractWrapper.ge(Contract::getSignDate, monthStart.toLocalDate())
                           .le(Contract::getSignDate, monthEnd.toLocalDate());
            List<Contract> signedContracts = contractMapper.selectList(contractWrapper);
            point.setSignedContractCount((long) signedContracts.size());
            BigDecimal signedAmount = signedContracts.stream().map(Contract::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            point.setSignedAmount(signedAmount);
            summary.setTotalSignedContractCount(summary.getTotalSignedContractCount() + signedContracts.size());
            summary.setTotalSignedAmount(summary.getTotalSignedAmount().add(signedAmount));

            // 回款金额
            LambdaQueryWrapper<PaymentRecord> paymentWrapper = new LambdaQueryWrapper<>();
            paymentWrapper.ge(PaymentRecord::getPaymentDate, monthStart.toLocalDate())
                          .le(PaymentRecord::getPaymentDate, monthEnd.toLocalDate());
            List<PaymentRecord> payments = paymentRecordMapper.selectList(paymentWrapper);
            BigDecimal paymentAmount = payments.stream().map(PaymentRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            point.setPaymentAmount(paymentAmount);
            summary.setTotalPaymentAmount(summary.getTotalPaymentAmount().add(paymentAmount));

            trendPoints.add(point);
        }

        trend.setTrendPoints(trendPoints);
        trend.setSummary(summary);

        return trend;
    }
}
