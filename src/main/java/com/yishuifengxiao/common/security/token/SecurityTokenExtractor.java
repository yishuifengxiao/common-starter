package com.yishuifengxiao.common.security.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * 令牌提取器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SecurityTokenExtractor {

	/**
	 * 从请求中提取出访问令牌信息
	 * 
	 * @param request          HttpServletRequest
	 * @param response         HttpServletResponse
	 * @param propertyResource 资源管理器
	 * @return 访问令牌信息
	 */
	String extractTokenValue(HttpServletRequest request, HttpServletResponse response,
			PropertyResource propertyResource);
}
