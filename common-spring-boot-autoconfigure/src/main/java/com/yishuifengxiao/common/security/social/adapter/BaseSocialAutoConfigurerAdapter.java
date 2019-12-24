package com.yishuifengxiao.common.security.social.adapter;

import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;

/**
 * spring social 1.1.6已经移除了该类，所以自己新建一下
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public abstract class BaseSocialAutoConfigurerAdapter extends SocialConfigurerAdapter {

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer configurer, Environment environment) {
		configurer.addConnectionFactory(createConnectionFactory());
	}

	/**
	 * 
	 * @return
	 */
	protected abstract ConnectionFactory<?> createConnectionFactory();

}