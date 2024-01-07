package com.yishuifengxiao.common.security.httpsecurity.authorize.provider;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
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
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
        http.exceptionHandling(exceptionHandlingCustomizer->{
                    exceptionHandlingCustomizer
                            .authenticationEntryPoint(authenticationPoint)
                            .accessDeniedHandler(authenticationPoint)
                    ;
                })
		;
		//@formatter:on  
    }

    @Override
    public int order() {
        return 1000;
    }


}
