package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceConfigurator;
import com.yishuifengxiao.common.security.httpsecurity.authorize.customizer.*;
import com.yishuifengxiao.common.security.httpsecurity.authorize.session.SessionInformationExpiredStrategyImpl;
import com.yishuifengxiao.common.security.websecurity.WebSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.security.websecurity.customizer.FirewallWebSecurityEnhanceCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.util.Map;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true")
public class SecurityCustomizerAutoConfiguration {


    /**
     * session 失效策略，可以在此方法中记录谁把谁的登陆状态挤掉
     *
     * @return session 失效策略
     */
    @Bean
    @ConditionalOnMissingBean
    public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
        return new SessionInformationExpiredStrategyImpl();
    }

    /**
     * 默认实现的HttpFirewall，主要是解决路径里包含 // 路径报错的问题
     *
     * @return web安全授权器实例
     */
    @Bean("firewallWebSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = {"firewallWebSecurityEnhanceCustomizer"})
    public WebSecurityEnhanceCustomizer firewallWebSecurityEnhanceCustomizer() {
        return new FirewallWebSecurityEnhanceCustomizer();
    }


    /**
     * 保存认证之间的请求
     *
     * @return 授权提供器实例
     */
    @Bean("requestCacheAuthorizeCustomizer")
    @ConditionalOnMissingBean(name = "requestCacheAuthorizeCustomizer")
    public HttpSecurityEnhanceCustomizer requestCacheAuthorizeCustomizer() {
        RequestCacheHttpSecurityEnhanceCustomizer requestCacheAuthorizeCustomizer = new RequestCacheHttpSecurityEnhanceCustomizer();
        return requestCacheAuthorizeCustomizer;
    }

    /**
     * 自定义授权提供器
     *
     * @param customResourceConfigurators 自定义授权提供器
     * @return 授权提供器实例
     */
    @Bean("resourceHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "resourceHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer resourceHttpSecurityEnhanceCustomizer(@Autowired(required = false) Map<String, CustomResourceConfigurator> customResourceConfigurators) {
        ResourceHttpSecurityEnhanceCustomizer resourceHttpSecurityEnhanceCustomizer = new ResourceHttpSecurityEnhanceCustomizer();
        resourceHttpSecurityEnhanceCustomizer.setCustomResourceConfigurators(customResourceConfigurators);
        return resourceHttpSecurityEnhanceCustomizer;
    }

    /**
     * 表单登陆授权管理
     *
     * @return 授权提供器 实例
     */
    @Bean("formLoginHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "formLoginHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer formLoginHttpSecurityEnhanceCustomizer() {
        FormLoginHttpSecurityEnhanceCustomizer formLoginHttpSecurityEnhanceCustomizer = new FormLoginHttpSecurityEnhanceCustomizer();
        return formLoginHttpSecurityEnhanceCustomizer;
    }


    /**
     * 退出授权管理
     *
     * @return 授权提供器 实例
     */
    @Bean("loginOutHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "loginOutHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer loginOutHttpSecurityEnhanceCustomizer() {
        LoginOutHttpSecurityEnhanceCustomizer loginOutHttpSecurityEnhanceCustomizer = new LoginOutHttpSecurityEnhanceCustomizer();
        return loginOutHttpSecurityEnhanceCustomizer;
    }

    /**
     * 记住我授权管理
     *
     * @param persistentTokenRepository token存储器
     * @param userDetailsService        用户认证处理器
     * @return 授权提供器 实例
     */
    @Bean("remeberMeHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "remeberMeHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer remeberMeHttpSecurityEnhanceCustomizer(PersistentTokenRepository persistentTokenRepository, UserDetailsService userDetailsService) {
        RemeberMeHttpSecurityEnhanceCustomizer remeberMeHttpSecurityEnhanceCustomizer = new RemeberMeHttpSecurityEnhanceCustomizer();
        remeberMeHttpSecurityEnhanceCustomizer.setPersistentTokenRepository(persistentTokenRepository);
        remeberMeHttpSecurityEnhanceCustomizer.setUserDetailsService(userDetailsService);
        return remeberMeHttpSecurityEnhanceCustomizer;
    }

    /**
     * session授权管理
     *
     * @param sessionInformationExpiredStrategy session失效策略
     * @return 授权提供器 实例
     */
    @Bean("sessionHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "sessionHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer sessionHttpSecurityEnhanceCustomizer(SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
        SessionHttpSecurityEnhanceCustomizer sessionHttpSecurityEnhanceCustomizer = new SessionHttpSecurityEnhanceCustomizer();
        sessionHttpSecurityEnhanceCustomizer.setSessionInformationExpiredStrategy(sessionInformationExpiredStrategy);
        return sessionHttpSecurityEnhanceCustomizer;
    }


    /**
     * Basic登陆授权提供器
     *
     * @return 授权提供器 实例
     */
    @Bean("httpBasicHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "httpBasicHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer httpBasicAuthorizeCustomizer() {
        HttpBasicHttpSecurityEnhanceCustomizer httpBasicHttpSecurityEnhanceCustomizer = new HttpBasicHttpSecurityEnhanceCustomizer();
        return httpBasicHttpSecurityEnhanceCustomizer;
    }

    /**
     * 异常处理授权提供器
     *
     * @return 授权提供器 实例
     */
    @Bean("exceptionHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "exceptionHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer exceptionAuthorizeCustomizer() {
        ExceptionHttpSecurityEnhanceCustomizer exceptionHttpSecurityEnhanceCustomizer = new ExceptionHttpSecurityEnhanceCustomizer();
        return exceptionHttpSecurityEnhanceCustomizer;
    }

    /**
     * 跨域处理授权提供器
     *
     * @return 授权提供器 实例
     */
    @Bean("corsHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "corsHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer corsHttpSecurityEnhanceCustomizer() {
        CorsHttpSecurityEnhanceCustomizer corsHttpSecurityEnhanceCustomizer = new CorsHttpSecurityEnhanceCustomizer();
        return corsHttpSecurityEnhanceCustomizer;
    }

    /**
     * CSRF处理授权提供器
     *
     * @return 授权提供器 实例
     */
    @Bean("csrfHttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "csrfHttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer csrfHttpSecurityEnhanceCustomizer() {
        CsrfHttpSecurityEnhanceCustomizer csrfHttpSecurityEnhanceCustomizer = new CsrfHttpSecurityEnhanceCustomizer();
        return csrfHttpSecurityEnhanceCustomizer;
    }


}
