package com.crm.business.event;

import com.crm.business.entity.Opportunity;
import org.springframework.context.ApplicationEvent;

/**
 * 商机赢单事件
 * 当商机状态变为"赢单"时触发此事件
 * 可用于自动创建合同、发送通知等后续处理
 *
 * @author CRM System
 * @since 1.0.0
 */
public class OpportunityWonEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * 赢单的商机
     */
    private final Opportunity opportunity;

    /**
     * 构造函数
     *
     * @param source 事件源
     * @param opportunity 赢单的商机
     */
    public OpportunityWonEvent(Object source, Opportunity opportunity) {
        super(source);
        this.opportunity = opportunity;
    }

    /**
     * 获取赢单的商机
     *
     * @return 商机实体
     */
    public Opportunity getOpportunity() {
        return opportunity;
    }

    /**
     * 获取商机ID
     *
     * @return 商机ID
     */
    public Long getOpportunityId() {
        return opportunity.getId();
    }

    /**
     * 获取客户ID
     *
     * @return 客户ID
     */
    public Long getCustomerId() {
        return opportunity.getCustomerId();
    }

    /**
     * 获取商机金额
     *
     * @return 商机金额
     */
    public java.math.BigDecimal getAmount() {
        return opportunity.getAmount();
    }
}
