package com.yishuifengxiao.common.security.authorize.intercept;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 资源授权配置器<br/>
 * 配置哪些资源需要经过权限处理
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public interface AuthorizeResourceProvider {
	/**
	 * 权限配置
	 * 
	 * @param http
	 * @throws Exception
	 */
	void configure(HttpSecurity http) throws Exception;

}
