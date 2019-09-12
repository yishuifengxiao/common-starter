/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
public class SessionAuthorizeProvider implements AuthorizeProvider {
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
		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		//定义AuthenticationFailureHandler，它将在SessionAuthenticationStrategy引发异常时使用。
		//如果未设置，将向客户端返回未经授权的（402）错误代码。
		//请注意，如果在基于表单的登录期间发生错误，则此属性不会发生，其中URL身份验证失败将优先
		.sessionAuthenticationFailureHandler(customAuthenticationFailureHandler)
		//.invalidSessionUrl(securityProperties.getSession().getSessionInvalidUrl()) //session过期时的跳转的url
		.maximumSessions(securityProperties.getSession().getMaximumSessions())//同一个用户最大的session数量
		.maxSessionsPreventsLogin(securityProperties.getSession().isMaxSessionsPreventsLogin())//session数量达到最大时，是否阻止第二个用户登陆
		//.invalidSessionUrl(customProperties.getSecurity().getSession().getSessionInvalidUrl())//session过期后的跳转
		.expiredSessionStrategy(sessionInformationExpiredStrategy)//session过期时的处理策略
		;
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 400;
	}

	public SessionAuthorizeProvider(SecurityProperties securityProperties,
			AuthenticationFailureHandler customAuthenticationFailureHandler,
			SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
		this.securityProperties = securityProperties;
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
		this.sessionInformationExpiredStrategy = sessionInformationExpiredStrategy;
	}

	public SessionAuthorizeProvider() {

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
