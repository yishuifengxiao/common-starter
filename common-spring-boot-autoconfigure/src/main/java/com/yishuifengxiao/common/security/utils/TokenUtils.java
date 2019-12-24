package com.yishuifengxiao.common.security.utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;

import com.yishuifengxiao.common.constant.SecurityConstant;

/**
 * 在oauth2的情况下，根据spring security的认证信息生成token
 * 
 * @author yishui
 * @date 2019年7月16日
 * @version 1.0.0
 */
public class TokenUtils {

	private ClientDetailsService clientDetailsService;

	private AuthorizationServerTokenServices authorizationServerTokenServices;

	private ConsumerTokenServices consumerTokenServices;

	private UserDetailsService userDetailsService;

	private PasswordEncoder passwordEncoder;

	private TokenExtractor tokenExtractor;

	/**
	 * 根据token的值移除存储的登录token
	 * 
	 * @param tokenValue token的
	 * @return 移除成功返回为true，否则为false
	 */
	public boolean removeToken(String tokenValue) {
		Assert.notNull(tokenValue, "token不能为空");
		return consumerTokenServices.revokeToken(tokenValue);
	}
     /**
      * 根据请求里token信息移除存储的登录token <br/>
      *  token 的提取方式参见 TokenExtractor
      * @param request  HttpServletRequest
      * @return  移除成功返回为true，否则为false
      */
	public boolean removeToken(HttpServletRequest request) {
		Authentication authentication = tokenExtractor.extract(request);
		if (authentication == null) {
			return false;
		}
		String token = authentication.getPrincipal().toString();
		return removeToken(token);
	}

	/**
	 * 根据认证信息生成token
	 * 
	 * @param request
	 * @param username     用户名
	 * @param clientId
	 * @param clientSecret 原始终端密码
	 * @param grantType    授权类型，默认为 custome
	 * @return
	 */
	public OAuth2AccessToken createToken(HttpServletRequest request, String username, String clientId,
			String clientSecret, String grantType) {
		UsernamePasswordAuthenticationToken authentication = extracted(request, username);

		return this.createToken(authentication, clientId, clientSecret, grantType);
	}

	/**
	 * 根据认证信息生成token
	 * 
	 * @param request
	 * @param username     用户名
	 * @param clientId
	 * @param clientSecret 原始终端密码
	 * @param grantType    授权类型，默认为 custome
	 * @return
	 */
	public OAuth2AccessToken createToken(HttpServletRequest request, String username, String clientId,
			String grantType) {
		UsernamePasswordAuthenticationToken authentication = extracted(request, username);

		ClientDetails clientDetails = extracted(clientId);

		return this.createToken(authentication, clientDetails, grantType);
	}

	/**
	 * 根据认证信息生成token
	 * 
	 * @param request
	 * @param username      用户名
	 * @param clientDetails 终端信息
	 * @param clientSecret  原始终端密码
	 * @param grantType     授权类型，默认为 custome
	 * @return
	 */
	public OAuth2AccessToken createToken(HttpServletRequest request, String username, ClientDetails clientDetails,
			String grantType) {
		UsernamePasswordAuthenticationToken authentication = extracted(request, username);
		return this.createToken(authentication, clientDetails, grantType);
	}

	/**
	 * 根据认证信息生成token 【请求头中必须包含basic信息】
	 * 
	 * @param request
	 * @param authentication spring security登陆成功后的认证信息
	 * @param grantType      授权类型
	 * @return
	 * @throws IOException
	 */
	public OAuth2AccessToken createToken(HttpServletRequest request, Authentication authentication, String grantType)
			throws IOException {
		String header = request.getHeader("Authorization");

		if (header == null || !header.toLowerCase().startsWith(SecurityConstant.BASIC_FLAG)) {
			throw new UnapprovedClientAuthenticationException("请求头中无client 信息");
		}

		String[] tokens = extractAndDecodeHeader(header, request);
		assert tokens.length == 2;

		String clientId = tokens[0];
		String clientSecret = tokens[1];
		return this.createToken(authentication, clientId, clientSecret, grantType);
	}

	/**
	 * 根据认证信息和客户端信息生成token
	 *
	 * @param authentication spring security登陆成功后的认证信息
	 * @param clientId       clientId
	 * @param clientSecret   原始终端密码
	 * @param grantType      授权类型,默认为custome
	 * @return OAuth2AccessToken
	 */
	public OAuth2AccessToken createToken(Authentication authentication, String clientId, String clientSecret,
			String grantType) {
		ClientDetails clientDetails = extracted(clientId);

		if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
			throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
		}
		return this.createToken(authentication, clientDetails, grantType);

	}

	/**
	 * 根据认证信息和客户端id信息生成token </br>
	 * <b>注意此方法不会校验终端密码，一定要在可信环境下使用</b>
	 * 
	 * @param authentication spring security登陆成功后的认证信息
	 * @param clientId       clientId
	 * @param grantType      授权类型,默认为custome
	 * @return OAuth2AccessToken
	 */
	public OAuth2AccessToken createToken(Authentication authentication, String clientId, String grantType) {
		ClientDetails clientDetails = extracted(clientId);
		return this.createToken(authentication, clientDetails, grantType);

	}

	/**
	 * 根据认证信息生成token
	 * 
	 * @param authentication spring security登陆成功后的认证信息
	 * @param clientDetails  终端登录成功后的认证信息
	 * @param grantType      授权类型,默认为custome
	 * @return
	 */
	public OAuth2AccessToken createToken(Authentication authentication, ClientDetails clientDetails, String grantType) {

		Assert.notNull(authentication, "Authentication认证信息不能为空");
		Assert.notNull(clientDetails, "ClientDetails认证信息不能为空");

		TokenRequest tokenRequest = new TokenRequest(new HashMap<String, String>(0), clientDetails.getClientId(),
				clientDetails.getScope(), StringUtils.isBlank(grantType) ? "custome" : grantType);

		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

		OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

		return oAuth2AccessToken;
	}

	/**
	 * 根据终端id获取到终端的完整信息
	 * 
	 * @param clientId
	 * @return
	 */
	private ClientDetails extracted(String clientId) {
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
		if (clientDetails == null) {
			throw new UnapprovedClientAuthenticationException(
					MessageFormat.format("clientId ({0}) 对应的信息不存在", clientId));
		}
		return clientDetails;
	}

	/**
	 * 根据用户生成UsernamePasswordAuthenticationToken
	 * 
	 * @param request
	 * @param username 用户名
	 * @return
	 */
	private UsernamePasswordAuthenticationToken extracted(HttpServletRequest request, String username) {
		Assert.notNull(username, "用户名不能为空");
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (userDetails == null) {
			throw new UnapprovedClientAuthenticationException(MessageFormat.format("用户名 ({0}) 不存在", username));
		}
		// 生成通过认证
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		return authentication;
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

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public ConsumerTokenServices getConsumerTokenServices() {
		return consumerTokenServices;
	}

	public void setConsumerTokenServices(ConsumerTokenServices consumerTokenServices) {
		this.consumerTokenServices = consumerTokenServices;
	}

	public TokenExtractor getTokenExtractor() {
		return tokenExtractor;
	}

	public void setTokenExtractor(TokenExtractor tokenExtractor) {
		this.tokenExtractor = tokenExtractor;
	}

}
