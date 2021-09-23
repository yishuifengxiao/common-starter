/**
 * 
 */
package com.yishuifengxiao.common.security.provider.custom;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/**
 * <p>
 * 自定义授权提供器
 * </p>
 * 
 * 用户根据自己实际项目需要确定如何根据实际项目变化配置是否给予授权,
 * 
 * 在使用时，自定义授权提供器实例会被注入到<code>CustomAuthorizeProvider</code>中
 * 
 * 
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CustomResourceProvider {
	/**
	 * 自定义权限判断
	 * 
	 * @param request HttpServletRequest
	 * @param auth    Authentication
	 * @return true表示允许授权
	 */
	public boolean hasPermission(HttpServletRequest request, Authentication auth);
}
