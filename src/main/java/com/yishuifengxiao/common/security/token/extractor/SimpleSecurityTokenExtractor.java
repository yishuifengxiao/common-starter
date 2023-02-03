package com.yishuifengxiao.common.security.token.extractor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.token.SecurityTokenExtractor;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * 系统令牌提取器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleSecurityTokenExtractor implements SecurityTokenExtractor {

	@Override
	public String extractTokenValue(HttpServletRequest request, HttpServletResponse response,
			PropertyResource propertyResource) {
		String tokenValue = this.getTokenValueInHeader(request, propertyResource);
		if (StringUtils.isBlank(tokenValue)) {
			tokenValue = this.getTokenValueInQuery(request, propertyResource);
		}
		return tokenValue;
	}

	/**
	 * 从请求参数里获取tokenValue
	 * 
	 * @param request          HttpServletRequest
	 * @param propertyResource 资源管理器
	 * @return tokenValue
	 */
	private String getTokenValueInQuery(HttpServletRequest request, PropertyResource propertyResource) {
		String requestParamter = propertyResource.security().getToken().getRequestParamter();
		if (StringUtils.isBlank(requestParamter)) {
			requestParamter = TokenConstant.TOKEN_REQUEST_PARAM;
		}

		String tokenValue = request.getParameter(requestParamter);

		if (StringUtils.isBlank(tokenValue)) {
			tokenValue = (String) request.getSession().getAttribute(requestParamter);
		}
		return tokenValue;
	}

	/**
	 * 从请求头里获取到tokenValue
	 * 
	 * @param request          HttpServletRequest
	 * @param propertyResource 资源管理器
	 * @return tokenValue
	 */
	private String getTokenValueInHeader(HttpServletRequest request, PropertyResource propertyResource) {
		String headerParamter = propertyResource.security().getToken().getHeaderParamter();
		if (StringUtils.isBlank(headerParamter)) {
			headerParamter = TokenConstant.TOKEN_HEADER_PARAM;
		}
		return request.getHeader(headerParamter);
	}

}
