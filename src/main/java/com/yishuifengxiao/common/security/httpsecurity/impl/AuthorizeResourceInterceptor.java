package com.yishuifengxiao.common.security.httpsecurity.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.matcher.ExcludeRequestMatcher;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * <p>授权资源拦截器</p>

 * 
 * 决定系统中哪些资源需要经过授权系统管理,
 * 
 * 这里采用黑名单匹配规则，除了在黑名单里的资源之外其他的资源都要经过授权管理
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class AuthorizeResourceInterceptor extends HttpSecurityInterceptor {

	private PropertyResource propertyResource;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// 所有的路径都要经过授权
		// 允许将HttpSecurity配置为仅在匹配提供的RequestMatcher时调用。如果需要更高级的配置，请考虑使用requestMatchers（）。
		// 调用requestMatcher（requestMatcher）将覆盖以前对requestMatchers（）、mvcMatcher（字符串）、antMatcher（字符串）、regexMatcher（字符串）和requestMatcher（requestMatcher）的调用。
		http.requestMatcher(new ExcludeRequestMatcher(propertyResource.getExcludeUrls()));

	}

	public PropertyResource getPropertyResource() {
		return propertyResource;
	}

	public void setPropertyResource(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}


}
