package com.yishuifengxiao.common.oauth2.authorization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @see JdbcOAuth2AuthorizationService
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public class AccessTokenMapper {


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
    }

    @SuppressWarnings("unchecked")
	public OAuth2Authorization deserialize(AccessToken val, RegisteredClientRepository registeredClientRepository) {
        if (null == val) {
            return null;
        }
        try {
            String registeredClientId = val.getRegisteredClientId();
            RegisteredClient registeredClient = registeredClientRepository.findById(registeredClientId);
            if (registeredClient == null) {
                throw new DataRetrievalFailureException("The RegisteredClient with id '" + registeredClientId + "' " + "was " + "not found in the " + "RegisteredClientRepository.");
            }

            OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
            String id = val.getId();
            String principalName = val.getPrincipalName();
            String authorizationGrantType = val.getAuthorizationGrantType();
            Set<String> authorizedScopes = Collections.emptySet();
            String authorizedScopesString = val.getAuthorizedScopes();
            if (authorizedScopesString != null) {
                authorizedScopes = StringUtils.commaDelimitedListToSet(authorizedScopesString);
            }
            Map<String, Object> attributes = string2Map(val.getAttributes());

            builder.id(id).principalName(principalName).authorizationGrantType(new AuthorizationGrantType(authorizationGrantType)).authorizedScopes(authorizedScopes).attributes((attrs) -> attrs.putAll(attributes));

            String state = val.getState();
            if (StringUtils.hasText(state)) {
                builder.attribute(OAuth2ParameterNames.STATE, state);
            }


            Instant tokenIssuedAt;
            Instant tokenExpiresAt;
            String authorizationCodeValue = val.getAuthorizationCodeValue();

            if (StringUtils.hasText(authorizationCodeValue)) {
                tokenIssuedAt = Instant.ofEpochMilli(val.getAuthorizationCodeIssuedAt());
                tokenExpiresAt = Instant.ofEpochMilli(val.getAuthorizationCodeExpiresAt());
                Map<String, Object> authorizationCodeMetadata = string2Map(val.getAuthorizationCodeMetadata());
                OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(authorizationCodeValue,
                        tokenIssuedAt, tokenExpiresAt);
                builder.token(authorizationCode, (metadata) -> metadata.putAll(authorizationCodeMetadata));
            }

            String accessTokenValue = val.getAccessTokenValue();
            if (StringUtils.hasText(accessTokenValue)) {
                tokenIssuedAt = Instant.ofEpochMilli(val.getAccessTokenIssuedAt());
                tokenExpiresAt = Instant.ofEpochMilli(val.getAccessTokenExpiresAt());
                Map<String, Object> accessTokenMetadata = string2Map(val.getAccessTokenMetadata());
                OAuth2AccessToken.TokenType tokenType = null;

                if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(val.getAccessTokenType())) {
                    tokenType = OAuth2AccessToken.TokenType.BEARER;
                }

                Set<String> scopes = Collections.emptySet();
                String accessTokenScopes = val.getAccessTokenScopes();
                if (accessTokenScopes != null) {
                    scopes = StringUtils.commaDelimitedListToSet(accessTokenScopes);
                }
                OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, accessTokenValue, tokenIssuedAt,
                        tokenExpiresAt, scopes);
                builder.token(accessToken, (metadata) -> metadata.putAll(accessTokenMetadata));
            }

            String oidcIdTokenValue = val.getOidcIdTokenValue();
            if (StringUtils.hasText(oidcIdTokenValue)) {
                tokenIssuedAt = Instant.ofEpochMilli(val.getOidcIdTokenIssuedAt());
                tokenExpiresAt = Instant.ofEpochMilli(val.getOidcIdTokenExpiresAt());
                Map<String, Object> oidcTokenMetadata = string2Map(val.getOidcIdTokenMetadata());

                OidcIdToken oidcToken = new OidcIdToken(oidcIdTokenValue, tokenIssuedAt, tokenExpiresAt, (Map<String,
                        Object>) oidcTokenMetadata.get(OAuth2Authorization.Token.CLAIMS_METADATA_NAME));
                builder.token(oidcToken, (metadata) -> metadata.putAll(oidcTokenMetadata));
            }

            String refreshTokenValue = val.getRefreshTokenValue();
            if (StringUtils.hasText(refreshTokenValue)) {
                tokenIssuedAt = Instant.ofEpochMilli(val.getRefreshTokenIssuedAt());
                tokenExpiresAt = Instant.ofEpochMilli(val.getRefreshTokenExpiresAt());
                Map<String, Object> refreshTokenMetadata = string2Map(val.getRefreshTokenMetadata());


                OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(refreshTokenValue, tokenIssuedAt,
                        tokenExpiresAt);
                builder.token(refreshToken, (metadata) -> metadata.putAll(refreshTokenMetadata));

            }
            return builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AccessToken serialize(OAuth2Authorization authorization) {
        if (null == authorization) {
            return null;
        }
        try {
            String id = authorization.getId();
            String registeredClientId = authorization.getRegisteredClientId();
            String principalName = authorization.getPrincipalName();
            String authorizationGrantType = authorization.getAuthorizationGrantType().getValue();

            String authorizedScopes = null;
            if (!CollectionUtils.isEmpty(authorization.getAuthorizedScopes())) {
                authorizedScopes = StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ",");
            }

            String attributes = map2String(null == authorization.getAttributes() ? new HashMap<>() :
                    authorization.getAttributes());


            String state = null;
            String authorizationState = authorization.getAttribute(OAuth2ParameterNames.STATE);
            if (StringUtils.hasText(authorizationState)) {
                state = authorizationState;
            }


            String authorizationCodeValue = null;
            Long authorizationCodeIssuedAt = null;
            Long authorizationCodeExpiresAt = null;
            String authorizationCodeMetadata = null;

            String accessTokenValue = null;
            Long accessTokenIssuedAt = null;
            Long accessTokenExpiresAt = null;
            String accessTokenMetadata = null;
            String accessTokenType = null;
            String accessTokenScopes = null;

            String oidcIdTokenValue = null;
            Long oidcIdTokenIssuedAt = null;
            Long oidcIdTokenExpiresAt = null;
            String oidcIdTokenMetadata = null;

            String refreshTokenValue = null;
            Long refreshTokenIssuedAt = null;
            Long refreshTokenExpiresAt = null;
            String refreshTokenMetadata = null;


            OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                    authorization.getToken(OAuth2AuthorizationCode.class);
            if (null != authorizationCode) {

                OAuth2AuthorizationCode token = authorizationCode.getToken();
                authorizationCodeMetadata = map2String(authorizationCode.getMetadata());
                if (null != token) {
                    authorizationCodeValue = token.getTokenValue();
                    authorizationCodeIssuedAt = token.getIssuedAt().toEpochMilli();
                    authorizationCodeExpiresAt = token.getExpiresAt().toEpochMilli();
                }
            }


            OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
            if (null != accessToken) {
                OAuth2AccessToken token = accessToken.getToken();
                accessTokenMetadata = map2String(accessToken.getMetadata());
                if (null != token) {
                    accessTokenIssuedAt = token.getIssuedAt().toEpochMilli();
                    accessTokenExpiresAt = token.getExpiresAt().toEpochMilli();
                    accessTokenValue = token.getTokenValue();
                    accessTokenType = token.getTokenType().getValue();
                    accessTokenScopes = token.getScopes().stream().collect(Collectors.joining(","));
                }
            }


            OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
            if (null != oidcIdToken) {
                oidcIdTokenMetadata = map2String(oidcIdToken.getMetadata());
                OidcIdToken token = oidcIdToken.getToken();
                if (null != token) {
                    oidcIdTokenIssuedAt = token.getIssuedAt().toEpochMilli();
                    oidcIdTokenExpiresAt = token.getIssuedAt().toEpochMilli();
                    oidcIdTokenValue = token.getTokenValue();
                }
            }


            OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();

            if (null != refreshToken) {
                refreshTokenMetadata = map2String(refreshToken.getMetadata());
                OAuth2RefreshToken token = refreshToken.getToken();
                if (null != token) {
                    refreshTokenIssuedAt = token.getIssuedAt().toEpochMilli();
                    refreshTokenExpiresAt = token.getIssuedAt().toEpochMilli();
                    refreshTokenValue = token.getTokenValue();
                }
            }


            return new AccessToken(id, registeredClientId, principalName, authorizationGrantType, authorizedScopes,
                    attributes, state, authorizationCodeValue, authorizationCodeIssuedAt, authorizationCodeExpiresAt,
                    authorizationCodeMetadata, accessTokenValue, accessTokenIssuedAt, accessTokenExpiresAt,
                    accessTokenMetadata, accessTokenType, accessTokenScopes, oidcIdTokenValue, oidcIdTokenIssuedAt,
                    oidcIdTokenExpiresAt, oidcIdTokenMetadata, refreshTokenValue, refreshTokenIssuedAt,
                    refreshTokenExpiresAt, refreshTokenMetadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private String map2String(Map map) throws JsonProcessingException {
        Map hashMap = new HashMap();
        if (null != map) {
            map.forEach((k, v) -> hashMap.put(k, v));
        }
        return objectMapper.writeValueAsString(hashMap);
    }

    @SuppressWarnings("rawtypes")
	private Map string2Map(String text) {
        Map map = new HashMap<>();
        if (org.apache.commons.lang3.StringUtils.isBlank(text)) {
            return map;
        }
        try {
            return objectMapper.readValue(text, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;

    }


}
