/**
 * 
 */
package com.yishuifengxiao.common.security.resource;

import java.util.List;
import java.util.Set;

import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.social.SocialProperties;

/**
 * 资源管理器<br/>
 * 管理系统中所有的资源
 * 
 * @author qingteng
 * @date 2020年11月27日
 * @version 1.0.0
 */
public interface PropertyResource {

	/**
	 * spring security 相关的配置
	 * 
	 * @return
	 */
	SecurityProperties security();

	/**
	 * spring social 相关的配置
	 * 
	 * @return
	 */
	SocialProperties social();

	/**
	 * 获取所有直接放行的资源
	 * 
	 * @return 直接放行的资源
	 */
	Set<String> getAllPermitUlrs();

	/**
	 * 获取所有不经过资源服务器授权管理的资源
	 * 
	 * @return 不经过资源服务器授权管理的资源
	 */
	List<String> getExcludeUrls();

	/**
	 * 获取所有自定义权限的资源
	 * 
	 * @return 自定义权限的资源
	 */
	Set<String> getAllCustomUrls();

	/**
	 * 获取所有不需要经过token校验的资源的路径
	 * 
	 * @return 不需要经过token校验的资源的路径
	 */
	Set<String> getAllUnCheckUrls();

	/**
	 * 获取所有忽视的资源
	 * 
	 * @return 忽视的资源
	 */
	String[] getAllIgnoreUrls();

}
