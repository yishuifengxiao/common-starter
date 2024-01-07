package com.yishuifengxiao.common.security.httpsecurity.authorize.customizer;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeCustomizer;
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
public class ExceptionAuthorizeCustomizer implements AuthorizeCustomizer {


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
