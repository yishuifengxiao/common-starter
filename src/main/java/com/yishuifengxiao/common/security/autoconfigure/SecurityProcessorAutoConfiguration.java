package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.httpsecurity.authorize.impl.*;
import com.yishuifengxiao.common.security.httpsecurity.authorize.session.SessionInformationExpiredStrategyImpl;
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
public class SecurityProcessorAutoConfiguration {


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


    @Configuration
    static class SecurityProviderConfiguration {


        /**
         * 自定义授权提供器
         *
         * @param customResourceProviders 自定义授权提供器
         * @return 授权提供器实例
         */
        @Bean("resourceAuthorizeProvider")
        @ConditionalOnMissingBean(name = "resourceAuthorizeProvider")
        public AuthorizeProvider resourceAuthorizeProvider(@Autowired(required = false) Map<String, CustomResourceProvider> customResourceProviders) {
            ResourceAuthorizeProvider resourceAuthorizeProvider = new ResourceAuthorizeProvider();
            resourceAuthorizeProvider.setCustomResourceProviders(customResourceProviders);
            return resourceAuthorizeProvider;
        }

        /**
         * 表单登陆授权管理
         *
         * @return 授权提供器 实例
         */
        @Bean("formLoginProvider")
        @ConditionalOnMissingBean(name = "formLoginProvider")
        public AuthorizeProvider formLoginProvider() {
            FormLoginAuthorizeProvider formLoginProvider = new FormLoginAuthorizeProvider();
            return formLoginProvider;
        }


        /**
         * 退出授权管理
         *
         * @return 授权提供器 实例
         */
        @Bean("loginOutProvider")
        @ConditionalOnMissingBean(name = "loginOutProvider")
        public AuthorizeProvider loginOutProvider() {
            LoginOutAuthorizeProvider loginOutProvider = new LoginOutAuthorizeProvider();
            return loginOutProvider;
        }

        /**
         * 记住我授权管理
         *
         * @param persistentTokenRepository token存储器
         * @param userDetailsService        用户认证处理器
         * @return 授权提供器 实例
         */
        @Bean("remeberMeProvider")
        @ConditionalOnMissingBean(name = "remeberMeProvider")
        public AuthorizeProvider remeberMeProvider(PersistentTokenRepository persistentTokenRepository, UserDetailsService userDetailsService) {
            RemeberMeAuthorizeProvider remeberMeProvider = new RemeberMeAuthorizeProvider();
            remeberMeProvider.setPersistentTokenRepository(persistentTokenRepository);
            remeberMeProvider.setUserDetailsService(userDetailsService);
            return remeberMeProvider;
        }

        /**
         * session授权管理
         *
         * @param sessionInformationExpiredStrategy session失效策略
         * @return 授权提供器 实例
         */
        @Bean("sessionProvider")
        @ConditionalOnMissingBean(name = "sessionProvider")
        public AuthorizeProvider sessionProvider(SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
            SessionAuthorizeProvider sessionProvider = new SessionAuthorizeProvider();
            sessionProvider.setSessionInformationExpiredStrategy(sessionInformationExpiredStrategy);
            return sessionProvider;
        }


        /**
         * Basic登陆授权提供器
         *
         * @return 授权提供器 实例
         */
        @Bean("httpBasicAuthorizeProvider")
        @ConditionalOnMissingBean(name = "httpBasicAuthorizeProvider")
        public AuthorizeProvider httpBasicAuthorizeProvider() {
            HttpBasicAuthorizeProvider httpBasicAuthorizeProvider = new HttpBasicAuthorizeProvider();
            return httpBasicAuthorizeProvider;
        }

        /**
         * 异常处理授权提供器
         *
         * @return 授权提供器 实例
         */
        @Bean("exceptionAuthorizeProvider")
        @ConditionalOnMissingBean(name = "exceptionAuthorizeProvider")
        public AuthorizeProvider exceptionAuthorizeProvider() {
            ExceptionAuthorizeProvider exceptionAuthorizeProvider = new ExceptionAuthorizeProvider();
            return exceptionAuthorizeProvider;
        }

        /**
         * 跨域处理授权提供器
         *
         * @return 授权提供器 实例
         */
        @Bean("corsAuthorizeProvider")
        @ConditionalOnMissingBean(name = "corsAuthorizeProvider")
        public AuthorizeProvider corsAuthorizeProvider() {
            CorsAuthorizeProvider corsAuthorizeProvider = new CorsAuthorizeProvider();
            return corsAuthorizeProvider;
        }

        /**
         * CSRF处理授权提供器
         *
         * @return 授权提供器 实例
         */
        @Bean("csrfAuthorizeProvider")
        @ConditionalOnMissingBean(name = "csrfAuthorizeProvider")
        public AuthorizeProvider csrfAuthorizeProvider() {
            CsrfAuthorizeProvider csrfAuthorizeProvider = new CsrfAuthorizeProvider();
            return csrfAuthorizeProvider;
        }
    }


}
