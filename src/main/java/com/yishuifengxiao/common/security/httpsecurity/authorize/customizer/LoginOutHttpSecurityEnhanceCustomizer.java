/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.customizer;

import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 用户登出相关的配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class LoginOutHttpSecurityEnhanceCustomizer implements HttpSecurityEnhanceCustomizer {


    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
        http.logout(logoutCustomizer->{
            logoutCustomizer
                    //退出登陆的URL
		            .logoutUrl(securityPropertyResource.security().getLoginOutUrl())
                    .logoutSuccessHandler(authenticationPoint)
                    //退出时删除cookie
                    .deleteCookies(securityPropertyResource.security().getCookieName());
                });

		//@formatter:on
    }

    @Override
    public int order() {
        return 200;
    }


}
