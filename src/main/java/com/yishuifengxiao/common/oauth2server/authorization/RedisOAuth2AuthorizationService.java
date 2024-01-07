package com.yishuifengxiao.common.oauth2server.authorization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 此接口的实现负责OAuth 2.0授权的管理。
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final static String PREFIX_BY_OAUTH2AUTHORIZATION = "oauth2authorization";

    private RedisTemplate<String, Object> redisTemplate;

    private RegisteredClientRepository registeredClientRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {

        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 反序列化时候遇到不匹配的属性并不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 序列化时候遇到空对象不抛出异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 反序列化的时候如果是无效子类型,不抛出异常
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        // 不使用默认的dateTime进行序列化,
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        // 使用JSR310提供的序列化类,里面包含了大量的JDK8时间序列化类
        objectMapper.registerModule(new JavaTimeModule());
        // 启用反序列化所需的类型信息,在属性中添加@class
//		objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
//				com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        //  Spring Security为持久化Spring Security相关的类提供了Jackson支持。
        //  这可以提高在使用分布式会话（会话复制、Spring Session等）时序列化Spring Security相关类的性能。
        // https://springdoc.cn/spring-security/servlet/integrations/jackson.html
        ClassLoader loader = OAuth2AuthorizationService.class.getClassLoader();
        List<com.fasterxml.jackson.databind.Module> modules = SecurityJackson2Modules.getModules(loader);
        objectMapper.registerModules(modules);
    }


    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        try {
            if (isComplete(authorization)) {
                this.redis("complete").put(authorization.getId(), objectMapper.writeValueAsString(authorization));
            } else {
                this.redis("uncomplete").put(authorization.getId(), objectMapper.writeValueAsString(authorization));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            if (null != val) {
                OAuth2Authorization authorization = objectMapper.readValue(String.valueOf(val),
                        OAuth2Authorization.class);
                if (null != authorization) {
                    return authorization;
                }
            }

            val = this.redis("uncomplete").get(id);
            if (null != val) {
                OAuth2Authorization authorization = objectMapper.readValue(String.valueOf(val),
                        OAuth2Authorization.class);
                if (null != authorization) {
                    return authorization;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    @Override
    public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        List<Object> values = this.redis("complete").values();
        try {
            for (Object authorization : values) {
                if (null == authorization) {
                    continue;
                }
                OAuth2Authorization val = objectMapper.readValue(String.valueOf(authorization),
                        OAuth2Authorization.class);
                ;
                if (null != val && hasToken(val, token, tokenType)) {
                    return val;
                }
            }
            values = this.redis("uncomplete").values();
            for (Object authorization : values) {
                if (null == authorization) {
                    continue;
                }
                OAuth2Authorization val = objectMapper.readValue(String.valueOf(authorization),
                        OAuth2Authorization.class);
                ;
                if (null != val && hasToken(val, token, tokenType)) {
                    return val;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @SuppressWarnings({"unused", "serial"})
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
