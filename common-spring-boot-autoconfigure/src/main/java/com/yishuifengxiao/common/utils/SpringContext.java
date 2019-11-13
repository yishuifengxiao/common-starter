package com.yishuifengxiao.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * spring上下文获取工具类
 * 
 * @author yishui
 * @date 2019年11月13日
 * @version 1.0.0
 */
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
	 * @return
	 */
	public static String[] getBeanDefinitionNames() {
		return SpringContext.applicationContext.getBeanDefinitionNames();
	}

	/**
	 * 根据class获取实例对象
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<? extends T> clazz) {
		Assert.notNull(clazz, "对象名字不能为空");
		return SpringContext.applicationContext.getBean(clazz);
	}

	/**
	 * 根据对象名字获取对象实例
	 * 
	 * @param <T>
	 * @param beanName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		Assert.notNull(beanName, "对象名字不能为空");
		return (T) SpringContext.applicationContext.getBean(beanName);
	}

	/**
	 * 获取spring的上下文对象
	 * 
	 * @return
	 */
	public static ApplicationContext getContext() {
		return SpringContext.applicationContext;
	}

	/**
	 * 通知向此应用程序注册的所有匹配侦听器事件。<br/>
	 * 如果指定的事件不是ApplicationEvent，则将其包装在PayloadApplicationEvent中。
	 * 
	 * @param event
	 */
	public static void publishEvent(Object event) {
		SpringContext.applicationContext.publishEvent(event);
	}

}
