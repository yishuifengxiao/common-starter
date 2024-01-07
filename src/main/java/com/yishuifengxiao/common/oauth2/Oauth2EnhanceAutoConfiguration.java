package com.yishuifengxiao.common.oauth2;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.yishuifengxiao.common.oauth2.authorization.RedisOAuth2AuthorizationConsentService;
import com.yishuifengxiao.common.oauth2.authorization.RedisOAuth2AuthorizationService;
import com.yishuifengxiao.common.oauth2.client.SimpleRegisteredClientRepository;
import com.yishuifengxiao.common.oauth2.impl.OAuth2AuthorizationEndpointEnhanceFilter;
import com.yishuifengxiao.common.oauth2.impl.SimpleOAuth2AuthorizationProvider;
import com.yishuifengxiao.common.oauth2.customizer.OAuth2AuthorizeCustomizer;
import com.yishuifengxiao.common.oauth2.support.Oauth2SecurityGlobalEnhanceFilter;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeCustomizer;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * <p>oauth2增强配置</p>
 * <p>
 * The OAuth 2.0 Authorization Framework 相关解释参见
 * <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1</a>
 * </p>
 * <p>迁移文档参见
 * <a href="https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide">https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide</a></p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({OAuth2AccessToken.class, WebMvcConfigurer.class})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties({Oauth2Properties.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true")
public class Oauth2EnhanceAutoConfiguration {

    /**
     * <p>用于自定义OAuth2授权服务器配置设置的AuthorizationServerSettings（必需）</p>
     * <p>默认的配置示例为</p>
     * <pre>
     *      <code>
     *                  return new Builder()
     *                 .authorizationEndpoint("/oauth2/authorize")
     *                 .tokenEndpoint("/oauth2/token")
     *                 .tokenIntrospectionEndpoint("/oauth2/introspect")
     *                 .tokenRevocationEndpoint("/oauth2/revoke")
     *                 .jwkSetEndpoint("/oauth2/jwks")
     *                 .oidcUserInfoEndpoint("/userinfo")
     *                 .oidcClientRegistrationEndpoint("/connect/register");
     *      </code>
     *  </pre>
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({AuthorizationServerSettings.class})
    public AuthorizationServerSettings authorizationServerSettings() {


        return AuthorizationServerSettings.builder().build();
    }

    /**
     * <p style="color:red">RegisteredClientRepository是必需的组件。</p>
     * <p>RegisteredClientRepository是可以注册新客户端和查询现有客户端的中心组件。
     * 其他组件在遵循特定协议流时使用它，如客户端身份验证、授权授权处理、令牌内省、动态客户端注册等。</p>
     *
     * @return RegisteredClientRepository
     */
    @Bean
    @ConditionalOnMissingBean({RegisteredClientRepository.class})
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) throws Exception {
        SimpleRegisteredClientRepository clientRepository = new SimpleRegisteredClientRepository();
        clientRepository.setPasswordEncoder(passwordEncoder);
        clientRepository.afterPropertiesSet();
        return clientRepository;
    }


    @Bean
    @ConditionalOnMissingBean({OAuth2AuthorizationProvider.class})
    public OAuth2AuthorizationProvider auth2AuthorizationProvider(RegisteredClientRepository registeredClientRepository, AuthorizationServerSettings authorizationServerSettings, AuthenticationPoint authenticationPoint, OAuth2AuthorizationService authorizationService, OAuth2AuthorizationConsentService authorizationConsentService, Oauth2Properties oauth2Properties) {
        OAuth2AuthorizationProvider auth2AuthorizationProvider =
                new SimpleOAuth2AuthorizationProvider(registeredClientRepository, authorizationServerSettings,
                        authenticationPoint, authorizationService, authorizationConsentService, oauth2Properties);
        return auth2AuthorizationProvider;
    }

    // @formatter:off
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnProperty(prefix = "yishuifengxiao.security.oauth2", name = {"enable"}, havingValue = "true")
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      AuthenticationConfiguration authenticationConfiguration,
                                                                      AuthenticationPoint authenticationPoint,
                                                                      OAuth2AuthorizationProvider auth2AuthorizationProvider) throws Exception {

//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http.securityMatcher(endpointsMatcher).authorizeHttpRequests((authorize) -> {
            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)authorize.anyRequest()).authenticated();
        }).csrf((csrf) -> {
            csrf.ignoringRequestMatchers(new RequestMatcher[]{endpointsMatcher});
        }).apply(authorizationServerConfigurer);



        //应用自定义配置
        auth2AuthorizationProvider.apply(authorizationServerConfigurer);

        http.addFilterBefore(new OAuth2AuthorizationEndpointEnhanceFilter(authorizationServerConfigurer,authenticationPoint)   ,
                ExceptionTranslationFilter.class);

        return http.build();
    }
    // @formatter:on

    @Bean("oAuth2AuthorizationProvider")
    @ConditionalOnMissingBean(name = "oAuth2AuthorizationProvider")
    public AuthorizeCustomizer oAuth2AuthorizationProvider(AuthenticationConfiguration authenticationConfiguration,
                                                           AuthenticationPoint authenticationPoint) throws Exception {
        return new OAuth2AuthorizeCustomizer(authenticationPoint);
    }


    @Configuration
    @ConditionalOnClass({RedisOperations.class})
    public class Oauth2RedisExtendAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean({OAuth2AuthorizationService.class})
        public OAuth2AuthorizationService redisOAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate
                , RegisteredClientRepository registeredClientRepository) {
            return new RedisOAuth2AuthorizationService(redisTemplate, registeredClientRepository);
        }


        @Bean
        @ConditionalOnMissingBean({OAuth2AuthorizationConsentService.class})
        public OAuth2AuthorizationConsentService redisOAuth2AuthorizationConsentService(RedisTemplate<String, Object> redisTemplate) {
            return new RedisOAuth2AuthorizationConsentService(redisTemplate);
        }

    }

    @Configuration
    @ConditionalOnMissingClass({"org.springframework.data.redis.core.RedisOperations"})
    public class Oauth2InMemoryExtendAutoConfiguration {

        @ConditionalOnMissingBean({OAuth2AuthorizationService.class})
        @Bean
        public OAuth2AuthorizationService inMemoryOAuth2AuthorizationService() {
            return new InMemoryOAuth2AuthorizationService();
        }

        @ConditionalOnMissingBean({OAuth2AuthorizationConsentService.class})
        @Bean
        public OAuth2AuthorizationConsentService inMemoryOAuth2AuthorizationConsentService() {
            return new InMemoryOAuth2AuthorizationConsentService();
        }

    }

    @Bean
    @ConditionalOnMissingBean({JWKSource.class})
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey =
                new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }


    @Bean
    @ConditionalOnMissingBean({JwtDecoder.class})
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }


    @Bean("oauth2SecurityGlobalEnhanceFilter")
    @ConditionalOnMissingBean(name = "oauth2SecurityGlobalEnhanceFilter")
    public Filter oauth2SecurityGlobalEnhanceFilter(RegisteredClientRepository registeredClientRepository,
                                                    OAuth2AuthorizationConsentService authorizationConsentService,
                                                    Oauth2Properties oauth2Properties,
                                                    AuthorizationServerSettings authorizationServerSettings) {
        return new Oauth2SecurityGlobalEnhanceFilter(oauth2Properties, registeredClientRepository,
                authorizationConsentService, authorizationServerSettings);
    }
}
