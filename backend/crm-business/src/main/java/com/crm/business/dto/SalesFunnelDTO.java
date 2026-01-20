package com.crm.business.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售漏斗DTO
 * 展示商机各阶段的数量和金额分布
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class SalesFunnelDTO {

    /**
     * 漏斗阶段列表
     */
    private List<FunnelStage> stages;

    /**
     * 总商机数
     */
    private Long totalCount;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 漏斗阶段
     */
    @Data
    public static class FunnelStage {
        /**
         * 阶段名称
         */
        private String stageName;

        /**
         * 阶段编码
         */
        private String stageCode;

        /**
         * 商机数量
         */
        private Long count;

        /**
         * 金额
         */
        private BigDecimal amount;

        /**
         * 赢单概率
         */
        private Integer probability;

        /**
         * 占比（数量）
         */
        private BigDecimal countRatio;

        /**
         * 占比（金额）
         */
        private BigDecimal amountRatio;

        /**
         * 转化率（相对上一阶段）
         */
        private BigDecimal conversionRate;
    }
}
