package com.yishuifengxiao.common.security.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

/**
 * 在oauth2的情况下，根据spring security的认证信息生成token
 * 
 * @author yishui
 * @date 2019年7月16日
 * @version 1.0.0
 */
public class TokenService {

	private ClientDetailsService clientDetailsService;

	private AuthorizationServerTokenServices authorizationServerTokenServices;

	/**
	 * 根据认证信息生成token 【请求头中必须包含basic信息】
	 * 
	 * @param request
	 * @param response
	 * @param authentication
	 * @return
	 * @throws IOException
	 */
	public OAuth2AccessToken token(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		return this.token(request, response, authentication, null);
	}

	/**
	 * 根据认证信息生成token 【请求头中必须包含basic信息】
	 * 
	 * @param request
	 * @param response
	 * @param authentication spring security登陆成功后的认证信息
	 * @param grantType      授权类型
	 * @return
	 * @throws IOException
	 */
	public OAuth2AccessToken token(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication, String grantType) throws IOException {
		String header = request.getHeader("Authorization");

		if (header == null || !header.toLowerCase().startsWith("basic ")) {
			throw new UnapprovedClientAuthenticationException("请求头中无client 信息");
		}

		String[] tokens = extractAndDecodeHeader(header, request);
		assert tokens.length == 2;

		String clientId = tokens[0];
		String clientSecret = tokens[1];
		return this.token(request, response, authentication, clientId, clientSecret, grantType);
	}

	/**
	 * 根据认证信息和客户端信息生成token
	 * 
	 * @param request
	 * @param response
	 * @param authentication spring security登陆成功后的认证信息
	 * @param clientId       clientId
	 * @param clientSecret   clientSecret
	 * @return OAuth2AccessToken
	 */
	public OAuth2AccessToken token(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication, String clientId, String clientSecret) {
		return this.token(request, response, authentication, clientId, clientSecret, null);
	}

	/**
	 * 根据认证信息和客户端信息生成token
	 * 
	 * @param request
	 * @param response
	 * @param authentication spring security登陆成功后的认证信息
	 * @param clientId       clientId
	 * @param clientSecret   clientSecret
	 * @param grantType      授权类型
	 * @return OAuth2AccessToken
	 */
	public OAuth2AccessToken token(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication, String clientId, String clientSecret, String grantType) {
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
		if (clientDetails == null) {
			throw new UnapprovedClientAuthenticationException(
					MessageFormat.format("clientId ({0}) 对应的信息不存在", clientId));
		}
		if (StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
			throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
		}

		TokenRequest tokenRequest = new TokenRequest(new HashMap<String, String>(), clientId, clientDetails.getScope(),
				StringUtils.isBlank(grantType) ? "custome" : grantType);

		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

		OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

		return oAuth2AccessToken;
	}

	/**
	 * Decodes the header into a username and password.
	 *
	 * @throws BadCredentialsException if the Basic header is not present or is not
	 *                                 valid Base64
	 */
	private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {

		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.getDecoder().decode(base64Token);
		} catch (IllegalArgumentException e) {
			throw new BadCredentialsException("Failed to decode basic authentication token");
		}

		String token = new String(decoded, "utf-8");

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}

	public ClientDetailsService getClientDetailsService() {
		return clientDetailsService;
	}

	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}

	public AuthorizationServerTokenServices getAuthorizationServerTokenServices() {
		return authorizationServerTokenServices;
	}

	public void setAuthorizationServerTokenServices(AuthorizationServerTokenServices authorizationServerTokenServices) {
		this.authorizationServerTokenServices = authorizationServerTokenServices;
	}

}
