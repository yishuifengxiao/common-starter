package com.yishuifengxiao.common.security.custom_auth.sms;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 实现自定义SmsCodeAuthenticationProvider
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmsAuthenticationProvider implements AuthenticationProvider {
    private SmsUserDetailsService smsUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        SmsAuthenticationToken smsCodeAuthenticationToken = (SmsAuthenticationToken) authentication;

        UserDetails userDetails = smsUserDetailsService
                .loadUserByUsername((String) smsCodeAuthenticationToken.getPrincipal());

        if (userDetails == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        SmsAuthenticationToken smsCodeAuthenticationResult = new SmsAuthenticationToken(
                (String) smsCodeAuthenticationToken.getPrincipal(), userDetails.getAuthorities());

        smsCodeAuthenticationResult.setDetails(smsCodeAuthenticationToken.getDetails());
        return smsCodeAuthenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 确定是否调用这个方法
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public SmsUserDetailsService getSmsUserDetailsService() {
        return smsUserDetailsService;
    }

    public void setSmsUserDetailsService(SmsUserDetailsService smsUserDetailsService) {
        this.smsUserDetailsService = smsUserDetailsService;
    }


}