package com.yishuifengxiao.common.core;

import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.yishuifengxiao.common.exception.ExceptionProperties;
import com.yishuifengxiao.common.support.ErrorMsgUtil;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.encoder.Md5;

import lombok.extern.slf4j.Slf4j;

/**
 * 注入一些全局通用的配置
 * 
 * @author yishui
 * @date 2019年2月13日
 * @version 0.0.1
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ExceptionProperties.class)
public class CommonAutoConfiguration {

	/**
	 * 注入一个spring 上下文工具类
	 * 
	 * @param applicationContext
	 * @return
	 */
	@Bean
	public SpringContext springContext(ApplicationContext applicationContext) {
		SpringContext springContext = new SpringContext();
		springContext.setApplicationContext(applicationContext);
		return springContext;
	}

	/**
	 * 生成一个自定义spring cache 键生成器
	 * 
	 * @return
	 */
	@Bean("simpleKeyGenerator")
	@ConditionalOnMissingBean(name = { "simpleKeyGenerator" })
	public KeyGenerator simpleKeyGenerator() {
		return new KeyGenerator() {

			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder prefix = new StringBuilder(target.getClass().getSimpleName()).append(":")
						.append(method.getName()).append(":");
				StringBuilder values = new StringBuilder("");
				if (null != params) {
					for (Object param : params) {
						if (null != param) {
							values.append(param.toString());
						}
					}
				}
				return prefix.append(Md5.md5Short(values.toString())).toString();
			}
		};
	}

	/**
	 * 异常信息工具
	 * 
	 * @param exceptionProperties 异常信息配置规则
	 * @return
	 */
	@Bean
	public ErrorMsgUtil errorMsgUtil(ExceptionProperties exceptionProperties) {
		return new ErrorMsgUtil(exceptionProperties);
	}

	/**
	 * 注入一个guava异步消息总线<br/>
	 * <br/>
	 * 使用该异步消息总线的示例如下：<br/>
	 * 
	 * <pre>
	 * &#64;Component
	 * public class DemoEventBus {
	 * 	&#64;Resource
	 * 	private EventBus asyncEventBus;
	 * 
	 * 	&#64;PostConstruct
	 * 	public void init() {
	 * 		asyncEventBus.register(this);
	 * 	}
	 * }
	 * 
	 * </pre>
	 * 
	 * 
	 * 发送消息
	 * 
	 * <pre>
	 * asyncEventBus.post("需要发送的数据");
	 * </pre>
	 * 
	 * 接收消息
	 * 
	 * <pre>
	 * &#64;Subscribe
	 * public void recieve(Object data) {
	 * 	// 注意guava是根据入参的数据类型进行接收的
	 * 	// 发送的数据可以被多个接收者同时接收
	 * }
	 * </pre>
	 * 
	 * @return guava异步消息总线
	 */
	@Bean("asyncEventBus")
	@ConditionalOnMissingBean(name = { "asyncEventBus" })
	public EventBus asyncEventBus() {
		BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(100);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 5, TimeUnit.SECONDS, queue,
				new ThreadPoolExecutor.CallerRunsPolicy());
		return new AsyncEventBus(executor);
	}

	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <全局通用配置> 相关的配置");
	}

}
