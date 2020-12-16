package com.yishuifengxiao.common.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.provider.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义授权配置 <br/>
 * 【注意】必须在spring上下文中注入一个名为 customAuthority
 * 的<code>CustomResourceProvider</code>对象
 * 
 * @see CustomResourceProvider
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
@Slf4j
public class CustomAuthorizeProvider implements AuthorizeProvider {


	/**
	 * 实例的名字必须为 <code>customAuthority</code>
	 */
	private CustomResourceProvider customAuthority;

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {
		log.debug("【易水组件】需要自定义权限的路径为 {}", propertyResource.getAllCustomUrls());
		for (String path : propertyResource.getAllCustomUrls()) {
			// 自定义权限
			expressionInterceptUrlRegistry.antMatchers(path)
					.access("@customAuthority.hasPermission(request, authentication)");
			expressionInterceptUrlRegistry.mvcMatchers(path)
					.access("@customAuthority.hasPermission(request, authentication)");
		}

	}

	@Override
	public int getOrder() {
		return 500;
	}



	public CustomResourceProvider getCustomAuthority() {
		return customAuthority;
	}

	public void setCustomAuthority(CustomResourceProvider customAuthority) {
		this.customAuthority = customAuthority;
	}

}
