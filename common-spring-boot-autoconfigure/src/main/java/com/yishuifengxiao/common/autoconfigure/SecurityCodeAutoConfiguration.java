package com.yishuifengxiao.common.autoconfigure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.adapter.SecurityAdapter;
import com.yishuifengxiao.common.security.adapter.impl.CodeConfigAdapter;
import com.yishuifengxiao.common.security.authentcation.SmsAuthenticationSecurityConfig;
import com.yishuifengxiao.common.security.filter.ValidateCodeFilter;
import com.yishuifengxiao.common.validation.holder.CodeProcessorHolder;

/**
 * spring security验证码拦截应用配置
 *
 * @author yishui
 * @version 0.0.1
 * @date 2018年6月15日
 */
@Configuration
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
        WebSecurityConfigurerAdapter.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security.code", name = "enable", havingValue = "true")
public class SecurityCodeAutoConfiguration {

    /**
     * 注入一个验证码过滤器
     *
     * @return
     */
    @Bean("validateCodeFilter")
    @ConditionalOnMissingBean(name = "validateCodeFilter")
    public ValidateCodeFilter validateCodeFilter(AuthenticationFailureHandler authenticationFailureHandler,
                                                 CodeProcessorHolder codeProcessorHolder, SecurityProperties securityProperties) {
        return new ValidateCodeFilter(authenticationFailureHandler, codeProcessorHolder, securityProperties);
    }

    /**
     * 注入一个验证码适配器
     *
     * @param validateCodeFilter
     * @return
     */
    @Bean("codeConfigAdapter")
    @ConditionalOnBean(name = "validateCodeFilter")
    @ConditionalOnMissingBean(name = "codeConfigAdapter")
    public SecurityAdapter codeConfigAdapter(ValidateCodeFilter validateCodeFilter) {
        return new CodeConfigAdapter(validateCodeFilter);

    }

    /**
     * 条件注入短信登录配置
     *
     * @return
     */
    @Bean("smsAuthenticationSecurityConfig")
    @ConditionalOnProperty(prefix = "yishuifengxiao.security.code", name = "smsLoginUrl")
    @ConditionalOnMissingBean(name = "smsAuthenticationSecurityConfig")
    @ConditionalOnBean(name = "smsUserDetailsService")
    public SmsAuthenticationSecurityConfig smsAuthenticationSecurityConfig(
            AuthenticationSuccessHandler authenticationFailureHandler,
            AuthenticationFailureHandler authenticationSuccessHandler, @Qualifier("smsUserDetailsService") UserDetailsService smsUserDetailsService,
            SecurityProperties securityProperties) {
        return new SmsAuthenticationSecurityConfig(authenticationFailureHandler,
                authenticationSuccessHandler, smsUserDetailsService,
                securityProperties.getCode().getSmsLoginUrl());
    }
}
