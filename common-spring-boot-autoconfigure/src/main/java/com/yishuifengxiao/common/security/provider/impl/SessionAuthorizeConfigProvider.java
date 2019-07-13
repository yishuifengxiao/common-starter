/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;

/**
 * spring security并发登录相关的配置
 * 
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1
 */
public class SessionAuthorizeConfigProvider implements AuthorizeProvider {
	/**
	 * 自定义属性配置
	 */
	protected SecurityProperties securityProperties;

	/**
	 * 自定义认证失败处理器
	 */
	protected AuthenticationFailureHandler customAuthenticationFailureHandler;
	/**
	 * session失效后的处理策略
	 */
	private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config)
			throws Exception {
		//@formatter:off 
		config
		.and()
		.sessionManagement()
		.sessionAuthenticationFailureHandler(customAuthenticationFailureHandler)//???session认证处理类？
		//.invalidSessionUrl(customProperties.getSecurity().getSession().getSessionInvalidUrl())//session过期后的跳转
		.maximumSessions(securityProperties.getSession().getMaximumSessions())//同一个用户最大的session数量
		.maxSessionsPreventsLogin(securityProperties.getSession().isMaxSessionsPreventsLogin())//session数量达到最大时，是否阻止第二个用户登陆
		.expiredSessionStrategy(sessionInformationExpiredStrategy)//session过期时的处理策略
		;
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 400;
	}

	public SessionAuthorizeConfigProvider(SecurityProperties securityProperties,
			AuthenticationFailureHandler customAuthenticationFailureHandler,
			SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
		this.securityProperties = securityProperties;
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
		this.sessionInformationExpiredStrategy = sessionInformationExpiredStrategy;
	}

	public SessionAuthorizeConfigProvider() {

	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public AuthenticationFailureHandler getCustomAuthenticationFailureHandler() {
		return customAuthenticationFailureHandler;
	}

	public void setCustomAuthenticationFailureHandler(AuthenticationFailureHandler customAuthenticationFailureHandler) {
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
	}

	public SessionInformationExpiredStrategy getSessionInformationExpiredStrategy() {
		return sessionInformationExpiredStrategy;
	}

	public void setSessionInformationExpiredStrategy(
			SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
		this.sessionInformationExpiredStrategy = sessionInformationExpiredStrategy;
	}

}
