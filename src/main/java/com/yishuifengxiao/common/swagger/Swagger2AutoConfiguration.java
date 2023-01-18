package com.yishuifengxiao.common.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import com.yishuifengxiao.common.tool.collections.SizeUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;


import lombok.extern.slf4j.Slf4j;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.swagger", name = {"base-package"})
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
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath*:/META-INF/resources/webjars/springfox-swagger-ui/", "classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath*:/META-INF/resources/", "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath*:/META-INF/resources/webjars/", "classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui/").setViewName("forward:/swagger-ui/index.html");
    }

    // @formatter:off

    /**
     * swagger-ui配置
     *
     * @return Docket实例
     */
    @Bean
    @ConditionalOnMissingClass
    public Docket createRestApi() {
        //全局配置信息
        List<Parameter> pars = this.buildParameter();
        return new Docket(DocumentationType.SWAGGER_2).groupName(swaggerProperties.getGroupName()).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage())).paths(PathSelectors.any()).build().globalOperationParameters(pars);

    }


    /**
     * 生成版本和作者信息
     *
     * @return api信息
     */
    private ApiInfo apiInfo() {
        String title = swaggerProperties.getTitle();

        if (StringUtils.isBlank(title)) {
            String applicationName = context.getApplicationName();
            String activeProfiles = String.join(";", context.getEnvironment().getActiveProfiles());
            title = new StringBuilder(StringUtils.isBlank(applicationName) ? "项目" : applicationName).append("【").append(activeProfiles).append("】").toString();
        }
        String description = swaggerProperties.getDescription();
        return new ApiInfoBuilder().title(title).description(StringUtils.isBlank(description) ? title : description).termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl()).contact(new Contact(swaggerProperties.getContactUser(), swaggerProperties.getContactUrl(), swaggerProperties.getContactEmail())).version(swaggerProperties.getVersion()).build();
    }


    /**
     * 生成全局配置信息
     *
     * @return 全局配置信息
     */
    private List<Parameter> buildParameter() {
        if (BooleanUtils.isTrue(this.swaggerProperties.getShowDetail())) {
            log.info("【yishuifengxiao-common-spring-boot-starter】 swagger-ui 授权参数为 {}", this.swaggerProperties.getAuths());
        }

        List<Parameter> pars = new ArrayList<>();
        if (SizeUtil.notEmpty(this.swaggerProperties.getAuths())) {
            this.swaggerProperties.getAuths().forEach(t -> {
                pars.add(new ParameterBuilder().name(t.getName()).description(t.getDescription()).modelRef(new ModelRef(t.getModelRef())).parameterType(t.getParameterType()).required(t.getRequired()).build());
            });
        }
        return pars;
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