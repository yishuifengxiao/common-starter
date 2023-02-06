/**
 *
 */
package com.yishuifengxiao.common.security.support;

import com.yishuifengxiao.common.security.token.SecurityValueExtractor;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.tool.context.SessionStorage;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
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
public abstract class SecurityHandler implements AccessDeniedHandler, AuthenticationFailureHandler, AuthenticationSuccessHandler, LogoutSuccessHandler, AuthenticationEntryPoint {

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

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        new AccessDeniedHandlerImpl() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response,
                               AccessDeniedException accessDeniedException) throws IOException {

                whenAccessDenied(propertyResource, request, response, accessDeniedException);

            }
        }.handle(request, response, accessDeniedException);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException authenticationException) throws IOException {

                whenAuthenticationFailure(propertyResource, request, response, authenticationException);

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
                    String sessionId = securityValueExtractor.extractUserUniqueIdentifier(request, response);

                    SecurityToken token = securityHelper.createUnsafe(authentication.getName(), sessionId);

                    // 将生成的token存储在session中
                    request.getSession().setAttribute(propertyResource.security().getToken().getUserUniqueIdentitier(), token.getValue());
                    // 登陆成功
                    whenAuthenticationSuccess(propertyResource, request, response, authentication, token);
                } catch (CustomException e) {
                    whenAuthenticationFailure(propertyResource, request, response, new AuthenticationServiceException(e.getMessage()));
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
                    SecurityToken token = SessionStorage.get(SecurityToken.class);
                    if (null != token && StringUtils.isNotBlank(token.getValue())) {
                        tokenBuilder.remove(token.getValue());
                    }
                } catch (Exception e) {
                    log.debug("【yishuifengxiao-common-spring-boot-starter】退出成功后移出访问令牌时出现问题，出现问题的原因为  {}", e.getMessage());

                    onException(propertyResource, request, response, e);
                }

                whenLogoutSuccess(propertyResource, request, response, authentication);

            }
        }.onLogoutSuccess(request, response, authentication);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        onException(propertyResource, request, response, authException);
    }

    /**
     * 登陆成功后的处理
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param authentication   认证信息
     * @param token            生成的token
     * @throws IOException 处理时发生问题
     */
    public abstract void whenAuthenticationSuccess(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Authentication authentication, SecurityToken token) throws IOException;

    /**
     * 登陆失败后的处理
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        失败的原因
     * @throws IOException 处理时发生问题
     */
    public abstract void whenAuthenticationFailure(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException;

    /**
     * 退出成功后的处理
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param authentication   认证信息
     * @throws IOException 处理时发生问题
     */
    public abstract void whenLogoutSuccess(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException;

    /**
     * <p>
     * 访问资源时权限被拒绝
     * </p>
     * 本身是一个合法的用户，但是对于部分资源没有访问权限
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        被拒绝的原因
     * @throws IOException 处理时发生问题
     */
    public abstract void whenAccessDenied(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException;

    /**
     * <p>
     * 访问资源时因为权限等原因发生了异常后的处理
     * </p>
     * 可能本身就不是一个合法的用户
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        发生异常的原因
     * @throws IOException 处理时发生问题
     */
    public abstract void onException(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException;

    /**
     * <p>
     * 输出前置校验时出现的异常信息
     * </p>
     * 在进行前置校验时出现了问题，一般情况下为用户名或密码错误之类的
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param data             响应信息
     * @throws IOException 处理时发生问题
     */
    public abstract void preAuth(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Response<CustomException> data) throws IOException;

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


}
