package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 跟进记录Mapper
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
}
