package com.yishuifengxiao.common.oauth2.client;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.Arrays;

/**
 * <p>默认实现的RegisteredClientRepository</p>
 * <p style="color:red">RegisteredClientRepository是必需的组件。
 * RegisteredClientRepository是可以注册新客户端和查询现有客户端的中心组件。
 * 其他组件在遵循特定协议流时使用它，如客户端身份验证、授权授权处理、令牌内省、动态客户端注册等。</p>
 * <P>客户端的主要目的是请求访问受保护的资源。客户端首先通过向授权服务器进行认证并呈现授权授权来请求访问令牌。
 * 授权服务器验证客户端和授权授权，如果它们有效，则发出访问令牌。客户端现在可以通过呈现访问令牌从资源服务器请求受保护的资源。</P>
 * <p>以下示例显示如何配置允许执行authorization_code授权流以请求访问令牌的RegisteredClient：</p>
 * <pre>
 *   <code>
 *    RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
 * 	    .clientId("client")
 * 	    .clientSecret("{noop}secret")
 * 	    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
 *  	    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
 * 	    .redirectUri("http://127.0.0.1:8080/authorized")
 *  	    .scope("scope-a")
 * 	    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
 *  	    .build();
 *   </code>
 * </pre>
 * <p>｛noop｝表示Spring Security的NoOppasswordEncoder的密码编码器id。</p>
 * <p>Spring Security的OAuth2 Client支持中的相应配置为：</p>
 * <pre>
 *     <code>
 * spring:
 *   security:
 *     oauth2:
 *       client:
 *         registration:
 *           client-a:
 *             customizer: spring
 *             client-id: client
 *             client-secret: secret
 *             authorization-grant-type: authorization_code
 *             redirect-uri: "http://127.0.0.1:8080/authorized"
 *             scope: scope-a
 *         customizer:
 *           spring:
 *             issuer-uri: http://localhost:9000
 *     </code>
 * </pre>
 *
 * <p>更多内容请参见
 * <a href="https://docs.spring.io/spring-authorization-server/docs/current/reference/html/core-model-components.html#registered-client">
 * https://docs.spring.io/spring-authorization-server/docs/current/reference/html/core-model-components
 * .html#registered-client</a></p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleRegisteredClientRepository implements RegisteredClientRepository, InitializingBean {

    private RegisteredClientRepository registeredClientRepository;


    private PasswordEncoder passwordEncoder;

    @Override
    public void save(RegisteredClient registeredClient) {
        this.registeredClientRepository.save(registeredClient);
    }

    @Override
    public RegisteredClient findById(String id) {
        return this.registeredClientRepository.findById(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return this.registeredClientRepository.findByClientId(clientId);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        // @formatter:off
        RegisteredClient registeredClient = RegisteredClient.withId("yishuifengxiao")
                .clientId("client")
                .clientSecret(this.passwordEncoder.encode("secret"))
                .clientAuthenticationMethods(methods->
                        methods.addAll(Arrays.asList(
                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                                ClientAuthenticationMethod.CLIENT_SECRET_POST,
                                ClientAuthenticationMethod.CLIENT_SECRET_JWT,
                                ClientAuthenticationMethod.PRIVATE_KEY_JWT,
                                ClientAuthenticationMethod.NONE)))
                .authorizationGrantTypes(types->types.addAll(Arrays.asList(
                        AuthorizationGrantType.AUTHORIZATION_CODE,
                        AuthorizationGrantType.REFRESH_TOKEN,
                        AuthorizationGrantType.CLIENT_CREDENTIALS,
                        AuthorizationGrantType.JWT_BEARER,
                        AuthorizationGrantType.PASSWORD
                        )))
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
                .redirectUri("http://www.yishuifengxiao.com")
                .scope("scope-a")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .authorizationCodeTimeToLive(Duration.ofHours(2))
                        .refreshTokenTimeToLive(Duration.ofHours(6))
                        .build())
                .build();
        // @formatter:on
        this.registeredClientRepository = new InMemoryRegisteredClientRepository(registeredClient);
    }

    public SimpleRegisteredClientRepository() {

    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
