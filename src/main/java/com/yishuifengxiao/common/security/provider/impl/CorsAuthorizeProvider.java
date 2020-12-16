package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * cros授权提供器
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
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
