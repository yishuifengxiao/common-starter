/**
 *
 */
package com.yishuifengxiao.common.security.support.impl;

import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.SecurityValueExtractor;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.tool.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 协助处理器
 * </p>
 *
 * 用于在各种 Handler 中根据情况相应地跳转到指定的页面或者输出json格式的数据
 *
 * @see AuthenticationEntryPoint
 * @see AccessDeniedHandler
 * @see AuthenticationFailureHandler
 * @see AuthenticationFailureHandler
 * @see AuthenticationSuccessHandler
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleAuthenticationPoint implements AuthenticationPoint {

    protected PropertyResource propertyResource;
    /**
     * 信息提取器
     */
    protected SecurityValueExtractor securityValueExtractor;

    /**
     * 安全处理工具
     */
    protected SecurityHelper securityHelper;
    /**
     * token生成器
     */
    protected TokenBuilder tokenBuilder;


    private SecurityHandler securityHandler;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        securityHandler.whenAccessDenied(propertyResource, request, response, accessDeniedException);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException authenticationException) throws IOException {

                securityHandler.whenAuthenticationFailure(propertyResource, request, response, authenticationException);

            }
        }.onAuthenticationFailure(request, response, exception);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        new SavedRequestAwareAuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

                try {
                    // 根据登陆信息生成一个token
                    String deviceId = securityValueExtractor.extractDeviceId(request, response);

                    SecurityToken token = securityHelper.createUnsafe(authentication.getName(), deviceId);

                    // 将生成的token存储在session中
                    request.getSession().setAttribute(propertyResource.security().getToken().getUserDeviceId(), token.getValue());
                    // 登陆成功
                    securityHandler.whenAuthenticationSuccess(propertyResource, request, response, authentication, token);
                } catch (CustomException e) {
                    securityHandler.whenAuthenticationFailure(propertyResource, request, response, new AuthenticationServiceException(e.getMessage()));
                }

            }

        }.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        new SimpleUrlLogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                    throws IOException, ServletException {

                try {
                    // 取出存储的信息
                    SecurityToken token = GuavaCache.get(SecurityToken.class);
                    if (null != token && StringUtils.isNotBlank(token.getValue())) {
                        tokenBuilder.remove(token.getValue());
                    }
                } catch (Exception e) {
                    log.debug("【yishuifengxiao-common-spring-boot-starter】退出成功后移出访问令牌时出现问题，出现问题的原因为  {}", e.getMessage());

                    securityHandler.onException(propertyResource, request, response, e);
                }

                securityHandler.whenLogoutSuccess(propertyResource, request, response, authentication);

            }
        }.onLogoutSuccess(request, response, authentication);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        securityHandler.onException(propertyResource, request, response, authException);
    }


    public PropertyResource getPropertyResource() {
        return propertyResource;
    }

    public void setPropertyResource(PropertyResource propertyResource) {
        this.propertyResource = propertyResource;
    }

    public SecurityValueExtractor getSecurityContextExtractor() {
        return securityValueExtractor;
    }

    public void setSecurityContextExtractor(SecurityValueExtractor securityValueExtractor) {
        this.securityValueExtractor = securityValueExtractor;
    }

    public SecurityHelper getSecurityHelper() {
        return securityHelper;
    }

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    public TokenBuilder getTokenBuilder() {
        return tokenBuilder;
    }

    public void setTokenBuilder(TokenBuilder tokenBuilder) {
        this.tokenBuilder = tokenBuilder;
    }

    public SecurityHandler getSecurityHandler() {
        return securityHandler;
    }

    public void setSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }
}
