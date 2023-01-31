package com.yishuifengxiao.common.oauth2;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.yishuifengxiao.common.oauth2.authorization.RedisOAuth2AuthorizationConsentService;
import com.yishuifengxiao.common.oauth2.authorization.RedisOAuth2AuthorizationService;
import com.yishuifengxiao.common.oauth2.client.SimpleRegisteredClientRepository;
import com.yishuifengxiao.common.oauth2.configurer.SimpleClientAuthentication;
import com.yishuifengxiao.common.oauth2.provider.OAuth2AuthorizeProvider;
import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.httpsecurity.authorize.AuthorizeProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2ClientAuthenticationConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * <p>
 * oauth2增强配置
 * </p>
 * <p>
 * The OAuth 2.0 Authorization Framework 相关解释参见 <a href=
 * "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1">https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.1</a>
 * </p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({ OAuth2AccessToken.class, WebMvcConfigurer.class })
@ConditionalOnBean(AbstractSecurityConfig.class)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties({ Oauth2Properties.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class Oauth2ExtendAutoConfiguration {

	/**
	 * <p>
	 * 用于自定义OAuth2授权服务器配置设置的AuthorizationServerSettings（必需）
	 * </p>
	 * <p>
	 * 默认的配置示例为
	 * </p>
	 * 
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
	 * </pre>
	 *
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean({ ProviderSettings.class })
	public ProviderSettings authorizationServerSettings() {

		return ProviderSettings.builder().build();
	}

	/**
	 * <p style="color:yellow">
	 * RegisteredClientRepository是必需的组件。
	 * </p>
	 * <p>
	 * RegisteredClientRepository是可以注册新客户端和查询现有客户端的中心组件。
	 * 其他组件在遵循特定协议流时使用它，如客户端身份验证、授权授权处理、令牌内省、动态客户端注册等。
	 * </p>
	 *
	 * @return RegisteredClientRepository
	 */
	@Bean
	@ConditionalOnMissingBean({ RegisteredClientRepository.class })
	public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
		SimpleRegisteredClientRepository clientRepository = new SimpleRegisteredClientRepository(passwordEncoder);
		return clientRepository;
	}

	@ConditionalOnMissingBean(name = { "redisTemplate" }, value = { OAuth2AuthorizationService.class })
	@Bean
	public OAuth2AuthorizationService authorizationService() {
		return new InMemoryOAuth2AuthorizationService();
	}

	@ConditionalOnMissingBean(name = { "redisTemplate" }, value = { OAuth2AuthorizationConsentService.class })
	@Bean
	public OAuth2AuthorizationConsentService authorizationConsentService() {
		return new InMemoryOAuth2AuthorizationConsentService();
	}

	@ConditionalOnMissingBean(name = { "oAuth2ClientAuthenticationConfigurer" })
	@Bean("oAuth2ClientAuthenticationConfigurer")
	public Customizer<OAuth2ClientAuthenticationConfigurer> oAuth2ClientAuthenticationConfigurer(
			AuthenticationSuccessHandler authenticationSuccessHandler,
			AuthenticationFailureHandler errorResponseHandler) {
		return new SimpleClientAuthentication(authenticationSuccessHandler, errorResponseHandler);
	}

	// @formatter:off

    @Bean
    @ConditionalOnProperty(prefix = "yishuifengxiao.security.oauth2", name = {"enable"}, havingValue = "true", matchIfMissing = true)
    public AuthorizeProvider oAuth2AuthorizeProvider(RegisteredClientRepository registeredClientRepository,
                                                     ProviderSettings providerSettings,
                                                    @Qualifier("oAuth2ClientAuthenticationConfigurer")  Customizer<OAuth2ClientAuthenticationConfigurer> clientAuthentication,
                                                     OAuth2AuthorizationService authorizationService,
                                                     OAuth2AuthorizationConsentService authorizationConsentService
                                                  ) {

        return new OAuth2AuthorizeProvider(registeredClientRepository,providerSettings,clientAuthentication,
                authorizationService,authorizationConsentService);
    }
    // @formatter:on

	@Configuration
	@ConditionalOnClass({ RedisOperations.class, RedisTemplate.class })
	public static class Oauth2RedisExtendAutoConfiguration {

		@Bean
		public OAuth2AuthorizationService authorizationService(RedisTemplate<String, Object> redisTemplate) {
			return new RedisOAuth2AuthorizationService(redisTemplate);
		}

		@Bean
		public OAuth2AuthorizationConsentService authorizationConsentService(
				RedisTemplate<String, Object> redisTemplate) {
			return new RedisOAuth2AuthorizationConsentService(redisTemplate);
		}

	}

	@Bean
	@ConditionalOnMissingBean({ JWKSource.class })
	public JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString())
				.build();
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
	@ConditionalOnMissingBean({ JwtDecoder.class })
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}
}
