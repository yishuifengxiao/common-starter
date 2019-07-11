/**
 * 
 */
package com.yishuifengxiao.common.security.security.provider.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.security.provider.AuthorizeConfigProvider;

/**
 * spring security记住我功能而定相关配置
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1 
 */
@Component
@ConditionalOnMissingBean(name = "remeberMeProvider")
public class RemeberMeAuthorizeConfigProvider implements AuthorizeConfigProvider {
	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;
	/**
	 * 记住我功能的实现
	 */
	@Autowired
	protected PersistentTokenRepository persistentTokenRepository;
	
	/**
	 * 自定义UserDetailsService实现类，查找用户
	 */
	@Autowired
	protected UserDetailsService userDetailsService;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config)
			throws Exception {
		//@formatter:off 
		config
		.and()
		.rememberMe()//记住我的功能
		.tokenRepository(persistentTokenRepository)//记住我的实现
		.tokenValiditySeconds(securityProperties.getCore().getRememberMeSeconds())//记住我的时间
		.userDetailsService(userDetailsService);//记住我的验证逻辑
		//@formatter:on  
	}

	@Override
	public int getOrder() {
		return 300;
	}

}
