package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.httpsecurity.authorize.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * csrf授权提供器
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CsrfAuthorizeProvider implements AuthorizeProvider {


	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		HttpSecurity http=expressionInterceptUrlRegistry.and();
		// 关闭csrf防护
		if (propertyResource.security().getCloseCsrf()) {
			http.csrf().disable();
		}
		//@formatter:on  

	}

	@Override
	public int order() {
		return 800;
	}


}
