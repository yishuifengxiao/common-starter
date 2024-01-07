package com.yishuifengxiao.common.security.support.impl;

import com.yishuifengxiao.common.security.constant.UriConstant;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.AbstractSecurityGlobalEnhanceFilter;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;


import java.io.IOException;
import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleSecurityGlobalEnhanceFilter extends AbstractSecurityGlobalEnhanceFilter {


    private RequestMatcher requestMatcher = null;

    private SecurityPropertyResource securityPropertyResource;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String formActionUrl = securityPropertyResource.contextPath() + securityPropertyResource.security().getFormActionUrl();
        String forgotPasswordUrl = StringUtils.isNotBlank(securityPropertyResource.security().getForgotPasswordUrl()) ? securityPropertyResource.contextPath() + securityPropertyResource.security().getForgotPasswordUrl().trim() : "";
        String loginPage = securityPropertyResource.contextPath() + securityPropertyResource.security().getLoginPage();
        String registerUrl = StringUtils.isNotBlank(securityPropertyResource.security().getRegisterUrl()) ? securityPropertyResource.contextPath() + securityPropertyResource.security().getRegisterUrl().trim() : "";
        HttpUtils.write(request, response, Response.sucData(new SecurityMeta(formActionUrl,
                loginPage,
                securityPropertyResource.security().getRememberMe().getRememberMeParameter(),
                forgotPasswordUrl,
                registerUrl
        )));
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

        /**
         * 登录页面表单提交地址
         */
        private String formActionUrl;

        /**
         * 默认的登录页面地址
         */
        private String defaultLoginUrl;

        private String rememberMeParameter;


        /**
         * 忘记密码的地址
         */
        private String forgotPasswordUrl;

        /**
         * 注册地址
         */
        private String registerUrl;
    }

    public SimpleSecurityGlobalEnhanceFilter(SecurityPropertyResource securityPropertyResource) {
        this.securityPropertyResource = securityPropertyResource;
        this.requestMatcher = new AntPathRequestMatcher(DEFAULT_SECURITY_AUTHORIZATION_SERVER_METADATA_ENDPOINT_URI, HttpMethod.GET.name());
    }
}
