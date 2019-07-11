/**
 * 
 */
package com.yishuifengxiao.common.security.security.provider.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.security.provider.AuthorizeConfigProvider;

/**
 * 用户登出相关的配置
 * 
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1
 */
@Component
@ConditionalOnMissingBean(name = "loginOutProvider")
public class LoginOutAuthorizeConfigProvider implements AuthorizeConfigProvider {
	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;
	
	/**
	 * 自定义登录退出处理器
	 */
	@Autowired
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

}
