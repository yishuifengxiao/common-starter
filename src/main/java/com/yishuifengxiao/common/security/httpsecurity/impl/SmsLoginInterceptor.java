package com.yishuifengxiao.common.security.httpsecurity.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.yishuifengxiao.common.security.authentcation.SmsAuthenticationFilter;
import com.yishuifengxiao.common.security.authentcation.SmsAuthenticationProvider;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;

/**
 * <strong>短信登陆拦截器</strong><br/>
 * <br/>
 * 将短信验证码的几个配置参数串联起来 将自定义的短信处理方式配置进spring security，使系统具备通过短信登陆的能力
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version v1.0.0
 */
public class SmsLoginInterceptor extends HttpSecurityInterceptor {

	private AuthenticationSuccessHandler customAuthenticationFailureHandler;

	private AuthenticationFailureHandler customAuthenticationSuccessHandler;

	private UserDetailsService userDetailsService;
	/**
	 * 短信登录的URL
	 */
	private String url;

	@Override
	public void configure(HttpSecurity http) throws Exception {

		SmsAuthenticationFilter smsCodeAuthenticationFilter = new SmsAuthenticationFilter(this.url);
		smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationFailureHandler);
		smsCodeAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationSuccessHandler);

		SmsAuthenticationProvider smsCodeAuthenticationProvider = new SmsAuthenticationProvider();
		smsCodeAuthenticationProvider.setUserDetailsService(userDetailsService);

		http.authenticationProvider(smsCodeAuthenticationProvider).addFilterAfter(smsCodeAuthenticationFilter,
				UsernamePasswordAuthenticationFilter.class);

	}

	public SmsLoginInterceptor(AuthenticationSuccessHandler customAuthenticationFailureHandler,
			AuthenticationFailureHandler customAuthenticationSuccessHandler, UserDetailsService userDetailsService,
			String url) {
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
		this.userDetailsService = userDetailsService;
		this.url = url;
	}

	public SmsLoginInterceptor() {

	}

	public AuthenticationSuccessHandler getCustomAuthenticationFailureHandler() {
		return customAuthenticationFailureHandler;
	}

	public void setCustomAuthenticationFailureHandler(AuthenticationSuccessHandler customAuthenticationFailureHandler) {
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
	}

	public AuthenticationFailureHandler getCustomAuthenticationSuccessHandler() {
		return customAuthenticationSuccessHandler;
	}

	public void setCustomAuthenticationSuccessHandler(AuthenticationFailureHandler customAuthenticationSuccessHandler) {
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}