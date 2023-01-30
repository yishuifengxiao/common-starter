package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.httpsecurity.interceptor.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.interceptor.impl.AuthorizeResourceInterceptor;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.thirdauth.SmsLoginInterceptor;
import com.yishuifengxiao.common.security.thirdauth.sms.SmsUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * spring security扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnBean(AbstractSecurityConfig.class)
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
        WebSecurityConfigurerAdapter.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
        "enable"}, havingValue = "true", matchIfMissing = true)
public class SecurityInterceptorAutoConfiguration {

    /**
     * 配置需要拦截哪些资源
     *
     * @param propertyResource 资源管理器
     * @return 资源授权拦截器
     */
    @Bean("authorizeResourceInterceptor")
    @ConditionalOnMissingBean(name = {"authorizeResourceInterceptor"})
    public HttpSecurityInterceptor authorizeResourceInterceptor(PropertyResource propertyResource) {
        AuthorizeResourceInterceptor authorizeResourceProvider = new AuthorizeResourceInterceptor();
        authorizeResourceProvider.setPropertyResource(propertyResource);
        return authorizeResourceProvider;
    }


    /**
     * 注入短信登录配置
     * <p>
     * 配置短信验证码登陆功能
     * </p>
     * 要想使短信验证码功能生效，需要配置： 1
     * 先配置一个短信登陆地址属性(<code>yishuifengxiao.security.code.sms-login-url</code>), 2
     * 再配置一个名为 smsUserDetailsService 的 <code>UserDetailsService</code> 实例
     *
     * @param authenticationFailureHandler 认证失败处理器
     * @param authenticationSuccessHandler 认证成功处理器
     * @param smsUserDetailsService        短信登陆逻辑
     * @param securityProperties           安全属性配置
     * @return 资源授权拦截器实例
     */
    @Bean("smsLoginInterceptor")
    @ConditionalOnProperty(prefix = "yishuifengxiao.security.code", name = "sms-login-url")
    @ConditionalOnMissingBean(name = "smsLoginInterceptor")
    @ConditionalOnBean({SmsUserDetailsService.class})
    public HttpSecurityInterceptor smsLoginInterceptor(AuthenticationSuccessHandler authenticationFailureHandler,
                                                       AuthenticationFailureHandler authenticationSuccessHandler,
                                                       SmsUserDetailsService smsUserDetailsService,
                                                       SecurityProperties securityProperties) {

        return new SmsLoginInterceptor(authenticationFailureHandler, authenticationSuccessHandler, smsUserDetailsService,
                securityProperties.getCode().getSmsLoginUrl());
    }


}
