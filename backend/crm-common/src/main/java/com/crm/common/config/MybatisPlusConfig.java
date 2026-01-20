package com.crm.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 配置类
 * 配置分页插件、乐观锁插件、自动填充等功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@MapperScan({"com.crm.**.mapper"})
public class MybatisPlusConfig {

    /**
     * 配置 MyBatis Plus 拦截器
     * 包含分页插件和乐观锁插件
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页插件（MySQL）
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置请求的页面大于最大页后操作，true 调回到首页，false 继续请求（默认 false）
        paginationInterceptor.setOverflow(false);
        // 设置单页分页条数限制，默认无限制
        paginationInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        log.info("MyBatis Plus 拦截器配置完成");
        return interceptor;
    }

    /**
     * 配置字段自动填充处理器
     * 自动填充 createTime、updateTime、deleted 字段
     *
     * @return MetaObjectHandler
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            /**
             * 插入时自动填充
             *
             * @param metaObject 元对象
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                log.debug("开始插入填充...");
                // 填充创建时间
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                // 填充更新时间
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                // 填充逻辑删除标记（默认未删除）
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }

            /**
             * 更新时自动填充
             *
             * @param metaObject 元对象
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                log.debug("开始更新填充...");
                // 填充更新时间
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}
