package com.yishuifengxiao.common.security.provider.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.authorize.custom.CustomAuthority;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;

/**
 * 自定义授权配置 <br/>
 * 【注意】必须在spring上下文中注入一个名为 customAuthority 的对象
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
public class CustomAuthorizeProvider implements AuthorizeProvider {

	private Logger log = LoggerFactory.getLogger(CustomAuthorizeProvider.class);

	/**
	 * 自定义属性配置
	 */
	protected SecurityProperties securityProperties;
    /**
     * 实例的名字必须为 <code>customAuthority</code>
     */
	private CustomAuthority customAuthority;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		log.debug("【自定义权限】需要自定义权限的路径为 {}", securityProperties.getCustom().getAll());
		for (String path : securityProperties.getCustom().getAll()) {
			// 自定义权限
			expressionInterceptUrlRegistry.antMatchers(path).access("@customAuthority.hasPermission(request, authentication)");
			expressionInterceptUrlRegistry.mvcMatchers(path).access("@customAuthority.hasPermission(request, authentication)");
		}

	}

	@Override
	public int getOrder() {
		return 500;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public CustomAuthority getCustomAuthority() {
		return customAuthority;
	}

	public void setCustomAuthority(CustomAuthority customAuthority) {
		this.customAuthority = customAuthority;
	}

}
