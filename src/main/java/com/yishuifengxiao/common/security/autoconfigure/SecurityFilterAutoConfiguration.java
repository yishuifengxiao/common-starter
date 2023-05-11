package com.yishuifengxiao.common.security.autoconfigure;

import com.yishuifengxiao.common.code.CodeProcessor;
import com.yishuifengxiao.common.code.repository.CodeRepository;
import com.yishuifengxiao.common.security.httpsecurity.SecurityRequestFilter;
import com.yishuifengxiao.common.security.httpsecurity.filter.TokenValidateFilter;
import com.yishuifengxiao.common.security.httpsecurity.filter.UsernamePasswordPreAuthFilter;
import com.yishuifengxiao.common.security.httpsecurity.filter.ValidateCodeFilter;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeHelper;
import com.yishuifengxiao.common.security.token.extractor.SecurityTokenExtractor;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.security.token.extractor.SimpleSecurityTokenExtractor;
import com.yishuifengxiao.common.security.token.extractor.SimpleSecurityValueExtractor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

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
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
        "enable"}, havingValue = "true", matchIfMissing = true)
public class SecurityFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({SecurityTokenExtractor.class})
    public SecurityTokenExtractor securityTokenExtractor() {
        return new SimpleSecurityTokenExtractor();
    }


    @Bean
    @ConditionalOnMissingBean({SecurityValueExtractor.class})
    public SecurityValueExtractor securityContextExtractor(PropertyResource propertyResource) {
        SimpleSecurityValueExtractor simpleSecurityExtractor = new SimpleSecurityValueExtractor(propertyResource);
        return simpleSecurityExtractor;
    }


    @Bean("usernamePasswordPreAuthFilter")
    @ConditionalOnMissingBean(name = {"usernamePasswordPreAuthFilter"})
    public SecurityRequestFilter usernamePasswordPreAuthFilter(SecurityHandler securityHandler,
                                                               UserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder,
                                                               PropertyResource propertyResource,
                                                               SecurityValueExtractor securityValueExtractor) {
        UsernamePasswordPreAuthFilter usernamePasswordPreAuthFilter = new UsernamePasswordPreAuthFilter(securityHandler, userDetailsService, passwordEncoder, propertyResource, securityValueExtractor);
        return usernamePasswordPreAuthFilter;
    }

    @Bean("securityTokenValidateFilter")
    @ConditionalOnMissingBean(name = {"securityTokenValidateFilter"})
    public SecurityRequestFilter securityTokenValidateFilter(PropertyResource propertyResource, SecurityHandler securityHandler, SecurityTokenExtractor securityTokenExtractor, AuthorizeHelper authorizeHelper) throws ServletException {

        TokenValidateFilter tokenValidateFilter = new TokenValidateFilter(propertyResource, securityHandler, securityTokenExtractor, authorizeHelper);
        tokenValidateFilter.afterPropertiesSet();
        return tokenValidateFilter;
    }

    /**
     * 注入一个验证码过滤器
     *
     * @param codeProcessor    验证码处理器
     * @param propertyResource 安全属性配置
     * @param securityHandler  协助处理器
     * @return 验证码过滤器
     */
    @Bean("validateCodeFilter")
    @ConditionalOnMissingBean(name = "validateCodeFilter")
    @ConditionalOnBean({CodeRepository.class})
    public SecurityRequestFilter validateCodeFilter(CodeProcessor codeProcessor, PropertyResource propertyResource, SecurityHandler securityHandler) {
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setCodeProcessor(codeProcessor);
        validateCodeFilter.setPropertyResource(propertyResource);
        validateCodeFilter.setSecurityHandler(securityHandler);
        return validateCodeFilter;
    }


}
