package com.yishuifengxiao.common.swagger;


import com.yishuifengxiao.common.tool.collections.CollUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * swagger扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.swagger", name = {"enable"}, havingValue = "true", matchIfMissing =
        true)
public class Swagger2AutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private SwaggerProperties swaggerProperties;
    @Autowired
    private ApplicationContext context;

    /**
     * 配置静态资源路径,防止出现访问swagger-ui界面时出现404
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //@formatter off
//        registry.addResourceHandler("/swagger-ui/**")
//                .addResourceLocations(
//                        "classpath*:/META-INF/META-INF/resources/webjars.springfox-swagger-ui/"
//                );
//
//        registry.addResourceHandler("doc.html")
//                .addResourceLocations(
//                        "classpath:/webjars/swagger-ui/",
//                        "classpath*:/webjars/swagger-ui/"
//                );
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations(
//                        "classpath:/webjars/swagger-ui/webjars/",
//                        "classpath*:/webjars/swagger-ui/webjars/"
//                );
        //@formatter on
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // @formatter:off
//        registry.addViewController("/swagger-ui/")
//                .setViewName("forward:/swagger-ui/index.html");
        // @formatter:on
    }

    // @formatter:off

    /**
     * swagger-ui配置
     *
     * @return Docket实例
     */
    @Bean
    @ConditionalOnMissingClass
    public GroupedOpenApi groupedOpenApi() {
        Set<String> apis = context.getBeansWithAnnotation(Tag.class).values().stream().map(v->v.getClass().getName())
                .map(v->StringUtils.substringBeforeLast(v,".")).collect(Collectors.toSet());
        String[] packagesToScan = apis.toArray(new String[apis.size()]);
        return GroupedOpenApi.builder()
                .group(swaggerProperties.getGroupName())
                .pathsToMatch("/sys/**")
                .packagesToScan(packagesToScan)
                .build();
    }


    @Bean
    @ConditionalOnMissingClass
    public OpenAPI springShopOpenAPI() {
        String applicationName = context.getEnvironment().getProperty("spring.application.name", "");
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        String title = swaggerProperties.getTitle();

        if (StringUtils.isBlank(title)) {
            StringBuilder builder = new StringBuilder(applicationName);
            if (null != activeProfiles && activeProfiles.length > 0) {
                builder = builder.append("【").append(String.join(";", activeProfiles)).append("】");
            }
            title = builder.append("在线接口文档").toString();
        }
        String description = swaggerProperties.getDescription();
        if (StringUtils.isBlank(description)) {
            StringBuilder builder = new StringBuilder("项目");
            if (StringUtils.isNotBlank(applicationName)) {
                builder.append("【").append(applicationName).append("】");
            }
            builder.append("在线接口文档");
            if (null != activeProfiles && activeProfiles.length > 0) {
                builder.append("。当前激活的环境为").append(String.join(";", activeProfiles));
            }
            description = builder.toString();
        }
        Contact contact = new Contact();
        contact.setName(swaggerProperties.getContactUser());
        contact.setUrl(swaggerProperties.getContactUrl());
        contact.setEmail(swaggerProperties.getContactEmail());
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .contact(contact)
                        .termsOfService(swaggerProperties.getTermsOfServiceUrl())
                        .version(swaggerProperties.getVersion())
                        .license(swaggerProperties.getLicense())
                )
                .externalDocs(swaggerProperties.getExternalDocumentation());
    }




    /**
     * 配置swagger文档访问权限认证
     *
     * @param swaggerProperties swagger扩展支持属性配置
     * @return swagger文档访问权限认证过滤器
     */
    @Bean
    @Order()
    public Filter swaggerAuthFilter(SwaggerProperties swaggerProperties) {
        SwaggerAuthFilter swaggerAuthFilter = new SwaggerAuthFilter();
        swaggerAuthFilter.setSwaggerProperties(swaggerProperties);
        return swaggerAuthFilter;
    }

    // @formatter:on

    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <Swagger-ui扩展支持> 相关的配置");
    }

}