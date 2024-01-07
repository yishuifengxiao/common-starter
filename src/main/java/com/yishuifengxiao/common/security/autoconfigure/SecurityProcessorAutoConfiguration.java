package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeCustomizer;
import com.yishuifengxiao.common.security.httpsecurity.authorize.custom.CustomResourceConfigurator;
import com.yishuifengxiao.common.security.httpsecurity.authorize.customizer.*;
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
         * 保存认证之间的请求
         *
         * @return 授权提供器实例
         */
        @Bean("requestCacheAuthorizeProvider")
        @ConditionalOnMissingBean(name = "requestCacheAuthorizeProvider")
        public AuthorizeCustomizer requestCacheAuthorizeProvider() {
            RequestCacheAuthorizeCustomizer requestCacheAuthorizeProvider = new RequestCacheAuthorizeCustomizer();
            return requestCacheAuthorizeProvider;
        }

        /**
         * 自定义授权提供器
         *
         * @param customResourceProviders 自定义授权提供器
         * @return 授权提供器实例
         */
        @Bean("resourceAuthorizeProvider")
        @ConditionalOnMissingBean(name = "resourceAuthorizeProvider")
        public AuthorizeCustomizer resourceAuthorizeProvider(@Autowired(required = false) Map<String, CustomResourceConfigurator> customResourceProviders) {
            ResourceAuthorizeCustomizer resourceAuthorizeProvider = new ResourceAuthorizeCustomizer();
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
        public AuthorizeCustomizer formLoginProvider() {
            FormLoginAuthorizeCustomizer formLoginProvider = new FormLoginAuthorizeCustomizer();
            return formLoginProvider;
        }


        /**
         * 退出授权管理
         *
         * @return 授权提供器 实例
         */
        @Bean("loginOutProvider")
        @ConditionalOnMissingBean(name = "loginOutProvider")
        public AuthorizeCustomizer loginOutProvider() {
            LoginOutAuthorizeCustomizer loginOutProvider = new LoginOutAuthorizeCustomizer();
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
        public AuthorizeCustomizer remeberMeProvider(PersistentTokenRepository persistentTokenRepository, UserDetailsService userDetailsService) {
            RemeberMeAuthorizeCustomizer remeberMeProvider = new RemeberMeAuthorizeCustomizer();
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
        public AuthorizeCustomizer sessionProvider(SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
            SessionAuthorizeCustomizer sessionProvider = new SessionAuthorizeCustomizer();
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
        public AuthorizeCustomizer httpBasicAuthorizeProvider() {
            HttpBasicAuthorizeCustomizer httpBasicAuthorizeProvider = new HttpBasicAuthorizeCustomizer();
            return httpBasicAuthorizeProvider;
        }

        /**
         * 异常处理授权提供器
         *
         * @return 授权提供器 实例
         */
        @Bean("exceptionAuthorizeProvider")
        @ConditionalOnMissingBean(name = "exceptionAuthorizeProvider")
        public AuthorizeCustomizer exceptionAuthorizeProvider() {
            ExceptionAuthorizeCustomizer exceptionAuthorizeProvider = new ExceptionAuthorizeCustomizer();
            return exceptionAuthorizeProvider;
        }

        /**
         * 跨域处理授权提供器
         *
         * @return 授权提供器 实例
         */
        @Bean("corsAuthorizeProvider")
        @ConditionalOnMissingBean(name = "corsAuthorizeProvider")
        public AuthorizeCustomizer corsAuthorizeProvider() {
            CorsAuthorizeCustomizer corsAuthorizeProvider = new CorsAuthorizeCustomizer();
            return corsAuthorizeProvider;
        }

        /**
         * CSRF处理授权提供器
         *
         * @return 授权提供器 实例
         */
        @Bean("csrfAuthorizeProvider")
        @ConditionalOnMissingBean(name = "csrfAuthorizeProvider")
        public AuthorizeCustomizer csrfAuthorizeProvider() {
            CsrfAuthorizeCustomizer csrfAuthorizeProvider = new CsrfAuthorizeCustomizer();
            return csrfAuthorizeProvider;
        }
    }


}
