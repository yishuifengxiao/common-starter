package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;

/**
 * csrf授权提供器
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public class CsrfAuthorizeProvider implements AuthorizeProvider {
	protected SecurityProperties securityProperties;

	@Override
	public void config(
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		HttpSecurity http=expressionInterceptUrlRegistry.and();
		// 关闭csrf防护
		if (securityProperties.getCloseCsrf()) {
			http.csrf().disable();
		}
		//@formatter:on  

	}

	@Override
	public int getOrder() {
		return 800;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}
	
	

}
