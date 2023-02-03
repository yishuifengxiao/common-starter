package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import com.yishuifengxiao.common.security.support.SecurityHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * cros授权提供器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CorsAuthorizeProvider implements AuthorizeProvider {


    @Override
    public void apply(PropertyResource propertyResource, SecurityHandler securityHandler, HttpSecurity http)
            throws Exception {
        //@formatter:off
		// 关闭cors保护
		if (propertyResource.security().getCloseCors()) {
			http.cors().disable();
		}
		//@formatter:on  

    }

    @Override
    public int order() {
        return 900;
    }


}
