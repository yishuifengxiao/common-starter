package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.httpsecurity.authorize.AuthorizeProvider;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * <p>自定义授权配置 </p>
 * 【注意】必须在spring上下文中注入一个名为 customAuthority
 * 的<code>CustomResourceProvider</code>对象
 * 
 * @see CustomResourceProvider
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomAuthorizeProvider implements AuthorizeProvider {


	/**
	 * 实例的名字必须为 <code>customAuthority</code>
	 */
	private CustomResourceProvider customAuthority;

	@Override
	public void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception {

		for (String path : propertyResource.allCustomUrls()) {
			// 自定义权限
			expressionInterceptUrlRegistry.antMatchers(path)
					.access("@customAuthority.hasPermission(request, authentication)");
			expressionInterceptUrlRegistry.mvcMatchers(path)
					.access("@customAuthority.hasPermission(request, authentication)");
		}

	}

	@Override
	public int order() {
		return 500;
	}



	public CustomResourceProvider getCustomAuthority() {
		return customAuthority;
	}

	public void setCustomAuthority(CustomResourceProvider customAuthority) {
		this.customAuthority = customAuthority;
	}

}
