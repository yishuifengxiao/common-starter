package com.yishuifengxiao.common.guava;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;


/**
 * <p>
 * guava增强组件自动配置
 * </p>
 *
 * <p>
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
 *    }
 * }
 *
 * </pre>
 * <p>
 * <p>
 * 发送消息
 *
 * <pre>
 * asyncEventBus.post("需要发送的数据");
 * </pre>
 * <p>
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
     * @param executor 自定义线程池
     * @return guava异步消息总线
     */
    @Bean
    @ConditionalOnMissingBean({EventBus.class})
    @ConditionalOnBean({Executor.class})
    public EventBus asyncEventBus(Executor executor) {

        return new AsyncEventBus(executor);
    }

    @Bean("globalEventPublisher")
    @ConditionalOnMissingBean({EventPublisher.class})
    @ConditionalOnBean({EventBus.class})
    public EventPublisher eventPublisher(EventBus asyncEventBus) throws Exception {
        SimpleEventPublisher eventPublisher = new SimpleEventPublisher();
        eventPublisher.setAsyncEventBus(asyncEventBus);
        eventPublisher.afterPropertiesSet();
        return eventPublisher;
    }

    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <guava增强支持> 相关的配置");
    }


}
