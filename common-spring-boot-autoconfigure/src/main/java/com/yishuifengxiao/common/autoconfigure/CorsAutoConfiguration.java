/**
 * 
 */
package com.yishuifengxiao.common.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 支持 跨域的 配置
 * 
 * @author yishui
 * @date 2019年2月13日
 * @version 0.0.1
 */
@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class CorsAutoConfiguration {

	/**
	 * cors协议支持
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = "yishuifengxiao.cors", name = { "enable" }, havingValue = "true")
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				//@formatter:off  
				registry
					.addMapping("/**")
					.allowedOrigins("*")
					.allowedMethods("HEAD", "GET", "PUT", "POST", "PATCH", "DELETE")
					.allowedHeaders("*")
					.allowCredentials(true);//允许带认证信息的配置
				//@formatter:on  
			}
		};
	}

	/**
	 * 解决跨域的问题
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = "yishuifengxiao.cors", name = { "enable" }, havingValue = "true")
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", corsConfiguration); // 4
		return new CorsFilter(source);
	}

}
