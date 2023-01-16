/**
 * 
 */
package com.yishuifengxiao.common.web;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import lombok.extern.slf4j.Slf4j;

/**
 * 跨域支持自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties({ CorsProperties.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.cors", name = { "enable" }, havingValue = "true", matchIfMissing = true)
@Slf4j
public class CorsAutoConfiguration {

	@Autowired
	private CorsProperties corsProperties;

	/**
	 * 注入一个跨域支持过滤器
	 * 
	 * @return 跨域支持过滤器
	 */
	@Bean("corsAllowedFilter")
	@ConditionalOnMissingBean(name = "corsAllowedFilter")
	public FilterRegistrationBean<CustomCorsFilter> corsAllowedFilter() {
		CustomCorsFilter corsFilter = new CustomCorsFilter(corsProperties);
		FilterRegistrationBean<CustomCorsFilter> registration = new FilterRegistrationBean<>(corsFilter);
		registration.setName("corsAllowedFilter");
		registration.setUrlPatterns(corsProperties.getUrlPatterns());
		registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registration;
	}

	/**
	 * 配置检查
	 */
	@PostConstruct
	public void checkConfig() {

		log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <跨域支持> 相关的配置");
	}

}
