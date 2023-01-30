package com.yishuifengxiao.common.security.autoconfigure;

import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.yishuifengxiao.common.code.CodeProcessor;
import com.yishuifengxiao.common.code.repository.CodeRepository;
import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.httpsecurity.authorize.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.httpsecurity.filter.SecurityRequestFilter;
import com.yishuifengxiao.common.security.httpsecurity.filter.extractor.SecurityContextExtractor;
import com.yishuifengxiao.common.security.httpsecurity.filter.extractor.SecurityTokenExtractor;
import com.yishuifengxiao.common.security.httpsecurity.filter.extractor.impl.SimpleSecurityContextExtractor;
import com.yishuifengxiao.common.security.httpsecurity.filter.extractor.impl.SimpleSecurityTokenExtractor;
import com.yishuifengxiao.common.security.httpsecurity.filter.impl.TokenValidateFilter;
import com.yishuifengxiao.common.security.httpsecurity.filter.impl.UsernamePasswordAuthFilter;
import com.yishuifengxiao.common.security.httpsecurity.filter.impl.ValidateCodeFilter;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHelper;

/**
 * spring security扩展支持自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnBean(AbstractSecurityConfig.class)
@ConditionalOnClass({DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
        WebSecurityConfigurerAdapter.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
        "enable"}, havingValue = "true", matchIfMissing = true)
public class SecurityFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({SecurityTokenExtractor.class})
    public SecurityTokenExtractor securityTokenExtractor() {
        return new SimpleSecurityTokenExtractor();
    }


    @Bean
    @ConditionalOnMissingBean({SecurityContextExtractor.class})
    public SecurityContextExtractor securityContextExtractor(PropertyResource propertyResource) {
        SimpleSecurityContextExtractor simpleSecurityExtractor = new SimpleSecurityContextExtractor(propertyResource);
        return simpleSecurityExtractor;
    }

    @Bean("usernamePasswordAuthFilter")
    @ConditionalOnMissingBean(name = {"usernamePasswordAuthFilter"})
    public SecurityRequestFilter usernamePasswordAuthFilter(HandlerProcessor handlerProcessor, SecurityHelper securityHelper, PropertyResource propertyResource, SecurityContextExtractor securityContextExtractor) {
        UsernamePasswordAuthFilter usernamePasswordAuthFilter = new UsernamePasswordAuthFilter(handlerProcessor, securityHelper, propertyResource, securityContextExtractor);
        return usernamePasswordAuthFilter;
    }

    @Bean("securityTokenValidateFilter")
    @ConditionalOnMissingBean(name = {"securityTokenValidateFilter"})
    public SecurityRequestFilter securityTokenValidateFilter(PropertyResource propertyResource, HandlerProcessor handlerProcessor, SecurityTokenExtractor securityTokenExtractor, SecurityHelper securityHelper) throws ServletException {

        TokenValidateFilter tokenValidateFilter = new TokenValidateFilter(propertyResource, handlerProcessor, securityTokenExtractor, securityHelper);
        tokenValidateFilter.afterPropertiesSet();
        return tokenValidateFilter;
    }

    /**
     * 注入一个验证码过滤器
     *
     * @param codeProcessor    验证码处理器
     * @param propertyResource 安全属性配置
     * @param handlerProcessor 协助处理器
     * @return 验证码过滤器
     */
    @Bean("validateCodeFilter")
    @ConditionalOnMissingBean(name = "validateCodeFilter")
    @ConditionalOnBean({CodeRepository.class})
    public SecurityRequestFilter validateCodeFilter(CodeProcessor codeProcessor, PropertyResource propertyResource, HandlerProcessor handlerProcessor) {
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setCodeProcessor(codeProcessor);
        validateCodeFilter.setPropertyResource(propertyResource);
        validateCodeFilter.setHandlerProcessor(handlerProcessor);
        return validateCodeFilter;
    }


}
