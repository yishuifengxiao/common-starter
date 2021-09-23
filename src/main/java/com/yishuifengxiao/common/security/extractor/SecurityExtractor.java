package com.yishuifengxiao.common.security.extractor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 信息提取器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SecurityExtractor {

	/**
	 * 从请求中提取出用户的登陆账号
	 * 
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @return 用户的登陆账号
	 */
	String extractUsername(HttpServletRequest request, HttpServletResponse response);

	/**
	 * 从请求中提取出用户的登陆密码
	 * 
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @return 用户的登陆密码
	 */
	String extractPassword(HttpServletRequest request, HttpServletResponse response);

	/**
	 * 从请求中提取出用户的唯一标识符
	 * 
	 * @param request  HttpServletRequest
	 * @param response HttpServletResponse
	 * @return 用户的唯一标识符
	 */
	String extractUserUniqueIdentitier(HttpServletRequest request, HttpServletResponse response);

}
