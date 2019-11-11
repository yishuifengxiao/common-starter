package com.yishuifengxiao.common.security.websecurity.adapter;

import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
/**
 * 默认的WebSecurityAdapter实现器。
 * 主要是解决问题 ： The request was rejected because the URL was not normalized ,解决路径里包含 // 路径报错的问题
 * @author yishui
 * @date 2019年11月7日
 * @version 1.0.0
 */
public class DefaultWebSecurityAdapter implements WebSecurityAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		web.httpFirewall(firewall);
	}

}
