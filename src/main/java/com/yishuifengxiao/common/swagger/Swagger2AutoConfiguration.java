package com.yishuifengxiao.common.swagger;

import com.yishuifengxiao.common.tool.collections.SizeUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
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
@EnableSwagger2
@EnableOpenApi
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.swagger", name = {"enable"}, havingValue = "true", matchIfMissing = true)
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
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath*:/swagger-ui/webjars/springfox-swagger-ui/", "classpath:/swagger-ui/webjars/springfox-swagger-ui/", "classpath*:/META-INF/resources/webjars/springfox-swagger-ui/", "classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath*:/swagger-ui/", "classpath:/swagger-ui/", "classpath*:/META-INF/resources/", "classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath*:/swagger-ui/webjars/", "classpath:/swagger-ui/webjars/", "classpath*:/META-INF/resources/webjars/", "classpath:/META-INF/resources/webjars/");
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
    public Docket createRestApi(ApplicationContext context) {

         Set<String> apis = context.getBeansWithAnnotation(Api.class).values().stream().map(v->v.getClass().getName()).map(v->StringUtils.substringAfterLast(v,".")).collect(Collectors.toSet());
        //全局配置信息
        List<RequestParameter> pars = this.buildParameter();
        // @formatter:off
        final ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2)
                .groupName(swaggerProperties.getGroupName())
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any());
                if(StringUtils.isNotBlank(swaggerProperties.getBasePackage())){
                    builder.apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()));
                }else {
                    apis.stream().forEach(RequestHandlerSelectors::basePackage);
                }

        return builder.build()
                .globalRequestParameters(pars);

        // @formatter:on
    }


    /**
     * 生成版本和作者信息
     *
     * @return api信息
     */
    private ApiInfo apiInfo() {
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
        // @formatter:off
        String description = swaggerProperties.getDescription();
        if (StringUtils.isBlank(description)) {
            StringBuilder builder = new StringBuilder("项目");
            if(StringUtils.isNotBlank(applicationName)){
                builder.append("【") .append(applicationName).append("】");
            }
            builder.append("在线接口文档");
            if (null != activeProfiles && activeProfiles.length > 0) {
                builder.append("。当前激活的环境为").append(String.join(";", activeProfiles));
            }
            description=builder.toString();
        }
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl()).
                contact(new Contact(
                        swaggerProperties.getContactUser(),
                        swaggerProperties.getContactUrl(),
                        swaggerProperties.getContactEmail())
                )
                .version(swaggerProperties.getVersion()).build();
        // @formatter:on
    }


    /**
     * 生成全局配置信息
     *
     * @return 全局配置信息
     */
    private List<RequestParameter> buildParameter() {
        if (BooleanUtils.isTrue(this.swaggerProperties.getShowDetail())) {
            log.info("【yishuifengxiao-common-spring-boot-starter】 swagger-ui 授权参数为 {}", this.swaggerProperties.getAuths());
        }

        List<RequestParameter> pars = new ArrayList<>();
        if (SizeUtil.isNotEmpty(this.swaggerProperties.getAuths())) {
            this.swaggerProperties.getAuths().forEach(t -> {
                pars.add(new RequestParameterBuilder().name(t.getName()).description(t.getDescription()).required(t.getRequired()).in(ParameterType.QUERY).required(true).query(q -> q.model(m -> m.scalarModel(ScalarType.STRING))).build());
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