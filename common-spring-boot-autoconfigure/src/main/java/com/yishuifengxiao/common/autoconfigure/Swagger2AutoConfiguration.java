package com.yishuifengxiao.common.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.yishuifengxiao.common.properties.SwaggerProperties;
import com.yishuifengxiao.common.properties.SwaggerProperties.AuthoriZationPar;

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
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.swagger", name = {"base-package"})
public class Swagger2AutoConfiguration {
	private final static Logger log = LoggerFactory.getLogger(Swagger2AutoConfiguration.class);

	@Autowired
	private SwaggerProperties swaggerProperties;

	/**
	 * swagger-ui配置
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingClass
	public Docket createRestApi() {

		List<Parameter> pars = new ArrayList<Parameter>();
		List<AuthoriZationPar> auths = swaggerProperties.getAuths();
		log.debug("【swagger-ui】 授权参数为 {}", auths);
		if (auths != null && auths.size() != 0) {
			swaggerProperties.getAuths().forEach(t -> {
				ParameterBuilder authorizationPar = new ParameterBuilder();
				authorizationPar.name(t.getName()).description(t.getDescription())
						.modelRef(new ModelRef(t.getModelRef())).parameterType(t.getParameterType()).build();
				pars.add(authorizationPar.build());
			});
		}

		return new Docket(DocumentationType.SWAGGER_2).groupName(swaggerProperties.getGroupName()).apiInfo(apiInfo())
				.select().apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
				.paths(PathSelectors.any()).build().globalOperationParameters(pars);

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(swaggerProperties.getTitle()).description(swaggerProperties.getDescription())
				.termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
				.contact(new Contact(swaggerProperties.getContactUser(), swaggerProperties.getContactUrl(),
						swaggerProperties.getContactEmail()))
				.version(swaggerProperties.getVersion()).build();
	}

}