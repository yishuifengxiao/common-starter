package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.AbstractSecurityGlobalEnhanceFilter;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.support.impl.BaseSecurityHandler;
import com.yishuifengxiao.common.security.support.impl.SimpleAuthenticationPoint;
import com.yishuifengxiao.common.security.support.impl.SimpleSecurityGlobalEnhanceFilter;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.web.WebEnhanceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/7 19:44
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true")
public class SecuritySupportAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityHandler securityHandler() {
        BaseSecurityHandler baseSecurityHandler = new BaseSecurityHandler();
        return baseSecurityHandler;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationPoint authenticationPoint(SecurityPropertyResource securityPropertyResource,
                                                   SecurityValueExtractor securityValueExtractor,
                                                   SecurityHandler securityHandler,
                                                   WebEnhanceProperties webEnhanceProperties,
                                                   TokenBuilder tokenBuilder) {
        SimpleAuthenticationPoint authenticationPoint = new SimpleAuthenticationPoint();
        authenticationPoint.setPropertyResource(securityPropertyResource);
        authenticationPoint.setTokenBuilder(tokenBuilder);
        authenticationPoint.setSecurityContextExtractor(securityValueExtractor);
        authenticationPoint.setSecurityHandler(securityHandler);
        authenticationPoint.setCorsProperties(webEnhanceProperties.getCors());
        return authenticationPoint;
    }

    /**
     * 全局增强功能
     *
     * @param securityPropertyResource
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({AbstractSecurityGlobalEnhanceFilter.class})
    public AbstractSecurityGlobalEnhanceFilter simpleSecurityGlobalEnhanceFilter(SecurityPropertyResource securityPropertyResource) {
        return new SimpleSecurityGlobalEnhanceFilter(securityPropertyResource);
    }

}
