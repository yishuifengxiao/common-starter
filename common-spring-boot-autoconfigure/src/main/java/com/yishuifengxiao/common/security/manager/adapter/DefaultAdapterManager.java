package com.yishuifengxiao.common.security.manager.adapter;

import java.util.List;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.yishuifengxiao.common.security.adapter.AbstractSecurityAdapter;

/**
 * 默认实现的适配器管理器
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public class DefaultAdapterManager implements AdapterManager {

	protected List<AbstractSecurityAdapter> securityAdapters;

	@Override
	public void config(HttpSecurity http) throws Exception {
		if (securityAdapters != null) {
			for (AbstractSecurityAdapter securityAdapter : securityAdapters) {
				http.apply(securityAdapter);
			}
		}

	}

	public DefaultAdapterManager() {

	}

	public DefaultAdapterManager(List<AbstractSecurityAdapter> securityAdapters) {

		this.securityAdapters = securityAdapters;
	}

	public List<AbstractSecurityAdapter> getSecurityAdapters() {
		return securityAdapters;
	}

	public void setSecurityAdapters(List<AbstractSecurityAdapter> securityAdapters) {
		this.securityAdapters = securityAdapters;
	}

}
