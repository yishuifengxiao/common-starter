/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.impl;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * spring security并发登录相关的配置
 *
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SessionAuthorizeProvider implements AuthorizeProvider {


    /**
     * session失效后的处理策略
     */
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    @Override
    public void apply(PropertyResource propertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
        http.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
		//定义AuthenticationFailureHandler，它将在SessionAuthenticationStrategy引发异常时使用。
		//如果未设置，将向客户端返回未经授权的（402）错误代码。
		//请注意，如果在基于表单的登录期间发生错误，则此属性不会发生，其中URL身份验证失败将优先
		.sessionAuthenticationFailureHandler(authenticationPoint)
		//.invalidSessionUrl(securityProperties.getSession().getSessionInvalidUrl()) //session过期时的跳转的url
		//同一个用户最大的session数量
		.maximumSessions(propertyResource.security().getSession().getMaximumSessions())
		//session数量达到最大时，是否阻止第二个用户登陆
		.maxSessionsPreventsLogin(propertyResource.security().getSession().isMaxSessionsPreventsLogin())
		//.invalidSessionUrl(customProperties.getSecurity().getSession().getSessionInvalidUrl())//session过期后的跳转
		//session过期时的处理策略
		.expiredSessionStrategy(sessionInformationExpiredStrategy)
		;
		//@formatter:on  
    }

    @Override
    public int order() {
        return 400;
    }


    public SessionInformationExpiredStrategy getSessionInformationExpiredStrategy() {
        return sessionInformationExpiredStrategy;
    }

    public void setSessionInformationExpiredStrategy(SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
        this.sessionInformationExpiredStrategy = sessionInformationExpiredStrategy;
    }

}
