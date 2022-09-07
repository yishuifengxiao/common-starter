package com.yishuifengxiao.common.core;

import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.web.WebExceptionProperties;
import com.yishuifengxiao.common.web.error.ErrorHandler;
import com.yishuifengxiao.common.web.error.ExceptionHelper;
import com.yishuifengxiao.common.web.error.SimpleExceptionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;

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
@Priority(1)
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
	 * @throws Exception
	 */
	@Bean("customExceptionHelper")
	@ConditionalOnMissingBean(name = "customExceptionHelper")
	public ExceptionHelper exceptionHelper(@Autowired(required = false) ErrorHandler errorHandler,
			WebExceptionProperties exceptionProperties) throws Exception {
		SimpleExceptionHelper exceptionHelper = new SimpleExceptionHelper();
		exceptionHelper.setErrorHandler(errorHandler);
		exceptionHelper.setExceptionProperties(exceptionProperties);
		exceptionHelper.afterPropertiesSet();
		return exceptionHelper;
	}

	/**
	 * 生成一个全局线程池
	 *
	 * <p>
	 * IO密集型=2Ncpu（可以测试后自己控制大小，2Ncpu一般没问题） （常出现于线程中：数据库数据交互、文件上传下载、网络数据传输等等）
	 * </p>
	 * <p>
	 * 计算密集型=Ncpu（常出现于线程中：复杂算法）
	 * </p>
	 * <p>
	 * java中：Ncpu=Runtime.getRuntime().availableProcessors()
	 * </p>
	 *
	 * @return 线程池
	 */
	@Bean
	@ConditionalOnMissingBean({ ThreadPool.class })
	public ThreadPool threadPool() {

		return new SimpleThreadPool();
	}

	/**
	 * 配置检查
	 */
	@PostConstruct
	public void checkConfig() {

		log.trace("【易水组件】: 开启 <全局通用支持> 相关的配置");
	}

}
