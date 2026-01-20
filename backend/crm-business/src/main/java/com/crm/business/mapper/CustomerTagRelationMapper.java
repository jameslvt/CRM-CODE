package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.CustomerTagRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户标签关联Mapper
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface CustomerTagRelationMapper extends BaseMapper<CustomerTagRelation> {
}
