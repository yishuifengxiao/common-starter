package com.yishuifengxiao.common.security.custom_auth;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeCustomizer;
import com.yishuifengxiao.common.security.custom_auth.sms.SmsAuthenticationFilter;
import com.yishuifengxiao.common.security.custom_auth.sms.SmsAuthenticationProvider;
import com.yishuifengxiao.common.security.custom_auth.sms.SmsUserDetailsService;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <p>短信登陆拦截器</p>
 * <p>
 * 将短信验证码的几个配置参数串联起来 将自定义的短信处理方式配置进spring security，使系统具备通过短信登陆的能力
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmsAuthorizeCustomizer implements AuthorizeCustomizer {


    private SmsUserDetailsService smsUserDetailsService;
    /**
     * 短信登录的URL
     */
    private String url;

    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        SmsAuthenticationFilter smsCodeAuthenticationFilter = new SmsAuthenticationFilter(this.url);
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(authenticationPoint);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(authenticationPoint);

        SmsAuthenticationProvider smsCodeAuthenticationProvider = new SmsAuthenticationProvider();
        smsCodeAuthenticationProvider.setSmsUserDetailsService(smsUserDetailsService);

        http.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    public SmsAuthorizeCustomizer(SmsUserDetailsService smsUserDetailsService, String url) {
        this.smsUserDetailsService = smsUserDetailsService;
        this.url = url;
    }

    public SmsAuthorizeCustomizer() {

    }


}