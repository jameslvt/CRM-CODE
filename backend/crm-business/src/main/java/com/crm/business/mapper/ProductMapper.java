package com.crm.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.business.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品Mapper
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
