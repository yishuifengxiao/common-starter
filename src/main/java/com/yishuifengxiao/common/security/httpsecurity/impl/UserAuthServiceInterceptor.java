package com.yishuifengxiao.common.security.httpsecurity.impl;

import javax.servlet.Filter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.yishuifengxiao.common.security.filter.UserAuthServiceFilter;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;

/**
 * 注入 用户认证逻辑<br/>
 * 将UserAuthServiceFilter注入到spring security过滤器链中
 * 
 * @see UserAuthServiceFilter
 * @author qingteng
 * @date 2020年11月26日
 * @version 1.0.0
 */
public class UserAuthServiceInterceptor extends HttpSecurityInterceptor{
	
	private Filter userAuthServiceFilter;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(userAuthServiceFilter, LogoutFilter.class);
	}

	public Filter getUserAuthServiceFilter() {
		return userAuthServiceFilter;
	}

	public void setUserAuthServiceFilter(Filter userAuthServiceFilter) {
		this.userAuthServiceFilter = userAuthServiceFilter;
	}
	
	

}
