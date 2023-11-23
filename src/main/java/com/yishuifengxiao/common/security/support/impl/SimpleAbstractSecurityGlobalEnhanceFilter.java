package com.yishuifengxiao.common.security.support.impl;

import com.yishuifengxiao.common.security.constant.UriConstant;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.AbstractSecurityGlobalEnhanceFilter;
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
public class SimpleAbstractSecurityGlobalEnhanceFilter extends AbstractSecurityGlobalEnhanceFilter {


    private RequestMatcher requestMatcher = null;

    private PropertyResource propertyResource;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.requestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String formActionUrl = propertyResource.contextPath() + propertyResource.security().getFormActionUrl();
        String forgotPasswordUrl = StringUtils.isNotBlank(propertyResource.security().getForgotPasswordUrl()) ? propertyResource.contextPath() + propertyResource.security().getForgotPasswordUrl().trim() : "";
        String loginPage = propertyResource.contextPath() + propertyResource.security().getLoginPage();
        String registerUrl = StringUtils.isNotBlank(propertyResource.security().getRegisterUrl()) ? propertyResource.contextPath() + propertyResource.security().getRegisterUrl().trim() : "";
        HttpUtils.write(request, response, Response.sucData(new SecurityMeta(formActionUrl,
                loginPage,
                propertyResource.security().getRemeberMe().getRememberMeParameter(),
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

    public SimpleAbstractSecurityGlobalEnhanceFilter(PropertyResource propertyResource) {
        this.propertyResource = propertyResource;
        this.requestMatcher = new AntPathRequestMatcher(DEFAULT_SECURITY_AUTHORIZATION_SERVER_METADATA_ENDPOINT_URI, HttpMethod.GET.name());
    }
}
