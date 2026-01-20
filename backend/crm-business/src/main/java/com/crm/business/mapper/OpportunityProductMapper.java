package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.OpportunityProduct;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商机产品关联Mapper
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface OpportunityProductMapper extends BaseMapper<OpportunityProduct> {
}
