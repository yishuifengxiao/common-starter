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

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.utils.HttpUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 抽象协助处理器<br/>
 * 
 * 用于在各种 Handler 中根据情况相应地跳转到指定的页面或者输出json格式的数据<br/>
 * 
 * @author qingteng
 * @date 2020年11月28日
 * @version 1.0.0
 */
@Slf4j
public abstract class BaseHandlerProcessor implements HandlerProcessor {
	/**
	 * 登陆成功后的处理
	 * 
	 * @param request
	 * @param response
	 * @param authentication
	 * @throws IOException
	 */
	@Override
	public void login(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
			SecurityToken token) throws IOException {
		log.debug("【易水组件】==============》 登陆成功");
		HttpUtil.out(response, Response.suc(token).setMsg("登陆成功"));
	}

	/**
	 * 登陆失败后的处理
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @throws IOException
	 */
	@Override
	public void failure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException {
		log.debug("【易水组件】==============》 登陆失败");
		HttpUtil.out(response, Response.of(Response.Const.CODE_INTERNAL_SERVER_ERROR, "登陆失败", exception.getMessage()));
	}

	/**
	 * 退出成功后的处理
	 * 
	 * @param request
	 * @param response
	 * @param authentication
	 * @throws IOException
	 */
	@Override
	public void exit(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		log.debug("【易水组件】==============》 退出成功");
		HttpUtil.out(response, Response.suc(authentication).setMsg("退出成功"));
	}

	/**
	 * 访问资源时权限被拒绝<br/>
	 * 本身是一个合法的用户，但是对于部分资源没有访问权限
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @throws IOException
	 */
	@Override
	public void deney(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
			throws IOException {
		log.debug("【易水组件】==============》 权限拒绝");
		HttpUtil.out(response, Response.of(Response.Const.CODE_FORBIDDEN, "无权访问此资源", exception.getMessage()));
	}

	/**
	 * 访问资源时因为权限等原因发生了异常后的处理<br/>
	 * 可能本身就不是一个合法的用户
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @throws IOException
	 */
	@Override
	public void exception(HttpServletRequest request, HttpServletResponse response, Exception exception)
			throws IOException {
		log.debug("【易水组件】==============》 权限异常");
		HttpUtil.out(response, Response.of(Response.Const.CODE_UNAUTHORIZED, "请携带上正确的授权信息", exception));
	}

	/**
	 * 输出前置校验时出现的异常信息<br/>
	 * 在进行前置校验时出现了问题，一般情况下为用户名或密码错误之类的
	 * 
	 * @param request
	 * @param response
	 * @param data     响应信息
	 * @throws IOException
	 */
	@Override
	public void preAuth(HttpServletRequest request, HttpServletResponse response, Response<CustomException> data)
			throws IOException {
		log.debug("【易水组件】==============》 自定义权限检查时发现问题 {}", data);
		HttpUtil.out(response, data);

	}

}
