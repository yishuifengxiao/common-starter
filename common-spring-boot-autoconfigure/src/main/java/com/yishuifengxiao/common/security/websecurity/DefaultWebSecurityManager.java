package com.yishuifengxiao.common.security.websecurity;

import java.util.List;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.security.authorize.ignore.IgnoreResourceProvider;
import com.yishuifengxiao.common.security.websecurity.adapter.WebSecurityAdapter;

/**
 * 默认的 WebSecurity 管理器
 * 
 * @author yishui
 * @date 2019年11月7日
 * @version 1.0.0
 */
public class DefaultWebSecurityManager implements WebSecurityManager {

	/**
	 * 忽视资源管理
	 */
	private IgnoreResourceProvider ignoreResourceProvider;

	private List<WebSecurityAdapter> webSecurityAdapters;

	@Override
	public void configure(WebSecurity web) throws Exception {
		// 配置忽视资源
		ignoreResourceProvider.configure(web.ignoring());
		// 配置其他的管理
		if (webSecurityAdapters != null) {
			for (WebSecurityAdapter webSecurityAdapter : webSecurityAdapters) {
				webSecurityAdapter.configure(web);
			}
		}
	}



	public IgnoreResourceProvider getIgnoreResourceProvider() {
		return ignoreResourceProvider;
	}



	public void setIgnoreResourceProvider(IgnoreResourceProvider ignoreResourceProvider) {
		this.ignoreResourceProvider = ignoreResourceProvider;
	}



	public List<WebSecurityAdapter> getWebSecurityAdapters() {
		return webSecurityAdapters;
	}

	public void setWebSecurityAdapters(List<WebSecurityAdapter> webSecurityAdapters) {
		this.webSecurityAdapters = webSecurityAdapters;
	}

}
