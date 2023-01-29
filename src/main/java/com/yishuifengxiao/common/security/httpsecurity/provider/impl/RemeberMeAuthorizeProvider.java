/**
 * 
 */
package com.yishuifengxiao.common.security.httpsecurity.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.yishuifengxiao.common.security.httpsecurity.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * spring security记住我功能而定相关配置
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class RemeberMeAuthorizeProvider implements AuthorizeProvider {

	/**
	 * 记住我功能的实现
	 */
	protected PersistentTokenRepository persistentTokenRepository;

	/**
	 * 自定义UserDetailsService实现类，查找用户
	 */
	protected UserDetailsService userDetailsService;

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		expressionInterceptUrlRegistry
		.and()
		//记住我的功能
		.rememberMe()
		//是否使用安全cookie
		.useSecureCookie(propertyResource.security().getRemeberMe().getUseSecureCookie())
		//记住我产生的token的key
		.key(propertyResource.security().getRemeberMe().getKey())
		.rememberMeParameter(propertyResource.security().getRemeberMe().getRememberMeParameter())
		//记住我的实现
		.tokenRepository(persistentTokenRepository)
		//记住我的时间
		.tokenValiditySeconds(propertyResource.security().getRemeberMe().getRememberMeSeconds())
		//记住我的验证逻辑
		.userDetailsService(userDetailsService);
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 300;
	}

	public PersistentTokenRepository getPersistentTokenRepository() {
		return persistentTokenRepository;
	}

	public void setPersistentTokenRepository(PersistentTokenRepository persistentTokenRepository) {
		this.persistentTokenRepository = persistentTokenRepository;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

}
