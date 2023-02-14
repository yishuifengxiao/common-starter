package com.yishuifengxiao.common.social;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.List;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@AutoConfigureBefore({SecurityAutoConfiguration.class})
@ConditionalOnClass({EnableWebSecurity.class, ClientRegistration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"sso"}, havingValue = "true", matchIfMissing = true)
@Configuration
public class SpringSocialAutoConfiguration {

    @Bean
    public AuthorizeProvider socialAuthorizeProvider(@Autowired(required = false) List<SocialProvider> socialProviders) throws Exception {
        SocialAuthorizeProvider authorizeProvider = new SocialAuthorizeProvider();
        authorizeProvider.setProviders(socialProviders);
        authorizeProvider.afterPropertiesSet();
        return authorizeProvider;
    }
}
