package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.event.AuthenticationFailureEvent;
import com.yishuifengxiao.common.security.handle.CustomHandle;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HeaderUtil;
import com.yishuifengxiao.common.utils.StringUtil;

/**
 * 登陆失败后处理
 * <hr/>
 * 1 采用实现AuthenticationFailureHandler接口的方法 <br/>
 * 2 采用继承 SimpleUrlAuthenticationFailureHandler 的方法
 * 
 * @author admin
 *
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private final static Logger log = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);
	/**
	 * 自定义属性配置
	 */
	private SecurityProperties securityProperties;
	/**
	 * 协助处理器
	 */
	private CustomHandle customHandle;
	
	
	private ApplicationContext context;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		//发布事件
		context.publishEvent(new AuthenticationFailureEvent(exception, request));
		
		log.debug("====================> 【认证服务】登录失败，失败的原因为 {}", exception.getMessage());
		log.debug("====================> 【认证服务】登录失败，系统希望的处理方式为 {}",
				securityProperties.getHandler().getFail().getReturnType());

		// 判断是否使用系统的默认处理方法
		if (HeaderUtil.useDefault(request, securityProperties.getHandler().getFail().getReturnType())) {
			super.onAuthenticationFailure(request, response, exception);
			return;
		}
		String msg = Response.Const.MSG_INTERNAL_SERVER_ERROR;
		if (StringUtil.containChinese(exception.getMessage())) {
			msg = exception.getMessage();
		}

		customHandle.handle(request, response, securityProperties.getHandler().getFail().getReturnType(),
				securityProperties.getHandler().getFail().getRedirectUrl(),
				new Response<>(Response.Const.CODE_INTERNAL_SERVER_ERROR, msg, exception));

	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public CustomHandle getCustomHandle() {
		return customHandle;
	}

	public void setCustomHandle(CustomHandle customHandle) {
		this.customHandle = customHandle;
	}

	public CustomAuthenticationFailureHandler(SecurityProperties securityProperties, CustomHandle customHandle) {

		this.securityProperties = securityProperties;
		this.customHandle = customHandle;
	}

	public CustomAuthenticationFailureHandler() {

	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public CustomAuthenticationFailureHandler(SecurityProperties securityProperties, CustomHandle customHandle,
			ApplicationContext context) {
		this.securityProperties = securityProperties;
		this.customHandle = customHandle;
		this.context = context;
	}

}