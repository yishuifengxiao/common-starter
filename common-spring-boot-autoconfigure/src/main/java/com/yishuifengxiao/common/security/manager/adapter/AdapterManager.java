package com.yishuifengxiao.common.security.manager.adapter;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
/**
 * 适配器管理器
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public interface AdapterManager {

	/**
	 * 配置自定义适配器
	 * 
	 * @param http
	 * @throws Exception 
	 */
	void config(HttpSecurity http) throws Exception;
}
