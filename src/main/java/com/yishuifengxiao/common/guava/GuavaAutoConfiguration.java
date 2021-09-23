package com.yishuifengxiao.common.guava;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import lombok.extern.slf4j.Slf4j;

/**
 * guava增强组件自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class GuavaAutoConfiguration {

	/**
	 * <p>
	 * 注入一个guava异步消息总线
	 * </p>
	 * 
	 * 使用该异步消息总线的示例如下：
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
	@Bean
	@ConditionalOnMissingBean({ EventBus.class })
	public EventBus asyncEventBus() {
		BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(100);
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 5, TimeUnit.SECONDS, queue,
				new ThreadPoolExecutor.CallerRunsPolicy());
		return new AsyncEventBus(executor);
	}

	/**
	 * 配置检查
	 */
	@PostConstruct
	public void checkConfig() {

		log.trace("【易水组件】: 开启 <guava增强支持> 相关的配置");
	}

}
