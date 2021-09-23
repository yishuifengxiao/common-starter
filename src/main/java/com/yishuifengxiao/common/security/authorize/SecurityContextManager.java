package com.yishuifengxiao.common.security.authorize;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * <p>安全管理器 </p>

 * 收集系统中的授权配置管理器(<code>AuthorizeConfigManager</code>)和资源授权拦截器<code>HttpSecurityInterceptor</code>实例，可以配置多个<code>HttpSecurityInterceptor</code>实例
 * 然后将收集到的实例配置spring security中
 * 

 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SecurityContextManager {

	/**
	 * 配置安全管理器
	 * 
	 * @param http HttpSecurity
	 * @throws Exception 配置时发生问题
	 */
	void config(HttpSecurity http) throws Exception;

	/**
	 * 配置WebSecurity
	 * 
	 * @param web WebSecurity
	 * @throws Exception 配置时发生问题
	 */
	void config(WebSecurity web) throws Exception;
}
