/**
 *
 */
package com.yishuifengxiao.common.security.httpsecurity.authorize.provider;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * spring security记住我功能而定相关配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class RemeberMeAuthorizeProvider implements AuthorizeProvider {

    /**
     * 记住我功能的实现
     */
    protected PersistentTokenRepository persistentTokenRepository;

    /**
     * 自定义UserDetailsService实现类，查找用户
     */
    protected UserDetailsService userDetailsService;

    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off
		//记住我的功能
        http.rememberMe(rememberMeCustomizer->{
            rememberMeCustomizer
                    .alwaysRemember(securityPropertyResource.security().getRememberMe().getAlwaysRemember())
                    //是否使用安全cookie
                    .useSecureCookie(securityPropertyResource.security().getRememberMe().getUseSecureCookie())
                    //记住我产生的token的key
                    .key(securityPropertyResource.security().getRememberMe().getKey())
                    .rememberMeParameter(securityPropertyResource.security().getRememberMe().getRememberMeParameter())
                    //记住我的实现
                    .tokenRepository(persistentTokenRepository)
                    //记住我的时间
                    .tokenValiditySeconds(securityPropertyResource.security().getRememberMe().getRememberMeSeconds())
                    //记住我的验证逻辑
                    .userDetailsService(userDetailsService);
        });


		//@formatter:on  
    }

    @Override
    public int order() {
        return 300;
    }

    public PersistentTokenRepository getPersistentTokenRepository() {
        return persistentTokenRepository;
    }

    public void setPersistentTokenRepository(PersistentTokenRepository persistentTokenRepository) {
        this.persistentTokenRepository = persistentTokenRepository;
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

}
