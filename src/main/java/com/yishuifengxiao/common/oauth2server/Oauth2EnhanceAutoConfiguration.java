package com.yishuifengxiao.common.oauth2server;

import com.yishuifengxiao.common.oauth2server.autoconfigure.Oauth2EnhanceExtendAutoConfiguration;
import com.yishuifengxiao.common.oauth2server.client.SimpleRegisteredClientRepository;
import com.yishuifengxiao.common.oauth2server.customizer.OAuth2HttpSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.oauth2server.impl.OAuth2AuthorizationEndpointEnhanceFilter;
import com.yishuifengxiao.common.oauth2server.impl.SimpleOAuth2AuthorizationProvider;
import com.yishuifengxiao.common.oauth2server.support.Oauth2SecurityGlobalEnhanceFilter;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
@Import({Oauth2EnhanceExtendAutoConfiguration.class})
public class Oauth2EnhanceAutoConfiguration {

    /**
     * <p style="color:green">用于自定义OAuth2授权服务器配置设置的AuthorizationServerSettings（必需）</p>
     * <p>默认的配置示例为</p>
     * <pre>
     *      <code>
     * return new Builder()
     * .authorizationEndpoint("/oauth2server/authorize")
     * .deviceAuthorizationEndpoint("/oauth2server/device_authorization")
     * .deviceVerificationEndpoint("/oauth2server/device_verification")
     * .tokenEndpoint("/oauth2server/token")
     * .jwkSetEndpoint("/oauth2server/jwks")
     * .tokenRevocationEndpoint("/oauth2server/revoke")
     * .tokenIntrospectionEndpoint("/oauth2server/introspect")
     * .oidcClientRegistrationEndpoint("/connect/register")
     * .oidcUserInfoEndpoint("/userinfo")
     * .oidcLogoutEndpoint("/connect/logout");
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
     * <p style="color:green">RegisteredClientRepository是必需的组件。</p>
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


    // @formatter:off
    @Bean
    @ConditionalOnMissingBean({OAuth2AuthorizationProvider.class})
    public OAuth2AuthorizationProvider auth2AuthorizationProvider(RegisteredClientRepository registeredClientRepository,
                                                                  AuthorizationServerSettings authorizationServerSettings,
                                                                  AuthenticationPoint authenticationPoint,
                                                                  OAuth2AuthorizationService authorizationService,
                                                                  OAuth2AuthorizationConsentService authorizationConsentService,
                                                                  Oauth2Properties oauth2Properties,
                                                                  JwtEncoder jwtEncoder) {
        // @formatter:on
        OAuth2AuthorizationProvider auth2AuthorizationProvider =
                new SimpleOAuth2AuthorizationProvider(registeredClientRepository,
                        authorizationServerSettings,
                        authenticationPoint, authorizationService, authorizationConsentService,
                        oauth2Properties, jwtEncoder);
        return auth2AuthorizationProvider;
    }


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnProperty(prefix = "yishuifengxiao.security.oauth2server", name = {"enable"},
            havingValue = "true")
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      AuthenticationPoint authenticationPoint,
                                                                      OAuth2AuthorizationProvider auth2AuthorizationProvider) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        http.apply(authorizationServerConfigurer);

        //应用自定义配置
        auth2AuthorizationProvider.apply(authorizationServerConfigurer);

        http.addFilterBefore(new OAuth2AuthorizationEndpointEnhanceFilter(authorizationServerConfigurer, authenticationPoint),
                ExceptionTranslationFilter.class);

        return http.build();
    }


    @Bean("oAuth2HttpSecurityEnhanceCustomizer")
    @ConditionalOnMissingBean(name = "oAuth2HttpSecurityEnhanceCustomizer")
    public HttpSecurityEnhanceCustomizer oAuth2HttpSecurityEnhanceCustomizer(AuthenticationConfiguration authenticationConfiguration, AuthenticationPoint authenticationPoint) throws Exception {
        return new OAuth2HttpSecurityEnhanceCustomizer(authenticationPoint);
    }


    @Bean("oauth2SecurityGlobalEnhanceFilter")
    @ConditionalOnMissingBean(name = "oauth2SecurityGlobalEnhanceFilter")
    public Filter oauth2SecurityGlobalEnhanceFilter(RegisteredClientRepository registeredClientRepository, OAuth2AuthorizationConsentService authorizationConsentService, Oauth2Properties oauth2Properties, AuthorizationServerSettings authorizationServerSettings) {
        return new Oauth2SecurityGlobalEnhanceFilter(oauth2Properties, registeredClientRepository
                , authorizationConsentService, authorizationServerSettings);
    }
}
