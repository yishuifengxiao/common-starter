/**
 * 
 */
package com.yishuifengxiao.common.security.extractor.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.extractor.SecurityExtractor;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * 系统信息提取器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleSecurityExtractor implements SecurityExtractor {

	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

	private PropertyResource propertyResource;

	@Override
	public String extractUsername(HttpServletRequest request, HttpServletResponse response) {
		String usernameParameter = propertyResource.security().getCore().getUsernameParameter();
		if (StringUtils.isBlank(usernameParameter)) {
			usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
		}
		return request.getParameter(usernameParameter.trim());
	}

	@Override
	public String extractPassword(HttpServletRequest request, HttpServletResponse response) {
		String passwordParameter = propertyResource.security().getCore().getPasswordParameter();
		if (StringUtils.isBlank(passwordParameter)) {
			passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
		}
		return request.getParameter(passwordParameter.trim());
	}

	/**
	 * 从请求中提取出用户的唯一标识符
	 * 
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @return 用户的唯一标识符
	 */
	@Override
	public String extractUserUniqueIdentitier(HttpServletRequest request, HttpServletResponse response) {
		String identitierParamter = propertyResource.security().getToken().getUserUniqueIdentitier();
		if (StringUtils.isBlank(identitierParamter)) {
			identitierParamter = TokenConstant.USER_UNIQUE_IDENTIFIER;
		}
		String identitierValue = request.getHeader(identitierParamter);
		if (StringUtils.isBlank(identitierValue)) {
			identitierValue = request.getParameter(identitierParamter);
		}
		if (StringUtils.isBlank(identitierValue)) {
			if (BooleanUtils.isTrue(propertyResource.security().getToken().getUseSessionId())) {
				// 使用sessionId作为用户的唯一标识符
				identitierValue = request.getSession().getId();
			}
		}
		return identitierValue;
	}

	public SimpleSecurityExtractor(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}

}
