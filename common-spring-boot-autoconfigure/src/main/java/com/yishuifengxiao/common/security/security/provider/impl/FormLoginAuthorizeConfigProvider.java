/**
 * 
 */
package com.yishuifengxiao.common.security.security.provider.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.security.provider.AuthorizeConfigProvider;

/**
 * spring security表单登录相关的配置
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1 
 */
@Component
@ConditionalOnMissingBean(name = "formLoginProvider")
public class FormLoginAuthorizeConfigProvider implements AuthorizeConfigProvider {
	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;
	
	/**
	 * 自定义认证成功处理器
	 */
	@Autowired
	@Qualifier("formAuthenticationSuccessHandler")
	protected AuthenticationSuccessHandler formAuthenticationSuccessHandler;
	/**
	 * 自定义认证失败处理器
	 */
	@Autowired
	@Qualifier("formAuthenticationFailureHandler")
	protected AuthenticationFailureHandler formAuthenticationFailureHandler;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config)
			throws Exception {
		//@formatter:off 
		config
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

}
