package com.yishuifengxiao.common.security.httpsecurity.impl;

import javax.servlet.Filter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;

/**
 * <strong>验证码拦截器</strong><br/>
 * <br/>
 * 将验证码过滤器配置到系统中，决定系统中的哪些资源需要进行验证码校验
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version v1.0.0
 */
public class CodeValidateInterceptor extends HttpSecurityInterceptor {

	private Filter validateCodeFilter;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(validateCodeFilter, AbstractPreAuthenticatedProcessingFilter.class);
	}

	public Filter getValidateCodeFilter() {
		return validateCodeFilter;
	}

	public void setValidateCodeFilter(Filter validateCodeFilter) {
		this.validateCodeFilter = validateCodeFilter;
	}

	public CodeValidateInterceptor(Filter validateCodeFilter) {
		this.validateCodeFilter = validateCodeFilter;
	}

	public CodeValidateInterceptor() {

	}

}