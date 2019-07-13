package com.yishuifengxiao.common.security.adapter.impl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.yishuifengxiao.common.security.adapter.SecurityAdapter;
import com.yishuifengxiao.common.security.authentcation.SmsAuthenticationFilter;
import com.yishuifengxiao.common.security.authentcation.SmsAuthenticationProvider;

/**
 * 将短信验证码的几个配置参数串联起来 将自定义的短信处理方式配置进spring security
 * 
 * @author admin
 *
 */

public class SmsLoginAuthenticationAdapter extends SecurityAdapter {

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

	public SmsLoginAuthenticationAdapter(AuthenticationSuccessHandler customAuthenticationFailureHandler,
			AuthenticationFailureHandler customAuthenticationSuccessHandler, UserDetailsService userDetailsService,
			String url) {
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
		this.userDetailsService = userDetailsService;
		this.url = url;
	}

	public SmsLoginAuthenticationAdapter() {

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