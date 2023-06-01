package com.yishuifengxiao.common.security.support.impl;

import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.constant.UriConstant;
import com.yishuifengxiao.common.security.support.SecurityGlobalEnhance;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleSecurityGlobalEnhance extends SecurityGlobalEnhance {


    private final RequestMatcher requestMatcher =
            new AntPathRequestMatcher(DEFAULT_SECURITY_AUTHORIZATION_SERVER_METADATA_ENDPOINT_URI,
                    HttpMethod.GET.name());

    private SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!this.requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String formActionUrl = securityProperties.getFormActionUrl();
        final String forgotPasswordUrl = securityProperties.getForgotPasswordUrl();
        HttpUtils.write(request, response, Response.sucData(new SecurityMeta(formActionUrl,
                securityProperties.getLoginPage(), securityProperties.getRemeberMe().getRememberMeParameter(),
                StringUtils.isNotBlank(forgotPasswordUrl) ? forgotPasswordUrl.trim() : "")));
        return;
    }

    /**
     * 配置静态资源路径,防止出现访问swagger-ui界面时出现404
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // @formatter:off
        registry.addResourceHandler(UriConstant.DEFAULT_LOGIN_URL + "**")
                .addResourceLocations(
                        "classpath:/webjars/security-enhance-ui/",
                        "classpath*:/webjars/security-enhance-ui/"
                );
        // @formatter:on
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // @formatter:off
        registry.addViewController(UriConstant.DEFAULT_LOGIN_URL)
                .setViewName("forward:" + UriConstant.DEFAULT_LOGIN_URL + "index" + ".html");
        // @formatter:on
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SecurityMeta implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 7993354065107286911L;

        private String formActionUrl;

        private String defaultLoginUrl;

        private String rememberMeParameter;


        /**
         * 忘记密码的地址
         */
        private String forgotPasswordUrl;
    }

    public SimpleSecurityGlobalEnhance(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
}
