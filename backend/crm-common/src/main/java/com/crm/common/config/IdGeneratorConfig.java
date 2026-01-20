package com.crm.common.config;

import com.crm.common.utils.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ID 生成器配置类
 * 配置雪花算法 ID 生成器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class IdGeneratorConfig {

    /**
     * 创建 IdGenerator Bean
     * 使用默认的 workerId=1, datacenterId=1
     *
     * @return IdGenerator 实例
     */
    @Bean
    public IdGenerator idGenerator() {
        log.info("初始化 IdGenerator Bean");
        return IdGenerator.getInstance();
    }
}
