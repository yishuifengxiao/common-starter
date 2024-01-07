package com.yishuifengxiao.common.security.httpsecurity.authorize.customizer;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeCustomizer;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * http basic登陆时的配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class HttpBasicAuthorizeCustomizer implements AuthorizeCustomizer {


    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
		// 开启http baisc认证
		if (securityPropertyResource.security().getHttpBasic()) {
			http.httpBasic(httpBasicCustomizer->{
                        httpBasicCustomizer
                        .authenticationEntryPoint(authenticationPoint)
                        .realmName(securityPropertyResource.security().getRealmName());
                    });// 开启basic认证

		}
		//@formatter:on  
    }

    @Override
    public int order() {
        return 700;
    }


}
