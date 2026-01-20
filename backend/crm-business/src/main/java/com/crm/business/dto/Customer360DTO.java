package com.crm.business.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 客户360度视图DTO
 * 包含客户的完整信息：基本信息、联系人、商机、合同、跟进记录等
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class Customer360DTO {

    /**
     * 客户基本信息
     */
    private CustomerDTO basicInfo;

    /**
     * 联系人列表
     */
    private List<ContactDTO> contacts;

    /**
     * 商机列表（简要信息）
     */
    private List<OpportunitySummaryDTO> opportunities;

    /**
     * 合同列表（简要信息）
     */
    private List<ContractSummaryDTO> contracts;

    /**
     * 跟进记录列表
     */
    private List<ActivitySummaryDTO> activities;

    /**
     * 统计信息
     */
    private StatisticsDTO statistics;

    /**
     * 商机简要信息
     */
    @Data
    public static class OpportunitySummaryDTO {
        private Long id;
        private String name;
        private BigDecimal amount;
        private String stage;
        private Integer probability;
        private String expectedDate;
        private String createTime;
    }

    /**
     * 合同简要信息
     */
    @Data
    public static class ContractSummaryDTO {
        private Long id;
        private String contractNo;
        private String name;
        private BigDecimal amount;
        private Integer status;
        private String statusName;
        private String signDate;
        private String createTime;
    }

    /**
     * 跟进记录简要信息
     */
    @Data
    public static class ActivitySummaryDTO {
        private Long id;
        private String type;
        private String content;
        private String createByName;
        private String createTime;
    }

    /**
     * 统计信息
     */
    @Data
    public static class StatisticsDTO {
        /**
         * 商机总金额
         */
        private BigDecimal totalOpportunityAmount;

        /**
         * 合同总金额
         */
        private BigDecimal totalContractAmount;

        /**
         * 已回款总金额
         */
        private BigDecimal totalPaymentReceived;

        /**
         * 商机数量
         */
        private Integer opportunityCount;

        /**
         * 合同数量
         */
        private Integer contractCount;

        /**
         * 联系人数量
         */
        private Integer contactCount;

        /**
         * 跟进记录数量
         */
        private Integer activityCount;
    }
}
