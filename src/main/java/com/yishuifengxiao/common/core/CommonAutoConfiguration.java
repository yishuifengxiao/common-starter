package com.yishuifengxiao.common.core;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yishuifengxiao.common.support.ErrorUtil;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.web.WebExceptionProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * 核心组件自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WebExceptionProperties.class)
public class CommonAutoConfiguration {

	/**
	 * 注入一个spring 上下文工具类
	 * 
	 * @param applicationContext spring上下文
	 * @return spring 上下文工具类
	 */
	@Bean
	public SpringContext springContext(ApplicationContext applicationContext) {
		SpringContext springContext = new SpringContext();
		springContext.setApplicationContext(applicationContext);
		return springContext;
	}

	/**
	 * 异常信息提取工具
	 * 
	 * @param exceptionProperties 异常信息配置规则
	 * @return 异常信息提取工具
	 */
	@Bean
	public ErrorUtil errorUtil(WebExceptionProperties exceptionProperties) {
		ErrorUtil errorUtil = new ErrorUtil();
		errorUtil.init(exceptionProperties);
		return errorUtil;
	}

	/**
	 * 配置检查
	 */
	@PostConstruct
	public void checkConfig() {

		log.trace("【易水组件】: 开启 <全局通用支持> 相关的配置");
	}

}
