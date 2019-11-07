package com.yishuifengxiao.common.security.websecurity.adapter;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * WebSecurity 管理适配器
 * 
 * @author yishui
 * @date 2019年11月7日
 * @version 1.0.0
 */
public interface WebSecurityAdapter {
	/**
	 * 配置WebSecurity 管理
	 * 
	 * @param web
	 * @throws Exception
	 */
	void configure(WebSecurity web) throws Exception;
}
