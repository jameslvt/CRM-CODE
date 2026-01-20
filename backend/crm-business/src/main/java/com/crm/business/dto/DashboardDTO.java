package com.crm.business.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表盘数据DTO
 * 包含销售核心指标汇总
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class DashboardDTO {

    /**
     * 线索统计
     */
    private LeadStats leadStats;

    /**
     * 客户统计
     */
    private CustomerStats customerStats;

    /**
     * 商机统计
     */
    private OpportunityStats opportunityStats;

    /**
     * 合同统计
     */
    private ContractStats contractStats;

    /**
     * 回款统计
     */
    private PaymentStats paymentStats;

    /**
     * 线索统计
     */
    @Data
    public static class LeadStats {
        /**
         * 总线索数
         */
        private Long totalCount;

        /**
         * 本月新增
         */
        private Long monthNewCount;

        /**
         * 待跟进数
         */
        private Long pendingCount;

        /**
         * 转化数
         */
        private Long convertedCount;

        /**
         * 转化率
         */
        private BigDecimal conversionRate;
    }

    /**
     * 客户统计
     */
    @Data
    public static class CustomerStats {
        /**
         * 总客户数
         */
        private Long totalCount;

        /**
         * 本月新增
         */
        private Long monthNewCount;

        /**
         * 公海池数量
         */
        private Long poolCount;

        /**
         * 活跃客户数
         */
        private Long activeCount;
    }

    /**
     * 商机统计
     */
    @Data
    public static class OpportunityStats {
        /**
         * 总商机数
         */
        private Long totalCount;

        /**
         * 本月新增
         */
        private Long monthNewCount;

        /**
         * 进行中数量
         */
        private Long ongoingCount;

        /**
         * 赢单数量
         */
        private Long wonCount;

        /**
         * 输单数量
         */
        private Long lostCount;

        /**
         * 赢单率
         */
        private BigDecimal winRate;

        /**
         * 总金额
         */
        private BigDecimal totalAmount;

        /**
         * 赢单金额
         */
        private BigDecimal wonAmount;
    }

    /**
     * 合同统计
     */
    @Data
    public static class ContractStats {
        /**
         * 总合同数
         */
        private Long totalCount;

        /**
         * 本月新增
         */
        private Long monthNewCount;

        /**
         * 执行中数量
         */
        private Long executingCount;

        /**
         * 总金额
         */
        private BigDecimal totalAmount;

        /**
         * 本月签约金额
         */
        private BigDecimal monthSignedAmount;
    }

    /**
     * 回款统计
     */
    @Data
    public static class PaymentStats {
        /**
         * 计划回款总额
         */
        private BigDecimal totalPlanAmount;

        /**
         * 实际回款总额
         */
        private BigDecimal totalActualAmount;

        /**
         * 本月回款金额
         */
        private BigDecimal monthActualAmount;

        /**
         * 待回款金额
         */
        private BigDecimal pendingAmount;

        /**
         * 逾期金额
         */
        private BigDecimal overdueAmount;

        /**
         * 回款完成率
         */
        private BigDecimal completionRate;
    }
}
