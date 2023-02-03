package com.yishuifengxiao.common.security.user;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

/**
 * 默认实现的授权提供者
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleGlobalUserDetails implements GlobalUserDetails {

    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Override

    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(this.userDetailsService);
        authenticationProvider.setPasswordEncoder(this.passwordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        authenticationProvider.setMessageSource(messageSource());
        return authenticationProvider;
    }

    /**
     * 错误提示信息国际化
     *
     * @return ReloadableResourceBundleMessageSource
     */
    private ReloadableResourceBundleMessageSource messageSource() {
        Locale.setDefault(Locale.CHINA);
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath*:messages_zh_CN", "classpath:messages_zh_CN",
                "classpath*:messages_zh_CN.properties", "messages_zh_CN.properties");
        return messageSource;
    }

    public SimpleGlobalUserDetails(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }
}
