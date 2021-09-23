package com.yishuifengxiao.common.social.adapter;

import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;

/**
 * spring social 1.1.6已经移除了该类，所以自己新建一下
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class BaseSocialAutoConfigurerAdapter extends SocialConfigurerAdapter {

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer configurer, Environment environment) {
		configurer.addConnectionFactory(createConnectionFactory());
	}

	/**
	 * 创建连接工厂
	 * @return 连接工厂
	 */
	protected abstract ConnectionFactory<?> createConnectionFactory();

}