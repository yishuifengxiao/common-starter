package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;

/**
 * cros授权提供器
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public class CorsAuthorizeProvider implements AuthorizeProvider {
	protected SecurityProperties securityProperties;

	@Override
	public void config(
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		HttpSecurity http=expressionInterceptUrlRegistry.and();
		// 关闭cors保护
		if (securityProperties.getCloseCors()) {
			http.cors().disable();
		}
		//@formatter:on  

	}

	@Override
	public int getOrder() {
		return 900;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}
	
	

}
