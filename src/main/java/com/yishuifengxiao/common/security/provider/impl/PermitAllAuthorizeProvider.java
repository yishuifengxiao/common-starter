/**
 * 
 */
package com.yishuifengxiao.common.security.provider.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;

import lombok.extern.slf4j.Slf4j;

/**
 * AuthorizeConfigProvider的默认配置
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
@Slf4j
public class PermitAllAuthorizeProvider implements AuthorizeProvider {

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry) {

		log.debug("【易水组件】所有直接放行的资源的为 {}", StringUtils.join(propertyResource.getAllPermitUlrs(), " ; "));

		// 所有直接放行的资源
		for (String url : propertyResource.getAllPermitUlrs()) {
			expressionInterceptUrlRegistry.antMatchers(url).permitAll();
		}

	}

	@Override
	public int getOrder() {
		return 600;
	}

}
