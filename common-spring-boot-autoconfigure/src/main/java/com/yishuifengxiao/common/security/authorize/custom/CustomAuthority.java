/**
 * 
 */
package com.yishuifengxiao.common.security.authorize.custom;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/**
 * 自定义授权接口
 * @author yishui
 * @date 2019年1月9日
 * @version 0.0.1 
 */
public interface CustomAuthority {
	/**
	 * 自定义权限判断 
	 * @param request HttpServletRequest
	 * @param auth Authentication
	 * @return true表示允许授权
	 */
	public boolean hasPermission(HttpServletRequest request, Authentication auth);
}
