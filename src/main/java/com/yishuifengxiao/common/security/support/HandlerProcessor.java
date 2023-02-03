/**
 *
 */
package com.yishuifengxiao.common.security.support;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.yishuifengxiao.common.security.exception.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.httpsecurity.authorize.handler.CustomAccessDeniedHandler;
import com.yishuifengxiao.common.security.httpsecurity.authorize.handler.CustomAuthenticationFailureHandler;
import com.yishuifengxiao.common.security.httpsecurity.authorize.handler.CustomAuthenticationSuccessHandler;
import com.yishuifengxiao.common.security.httpsecurity.authorize.handler.CustomLogoutSuccessHandler;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * <p>
 * 协助处理器
 * </p>
 *
 * 用于在各种 Handler 中根据情况相应地跳转到指定的页面或者输出json格式的数据
 *
 * @see ExceptionAuthenticationEntryPoint
 * @see CustomAccessDeniedHandler
 * @see CustomAuthenticationFailureHandler
 * @see CustomAuthenticationSuccessHandler
 * @see CustomLogoutSuccessHandler
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface HandlerProcessor {

    /**
     * 登陆成功后的处理
     *@param propertyResource 系统里配置的资源
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param authentication 认证信息
     * @param token          生成的token
     * @throws IOException 处理时发生问题
     */
    void loginSuccess(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Authentication authentication, SecurityToken token) throws IOException;

    /**
     * 登陆失败后的处理
     *
     * @param propertyResource 系统里配置的资源
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param exception        失败的原因
     * @throws IOException 处理时发生问题
     */
    void loginFailure(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException;

    /**
     * 退出成功后的处理
     *@param propertyResource 系统里配置的资源
     * @param request        HttpServletRequest
     * @param response       HttpServletResponse
     * @param authentication 认证信息
     * @throws IOException 处理时发生问题
     */
    void exit(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException;

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
    void deney(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException;

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
    void exception(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException;

    /**
     * <p>
     * 输出前置校验时出现的异常信息
     * </p>
     * 在进行前置校验时出现了问题，一般情况下为用户名或密码错误之类的
     *@param propertyResource 系统里配置的资源
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param data     响应信息
     * @throws IOException 处理时发生问题
     */
    void preAuth(PropertyResource propertyResource, HttpServletRequest request, HttpServletResponse response, Response<CustomException> data) throws IOException;

}
