package com.yishuifengxiao.common.security.authorize.ignore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.properties.SecurityProperties;

public class DefaultIgnoreResourcesConfig implements IgnoreResourcesConfig {
	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;
	@Override
	public void configure(WebSecurity web) throws Exception {

		// @formatter:off
		web
		.ignoring()
		.antMatchers(HttpMethod.OPTIONS, "/**")
		.mvcMatchers(securityProperties.getIgnore().getIgnore())
		.antMatchers(securityProperties.getIgnore().getIgnore())// 设置忽视目录
		;
		// .antMatchers("/**/**.js", "/lang/*.json", "/**/**.css", "/**/**.js",
		// "/**/**.map", "/**/**.html","/**/**.jsp",
		// "/**/**.png")
		// .antMatchers("/zui/**","/js/**","/images/**")
		// .antMatchers("/uuac/zui/**","/uuac/js/**","/uuac/images/**")
		// .antMatchers("/webjars/**", "/images/**",
		// "/swagger-ui.html","/swagger-resources/**","/v2/api-docs","/configuration/ui","/configuration/security","/actuator/**");
		// @formatter:on
	
	}
	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}
	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}
	
	

}
