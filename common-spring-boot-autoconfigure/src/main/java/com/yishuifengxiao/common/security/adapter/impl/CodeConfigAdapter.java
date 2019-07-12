package com.yishuifengxiao.common.security.adapter.impl;

import javax.servlet.Filter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.yishuifengxiao.common.security.adapter.SecurityAdapter;

/**
 * 自定义验证码过滤器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version v1.0.0
 */
public class CodeConfigAdapter extends SecurityAdapter {

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

	public CodeConfigAdapter(Filter validateCodeFilter) {
		this.validateCodeFilter = validateCodeFilter;
	}

	public CodeConfigAdapter() {

	}

}