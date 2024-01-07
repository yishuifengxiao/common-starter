package com.yishuifengxiao.common.security.httpsecurity.authorize.provider;

import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.SecurityPropertyResource;

/**
 * cros授权提供器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CorsAuthorizeProvider implements AuthorizeProvider {


    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http)
            throws Exception {
        //@formatter:off
		// 关闭cors保护
		if (securityPropertyResource.security().getCloseCors()) {
			http.cors(corsCustomizer->{
                corsCustomizer
                        .disable();
            });
		}
		//@formatter:on  

    }

    @Override
    public int order() {
        return 900;
    }


}
