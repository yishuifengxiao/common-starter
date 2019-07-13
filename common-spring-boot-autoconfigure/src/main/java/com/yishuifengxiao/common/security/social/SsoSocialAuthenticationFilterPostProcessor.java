package com.yishuifengxiao.common.security.social;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;

public class SsoSocialAuthenticationFilterPostProcessor implements SocialAuthenticationFilterPostProcessor {
   
    private AuthenticationSuccessHandler jsAuthenticationSuccessHandler;

    // 后处理器
    public void process(SocialAuthenticationFilter socialAuthenticationFilter) {
        socialAuthenticationFilter.setAuthenticationSuccessHandler(jsAuthenticationSuccessHandler);
    }

	public AuthenticationSuccessHandler getJsAuthenticationSuccessHandler() {
		return jsAuthenticationSuccessHandler;
	}

	public void setJsAuthenticationSuccessHandler(AuthenticationSuccessHandler jsAuthenticationSuccessHandler) {
		this.jsAuthenticationSuccessHandler = jsAuthenticationSuccessHandler;
	}
    
    
}