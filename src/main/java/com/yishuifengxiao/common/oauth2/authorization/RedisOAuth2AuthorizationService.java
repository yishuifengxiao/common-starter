package com.yishuifengxiao.common.oauth2.authorization;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.util.Assert;

/**
 * 此接口的实现负责OAuth 2.0授权的管理。
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final static String PREFIX_BY_ID = "OAuth2Authorization::authorizations::";
    private final static String PREFIX_BY_TOKEN = "OAuth2Authorization::initializedAuthorizations::";

    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(OAuth2Authorization authorization) {
        if (isComplete(authorization)) {
            redisTemplate.boundHashOps(PREFIX_BY_ID).put(authorization.getId(), authorization);
        } else {
            redisTemplate.boundHashOps(PREFIX_BY_TOKEN).put(authorization.getId(), authorization);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        if (isComplete(authorization)) {
            redisTemplate.boundHashOps(PREFIX_BY_ID).delete(authorization.getId());
        } else {
            redisTemplate.boundHashOps(PREFIX_BY_TOKEN).delete(authorization.getId());
        }
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        OAuth2Authorization authorization = (OAuth2Authorization) redisTemplate.boundHashOps(PREFIX_BY_ID).get(id);
        return authorization != null ?
                authorization :
                (OAuth2Authorization) redisTemplate.boundHashOps(PREFIX_BY_TOKEN).get(id);

    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        for (Object val : redisTemplate.boundHashOps(PREFIX_BY_ID).values()) {
            OAuth2Authorization authorization = (OAuth2Authorization) val;
            if (hasToken(authorization, token, tokenType)) {
                return authorization;
            }
        }
        for (Object val : redisTemplate.boundHashOps(PREFIX_BY_TOKEN).values()) {
            OAuth2Authorization authorization = (OAuth2Authorization) val;
            if (hasToken(authorization, token, tokenType)) {
                return authorization;
            }
        }
        return null;
    }

    private static boolean hasToken(OAuth2Authorization authorization, String token, @Nullable OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return matchesState(authorization, token) ||
                    matchesAuthorizationCode(authorization, token) ||
                    matchesAccessToken(authorization, token) ||
                    matchesRefreshToken(authorization, token);
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
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        return accessToken != null && accessToken.getToken().getTokenValue().equals(token);
    }

    private static boolean matchesRefreshToken(OAuth2Authorization authorization, String token) {
        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
                authorization.getToken(OAuth2RefreshToken.class);
        return refreshToken != null && refreshToken.getToken().getTokenValue().equals(token);
    }

    private static boolean isComplete(OAuth2Authorization authorization) {
        return authorization.getAccessToken() != null;
    }

    public RedisOAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
