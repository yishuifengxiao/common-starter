package com.yishuifengxiao.common.security.websecurity.provider.impl;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.security.constant.OAuth2Constant;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.websecurity.provider.WebSecurityProvider;

/**
 * 忽视资源授权器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class IgnoreResourceProvider implements WebSecurityProvider {


	@Override
	public void configure(PropertyResource propertyResource, WebSecurity web) throws Exception {


		// @formatter:off
		web.ignoring()
		.antMatchers(HttpMethod.OPTIONS, "/**")
		.antMatchers(OAuth2Constant.OAUTH_CHECK_TOKEN)
		//设置忽视目录
		.mvcMatchers(propertyResource.getAllIgnoreUrls())
		.antMatchers(propertyResource.getAllIgnoreUrls())
		;
		// @formatter:on

	}



}
