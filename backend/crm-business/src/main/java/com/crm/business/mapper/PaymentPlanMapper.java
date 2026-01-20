package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.PaymentPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回款计划Mapper
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface PaymentPlanMapper extends BaseMapper<PaymentPlan> {
}
