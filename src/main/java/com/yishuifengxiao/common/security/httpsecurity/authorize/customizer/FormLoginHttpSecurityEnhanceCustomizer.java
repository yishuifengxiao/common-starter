/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.customizer;

import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * spring security表单登录相关的配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class FormLoginHttpSecurityEnhanceCustomizer implements HttpSecurityEnhanceCustomizer {


    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
        http.formLogin(formLoginCustomizer->{
			formLoginCustomizer
					//权限拦截时默认跳转的页面
					.loginPage(securityPropertyResource.security().getLoginPage())
					//处理登录请求的URL
					.loginProcessingUrl(securityPropertyResource.security().getFormActionUrl())
					//用户名参数的名字
					.usernameParameter(securityPropertyResource.security().getUsernameParameter())
					// 密码参数的名字
					.passwordParameter(securityPropertyResource.security().getPasswordParameter())
					//自定义认证成功处理器
					.successHandler(authenticationPoint)
					//自定义认证失败处理器
					.failureHandler(authenticationPoint);
		});

		//@formatter:on  
    }

    @Override
    public int order() {
        return 100;
    }


}
