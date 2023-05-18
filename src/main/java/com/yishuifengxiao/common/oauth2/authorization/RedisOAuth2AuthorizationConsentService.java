package com.yishuifengxiao.common.oauth2.authorization;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 此接口的实现负责OAuth 2.0授权同意的管理。
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {

    private final static String PREFIX_BY_ID = "OAuth2AuthorizationConsentService::authorizations::";

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        int id = getId(authorizationConsent);
        redisTemplate.boundValueOps(new StringBuilder(PREFIX_BY_ID).append(id).toString()).set(authorizationConsent);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        int id = getId(authorizationConsent);
        redisTemplate.delete(new StringBuilder(PREFIX_BY_ID).append(id).toString());
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");
        int id = getId(registeredClientId, principalName);
        return (OAuth2AuthorizationConsent) redisTemplate.boundValueOps(new StringBuilder(PREFIX_BY_ID).append(id).toString()).get();
    }


    private static int getId(String registeredClientId, String principalName) {
        return Objects.hash(registeredClientId, principalName);
    }

    private static int getId(OAuth2AuthorizationConsent authorizationConsent) {
        return getId(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }

    public RedisOAuth2AuthorizationConsentService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
