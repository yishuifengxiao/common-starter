package com.yishuifengxiao.common.oauth2;

import com.yishuifengxiao.common.oauth2.filter.TokenEndpointFilter;
import com.yishuifengxiao.common.oauth2.filter.extractor.CustomTokenExtractor;
import com.yishuifengxiao.common.oauth2.support.OAuth2TokenUtil;
import com.yishuifengxiao.common.oauth2.token.TokenStrategy;
import com.yishuifengxiao.common.oauth2.token.enhancer.CustomeTokenEnhancer;
import com.yishuifengxiao.common.oauth2.token.impl.TokenStrategyImpl;
import com.yishuifengxiao.common.oauth2.translator.AuthWebResponseExceptionTranslator;
import com.yishuifengxiao.common.oauth2.user.ClientDetailsServiceImpl;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeHelper;
import com.yishuifengxiao.common.web.error.ErrorHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

/**
 * oauth2扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
@Slf4j
@Configuration
@ConditionalOnClass({OAuth2AccessToken.class, WebMvcConfigurer.class, EnableAuthorizationServer.class})
@ConditionalOnBean({ AuthorizationServerEndpointsConfiguration.class})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties({Oauth2Properties.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true", matchIfMissing = false)
public class Oauth2ExtendAutoConfiguration {


    @ConditionalOnMissingBean({TokenStore.class})
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean("customClientDetailsService")
    @ConditionalOnMissingBean(name = "customClientDetailsService")
    public ClientDetailsService customClientDetailsService(PasswordEncoder passwordEncoder) {
        ClientDetailsServiceImpl customClientDetailsService = new ClientDetailsServiceImpl();
        customClientDetailsService.setPasswordEncoder(passwordEncoder);
        return customClientDetailsService;
    }

    /**
     * <p>
     * 必须加入，不然自定义权限表达式不生效
     * </p>
     * <p>
     * 在 Oauth2Resource 中被public void configure(ResourceServerSecurityConfigurer
     * resources)收集并配置
     *
     * @param applicationContext spring上下文
     * @return DefaultWebSecurityExpressionHandler
     */
    @Bean
    public DefaultWebSecurityExpressionHandler expressionHandler(ApplicationContext applicationContext) {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;

    }

    /**
     * 注入token加强工具
     *
     * @return token加强工具
     */
    @Bean
    @ConditionalOnMissingBean({TokenEnhancer.class})
    public TokenEnhancer tokenEnhancer() {
        return new CustomeTokenEnhancer();
    }

    /**
     * 自定义token提取器
     *
     * @return 自定义token提取器
     */
    @Bean
    @ConditionalOnMissingBean(value = {TokenExtractor.class})
    public TokenExtractor tokenExtractor() {
        return new CustomTokenExtractor();
    }

    /**
     * token自动续签策略工具
     *
     * @param tokenStore                       token存取器
     * @param authorizationServerTokenServices AuthorizationServerTokenServices实例
     * @return token自动续签策略工具
     */
    @Bean
    @ConditionalOnMissingBean({TokenStrategy.class})
    public TokenStrategy tokenStrategy(TokenStore tokenStore, AuthorizationServerTokenServices authorizationServerTokenServices) {
        TokenStrategyImpl tokenStrategy = new TokenStrategyImpl();
        tokenStrategy.setAuthorizationServerTokenServices(authorizationServerTokenServices);
        tokenStrategy.setTokenStore(tokenStore);
        return tokenStrategy;
    }

    /**
     * token生成工具
     *
     * @param clientDetailsService             ClientDetailsService
     * @param authorizationServerTokenServices AuthorizationServerTokenServices
     * @param tokenExtractor                   token提取器
     * @param consumerTokenServices            ConsumerTokenServices
     * @param userDetailsService               UserDetailsService
     * @param passwordEncoder                  密码加密器
     * @return token生成工具
     */
    @Bean
    @ConditionalOnMissingBean({OAuth2TokenUtil.class})
    public OAuth2TokenUtil oAuth2TokenUtil(@Qualifier("customClientDetailsService") ClientDetailsService clientDetailsService, AuthorizationServerTokenServices authorizationServerTokenServices, TokenExtractor tokenExtractor, ConsumerTokenServices consumerTokenServices, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        OAuth2TokenUtil tokenUtils = new OAuth2TokenUtil(clientDetailsService, authorizationServerTokenServices, consumerTokenServices, userDetailsService, passwordEncoder, tokenExtractor);
        return tokenUtils;
    }


    /**
     * Basic interface for determining whether a given userService authentication request
     * has been approved by the current user. 【认证服务器中需要显示使用到】
     *
     * @param tokenStore           token存取器
     * @param clientDetailsService ClientDetailsService
     * @return TokenStoreUserApprovalHandler
     */
    @Bean
    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore,
                                                             @Qualifier("customClientDetailsService") ClientDetailsService clientDetailsService) {
        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
        handler.setTokenStore(tokenStore);
        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
        handler.setClientDetailsService(clientDetailsService);
        return handler;
    }

    /**
     * Interface for saving, retrieving and revoking user approvals (per userService, per
     * scope).
     *
     * @param tokenStore token存取器
     * @return ApprovalStore
     */
    @Bean
    public ApprovalStore approvalStore(TokenStore tokenStore) {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore);
        return store;
    }

    /**
     * Oauth2Server中用于异常转换
     *
     * @return WebResponseExceptionTranslator
     */
    @Bean("authWebResponseExceptionTranslator")
    @ConditionalOnMissingBean(name = "authWebResponseExceptionTranslator")
    public WebResponseExceptionTranslator<OAuth2Exception> authWebResponseExceptionTranslator(@Autowired(required = false) ErrorHelper errorHelper) {
        AuthWebResponseExceptionTranslator authWebResponseExceptionTranslator = new AuthWebResponseExceptionTranslator(errorHelper);
        return authWebResponseExceptionTranslator;
    }

    /**
     * 配置一个过滤器，用于在oauth2中提前验证用户名和密码以及clientId
     *
     * @param securityHandler     协助处理器
     * @param propertyResource     资源管理器
     * @param authorizeHelper       安全信息处理器
     * @param clientDetailsService ClientDetailsService
     * @param passwordEncoder      加密器
     * @param oauth2Properties     oauth2扩展支持属性配置
     * @return 过滤器
     */
    @Bean("tokenEndpointAuthenticationFilter")
    @ConditionalOnMissingBean(name = "tokenEndpointAuthenticationFilter")
    public Filter tokenEndpointAuthenticationFilter(SecurityHandler securityHandler, PropertyResource propertyResource, AuthorizeHelper authorizeHelper,
                                                    ClientDetailsService clientDetailsService, PasswordEncoder passwordEncoder, Oauth2Properties oauth2Properties) {
        TokenEndpointFilter tokenEndpointFilter = new TokenEndpointFilter(securityHandler, propertyResource, authorizeHelper, clientDetailsService, passwordEncoder, oauth2Properties);
        return tokenEndpointFilter;
    }


    @ConditionalOnClass({RedisOperations.class})
    @Configuration
    public static class RedisExtend {

        @ConditionalOnMissingBean({TokenStore.class})
        @Bean
        public TokenStore tokenStore(RedisConnectionFactory connectionFactory) {
            return new RedisTokenStore(connectionFactory);
        }
    }


    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <Oauth2扩展支持> 相关的配置");
    }

}
