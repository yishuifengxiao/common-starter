package com.yishuifengxiao.common.core;

import com.yishuifengxiao.common.support.SpringContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 核心组件自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@Priority(1)
public class CommonAutoConfiguration implements ApplicationContextAware {

    private SpringContext springContext = new SpringContext();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        springContext.setApplicationContext(applicationContext);
    }

    /**
     * 创建并配置一个通用的线程池执行器 Bean。
     * 该线程池用于异步任务的执行，具备可调节的核心线程数、最大线程数、队列容量等参数，
     * 并设置了合理的拒绝策略和线程管理机制。
     *
     * @return 配置完成的 {@link Executor} 实例，用于异步任务执行
     */
    @Bean
    @ConditionalOnMissingBean(value = {Executor.class})
    @ConditionalOnClass(ThreadPoolTaskExecutor.class)
    public Executor syncExecutor() {

        // 获取可用处理器的Java虚拟机的数量
        int sum = Runtime.getRuntime().availableProcessors();

        // 实例化自定义线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 设置线程池中的核心线程数(最小线程数)
        executor.setCorePoolSize(sum);

        // 设置线程池中的最大线程数
        executor.setMaxPoolSize(64);

        // 设置线程池中任务队列的容量
        executor.setQueueCapacity(Integer.MAX_VALUE);

        // 设置线程池中空闲线程的存活时间（单位：秒）
        executor.setKeepAliveSeconds(60);

        // 设置线程池中线程的名称前缀，便于识别和调试
        executor.setThreadNamePrefix("async-");

        // 设置线程池关闭时等待所有任务完成的时间（单位：秒）
        executor.setAwaitTerminationSeconds(60);

        // 设置线程池中任务队列已满且达到最大线程数时的拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

        // 设置线程池在关闭时是否等待所有任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 初始化线程池的配置
        executor.initialize();

        return executor;
    }


    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <全局通用支持> 相关的配置");
    }

}
