package com.yishuifengxiao.common.core;

import com.yishuifengxiao.common.support.SpringContext;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    @ConditionalOnMissingBean({ThreadPoolProducer.class})
    public ThreadPoolProducer threadPoolProducer() {

        return () -> new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                //
                Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }


    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <全局通用支持> 相关的配置");
    }

}
