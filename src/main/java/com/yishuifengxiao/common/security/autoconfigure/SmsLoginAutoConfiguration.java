package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.smsauth.SmsAuthorizeProvider;
import com.yishuifengxiao.common.security.smsauth.sms.SmsUserDetailsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * spring security扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true", matchIfMissing = true)
public class SmsLoginAutoConfiguration {


    /**
     * 注入短信登录配置
     * <p>
     * 配置短信验证码登陆功能
     * </p>
     * 要想使短信验证码功能生效，需要配置： 1
     * 先配置一个短信登陆地址属性(<code>yishuifengxiao.security.code.sms-login-url</code>), 2
     * 再配置一个名为 smsUserDetailsService 的 <code>UserDetailsService</code> 实例
     *
     * @param smsUserDetailsService        短信登陆逻辑
     * @param securityProperties           安全属性配置
     * @return 资源授权拦截器实例
     */
    @Bean("smsLoginInterceptor")
    @ConditionalOnProperty(prefix = "yishuifengxiao.security.code", name = "sms-login-url")
    @ConditionalOnMissingBean(name = "smsLoginInterceptor")
    @ConditionalOnBean({SmsUserDetailsService.class})
    public AuthorizeProvider smsLoginInterceptor(SmsUserDetailsService smsUserDetailsService, SecurityProperties securityProperties) {

        return new SmsAuthorizeProvider(smsUserDetailsService, securityProperties.getCode().getSmsLoginUrl());
    }


}
