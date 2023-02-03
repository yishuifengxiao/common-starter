/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 用户登出相关的配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class LoginOutAuthorizeProvider implements AuthorizeProvider {


    @Override
    public void apply(PropertyResource propertyResource, SecurityHandler securityHandler, HttpSecurity http) throws Exception {
        //@formatter:off
        http.logout()
		//退出登陆的URL
		.logoutUrl(propertyResource.security().getLoginOutUrl())
		.logoutSuccessHandler(securityHandler)
		//退出时删除cookie
		.deleteCookies(propertyResource.security().getCookieName());
		//@formatter:on
    }

    @Override
    public int order() {
        return 200;
    }




}
