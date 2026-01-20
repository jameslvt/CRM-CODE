package com.crm.business.event;

import com.crm.business.entity.Contract;
import org.springframework.context.ApplicationEvent;

/**
 * 合同签署事件
 * 当合同审批通过并开始执行时触发此事件
 * 可用于创建回款计划、发送通知等后续处理
 *
 * @author CRM System
 * @since 1.0.0
 */
public class ContractSignedEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    /**
     * 签署的合同
     */
    private final Contract contract;

    /**
     * 构造函数
     *
     * @param source 事件源
     * @param contract 签署的合同
     */
    public ContractSignedEvent(Object source, Contract contract) {
        super(source);
        this.contract = contract;
    }

    /**
     * 获取签署的合同
     *
     * @return 合同实体
     */
    public Contract getContract() {
        return contract;
    }

    /**
     * 获取合同ID
     *
     * @return 合同ID
     */
    public Long getContractId() {
        return contract.getId();
    }

    /**
     * 获取客户ID
     *
     * @return 客户ID
     */
    public Long getCustomerId() {
        return contract.getCustomerId();
    }

    /**
     * 获取合同金额
     *
     * @return 合同金额
     */
    public java.math.BigDecimal getAmount() {
        return contract.getAmount();
    }

    /**
     * 获取合同编号
     *
     * @return 合同编号
     */
    public String getContractNo() {
        return contract.getContractNo();
    }
}
