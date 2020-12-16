package com.yishuifengxiao.common.security.authorize;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * <strong>安全管理器 </strong><br/>
 * <br/>
 * 收集系统中的授权配置管理器(<code>AuthorizeConfigManager</code>)和资源授权拦截器<code>HttpSecurityInterceptor</code>实例，可以配置多个<code>HttpSecurityInterceptor</code>实例
 * 然后将收集到的实例配置spring security中
 * 
 * @see AuthorizeConfigManager
 * @see HttpSecurityInterceptor
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public interface SecurityContextManager {

	/**
	 * 配置安全管理器
	 * 
	 * @param http
	 * @throws Exception
	 */
	void config(HttpSecurity http) throws Exception;

	/**
	 * 配置WebSecurity
	 * 
	 * @param web
	 * @throws Exception
	 */
	void config(WebSecurity web) throws Exception;
}
