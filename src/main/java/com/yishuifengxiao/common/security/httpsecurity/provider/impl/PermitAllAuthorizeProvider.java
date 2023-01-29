/**
 * 
 */
package com.yishuifengxiao.common.security.httpsecurity.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.httpsecurity.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * AuthorizeConfigProvider的默认配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */

public class PermitAllAuthorizeProvider implements AuthorizeProvider {

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry) {

		// 所有直接放行的资源
		for (String url : propertyResource.allPermitUrs()) {
			expressionInterceptUrlRegistry.antMatchers(url).permitAll();
		}

	}

	@Override
	public int getOrder() {
		return 600;
	}

}
