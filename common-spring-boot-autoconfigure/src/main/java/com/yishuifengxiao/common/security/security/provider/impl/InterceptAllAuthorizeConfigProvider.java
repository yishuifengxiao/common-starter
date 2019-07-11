/**
 * 
 */
package com.yishuifengxiao.common.security.security.provider.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.security.provider.AuthorizeConfigProvider;

/**
 * 拦截所有的资源<br/>
 * <strong>注意此过滤器一定要最后加载</strong>
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version 0.0.1
 */
public class InterceptAllAuthorizeConfigProvider implements AuthorizeConfigProvider {

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config)
			throws Exception {
		//只要经过了授权就能访问
		config.anyRequest().authenticated();

	}

	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}

}
