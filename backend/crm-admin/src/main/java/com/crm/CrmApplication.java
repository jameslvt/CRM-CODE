package com.crm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * CRM 系统主启动类
 * 企业级 CRM 系统 - 模块化单体架构
 *
 * 功能模块:
 * - crm-common: 公共模块（工具类、异常、Result 封装）
 * - crm-system: 系统模块（用户、角色、部门、菜单权限）
 * - crm-business: 核心业务模块（线索、客户、商机、合同、回款）
 * - crm-admin: 启动模块（安全配置、Swagger 配置）
 *
 * 技术栈:
 * - Java 8
 * - Spring Boot 2.7.x
 * - MyBatis Plus
 * - MySQL 5.7
 * - Redis 5.x/6.x
 * - JWT 认证
 * - Knife4j (Swagger 2)
 *
 * @author CRM System
 * @since 1.0.0
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@MapperScan({"com.crm.system.mapper", "com.crm.business.mapper"})
public class CrmApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
        System.out.println("\n" +
                "   ______ _____  __  __   _____           _                 \n" +
                "  / ____/|  __ \\|  \\/  | / ____|         | |                \n" +
                " | |     | |__) | \\  / || (___  _   _ ___| |_ ___ _ __ ___  \n" +
                " | |     |  _  /| |\\/| | \\___ \\| | | / __| __/ _ \\ '_ ` _ \\ \n" +
                " | |____ | | \\ \\| |  | | ____) | |_| \\__ \\ ||  __/ | | | | |\n" +
                "  \\_____|_|  \\_\\_|  |_||_____/ \\__, |___/\\__\\___|_| |_| |_|\n" +
                "                                 __/ |                       \n" +
                "                                |___/                        \n" +
                "\n" +
                "========================================================\n" +
                "  CRM System Started Successfully!\n" +
                "  API Documentation: http://localhost:8080/doc.html\n" +
                "  Version: 1.0.0\n" +
                "========================================================\n");
    }
}
