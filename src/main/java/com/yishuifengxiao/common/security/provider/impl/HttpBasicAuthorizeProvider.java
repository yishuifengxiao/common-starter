package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * http basic登陆时的配置
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public class HttpBasicAuthorizeProvider implements AuthorizeProvider {

	/**
	 * 异常处理的端点
	 */
	private AuthenticationEntryPoint exceptionAuthenticationEntryPoint;

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		HttpSecurity http = expressionInterceptUrlRegistry.and();

		// 开启http baisc认证
		if (propertyResource.security().getHttpBasic()) {
			http.httpBasic() // 开启basic认证
					.authenticationEntryPoint(exceptionAuthenticationEntryPoint)
					.realmName(propertyResource.security().getRealmName());
		}
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 700;
	}


	public AuthenticationEntryPoint getExceptionAuthenticationEntryPoint() {
		return exceptionAuthenticationEntryPoint;
	}

	public void setExceptionAuthenticationEntryPoint(AuthenticationEntryPoint exceptionAuthenticationEntryPoint) {
		this.exceptionAuthenticationEntryPoint = exceptionAuthenticationEntryPoint;
	}



}
