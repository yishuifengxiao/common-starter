package com.yishuifengxiao.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.yishuifengxiao.common.security.authorize.SecurityContextManager;

/**
 * 安全服务器配置
 * 
 * @author yishui
 * @date 2018年11月19日
 * @Version 0.0.1
 */
public abstract class AbstractSecurityConfig extends WebSecurityConfigurerAdapter {



	/**
	 * 安全授权配置管理器
	 */
	@Autowired
	protected SecurityContextManager securityContextManager;

	@Autowired
	@Qualifier("authenticationProvider")
	protected DaoAuthenticationProvider authenticationProvider;

	@Autowired
	public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {

		// @formatter:off
		// auth.inMemoryAuthentication().withUser("yishui").password(passwordEncoder.encode("12345678")).roles("ADMIN").and()
		// .withUser("bob").password("abc123").roles("USER");
		// 此设置会导致auth.authenticationProvider(authenticationProvider) 无效
		//auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		auth.authenticationProvider(authenticationProvider);
		// @formatter:on
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		this.applyAuthenticationConfig(http);
	}

	/**
	 * 默认的spring security配置【需要在子类中调用此方法】
	 * 
	 * @param http
	 * @throws Exception
	 */
	private void applyAuthenticationConfig(HttpSecurity http) throws Exception {

		// @formatter:off

		// 注入所有的自定义授权适配器
		securityContextManager.config(http);

		// .anonymous().disable()//禁止匿名访问要放在后面
		// @formatter:on

	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * 设置忽视的目录
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		securityContextManager.config(web);
	}

	public SecurityContextManager getSecurityContextManager() {
		return securityContextManager;
	}

	public void setSecurityContextManager(SecurityContextManager securityContextManager) {
		this.securityContextManager = securityContextManager;
	}

}