package com.yishuifengxiao.common.security.manager;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 安全管理器管理器
 * 
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
}
