/**
 * 
 */
package com.yishuifengxiao.common.security.httpsecurity.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.yishuifengxiao.common.security.httpsecurity.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * 用户登出相关的配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class LoginOutAuthorizeProvider implements AuthorizeProvider {

	
	/**
	 * 自定义登录退出处理器
	 */
	protected LogoutSuccessHandler customLogoutSuccessHandler;

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry) throws Exception {
		//@formatter:off  
		expressionInterceptUrlRegistry.and()
		.logout()
		//退出登陆的URL
		.logoutUrl(propertyResource.security().getCore().getLoginOutUrl())
		.logoutSuccessHandler(customLogoutSuccessHandler)
		//退出时删除cookie
		.deleteCookies(propertyResource.security().getCore().getCookieName());
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 200;
	}


	public LogoutSuccessHandler getCustomLogoutSuccessHandler() {
		return customLogoutSuccessHandler;
	}

	public void setCustomLogoutSuccessHandler(LogoutSuccessHandler customLogoutSuccessHandler) {
		this.customLogoutSuccessHandler = customLogoutSuccessHandler;
	}


	
	
	

}
