package com.yishuifengxiao.common.security.websecurity.provider.impl;



import jakarta.servlet.DispatcherType;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

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
	public void configure(PropertyResource propertyResource, WebSecurity web) {

		// @formatter:off
		web.ignoring()
				// 忽视OPTIONS请求
				.requestMatchers(HttpMethod.OPTIONS)
				//设置忽视目录
				.requestMatchers(propertyResource.allIgnoreUrls())
				// 忽视 ERROR 当容器将处理传递给错误处理程序机制（如定义的错误页）时。
				.dispatcherTypeMatchers(DispatcherType.ERROR)
		;
		// @formatter:on
	}

}
