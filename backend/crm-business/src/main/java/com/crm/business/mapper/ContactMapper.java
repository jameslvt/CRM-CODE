package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.Contact;
import org.apache.ibatis.annotations.Mapper;

/**
 * 联系人Mapper
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
}
