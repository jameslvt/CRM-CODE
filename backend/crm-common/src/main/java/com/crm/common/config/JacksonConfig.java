package com.crm.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson 配置类
 * 解决前端JavaScript精度丢失问题
 * 
 * 问题: JavaScript的Number类型只能安全表示 ±2^53-1 范围内的整数
 * 雪花算法生成的Long类型ID超出此范围,会导致精度丢失
 * 解决: 将Long类型序列化为字符串传给前端
 *
 * @author CRM System
 * @since 1.0.0
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper
     * 将Long和long类型序列化为字符串,避免前端精度丢失
     *
     * @param builder Jackson构建器
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 创建自定义模块
        SimpleModule simpleModule = new SimpleModule();

        // Long类型序列化为字符串
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        // 注册模块
        objectMapper.registerModule(simpleModule);

        return objectMapper;
    }
}
