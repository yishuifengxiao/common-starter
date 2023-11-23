package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.code.CodeProducer;
import com.yishuifengxiao.common.code.holder.CodeHolder;
import com.yishuifengxiao.common.security.httpsecurity.AbstractSecurityRequestFilter;
import com.yishuifengxiao.common.security.httpsecurity.filter.AbstractSecurityTokenValidateFilter;
import com.yishuifengxiao.common.security.httpsecurity.filter.ValidateCodeFilterAbstract;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.extractor.SecurityTokenResolver;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.security.token.extractor.SimpleSecurityTokenResolver;
import com.yishuifengxiao.common.security.token.extractor.SimpleSecurityValueExtractor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.servlet.ServletException;

/**
 * spring security扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {"enable"}, havingValue = "true")
public class SecurityFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({SecurityTokenResolver.class})
    public SecurityTokenResolver securityTokenResolver() {
        return new SimpleSecurityTokenResolver();
    }


    @Bean
    @ConditionalOnMissingBean({SecurityValueExtractor.class})
    public SecurityValueExtractor securityContextExtractor(PropertyResource propertyResource) {
        SimpleSecurityValueExtractor simpleSecurityExtractor = new SimpleSecurityValueExtractor(propertyResource);
        return simpleSecurityExtractor;
    }


    @Bean("securityTokenValidateFilter")
    @ConditionalOnMissingBean(name = {"securityTokenValidateFilter"})
    public AbstractSecurityRequestFilter securityTokenValidateFilter(PropertyResource propertyResource,
                                                                     SecurityHandler securityHandler,
                                                                     SecurityTokenResolver securityTokenResolver,
                                                                     TokenBuilder tokenBuilder) throws ServletException {

        AbstractSecurityTokenValidateFilter securityTokenValidateFilter = new AbstractSecurityTokenValidateFilter(propertyResource,
                securityHandler, securityTokenResolver, tokenBuilder);
        securityTokenValidateFilter.afterPropertiesSet();
        return securityTokenValidateFilter;
    }

    /**
     * 注入一个验证码过滤器
     *
     * @param codeProducer     验证码处理器
     * @param propertyResource 安全属性配置
     * @param securityHandler  协助处理器
     * @return 验证码过滤器
     */
    @Bean("validateCodeFilter")
    @ConditionalOnMissingBean(name = "validateCodeFilter")
    @ConditionalOnBean({CodeHolder.class})
    public AbstractSecurityRequestFilter validateCodeFilter(CodeProducer codeProducer, PropertyResource propertyResource,
                                                            SecurityHandler securityHandler) {
        ValidateCodeFilterAbstract validateCodeFilter = new ValidateCodeFilterAbstract();
        validateCodeFilter.setCodeProducer(codeProducer);
        validateCodeFilter.setPropertyResource(propertyResource);
        validateCodeFilter.setSecurityHandler(securityHandler);
        return validateCodeFilter;
    }


}
