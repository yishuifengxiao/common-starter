/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;

/**
 * spring security表单登录相关的配置
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1 
 */
public class FormLoginAuthorizeProvider implements AuthorizeProvider {
	/**
	 * 自定义属性配置
	 */
	protected SecurityProperties securityProperties;
	
	/**
	 * 自定义认证成功处理器
	 */
	protected AuthenticationSuccessHandler formAuthenticationSuccessHandler;
	/**
	 * 自定义认证失败处理器
	 */
	protected AuthenticationFailureHandler formAuthenticationFailureHandler;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		//@formatter:off 
		expressionInterceptUrlRegistry
		.and()
		.formLogin()
		.loginPage(securityProperties.getCore().getRedirectUrl())//权限拦截时默认跳转的页面
		.loginProcessingUrl(securityProperties.getCore().getFormActionUrl())//处理登录请求的URL
		.usernameParameter(securityProperties.getCore().getUsernameParameter())//用户名参数的名字
		.passwordParameter(securityProperties.getCore().getPasswordParameter())// 密码参数的名字
		.successHandler(formAuthenticationSuccessHandler)//自定义认证成功处理器
		.failureHandler(formAuthenticationFailureHandler);//自定义认证失败处理器
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 100;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
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

	public FormLoginAuthorizeProvider(SecurityProperties securityProperties,
			AuthenticationSuccessHandler formAuthenticationSuccessHandler,
			AuthenticationFailureHandler formAuthenticationFailureHandler) {
		this.securityProperties = securityProperties;
		this.formAuthenticationSuccessHandler = formAuthenticationSuccessHandler;
		this.formAuthenticationFailureHandler = formAuthenticationFailureHandler;
	}

	public FormLoginAuthorizeProvider() {

	}

	
	
}
