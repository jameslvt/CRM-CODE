package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回款记录Mapper
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
}
