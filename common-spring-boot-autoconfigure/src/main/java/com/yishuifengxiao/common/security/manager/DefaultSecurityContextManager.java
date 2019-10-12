package com.yishuifengxiao.common.security.manager;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.manager.adapter.AdapterManager;
import com.yishuifengxiao.common.security.manager.authorize.AuthorizeConfigManager;

/**
 * 默认的安全管理器
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public class DefaultSecurityContextManager implements SecurityContextManager {

	private AuthorizeConfigManager authorizeConfigManager;

	private AdapterManager adapterManager;

	@Override
	public void config(HttpSecurity http) throws Exception {

		// 加入自定义的授权配置
		authorizeConfigManager.config(http.authorizeRequests());
		// 加入自定义授权适配器配置
		adapterManager.config(http);
	}

	public DefaultSecurityContextManager() {

	}

	public DefaultSecurityContextManager(AuthorizeConfigManager authorizeConfigManager, AdapterManager adapterManager) {

		this.authorizeConfigManager = authorizeConfigManager;
		this.adapterManager = adapterManager;
	}

	public AuthorizeConfigManager getAuthorizeConfigManager() {
		return authorizeConfigManager;
	}

	public void setAuthorizeConfigManager(AuthorizeConfigManager authorizeConfigManager) {
		this.authorizeConfigManager = authorizeConfigManager;
	}

	public AdapterManager getAdapterManager() {
		return adapterManager;
	}

	public void setAdapterManager(AdapterManager adapterManager) {
		this.adapterManager = adapterManager;
	}

}
