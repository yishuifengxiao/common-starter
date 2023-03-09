package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 异常处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionAuthorizeProvider implements AuthorizeProvider {


    @Override
    public void apply(PropertyResource propertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
        http.exceptionHandling()
		// 定义的不存在access_token时候响应
		.authenticationEntryPoint(authenticationPoint).accessDeniedHandler(authenticationPoint)
		//自定义权限拒绝处理器
		;
		//@formatter:on  
    }

    @Override
    public int order() {
        return 1000;
    }


}
