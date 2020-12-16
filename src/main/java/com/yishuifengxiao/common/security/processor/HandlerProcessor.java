/**
 * 
 */
package com.yishuifengxiao.common.security.processor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.yishuifengxiao.common.security.endpoint.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.handler.CustomAccessDeniedHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationFailureHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationSuccessHandler;
import com.yishuifengxiao.common.security.handler.CustomLogoutSuccessHandler;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * <strong>协助处理器</strong><br/>
 * <br/>
 * 
 * 用于在各种 Handler 中根据情况相应地跳转到指定的页面或者输出json格式的数据<br/>
 * 
 * @see ExceptionAuthenticationEntryPoint
 * @see CustomAccessDeniedHandler
 * @see CustomAuthenticationFailureHandler
 * @see CustomAuthenticationSuccessHandler
 * @see CustomLogoutSuccessHandler
 * 
 * @author yishui
 * @Date 2019年4月2日
 * @version 1.0.0
 */
public interface HandlerProcessor {

	/**
	 * 登陆成功后的处理
	 * 
	 * @param request
	 * @param response
	 * @param authentication
	 * @param  token 生成的token
	 * @throws IOException
	 */
	void login(HttpServletRequest request, HttpServletResponse response, Authentication authentication,SecurityToken token)
			throws IOException;

	/**
	 * 登陆失败后的处理
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @throws IOException
	 */
	void failure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException;

	/**
	 * 退出成功后的处理
	 * 
	 * @param request
	 * @param response
	 * @param authentication
	 * @throws IOException
	 */
	void exit(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException;

	/**
	 * 访问资源时权限被拒绝<br/>
	 * 本身是一个合法的用户，但是对于部分资源没有访问权限
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @throws IOException
	 */
	void deney(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
			throws IOException;

	/**
	 * 访问资源时因为权限等原因发生了异常后的处理<br/>
	 * 可能本身就不是一个合法的用户
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @throws IOException
	 */
	void exception(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException;

	/**
	 * 输出前置校验时出现的异常信息<br/>
	 * 在进行前置校验时出现了问题，一般情况下为用户名或密码错误之类的
	 * 
	 * @param request
	 * @param response
	 * @param data     响应信息
	 * @throws IOException
	 */
	void preAuth(HttpServletRequest request, HttpServletResponse response, Response<CustomException> data) throws IOException;

}
