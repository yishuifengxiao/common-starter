package com.yishuifengxiao.common.security;

import com.yishuifengxiao.common.redis.RedisCoreAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityFilterAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityProcessorAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityRedisAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SmsLoginAutoConfiguration;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityManager;
import com.yishuifengxiao.common.security.httpsecurity.AbstractSecurityRequestFilter;
import com.yishuifengxiao.common.security.httpsecurity.SimpleHttpSecurityManager;
import com.yishuifengxiao.common.security.httpsecurity.authorize.rememberme.InMemoryTokenRepository;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.AbstractSecurityGlobalEnhanceFilter;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.support.impl.BaseSecurityHandler;
import com.yishuifengxiao.common.security.support.impl.SimpleAuthenticationPoint;
import com.yishuifengxiao.common.security.support.impl.SimplePropertyResource;
import com.yishuifengxiao.common.security.support.impl.SimpleAbstractSecurityGlobalEnhanceFilter;
import com.yishuifengxiao.common.security.token.TokenUtil;
import com.yishuifengxiao.common.security.token.builder.SimpleTokenBuilder;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.security.token.holder.impl.InMemoryTokenHolder;
import com.yishuifengxiao.common.security.user.encoder.SimplePasswordEncoder;
import com.yishuifengxiao.common.security.user.userdetails.CustomeUserDetailsServiceImpl;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;
import com.yishuifengxiao.common.security.websecurity.provider.FirewallWebSecurityProvider;
import com.yishuifengxiao.common.web.WebEnhanceProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;


import java.util.List;

/**
 * <p>
 * spring security扩展支持自动配置</p
 * <p>
 * 新版文档参见
 * https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
 * </p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class})
@EnableConfigurationProperties({SecurityProperties.class})
@Import({SecurityProcessorAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
        SmsLoginAutoConfiguration.class, SecurityRedisAutoConfiguration.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true")
@AutoConfigureAfter({RedisCoreAutoConfiguration.class})
public class SecurityEnhanceAutoConfiguration {

    /**
     * 注入自定义密码加密类
     *
     * @param propertyResource 资源管理器
     * @return 加密器
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder(PropertyResource propertyResource) {
        return new SimplePasswordEncoder(propertyResource);
    }

    /**
     * <p>
     * 注入用户查找配置类
     * </p>
     * 在系统没有注入UserDetailsService时，注册一个默认的UserDetailsService实例
     *
     * @param passwordEncoder 加密器
     * @return UserDetailsService
     */
    @Bean
    @ConditionalOnMissingBean({UserDetailsService.class})
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new CustomeUserDetailsServiceImpl(passwordEncoder);
    }

    /**
     * 记住密码策略【存储内存中在redis数据库中】
     *
     * @return 记住密码策略
     */
    @Bean
    @ConditionalOnMissingBean(value = {PersistentTokenRepository.class})
    @ConditionalOnMissingClass({"org.springframework.data.redis.core.RedisOperations"})
    public PersistentTokenRepository inMemoryTokenRepository() {
        return new InMemoryTokenRepository();
    }

    /**
     * 注入一个基于内存的token存取工具
     *
     * @return token存取工具
     */
    @Bean
    @ConditionalOnMissingBean(value = {TokenHolder.class})
    @ConditionalOnMissingClass({"org.springframework.data.redis.core.RedisOperations"})
    public TokenHolder tokenHolder() {
        return new InMemoryTokenHolder();
    }


    /**
     * 注入一个资源管理器
     *
     * @param securityProperties 安全属性配置
     * @return 资源管理器
     */
    @Bean
    public PropertyResource propertyResource(SecurityProperties securityProperties, Environment environment) {
        SimplePropertyResource propertyResource = new SimplePropertyResource();
        propertyResource.setSecurityProperties(securityProperties);
        propertyResource.setContextPath(environment.getProperty("server.servlet.context-path"));
        return propertyResource;
    }


    @Bean
    @ConditionalOnMissingBean({TokenUtil.class})
    public TokenUtil tokenUtil(PropertyResource propertyResource, PasswordEncoder passwordEncoder,
                               UserDetailsService userDetailsService, TokenBuilder tokenBuilder,
                               SecurityValueExtractor securityValueExtractor) {
        return new TokenUtil(propertyResource, passwordEncoder, userDetailsService, tokenBuilder,
                securityValueExtractor);
    }

    /**
     * 默认实现的HttpFirewall，主要是解决路径里包含 // 路径报错的问题
     *
     * @return web安全授权器实例
     */
    @Bean("firewallWebSecurityProvider")
    @ConditionalOnMissingBean(name = {"firewallWebSecurityProvider"})
    public WebSecurityProvider firewallWebSecurityProvider() {
        return new FirewallWebSecurityProvider();
    }


    /**
     * 注入一个HttpSecurity安全管理器
     *
     * @param authorizeConfigProviders       系统中所有的授权提供器实例
     * @param abstractSecurityRequestFilters 系统中所有的 web安全授权器实例
     * @param propertyResource               资源管理器
     * @return 安全管理器
     */
    @Bean
    @ConditionalOnMissingBean({HttpSecurityManager.class})
    public HttpSecurityManager httpSecurityManager(List<AuthorizeProvider> authorizeConfigProviders,
                                                   AuthenticationPoint authenticationPoint,
                                                   UserDetailsService userDetailsService,
                                                   List<AbstractSecurityRequestFilter> abstractSecurityRequestFilters,
                                                   PropertyResource propertyResource) {
        SimpleHttpSecurityManager httpSecurityManager = new SimpleHttpSecurityManager(authorizeConfigProviders,
                propertyResource, userDetailsService, authenticationPoint, abstractSecurityRequestFilters);
        httpSecurityManager.afterPropertiesSet();
        return httpSecurityManager;
    }


    /**
     * 注入一个TokenBuilder
     *
     * @param tokenHolder token存取工具
     * @return TokenBuilder实例
     */
    @Bean
    @ConditionalOnMissingBean({TokenBuilder.class})
    public TokenBuilder tokenBuilder(TokenHolder tokenHolder) {
        SimpleTokenBuilder simpleTokenBuilder = new SimpleTokenBuilder();
        simpleTokenBuilder.setTokenHolder(tokenHolder);
        return simpleTokenBuilder;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityHandler securityHandler() {
        BaseSecurityHandler baseSecurityHandler = new BaseSecurityHandler();
        return baseSecurityHandler;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationPoint authenticationPoint(PropertyResource propertyResource,
                                                   SecurityValueExtractor securityValueExtractor,
                                                   SecurityHandler securityHandler,
                                                   WebEnhanceProperties webEnhanceProperties,
                                                   TokenBuilder tokenBuilder) {
        SimpleAuthenticationPoint authenticationPoint = new SimpleAuthenticationPoint();
        authenticationPoint.setPropertyResource(propertyResource);
        authenticationPoint.setTokenBuilder(tokenBuilder);
        authenticationPoint.setSecurityContextExtractor(securityValueExtractor);
        authenticationPoint.setSecurityHandler(securityHandler);
        authenticationPoint.setCorsProperties(webEnhanceProperties.getCors());
        return authenticationPoint;
    }

    /**
     * 错误提示国际化
     *
     * @return AcceptHeaderLocaleResolver
     */
    @Bean
    public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
        return new AcceptHeaderLocaleResolver();
    }

    /**
     * 解决DaoAuthenticationProvider 的hideUserNotFoundExceptions默认为true导致的UsernameNotFoundException被隐藏的问题
     *
     * @param passwordEncoder
     * @param userDetailsService
     * @return
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder,
                                                               UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setPostAuthenticationChecks(new AccountStatusUserDetailsChecker());
        return provider;
    }


    /**
     * <p style="color:red;"> spring security 自定义入口</p>
     *
     * @param http
     * @param httpSecurityManager
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, HttpSecurityManager httpSecurityManager) throws Exception {
        httpSecurityManager.apply(http);
        return http.build();
    }


    /**
     * <p style="color:red;"> spring security 自定义入口</p>
     *
     * @param webSecurityManager
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(List<WebSecurityProvider> webSecurityProviders,
                                                       PropertyResource propertyResource) {

        // 设置忽视的目录
        return web -> webSecurityProviders.stream().forEach(v -> v.configure(propertyResource, web));
    }

    /**
     * <p>获取AuthenticationManager（认证管理器），登录时认证使用</p>
     * <p style="color:red;"> 显示注入一个AuthenticationManager以便兼容全局使用</p>
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 全局增强功能
     *
     * @param propertyResource
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({AbstractSecurityGlobalEnhanceFilter.class})
    public AbstractSecurityGlobalEnhanceFilter securityGlobalEnhance(PropertyResource propertyResource) {
        return new SimpleAbstractSecurityGlobalEnhanceFilter(propertyResource);
    }

    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <安全支持> 相关的配置");
    }

}
