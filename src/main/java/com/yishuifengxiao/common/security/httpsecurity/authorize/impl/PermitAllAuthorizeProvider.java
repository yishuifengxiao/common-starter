/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * AuthorizeConfigProvider的默认配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */

public class PermitAllAuthorizeProvider implements AuthorizeProvider {

    @Override
    public void apply(PropertyResource propertyResource, HttpSecurity http) throws Exception {

        // 所有直接放行的资源
        final ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();

        for (String url : propertyResource.anonymousUrls()) {
            registry.antMatchers(url).anonymous();
        }
        for (String url : propertyResource.allPermitUrs()) {
            registry.antMatchers(url).permitAll();
        }
    }

    @Override
    public int order() {
        return 600;
    }

}
