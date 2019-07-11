/**
 * 
 */
package com.yishuifengxiao.common.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.properties.CorsProperties;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.properties.SwaggerProperties;

/**
 * 支持 跨域的 配置
 * 
 * @author yishui
 * @date 2019年2月13日
 * @version 0.0.1
 */
@Configuration
@EnableConfigurationProperties({ SwaggerProperties.class, CorsProperties.class,CodeProperties.class,SecurityProperties.class })
public class CorsAutoConfiguration {
	private final static Logger log = LoggerFactory.getLogger(CorsAutoConfiguration.class);

	/**
	 * cors协议支持
	 * 
	 * @return
	 */
	@Bean("corsConfigurer")
	@ConditionalOnProperty(prefix = "yishuifengxiao.cors", name = { "enable" }, havingValue = "true")
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				log.debug("=================================================> 开启跨域了");
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
	@Bean("corsFilter")
	@ConditionalOnProperty(prefix = "yishuifengxiao.cors", name = { "enable" }, havingValue = "true")
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}

}
