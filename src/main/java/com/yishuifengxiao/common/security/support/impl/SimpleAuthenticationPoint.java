/**
 *
 */
package com.yishuifengxiao.common.security.support.impl;

import com.yishuifengxiao.common.guava.GuavaCache;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.authentication.SimpleWebAuthenticationDetails;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.utils.HttpUtils;
import com.yishuifengxiao.common.web.WebEnhanceProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 协助处理器
 * </p>
 * <p>
 * 用于在各种 Handler 中根据情况相应地跳转到指定的页面或者输出json格式的数据
 *
 * @author yishui
 * @version 1.0.0
 * @see AuthenticationEntryPoint
 * @see AccessDeniedHandler
 * @see AuthenticationFailureHandler
 * @see AuthenticationFailureHandler
 * @see AuthenticationSuccessHandler
 * @since 1.0.0
 */
@Slf4j
public class SimpleAuthenticationPoint implements AuthenticationPoint {

    protected SecurityPropertyResource securityPropertyResource;
    /**
     * 信息提取器
     */
    protected SecurityValueExtractor securityValueExtractor;

    /**
     * token生成器
     */
    protected TokenBuilder tokenBuilder;


    private SecurityHandler securityHandler;

    private WebEnhanceProperties.CorsProperties corsProperties;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        securityHandler.whenAccessDenied(securityPropertyResource, request, response, accessDeniedException);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException authenticationException) throws IOException {

                securityHandler.whenAuthenticationFailure(securityPropertyResource, request, response, authenticationException);

            }
        }.onAuthenticationFailure(request, response, exception);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        new SavedRequestAwareAuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {

                try {
                    // 根据登陆信息生成一个token
                    String deviceId = securityValueExtractor.extractDeviceId(request, response);
                    SecurityToken token = tokenBuilder.createNewToken(authentication, deviceId,
                            securityPropertyResource.security().getToken().getValidSeconds(),
                            securityPropertyResource.security().getToken().getPreventsLogin(),
                            securityPropertyResource.security().getToken().getMaxSessions(), authentication.getAuthorities());

                    // 将生成的token存储在session中
                    request.getSession().setAttribute(securityPropertyResource.security().getToken().getRequestParameter(),
                            token.getValue());
                    // 将生成的token存储在cookie中
                    Cookie cookie = new Cookie(securityPropertyResource.security().getToken().getRequestParameter(),
                            token.getValue());
                    //Cookie的路径为“/”，这意味着Cookie在整个应用程序中都可用
                    cookie.setPath("/");
                    //如果设置为true，则仅在使用安全协议（HTTPS或SSL）时将cookie从浏览器发送到服务器。默认为false
                    cookie.setSecure(false);
                    //指定cookie在用户计算机中存储的时间，以秒为单位。如果未设置，则退出Web浏览器时将删除cookie
                    cookie.setMaxAge(token.getValidSeconds());
                    response.addCookie(cookie);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    //controlAllowHeaders

                    String controlAllowHeaders = StringUtils.isBlank(corsProperties.getAllowedHeaders()) ?
                            HttpUtils.accessControlAllowHeaders(request, response) :
                            corsProperties.getAllowedHeaders();

                    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, controlAllowHeaders);
                    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                            HttpUtils.accessControlAllowOrigin(request));
                    // 登陆成功
                    securityHandler.whenAuthenticationSuccess(securityPropertyResource, request, response, authentication,
                            token);
                } catch (Exception e) {
                    securityHandler.whenAuthenticationFailure(securityPropertyResource, request, response,
                            new AuthenticationServiceException(e.getMessage()));
                }

            }

        }.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        new SimpleUrlLogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

                try {
                    SecurityToken token = null;
                    if (null != authentication.getDetails() && authentication.getDetails() instanceof SimpleWebAuthenticationDetails) {
                        token = ((SimpleWebAuthenticationDetails) authentication.getDetails()).getToken();
                    }
                    if (null == token) {
                        // 取出存储的信息
                        token = GuavaCache.currentGet(SecurityToken.class);
                    }
                    if (null != token && StringUtils.isNotBlank(token.getValue())) {
                        tokenBuilder.remove(token);
                    }
                } catch (Exception e) {
                    log.debug("【yishuifengxiao-common-spring-boot-starter】退出成功后移出访问令牌时出现问题，出现问题的原因为  {}",
                            e.getMessage());

                    securityHandler.onException(securityPropertyResource, request, response, e);
                }

                securityHandler.whenLogoutSuccess(securityPropertyResource, request, response, authentication);

            }
        }.onLogoutSuccess(request, response, authentication);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        securityHandler.onException(securityPropertyResource, request, response, authException);
    }


    public SecurityPropertyResource getPropertyResource() {
        return securityPropertyResource;
    }

    public void setPropertyResource(SecurityPropertyResource securityPropertyResource) {
        this.securityPropertyResource = securityPropertyResource;
    }

    public SecurityValueExtractor getSecurityContextExtractor() {
        return securityValueExtractor;
    }

    public void setSecurityContextExtractor(SecurityValueExtractor securityValueExtractor) {
        this.securityValueExtractor = securityValueExtractor;
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

    public WebEnhanceProperties.CorsProperties getCorsProperties() {
        return corsProperties;
    }

    public void setCorsProperties(WebEnhanceProperties.CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }
}
