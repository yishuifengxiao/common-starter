package com.yishuifengxiao.common.security.social.processor.impl;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;

import com.yishuifengxiao.common.security.social.processor.SocialAuthenticationFilterPostProcessor;

/**
 * spring social认证过滤器
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class SsoSocialAuthenticationFilterPostProcessor implements SocialAuthenticationFilterPostProcessor {

	private AuthenticationSuccessHandler jsAuthenticationSuccessHandler;


	@Override
	public void process(SocialAuthenticationFilter socialAuthenticationFilter) {
		socialAuthenticationFilter.setAuthenticationSuccessHandler(jsAuthenticationSuccessHandler);
	}

	public AuthenticationSuccessHandler getJsAuthenticationSuccessHandler() {
		return jsAuthenticationSuccessHandler;
	}

	public void setJsAuthenticationSuccessHandler(AuthenticationSuccessHandler jsAuthenticationSuccessHandler) {
		this.jsAuthenticationSuccessHandler = jsAuthenticationSuccessHandler;
	}

	public SsoSocialAuthenticationFilterPostProcessor(AuthenticationSuccessHandler jsAuthenticationSuccessHandler) {
		this.jsAuthenticationSuccessHandler = jsAuthenticationSuccessHandler;
	}

	public SsoSocialAuthenticationFilterPostProcessor() {

	}

}