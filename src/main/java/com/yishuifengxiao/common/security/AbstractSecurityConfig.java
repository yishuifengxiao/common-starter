package com.yishuifengxiao.common.security;

import com.yishuifengxiao.common.security.user.GlobalUserDetails;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityManager;
import com.yishuifengxiao.common.security.websecurity.WebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 安全服务器配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 安全授权配置管理器
     */
    @Autowired
    protected HttpSecurityManager httpSecurityManager;

    @Autowired
    private WebSecurityManager webSecurityManager;

    @Autowired
    protected GlobalUserDetails globalUserDetails;

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {

        // @formatter:off
		// auth.inMemoryAuthentication().withUser("yishui").password(passwordEncoder.encode("12345678")).roles("ADMIN").and()
		// .withUser("bob").password("abc123").roles("USER");
		// 此设置会导致auth.authenticationProvider(authenticationProvider) 无效
		//auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		auth.authenticationProvider(globalUserDetails.authenticationProvider());
		// @formatter:on

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        httpSecurityManager.config(http);
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        //设置忽视的目录
        webSecurityManager.config(web);
    }

}