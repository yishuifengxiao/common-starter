package com.yishuifengxiao.common.security.social.processor.impl;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;

import com.yishuifengxiao.common.security.social.processor.SocialAuthenticationFilterPostProcessor;

public class SsoSocialAuthenticationFilterPostProcessor implements SocialAuthenticationFilterPostProcessor {
   
    private AuthenticationSuccessHandler jsAuthenticationSuccessHandler;

    // 后处理器
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