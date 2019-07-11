/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.provider.AuthorizeConfigProvider;

/**
 * 用户登出相关的配置
 * 
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1
 */
public class LoginOutAuthorizeConfigProvider implements AuthorizeConfigProvider {
	/**
	 * 自定义属性配置
	 */
	protected SecurityProperties securityProperties;
	
	/**
	 * 自定义登录退出处理器
	 */
	protected LogoutSuccessHandler customLogoutSuccessHandler;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) throws Exception {
		//@formatter:off  
		config.and()
		.logout()
		.logoutUrl(securityProperties.getCore().getLoginOutUrl())//退出登陆的URL
		.logoutSuccessHandler(customLogoutSuccessHandler)
		.deleteCookies(securityProperties.getHandler().getExit().getCookieName());//退出时删除cookie
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 200;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public LogoutSuccessHandler getCustomLogoutSuccessHandler() {
		return customLogoutSuccessHandler;
	}

	public void setCustomLogoutSuccessHandler(LogoutSuccessHandler customLogoutSuccessHandler) {
		this.customLogoutSuccessHandler = customLogoutSuccessHandler;
	}

	public LoginOutAuthorizeConfigProvider(SecurityProperties securityProperties,
			LogoutSuccessHandler customLogoutSuccessHandler) {
		this.securityProperties = securityProperties;
		this.customLogoutSuccessHandler = customLogoutSuccessHandler;
	}

	public LoginOutAuthorizeConfigProvider() {

	}
	
	
	

}
