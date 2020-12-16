/**
 * 
 */
package com.yishuifengxiao.common.cors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * 支持 跨域的 配置
 * 
 * @author yishui
 * @date 2019年2月13日
 * @version 0.0.1
 */
@Configuration
@EnableConfigurationProperties({ CorsProperties.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.cors", name = { "enable" }, havingValue = "true", matchIfMissing = true)
@Slf4j
public class CorsAutoConfiguration {

	@Autowired
	private CorsProperties corsProperties;

	/**
	 * cors协议支持
	 * 
	 * @return
	 */
	@Bean("corsConfigurer")
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				//@formatter:off  
				registry
					.addMapping(corsProperties.getUrl())
					.allowedOrigins(corsProperties.getAllowedOrigins())
					.allowedMethods(corsProperties.getAllowedMethods())
					.allowedHeaders(corsProperties.getAllowedHeaders())
					.allowCredentials(corsProperties.getAllowCredentials());//允许带认证信息的配置
				//@formatter:on  
			}
		};
	}

	/**
	 * 解决跨域的问题
	 * 
	 * @return
	 */
	@Bean("corsFilter")
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin(corsProperties.getAllowedOrigins());
		corsConfiguration.addAllowedHeader(corsProperties.getAllowedHeaders());
		corsConfiguration.addAllowedMethod(corsProperties.getAllowedMethods());
		source.registerCorsConfiguration(corsProperties.getUrl(), corsConfiguration);
		return new CorsFilter(source);
	}
	
	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <跨域配置> 相关的配置");
	}

}
