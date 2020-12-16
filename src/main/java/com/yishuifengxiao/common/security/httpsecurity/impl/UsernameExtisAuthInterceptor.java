package com.yishuifengxiao.common.security.httpsecurity.impl;

import javax.servlet.Filter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;

/**
 * 主要是为注入UsernamePasswordAuthFilter<br/>
 * <br/>
 * 用于在UsernamePasswordAuthenticationFilter之前提前校验一下用户名是否已经存在
 * 
 * @see UsernamePasswordAuthFilter
 * @author qingteng
 * @date 2020年11月24日
 * @version 1.0.0
 */
public class UsernameExtisAuthInterceptor extends HttpSecurityInterceptor {

	private Filter usernamePasswordAuthFilter;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(usernamePasswordAuthFilter, UsernamePasswordAuthenticationFilter.class);
	}

	public Filter getUsernamePasswordAuthFilter() {
		return usernamePasswordAuthFilter;
	}

	public void setUsernamePasswordAuthFilter(Filter usernamePasswordAuthFilter) {
		this.usernamePasswordAuthFilter = usernamePasswordAuthFilter;
	}

}
