/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;

/**
 * AuthorizeConfigProvider的默认配置
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
public class PermitAllAuthorizeProvider implements AuthorizeProvider {
	/**
	 * 自定义属性配置
	 */
	protected SecurityProperties securityProperties;
	

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry) {
		// @formatter:off
		expressionInterceptUrlRegistry.antMatchers(
				"/oauth/token", 
				"/oauth/check_token",
				 // 权限拦截时默认的跳转地址
				securityProperties.getCore().getRedirectUrl(),
				// 登陆页面的URL
				securityProperties.getCore().getLoginPage(), 
				// 登陆页面表单提交地址
				securityProperties.getCore().getFormActionUrl(), 
				//退出页面
				securityProperties.getCore().getLoginOutUrl(),
				 //session失效时跳转的页面
				securityProperties.getSession().getSessionInvalidUrl()
				)
		       // 登出页面的地址
				.permitAll()
		;

	}

	@Override
	public int getOrder() {
		return 600;
	}

	public PermitAllAuthorizeProvider(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public PermitAllAuthorizeProvider() {

	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}
	
	

}
