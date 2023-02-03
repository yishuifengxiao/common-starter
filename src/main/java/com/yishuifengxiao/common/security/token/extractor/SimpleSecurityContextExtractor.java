/**
 * 
 */
package com.yishuifengxiao.common.security.token.extractor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.token.SecurityContextExtractor;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * 系统信息提取器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleSecurityContextExtractor implements SecurityContextExtractor {

	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

	private PropertyResource propertyResource;

	@Override
	public String extractUsername(HttpServletRequest request, HttpServletResponse response) {
		String usernameParameter = propertyResource.security().getUsernameParameter();
		if (StringUtils.isBlank(usernameParameter)) {
			usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
		}
		return request.getParameter(usernameParameter.trim());
	}

	@Override
	public String extractPassword(HttpServletRequest request, HttpServletResponse response) {
		String passwordParameter = propertyResource.security().getPasswordParameter();
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
	public String extractUserUniqueIdentifier(HttpServletRequest request, HttpServletResponse response) {
		String identitiesParameter = propertyResource.security().getToken().getUserUniqueIdentitier();
		if (StringUtils.isBlank(identitiesParameter)) {
			identitiesParameter = TokenConstant.USER_UNIQUE_IDENTIFIER;
		}
		String identitierValue = request.getHeader(identitiesParameter);
		if (StringUtils.isBlank(identitierValue)) {
			identitierValue = request.getParameter(identitiesParameter);
		}
		if (StringUtils.isBlank(identitierValue)) {
			if (BooleanUtils.isTrue(propertyResource.security().getToken().getUseSessionId())) {
				// 使用sessionId作为用户的唯一标识符
				identitierValue = request.getSession().getId();
			}
		}
		return identitierValue;
	}

	public SimpleSecurityContextExtractor(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}

}
