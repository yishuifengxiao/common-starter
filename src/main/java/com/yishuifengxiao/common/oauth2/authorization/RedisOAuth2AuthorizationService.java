package com.yishuifengxiao.common.oauth2.authorization;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

/**
 * 此接口的实现负责OAuth 2.0授权的管理。
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final static String PREFIX_BY_OAUTH2AUTHORIZATION = "oauth2authorization";
    private AccessTokenMapper accessTokenMapper = new AccessTokenMapper();

    private RedisTemplate<String, Object> redisTemplate;

    private RegisteredClientRepository registeredClientRepository;



    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        if (isComplete(authorization)) {
            this.redis("complete").put(authorization.getId(), accessTokenMapper.serialize(authorization));
        } else {
            this.redis("uncomplete").put(authorization.getId(), accessTokenMapper.serialize(authorization));
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        if (isComplete(authorization)) {
            this.redis("complete").delete(authorization.getId());
        } else {
            this.redis("uncomplete").delete(authorization.getId());
        }
    }

    @Nullable
    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        Object val = this.redis("complete").get(id);
        if (null != val) {
            OAuth2Authorization authorization = accessTokenMapper.deserialize((AccessToken) val,
                    registeredClientRepository);
            if (null != authorization) {
                return authorization;
            }
        }

        val = this.redis("uncomplete").get(id);
        return null == val ? null : accessTokenMapper.deserialize((AccessToken) val, registeredClientRepository);
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        List<Object> values = this.redis("complete").values();
        for (Object authorization : values) {
            OAuth2Authorization val = accessTokenMapper.deserialize((AccessToken) authorization,
                    registeredClientRepository);
            if (null != val && hasToken(val, token, tokenType)) {
                return val;
            }
        }
        values = this.redis("uncomplete").values();
        for (Object authorization : values) {
            OAuth2Authorization val = accessTokenMapper.deserialize((AccessToken) authorization,
                    registeredClientRepository);
            if (null != val && hasToken(val, token, tokenType)) {
                return val;
            }
        }
        return null;
    }

    private static boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    private static boolean hasToken(OAuth2Authorization authorization, String token,
                                    @Nullable OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return matchesState(authorization, token) || matchesAuthorizationCode(authorization, token) || matchesAccessToken(authorization, token) || matchesRefreshToken(authorization, token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            return matchesState(authorization, token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            return matchesAuthorizationCode(authorization, token);
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            return matchesAccessToken(authorization, token);
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            return matchesRefreshToken(authorization, token);
        }
        return false;
    }

    private static boolean matchesState(OAuth2Authorization authorization, String token) {
        return token.equals(authorization.getAttribute(OAuth2ParameterNames.STATE));
    }

    private static boolean matchesAuthorizationCode(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        return authorizationCode != null && authorizationCode.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesAccessToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        return accessToken != null && accessToken.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesRefreshToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        return refreshToken != null && refreshToken.getToken().getTokenValue().equals(token);
    }

    @SuppressWarnings({ "unused", "serial" })
	private static final class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;

        private MaxSizeHashMap(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > this.maxSize;
        }

    }

    private BoundHashOperations<String, String, Object> redis(String type) {
        return redisTemplate.boundHashOps(new StringBuilder(PREFIX_BY_OAUTH2AUTHORIZATION).append(":").append(type).toString());
    }

    public RedisOAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate,
                                           RegisteredClientRepository registeredClientRepository) {
        this.redisTemplate = redisTemplate;
        this.registeredClientRepository = registeredClientRepository;
    }
}
