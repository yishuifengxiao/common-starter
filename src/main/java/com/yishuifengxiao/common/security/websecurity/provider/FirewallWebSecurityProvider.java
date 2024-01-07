package com.yishuifengxiao.common.security.websecurity.provider;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.firewall.DefaultHttpFirewall;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;

/**
 * 
 * <p>
 * Firewall安全授权器
 * </p>
 * 主要是解决问题 ： The request was rejected because the URL was not normalized
 * ,解决路径里包含 // 路径报错的问题
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class FirewallWebSecurityProvider implements WebSecurityProvider {

	@Override
	public void configure(SecurityPropertyResource securityPropertyResource, WebSecurity web) {
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		web.httpFirewall(firewall);

	}

}
