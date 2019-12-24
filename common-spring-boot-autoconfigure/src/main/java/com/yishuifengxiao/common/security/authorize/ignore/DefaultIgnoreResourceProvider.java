package com.yishuifengxiao.common.security.authorize.ignore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity.IgnoredRequestConfigurer;

import com.yishuifengxiao.common.properties.SecurityProperties;

/**
 * 配置忽视的资源
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class DefaultIgnoreResourceProvider implements IgnoreResourceProvider {
	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;

	@Override
	public void configure(IgnoredRequestConfigurer ignoring) throws Exception {

		// @formatter:off
		ignoring
		.antMatchers(HttpMethod.OPTIONS, "/**")
		.antMatchers("/oauth/check_token")
		.mvcMatchers(securityProperties.getIgnore().getIgnore())
		//// 设置忽视目录
		.antMatchers(securityProperties.getIgnore().getIgnore())
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
