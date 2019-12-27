/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;

/**
 * spring security记住我功能而定相关配置
 * 
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1
 */
public class RemeberMeAuthorizeProvider implements AuthorizeProvider {
	/**
	 * 自定义属性配置
	 */
	protected SecurityProperties securityProperties;
	/**
	 * 记住我功能的实现
	 */
	protected PersistentTokenRepository persistentTokenRepository;

	/**
	 * 自定义UserDetailsService实现类，查找用户
	 */
	protected UserDetailsService userDetailsService;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		expressionInterceptUrlRegistry
		.and()
		//记住我的功能
		.rememberMe()
		//是否使用安全cookie
		.useSecureCookie(securityProperties.getRemeberMe().getUseSecureCookie())
		//记住我产生的token的key
		.key(securityProperties.getRemeberMe().getKey())
		.rememberMeParameter(securityProperties.getRemeberMe().getRememberMeParameter())
		//记住我的实现
		.tokenRepository(persistentTokenRepository)
		//记住我的时间
		.tokenValiditySeconds(securityProperties.getRemeberMe().getRememberMeSeconds())
		//记住我的验证逻辑
		.userDetailsService(userDetailsService);
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 300;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
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

	public RemeberMeAuthorizeProvider(SecurityProperties securityProperties,
			PersistentTokenRepository persistentTokenRepository, UserDetailsService userDetailsService) {
		this.securityProperties = securityProperties;
		this.persistentTokenRepository = persistentTokenRepository;
		this.userDetailsService = userDetailsService;
	}

	public RemeberMeAuthorizeProvider() {

	}

}
