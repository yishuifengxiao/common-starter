package com.yishuifengxiao.common.security;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.yishuifengxiao.common.security.autoconfigure.SecurityFilterAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityProcessorAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityRedisAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SmsLoginAutoConfiguration;
import com.yishuifengxiao.common.security.exception.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityManager;
import com.yishuifengxiao.common.security.httpsecurity.SecurityRequestFilter;
import com.yishuifengxiao.common.security.httpsecurity.SimpleHttpSecurityManager;
import com.yishuifengxiao.common.security.httpsecurity.authorize.rememberme.InMemoryTokenRepository;
import com.yishuifengxiao.common.security.support.HandlerProcessor;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.support.impl.SimplePropertyResource;
import com.yishuifengxiao.common.security.support.impl.SimpleSecurityHelper;
import com.yishuifengxiao.common.security.token.SecurityContextExtractor;
import com.yishuifengxiao.common.security.token.builder.SimpleTokenBuilder;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.security.token.holder.impl.InMemoryTokenHolder;
import com.yishuifengxiao.common.security.user.GlobalUserDetails;
import com.yishuifengxiao.common.security.user.SimpleGlobalUserDetails;
import com.yishuifengxiao.common.security.user.encoder.impl.SimpleBasePasswordEncoder;
import com.yishuifengxiao.common.security.user.userdetails.CustomeUserDetailsServiceImpl;
import com.yishuifengxiao.common.security.utils.TokenUtil;
import com.yishuifengxiao.common.security.websecurity.SimpleWebSecurityManager;
import com.yishuifengxiao.common.security.websecurity.WebSecurityManager;
import com.yishuifengxiao.common.security.websecurity.provider.WebSecurityProvider;
import com.yishuifengxiao.common.security.websecurity.provider.impl.FirewallWebSecurityProvider;
import com.yishuifengxiao.common.social.SocialProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * spring security扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class, WebSecurityConfigurerAdapter.class})
@ConditionalOnBean(AbstractSecurityConfig.class)
@EnableConfigurationProperties({SecurityProperties.class, SocialProperties.class})
@Import({SecurityProcessorAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
        SmsLoginAutoConfiguration.class, SecurityRedisAutoConfiguration.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class SecurityCoreAutoConfiguration {

    /**
     * 注入自定义密码加密类
     *
     * @param propertyResource 资源管理器
     * @return 加密器
     */
    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder(PropertyResource propertyResource) {
        return new SimpleBasePasswordEncoder(propertyResource);
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
     * <p>
     * 提供用户名密码校验能力
     * </p>
     *
     * <pre>
     * 此配置会被AbstractSecurityConfig收集，通过public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception 注入到spring security中
     * </pre>
     *
     * @param userDetailsService UserDetailsService
     * @param passwordEncoder    加密器
     * @return DaoAuthenticationProvider
     */
    @Bean
    @ConditionalOnMissingBean({GlobalUserDetails.class})
    public GlobalUserDetails globalUserDetails(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        GlobalUserDetails globalUserDetails = new SimpleGlobalUserDetails(userDetailsService, passwordEncoder);
        return globalUserDetails;
    }


    /**
     * 记住密码策略【存储内存中在redis数据库中】
     *
     * @return 记住密码策略
     */
    @Bean
    @ConditionalOnMissingBean(name = {"redisTemplate"}, value = {PersistentTokenRepository.class})
    public PersistentTokenRepository inMemoryTokenRepository() {
        return new InMemoryTokenRepository();
    }


    /**
     * 注入一个资源管理器
     *
     * @param securityProperties 安全属性配置
     * @param socialProperties   spring social属性配置
     * @return 资源管理器
     */
    @Bean
    public PropertyResource propertyResource(SecurityProperties securityProperties, SocialProperties socialProperties) {
        SimplePropertyResource propertyResource = new SimplePropertyResource();
        propertyResource.setSecurityProperties(securityProperties);
        propertyResource.setSocialProperties(socialProperties);
        return propertyResource;
    }


    @Bean
    @ConditionalOnMissingBean({SecurityHelper.class})
    public SecurityHelper securityHelper(PropertyResource propertyResource, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, TokenBuilder tokenBuilder) {
        SecurityHelper securityHelper = new SimpleSecurityHelper(propertyResource, userDetailsService, passwordEncoder, tokenBuilder);
        return securityHelper;
    }

    @Bean
    @ConditionalOnMissingBean({TokenUtil.class})
    public TokenUtil tokenUtil(SecurityHelper securityHelper, SecurityContextExtractor securityContextExtractor) {
        return new TokenUtil(securityHelper, securityContextExtractor);
    }

    /**
     * 资源异常处理器
     *
     * @param handlerProcessor 协助处理器
     * @param propertyResource 资源管理器
     * @return 资源异常处理器
     */
    @Bean("exceptionAuthenticationEntryPoint")
    @ConditionalOnMissingBean(name = "exceptionAuthenticationEntryPoint")
    public AuthenticationEntryPoint exceptionAuthenticationEntryPoint(HandlerProcessor handlerProcessor, PropertyResource propertyResource) {
        ExceptionAuthenticationEntryPoint point = new ExceptionAuthenticationEntryPoint();
        point.setHandlerProcessor(handlerProcessor);
        point.setPropertyResource(propertyResource);
        return point;
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
     * @param authorizeConfigProviders 系统中所有的授权提供器实例
     * @param securityRequestFilters   系统中所有的 web安全授权器实例
     * @param propertyResource         资源管理器
     * @return 安全管理器
     */
    @Bean
    @ConditionalOnMissingBean({HttpSecurityManager.class})
    public HttpSecurityManager httpSecurityManager(List<AuthorizeProvider> authorizeConfigProviders, List<SecurityRequestFilter> securityRequestFilters, PropertyResource propertyResource) {
        SimpleHttpSecurityManager httpSecurityManager = new SimpleHttpSecurityManager(authorizeConfigProviders, propertyResource, securityRequestFilters);
        httpSecurityManager.afterPropertiesSet();
        return httpSecurityManager;
    }


    /**
     * 注入一个WebSecurity安全管理器
     *
     * @param webSecurityProviders WebSecurity安全管理提供器
     * @param propertyResource     资源管理器
     * @return 安全管理器
     */
    @Bean
    @ConditionalOnMissingBean({WebSecurityManager.class})
    public WebSecurityManager webSecurityManager(List<WebSecurityProvider> webSecurityProviders, PropertyResource propertyResource) {
        WebSecurityManager webSecurityManager = new SimpleWebSecurityManager(webSecurityProviders, propertyResource);
        return webSecurityManager;
    }


    /**
     * 注入一个基于内存的token存取工具
     *
     * @return token存取工具
     */
    @Bean
    @ConditionalOnMissingBean(name = {"redisTemplate"}, value = {TokenHolder.class})
    public TokenHolder tokenHolder() {
        return new InMemoryTokenHolder();
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


    /**
     * 错误提示国际化
     *
     * @return AcceptHeaderLocaleResolver
     */
    @Bean
    public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
        return new AcceptHeaderLocaleResolver();
    }


    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <安全支持> 相关的配置");
    }

}
