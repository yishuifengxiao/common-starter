package com.yishuifengxiao.common.security.smsauth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.yishuifengxiao.common.security.httpsecurity.authorize.AbstractAuthorizeProvider;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.smsauth.sms.SmsAuthenticationFilter;
import com.yishuifengxiao.common.security.smsauth.sms.SmsAuthenticationProvider;
import com.yishuifengxiao.common.security.smsauth.sms.SmsUserDetailsService;

/**
 * <p>短信登陆拦截器</p>
 * <p>
 * 将短信验证码的几个配置参数串联起来 将自定义的短信处理方式配置进spring security，使系统具备通过短信登陆的能力
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmsAuthorizeProvider extends AbstractAuthorizeProvider {


    private SmsUserDetailsService smsUserDetailsService;
    /**
     * 短信登录的URL
     */
    private String url;

    @Override
    public void configure(HttpSecurity http, AuthenticationPoint authenticationPoint) throws Exception {

        SmsAuthenticationFilter smsCodeAuthenticationFilter = new SmsAuthenticationFilter(this.url);
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationPoint);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationPoint);

        SmsAuthenticationProvider smsCodeAuthenticationProvider = new SmsAuthenticationProvider();
        smsCodeAuthenticationProvider.setSmsUserDetailsService(smsUserDetailsService);

        http.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

    public SmsAuthorizeProvider(SmsUserDetailsService smsUserDetailsService, String url) {
        this.smsUserDetailsService = smsUserDetailsService;
        this.url = url;
    }

    public SmsAuthorizeProvider() {

    }

}