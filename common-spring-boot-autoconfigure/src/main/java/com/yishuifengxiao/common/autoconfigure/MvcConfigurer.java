package com.yishuifengxiao.common.autoconfigure;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 增加swagger ui静态资源配置
 * 
 * @author yishui
 * @date 2019年1月21日
 * @Version 0.0.1
 */
@EnableAutoConfiguration
public class MvcConfigurer implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("doc.html").addResourceLocations("classpath*:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath*:/META-INF/resources/webjars/");
	}
}
