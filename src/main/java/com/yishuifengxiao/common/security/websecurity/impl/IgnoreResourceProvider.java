package com.yishuifengxiao.common.security.websecurity.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.security.constant.OAuth2Constant;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * 忽视资源授权器
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
@Slf4j
public class IgnoreResourceProvider implements WebSecurityProvider {


	@Override
	public void configure(PropertyResource propertyResource, WebSecurity web) throws Exception {

		log.debug("【易水组件】所有忽视管理的资源的为 {}", StringUtils.join(propertyResource.getAllIgnoreUrls(), " ; "));
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
