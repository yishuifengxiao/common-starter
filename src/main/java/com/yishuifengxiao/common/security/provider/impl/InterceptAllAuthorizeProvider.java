/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * <p>拦截所有的资源</p>
 * <strong>注意此过滤器一定要最后加载</strong>
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class InterceptAllAuthorizeProvider implements AuthorizeProvider {

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//只要经过了授权就能访问
		expressionInterceptUrlRegistry.anyRequest().authenticated();

	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}

}
