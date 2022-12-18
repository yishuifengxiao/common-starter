package com.yishuifengxiao.common.resourceserver.introspection;

import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.AUDIENCE;
import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.CLIENT_ID;
import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.EXPIRES_AT;
import static org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames.SCOPE;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.oauth2.sdk.TokenIntrospectionErrorResponse;
import com.nimbusds.oauth2.sdk.TokenIntrospectionResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.yishuifengxiao.common.resourceserver.provider.ResourceAuthorizeProvider;

/**
 * <p>
 * token探寻器
 * </p>
 * 
 * <p>
 * 用于内省和验证OAuth 2.0令牌的协定。此接口的典型实现将向OAuth
 * 2.0自省终结点发出请求，以验证令牌并返回其属性，表示成功验证。另一个这个接口的合理实现是查询令牌的后备存储，例如分布式缓存。
 * </p>
 * 在<code>ResourceAuthorizeProvider</code>中被配置
 * 
 * @see ResourceAuthorizeProvider
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

	@SuppressWarnings("unused")
	private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {
	};

	private Converter<String, RequestEntity<?>> requestEntityConverter;
	private RestOperations restOperations;

	public CustomOpaqueTokenIntrospector(String introspectionUri) {
		Assert.notNull(introspectionUri, "introspectionUri cannot be null");

		this.requestEntityConverter = this.defaultRequestEntityConverter(URI.create(introspectionUri));
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new CustomResponseErrorHandler());
		// restTemplate.getInterceptors().add(new
		// BasicAuthenticationInterceptor(clientId, clientSecret));
		this.restOperations = restTemplate;
	}

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		RequestEntity<?> requestEntity = this.requestEntityConverter.convert(token);
		if (requestEntity == null) {
			throw new OAuth2IntrospectionException("Provided token [" + token + "] isn't active");
		}

		ResponseEntity<String> responseEntity = makeRequest(requestEntity);

		HTTPResponse httpResponse = adaptToCustomResponse(responseEntity);
		TokenIntrospectionResponse introspectionResponse = parseCustomResponse(httpResponse);
		CustomTokenIntrospectionSuccessResponse introspectionSuccessResponse = castToCustomResponse(
				introspectionResponse);

		return convertClaimsSet(introspectionSuccessResponse);
	}

	@SuppressWarnings("deprecation")
	private OAuth2AuthenticatedPrincipal convertClaimsSet(CustomTokenIntrospectionSuccessResponse response) {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		Map<String, Object> claims = response.toJSONObject();
		if (response.getAudience() != null) {
			List<String> audiences = new ArrayList<>();
			for (Audience audience : response.getAudience()) {
				audiences.add(audience.getValue());
			}
			claims.put(AUDIENCE, Collections.unmodifiableList(audiences));
		}
		if (response.getClientID() != null) {
			claims.put(CLIENT_ID, response.getClientID().getValue());
		}
		if (response.getExpirationTime() != null) {
			Instant exp = response.getExpirationTime().toInstant();
			claims.put(EXPIRES_AT, exp);
		}

		if (response.getScope() != null) {
			List<String> scopes = Collections.unmodifiableList(response.getScope().toStringList());
			claims.put(SCOPE, scopes);
		}

		if (response.getAuthorities() != null) {
			List<String> authoritie = Collections.unmodifiableList(response.getAuthorities().toStringList());
			for (String authority : authoritie) {
				authorities.add(new SimpleGrantedAuthority(authority));
			}
		}

		return new DefaultOAuth2AuthenticatedPrincipal(response.getUsername(), claims, authorities);
	}

	private CustomTokenIntrospectionSuccessResponse castToCustomResponse(
			TokenIntrospectionResponse introspectionResponse) {

		if (!introspectionResponse.indicatesSuccess()) {
			throw new OAuth2IntrospectionException("Token introspection failed");
		}
		return (CustomTokenIntrospectionSuccessResponse) introspectionResponse;

	}

	private TokenIntrospectionResponse parseCustomResponse(HTTPResponse response) {
		try {

			if (response.getStatusCode() == HTTPResponse.SC_OK) {
				return CustomTokenIntrospectionSuccessResponse.parse(response);
			} else {
				return TokenIntrospectionErrorResponse.parse(response);
			}
		} catch (Exception ex) {
			throw new OAuth2IntrospectionException(ex.getMessage(), ex);
		}
	}

	private HTTPResponse adaptToCustomResponse(ResponseEntity<String> responseEntity) {
		HTTPResponse response = new HTTPResponse(responseEntity.getStatusCodeValue());
		response.setHeader(HttpHeaders.CONTENT_TYPE, responseEntity.getHeaders().getContentType().toString());
		response.setContent(responseEntity.getBody());

		if (response.getStatusCode() != HTTPResponse.SC_OK) {
			String errMsg = extractErrorMsg(responseEntity.getBody());
			throw new OAuth2IntrospectionException(errMsg);
		}
		return response;
	}

	private String extractErrorMsg(String body) {
		String msg = "请求需要认证";
		if (StringUtils.containsIgnoreCase(body, "invalid_token")) {
			msg = "无效的认证信息";
		}
		if (StringUtils.containsIgnoreCase(body, "expired")) {
			msg = "认证信息已过期";
		}
		return msg;
	}

	private ResponseEntity<String> makeRequest(RequestEntity<?> requestEntity) {

		try {
			// return this.restOperations.exchange(requestEntity,
			// PARAMETERIZED_RESPONSE_TYPE);
			return this.restOperations.exchange(requestEntity, String.class);
		} catch (Exception ex) {
			throw new OAuth2IntrospectionException(ex.getMessage(), ex);
		}
	}

	private Converter<String, RequestEntity<?>> defaultRequestEntityConverter(URI introspectionUri) {
		return token -> {
			HttpHeaders headers = requestHeaders();
			MultiValueMap<String, String> body = requestBody(token);
			return new RequestEntity<>(body, headers, HttpMethod.POST, introspectionUri);
		};
	}

	@SuppressWarnings("deprecation")
	private HttpHeaders requestHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
		return headers;
	}

	private MultiValueMap<String, String> requestBody(String token) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("token", token);
		return body;
	}

	public static class CustomResponseErrorHandler implements ResponseErrorHandler {

		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return false;
		}

		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			throw new IOException("验证token有效性时出现问题");
		}
	}

}
