package com.yishuifengxiao.common.security.httpsecurity.authorize.provider;

import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * csrf授权提供器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CsrfAuthorizeProvider implements AuthorizeProvider {


    @Override
    public void apply(PropertyResource propertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http)
            throws Exception {
        //@formatter:off
		// 关闭csrf防护
		if (propertyResource.security().getCloseCsrf()) {
			http.csrf(csrfCustomizer->{
                csrfCustomizer
                        .disable();


            });
		}
		//@formatter:on  

    }

    @Override
    public int order() {
        return 800;
    }


}
