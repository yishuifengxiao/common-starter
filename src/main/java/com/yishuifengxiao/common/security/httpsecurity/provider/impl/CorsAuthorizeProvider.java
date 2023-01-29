package com.yishuifengxiao.common.security.httpsecurity.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.httpsecurity.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * cros授权提供器
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CorsAuthorizeProvider implements AuthorizeProvider {


	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		HttpSecurity http=expressionInterceptUrlRegistry.and();
		// 关闭cors保护
		if (propertyResource.security().getCloseCors()) {
			http.cors().disable();
		}
		//@formatter:on  

	}

	@Override
	public int getOrder() {
		return 900;
	}



}
