package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * 异常处理器
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public class ExceptionAuthorizeProvider implements AuthorizeProvider {

	private AuthenticationEntryPoint exceptionAuthenticationEntryPoint;

	private AccessDeniedHandler customAccessDeniedHandler;

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		expressionInterceptUrlRegistry.and()
		.exceptionHandling()
		// 定义的不存在access_token时候响应
		.authenticationEntryPoint(exceptionAuthenticationEntryPoint)
		//自定义权限拒绝处理器
		.accessDeniedHandler(customAccessDeniedHandler)
		;
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 1000;
	}

	public AuthenticationEntryPoint getExceptionAuthenticationEntryPoint() {
		return exceptionAuthenticationEntryPoint;
	}

	public void setExceptionAuthenticationEntryPoint(AuthenticationEntryPoint exceptionAuthenticationEntryPoint) {
		this.exceptionAuthenticationEntryPoint = exceptionAuthenticationEntryPoint;
	}

	public AccessDeniedHandler getCustomAccessDeniedHandler() {
		return customAccessDeniedHandler;
	}

	public void setCustomAccessDeniedHandler(AccessDeniedHandler customAccessDeniedHandler) {
		this.customAccessDeniedHandler = customAccessDeniedHandler;
	}

	public ExceptionAuthorizeProvider(AuthenticationEntryPoint exceptionAuthenticationEntryPoint,
			AccessDeniedHandler customAccessDeniedHandler) {
		this.exceptionAuthenticationEntryPoint = exceptionAuthenticationEntryPoint;
		this.customAccessDeniedHandler = customAccessDeniedHandler;
	}

	public ExceptionAuthorizeProvider() {

	}

}
