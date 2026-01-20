package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.Lead;
import org.apache.ibatis.annotations.Mapper;

/**
 * 线索Mapper
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Mapper
public interface LeadMapper extends BaseMapper<Lead> {
}
