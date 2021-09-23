/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * spring security表单登录相关的配置
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class FormLoginAuthorizeProvider implements AuthorizeProvider {

	/**
	 * 自定义认证成功处理器
	 */
	protected AuthenticationSuccessHandler formAuthenticationSuccessHandler;
	/**
	 * 自定义认证失败处理器
	 */
	protected AuthenticationFailureHandler formAuthenticationFailureHandler;

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		expressionInterceptUrlRegistry
		.and()
		.formLogin()
		//权限拦截时默认跳转的页面
		.loginPage(propertyResource.security().getCore().getLoginPage())
		//处理登录请求的URL
		.loginProcessingUrl(propertyResource.security().getCore().getFormActionUrl())
		//用户名参数的名字
		.usernameParameter(propertyResource.security().getCore().getUsernameParameter())
		// 密码参数的名字
		.passwordParameter(propertyResource.security().getCore().getPasswordParameter())
		//自定义认证成功处理器
		.successHandler(formAuthenticationSuccessHandler)
		//自定义认证失败处理器
		.failureHandler(formAuthenticationFailureHandler);
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 100;
	}

	public AuthenticationSuccessHandler getFormAuthenticationSuccessHandler() {
		return formAuthenticationSuccessHandler;
	}

	public void setFormAuthenticationSuccessHandler(AuthenticationSuccessHandler formAuthenticationSuccessHandler) {
		this.formAuthenticationSuccessHandler = formAuthenticationSuccessHandler;
	}

	public AuthenticationFailureHandler getFormAuthenticationFailureHandler() {
		return formAuthenticationFailureHandler;
	}

	public void setFormAuthenticationFailureHandler(AuthenticationFailureHandler formAuthenticationFailureHandler) {
		this.formAuthenticationFailureHandler = formAuthenticationFailureHandler;
	}

}
