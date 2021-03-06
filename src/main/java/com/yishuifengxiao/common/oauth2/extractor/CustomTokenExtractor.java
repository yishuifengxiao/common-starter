/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.yishuifengxiao.common.oauth2.extractor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.yishuifengxiao.common.oauth2.Oauth2Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link TokenExtractor} that strips the authenticator from a bearer token
 * request (with an Authorization header in the form "Bearer
 * <code>&lt;TOKEN&gt;</code>", or as a request parameter if that fails). The
 * access token is the principal in the authentication token that is
 * extracted.<br/>
 * 
 * 从请求中提取出token信息<br/>
 * 
 * 提取过程如下：<br/>
 * 
 * 1 先从请求头参数里获取 <br/>
 * 2 再从请求参数里获取 <br/>
 * 3 最后尝试先从session里获取
 * 
 * 该提取器会被 <code>Oauth2Resource</code>收集，然后配置到oauth2中
 * 
 * @see Oauth2Resource
 * @author Dave Syer
 * 
 */
@Slf4j
public class CustomTokenExtractor implements TokenExtractor {

	@Override
	public Authentication extract(HttpServletRequest request) {
		String tokenValue = extractToken(request);
		if (tokenValue != null) {
			PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(tokenValue,
					"");
			return authentication;
		}
		return null;
	}

	protected String extractToken(HttpServletRequest request) {
		// first check the header...
		String token = extractHeaderToken(request);

		// bearer type allows a request parameter as well
		if (token == null) {
			log.debug("Token not found in headers. Trying request parameters.");
			token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
			if (token == null) {
				log.debug("Token not found in request parameters. Trying session parameters.  ");
				token = (String) request.getSession().getAttribute(OAuth2AccessToken.ACCESS_TOKEN);
				if (token == null) {
					log.debug("Token not found in session by key {}. Not an OAuth2 request.");
				}
			} else {
				request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, OAuth2AccessToken.BEARER_TYPE);
			}
		}

		return token;
	}

	/**
	 * Extract the OAuth bearer token from a header.
	 * 
	 * @param request The request.
	 * @return The token, or null if no OAuth authorization header was supplied.
	 */
	protected String extractHeaderToken(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaders("Authorization");
		// typically there is only one (most servers enforce that)
		while (headers.hasMoreElements()) {
			String value = headers.nextElement();
			if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
				String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
				// Add this here for the auth details later. Would be better to change the
				// signature of this method.
				request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE,
						value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
				int commaIndex = authHeaderValue.indexOf(',');
				if (commaIndex > 0) {
					authHeaderValue = authHeaderValue.substring(0, commaIndex);
				}
				return authHeaderValue;
			}
		}

		return null;
	}

}
