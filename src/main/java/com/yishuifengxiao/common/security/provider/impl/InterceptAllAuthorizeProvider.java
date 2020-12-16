/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * 拦截所有的资源<br/>
 * <strong>注意此过滤器一定要最后加载</strong>
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version 0.0.1
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
