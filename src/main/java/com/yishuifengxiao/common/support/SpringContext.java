package com.yishuifengxiao.common.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * spring上下文工具
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SpringContext implements ApplicationContextAware {
    /**
     * spring 上下文
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContext.applicationContext = applicationContext;
    }

    /**
     * 返回工厂中所有定义的实例的名字
     *
     * @return 工厂中所有定义的实例的名字
     */
    public static String[] getBeanDefinitionNames() {
        return SpringContext.applicationContext.getBeanDefinitionNames();
    }

    /**
     * 根据class获取实例对象
     *
     * @param <T>   实例对象的类型
     * @param clazz Class
     * @return 实例对象
     */
    public static <T> T getBean(Class<? extends T> clazz) {
        Assert.notNull(clazz, "对象名字不能为空");
        return SpringContext.applicationContext.getBean(clazz);
    }

    /**
     * 根据对象名字获取对象实例
     *
     * @param <T>      实例对象的类型
     * @param beanName 实例对象的名字
     * @return 实例对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        Assert.notNull(beanName, "对象名字不能为空");
        return (T) SpringContext.applicationContext.getBean(beanName);
    }

    /**
     * 获取spring的上下文对象
     *
     * @return spring的上下文对象
     */
    public static ApplicationContext getContext() {
        return SpringContext.applicationContext;
    }

    /**
     * <p>
     * 通知向此应用程序注册的所有匹配侦听器事件
     * </p>
     * 如果指定的事件不是ApplicationEvent，则将其包装在PayloadApplicationEvent中。
     *
     * @param event 需要发布的事件
     */
    public static void publishEvent(Object event) {
        try {
            SpringContext.applicationContext.publishEvent(event);
        } catch (Exception e) {
            log.info("发布spring事件时出现问题 {} ", e);
        }

    }

}
