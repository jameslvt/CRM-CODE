package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 合同实体
 * 对应数据库表 crm_contract
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_contract")
public class Contract extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contractNo;

    /**
     * 合同名称
     */
    @TableField("name")
    private String name;

    /**
     * 客户 ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 商机 ID
     */
    @TableField("opportunity_id")
    private Long opportunityId;

    /**
     * 合同金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;

    /**
     * 签约日期
     */
    @TableField("sign_date")
    private LocalDate signDate;

    /**
     * 状态: 1-草稿, 2-审批中, 3-执行中, 4-已完成, 5-已终止
     */
    @TableField("status")
    private Integer status;

    /**
     * 负责人 ID
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 合同文件 URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 更新人
     */
    @TableField("update_by")
    private Long updateBy;
}
