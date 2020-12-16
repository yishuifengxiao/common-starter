package com.yishuifengxiao.common.security.websecurity.impl;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.firewall.DefaultHttpFirewall;

import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;

/**
 * 
 * Firewall安全授权器<br/>
 * <br/>
 * 主要是解决问题 ： The request was rejected because the URL was not normalized
 * ,解决路径里包含 // 路径报错的问题
 * 
 * @author yishui
 * @date 2019年11月7日
 * @version 1.0.0
 */
public class FirewallWebSecurityProvider implements WebSecurityProvider {

	@Override
	public void configure(PropertyResource propertyResource, WebSecurity web) throws Exception {
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		web.httpFirewall(firewall);

	}

}
