/**
 * 
 */
package com.yishuifengxiao.common.security.security.provider.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.security.provider.AuthorizeConfigProvider;

/**
 * spring security并发登录相关的配置
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1 
 */
@Component
@ConditionalOnMissingBean(name = "sessionProvider")
public class SessionAuthorizeConfigProvider implements AuthorizeConfigProvider {
	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;
	
	/**
	 * 自定义认证失败处理器
	 */
	@Autowired
	protected AuthenticationFailureHandler customAuthenticationFailureHandler;
	/**
	 * session失效后的处理策略
	 */
	@Autowired
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

}
