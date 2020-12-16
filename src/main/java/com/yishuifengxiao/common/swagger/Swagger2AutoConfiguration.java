package com.yishuifengxiao.common.swagger;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.yishuifengxiao.common.tool.collections.EmptyUtil;

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
 * Swagger2生成接口文档
 * 
 * @author yishui
 * @date 2018年6月15日
 * @version 0.0.1
 */
@Slf4j
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.swagger", name = { "base-package" })
public class Swagger2AutoConfiguration implements WebMvcConfigurer {

	@Autowired
	private SwaggerProperties swaggerProperties;

	/**
	 * 配置静态资源路径,防止出现访问swagger-ui界面时出现404
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui/**").addResourceLocations(
				"classpath*:/META-INF/resources/webjars/springfox-swagger-ui/",
				"classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
		registry.addResourceHandler("doc.html").addResourceLocations("classpath*:/META-INF/resources/",
				"classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath*:/META-INF/resources/webjars/",
				"classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/swagger-ui/").setViewName("forward:/swagger-ui/index.html");
	}

	// @formatter:off
	
	/**
	 * swagger-ui配置
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingClass
	public Docket createRestApi() {
	    //全局配置信息
		List<Parameter> pars=this.buildParameter();
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName(swaggerProperties.getGroupName())
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
				.paths(PathSelectors.any())
				.build()
				.globalOperationParameters(pars)
				;

	}
	
	
	
    /**
     * 生成版本和作者信息
     * @return
     */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title(swaggerProperties.getTitle())
				.description(swaggerProperties.getDescription())
				.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
				.contact(new Contact(
						swaggerProperties.getContactUser(),
						swaggerProperties.getContactUrl(),
						swaggerProperties.getContactEmail())
						)
				.version(swaggerProperties.getVersion())
				.build();
	}
	

	/**
	 * 生成全局配置信息
	 * @return
	 */
	private List<Parameter>  buildParameter(){
		log.debug("【易水组件】 swagger-ui 授权参数为 {}", this.swaggerProperties.getAuths());
		List<Parameter> pars = new ArrayList<>();
		if(EmptyUtil.notEmpty(this.swaggerProperties.getAuths())) {
			this.swaggerProperties.getAuths().forEach(t->{
				pars.add(new ParameterBuilder()
							.name(t.getName())
							.description(t.getDescription())
							.modelRef(new ModelRef(t.getModelRef()))
							.parameterType(t.getParameterType())
							.required(t.getRequired())
							.build());
			});
		}
		return pars;
	}
	
	// @formatter:on

	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <Swagger-ui 相关配置> 相关的配置");
	}

}