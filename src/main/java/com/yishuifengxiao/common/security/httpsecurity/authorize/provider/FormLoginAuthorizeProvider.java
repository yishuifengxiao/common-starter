/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.provider;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * spring security表单登录相关的配置
 *
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class FormLoginAuthorizeProvider implements AuthorizeProvider {


    @Override
    public void apply(PropertyResource propertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
        http.formLogin()
		//权限拦截时默认跳转的页面
		.loginPage(propertyResource.security().getLoginPage())
		//处理登录请求的URL
		.loginProcessingUrl(propertyResource.security().getFormActionUrl())
		//用户名参数的名字
		.usernameParameter(propertyResource.security().getUsernameParameter())
		// 密码参数的名字
		.passwordParameter(propertyResource.security().getPasswordParameter())
		//自定义认证成功处理器
		.successHandler(authenticationPoint)
		//自定义认证失败处理器
		.failureHandler(authenticationPoint);
		//@formatter:on  
    }

    @Override
    public int order() {
        return 100;
    }


}
