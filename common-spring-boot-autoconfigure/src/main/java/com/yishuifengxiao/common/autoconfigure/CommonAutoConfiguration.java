package com.yishuifengxiao.common.autoconfigure;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yishuifengxiao.common.utils.SpringContext;

/**
 * 注入一些全局通用的配置
 * 
 * @author yishui
 * @date 2019年2月13日
 * @version 0.0.1
 */
@Configuration
public class CommonAutoConfiguration {
    
	/**
	 * 注入一个spring 上下文工具类
	 * @param applicationContext
	 * @return
	 */
	@Bean
	public SpringContext springContext(ApplicationContext applicationContext) {
		SpringContext springContext = new SpringContext();
		springContext.setApplicationContext(applicationContext);
		return springContext;
	}

}
