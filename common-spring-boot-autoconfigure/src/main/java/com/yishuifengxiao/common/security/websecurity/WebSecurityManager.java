package com.yishuifengxiao.common.security.websecurity;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * WebSecurity 管理
 * 
 * @author yishui
 * @date 2019年11月7日
 * @version 1.0.0
 */
public interface WebSecurityManager {
	/**
	 * 配置WebSecurity 管理
	 * 
	 * @param web
	 * @throws Exception
	 */
	void configure(WebSecurity web) throws Exception;
}
