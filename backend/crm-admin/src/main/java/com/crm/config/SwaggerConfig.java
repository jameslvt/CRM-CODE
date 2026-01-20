package com.crm.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Swagger/Knife4j 配置类
 * 配置 API 文档生成规则、认证方式等
 * 访问地址: http://localhost:8080/doc.html
 *
 * @author CRM System
 * @since 1.0.0
 */
@Configuration
@EnableSwagger2WebMvc
@EnableKnife4j
public class SwaggerConfig {

    /**
     * 系统模块 API 文档
     */
    @Bean
    public Docket systemApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("1. 系统模块")
                .apiInfo(systemApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.crm.system.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 业务模块 API 文档
     */
    @Bean
    public Docket businessApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("2. 业务模块")
                .apiInfo(businessApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.crm.business.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * 系统模块 API 信息
     */
    private ApiInfo systemApiInfo() {
        return new ApiInfoBuilder()
                .title("CRM 系统 - 系统模块 API 文档")
                .description("系统模块包括：用户管理、角色管理、部门管理、菜单管理、权限管理等")
                .version("1.0.0")
                .contact(new Contact("CRM System", "https://github.com/crm-system", "crm@example.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

    /**
     * 业务模块 API 信息
     */
    private ApiInfo businessApiInfo() {
        return new ApiInfoBuilder()
                .title("CRM 系统 - 业务模块 API 文档")
                .description("业务模块包括：线索管理、客户管理、联系人管理、商机管理、产品管理、合同管理、回款管理等")
                .version("1.0.0")
                .contact(new Contact("CRM System", "https://github.com/crm-system", "crm@example.com"))
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

    /**
     * 配置安全认证方案（JWT）
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> securitySchemes = new ArrayList<>();
        // 配置 JWT 认证
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        securitySchemes.add(apiKey);
        return securitySchemes;
    }

    /**
     * 配置安全上下文
     * 指定哪些接口需要认证
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build()
        );
        return securityContexts;
    }

    /**
     * 默认的安全引用
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }

    /**
     * 修复 Swagger 与 Spring Boot 2.6+ 兼容性问题的 BeanPostProcessor
     */
    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T> T getHandlerMappings(Object bean) {
                try {
                    Field field = bean.getClass().getDeclaredField("handlerMappings");
                    field.setAccessible(true);
                    return (T) field.get(bean);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }

            private void customizeSpringfoxHandlerMappings(List<Object> mappings) {
                List<Object> copy = mappings.stream()
                        .filter(mapping -> mapping.toString().contains("RequestMappingHandlerMapping"))
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }
        };
    }
}
