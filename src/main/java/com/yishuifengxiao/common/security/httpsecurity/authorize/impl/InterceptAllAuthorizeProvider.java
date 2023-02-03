/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * <p>拦截所有的资源</p>
 * <strong>注意此过滤器一定要最后加载</strong>
 *
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class InterceptAllAuthorizeProvider implements AuthorizeProvider {

    @Override
    public void apply(PropertyResource propertyResource, HttpSecurity http)
            throws Exception {
        //只要经过了授权就能访问
        http.authorizeRequests().anyRequest().authenticated();

    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

}
