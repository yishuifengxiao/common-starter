package com.yishuifengxiao.common.oauth2server.autoconfigure;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.yishuifengxiao.common.oauth2server.Oauth2Properties;
import com.yishuifengxiao.common.oauth2server.authorization.RedisOAuth2AuthorizationConsentService;
import com.yishuifengxiao.common.oauth2server.authorization.RedisOAuth2AuthorizationService;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * @author qingteng
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({OAuth2AccessToken.class, WebMvcConfigurer.class})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties({Oauth2Properties.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true")
public class Oauth2EnhanceExtendAutoConfiguration {


    @Configuration
    @ConditionalOnClass({RedisOperations.class})
    public static class Oauth2RedisExtendAutoConfiguration {

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
    public static class Oauth2InMemoryExtendAutoConfiguration {

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


}
