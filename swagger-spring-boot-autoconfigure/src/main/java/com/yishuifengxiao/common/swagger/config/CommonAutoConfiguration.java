/**
 * 
 */
package com.yishuifengxiao.common.swagger.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.yishuifengxiao.common.swagger.properties.CorsProperties;
import com.yishuifengxiao.common.swagger.properties.SwaggerProperties;

/**
 * @author yishui
 * @date 2019年1月17日
 * @version 0.0.1
 */
@Configuration
@EnableConfigurationProperties({ SwaggerProperties.class, CorsProperties.class })
public class CommonAutoConfiguration {
	private final static Logger log = LoggerFactory.getLogger(CommonAutoConfiguration.class);

	private SwaggerProperties swaggerProperties;

	@PostConstruct
	public void checkConfigFileExists() {
		log.debug("===================================> 自定义配置为 {}", swaggerProperties);
	}

	public SwaggerProperties getSwaggerProperties() {
		return swaggerProperties;
	}

	public void setSwaggerProperties(SwaggerProperties swaggerProperties) {
		this.swaggerProperties = swaggerProperties;
	}

}
