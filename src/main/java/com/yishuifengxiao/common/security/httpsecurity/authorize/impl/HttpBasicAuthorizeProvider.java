package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.yishuifengxiao.common.security.httpsecurity.authorize.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * http basic登陆时的配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpBasicAuthorizeProvider implements AuthorizeProvider {

    /**
     * 异常处理的端点
     */
    private AuthenticationEntryPoint exceptionAuthenticationEntryPoint;

    @Override
    public void config(PropertyResource propertyResource, ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry) throws Exception {
        //@formatter:off
		HttpSecurity http = expressionInterceptUrlRegistry.and();

		// 开启http baisc认证
		if (propertyResource.security().getHttpBasic()) {
			http.httpBasic() // 开启basic认证
					.authenticationEntryPoint(exceptionAuthenticationEntryPoint)
					.realmName(propertyResource.security().getRealmName());
		}
		//@formatter:on  
    }

    @Override
    public int order() {
        return 700;
    }


    public AuthenticationEntryPoint getExceptionAuthenticationEntryPoint() {
        return exceptionAuthenticationEntryPoint;
    }

    public void setExceptionAuthenticationEntryPoint(AuthenticationEntryPoint exceptionAuthenticationEntryPoint) {
        this.exceptionAuthenticationEntryPoint = exceptionAuthenticationEntryPoint;
    }


}
