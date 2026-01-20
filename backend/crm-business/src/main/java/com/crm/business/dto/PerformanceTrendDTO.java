package com.crm.business.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 业绩趋势DTO
 * 展示销售业绩的时间趋势数据
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class PerformanceTrendDTO {

    /**
     * 趋势数据点列表
     */
    private List<TrendPoint> trendPoints;

    /**
     * 统计周期类型: day、week、month、quarter、year
     */
    private String periodType;

    /**
     * 汇总数据
     */
    private TrendSummary summary;

    /**
     * 趋势数据点
     */
    @Data
    public static class TrendPoint {
        /**
         * 时间标签（如：2024-01、2024-02）
         */
        private String period;

        /**
         * 新增线索数
         */
        private Long newLeadCount;

        /**
         * 新增客户数
         */
        private Long newCustomerCount;

        /**
         * 新增商机数
         */
        private Long newOpportunityCount;

        /**
         * 商机金额
         */
        private BigDecimal opportunityAmount;

        /**
         * 赢单数
         */
        private Long wonCount;

        /**
         * 赢单金额
         */
        private BigDecimal wonAmount;

        /**
         * 签约合同数
         */
        private Long signedContractCount;

        /**
         * 签约金额
         */
        private BigDecimal signedAmount;

        /**
         * 回款金额
         */
        private BigDecimal paymentAmount;
    }

    /**
     * 趋势汇总
     */
    @Data
    public static class TrendSummary {
        /**
         * 总新增线索数
         */
        private Long totalNewLeadCount;

        /**
         * 总新增客户数
         */
        private Long totalNewCustomerCount;

        /**
         * 总新增商机数
         */
        private Long totalNewOpportunityCount;

        /**
         * 总商机金额
         */
        private BigDecimal totalOpportunityAmount;

        /**
         * 总赢单数
         */
        private Long totalWonCount;

        /**
         * 总赢单金额
         */
        private BigDecimal totalWonAmount;

        /**
         * 总签约合同数
         */
        private Long totalSignedContractCount;

        /**
         * 总签约金额
         */
        private BigDecimal totalSignedAmount;

        /**
         * 总回款金额
         */
        private BigDecimal totalPaymentAmount;

        /**
         * 环比增长率（赢单金额）
         */
        private BigDecimal wonAmountGrowthRate;

        /**
         * 环比增长率（回款金额）
         */
        private BigDecimal paymentAmountGrowthRate;
    }
}
