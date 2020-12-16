package com.yishuifengxiao.common.oauth2.interceptor;

import javax.servlet.Filter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;

/**
 * 用于在发生Oauth2请求时提前验证用户密码是否正确
 * 
 * @see TokenEndpointFilter
 * @author qingteng
 * @date 2020年11月23日
 * @version 1.0.0
 */
public class TokenEndpointInterceptor extends HttpSecurityInterceptor {

	private Filter filter;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(filter, LogoutFilter.class);
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

}
