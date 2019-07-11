/**
 * 
 */
package com.yishuifengxiao.common.security.endpoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.event.ExceptionAuthenticationEntryPointEvent;
import com.yishuifengxiao.common.security.handle.CustomHandle;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HeaderUtil;

/**
 * 当参数中不存在token时的提示信息 处理器<br/>
 * 参见 https://www.cnblogs.com/mxmbk/p/9782409.html
 * 
 * @author yishui
 * @Date 2019年4月2日
 * @version 1.0.0
 */
public class ExceptionAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

	private final static Logger log = LoggerFactory.getLogger(ExceptionAuthenticationEntryPoint.class);

	private SecurityProperties securityProperties;

	/**
	 * 协助处理器
	 */
	private CustomHandle customHandle;
	
	private ApplicationContext context;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		//发布信息
		context.publishEvent(new ExceptionAuthenticationEntryPointEvent(authException, request));
		
		log.debug("====================> 【资源服务】获取资源失败(可能是缺少token)，失败的原因为 {}",authException.getMessage());
		log.debug("====================> 【资源服务】获取资源失败(可能是缺少token)，系统希望的处理方式为 {}",securityProperties.getHandler().getException().getReturnType());


		// 判断是否使用系统的默认处理方法
		if (HeaderUtil.useDefault(request, securityProperties.getHandler().getException().getReturnType())) {
			super.commence(request, response, authException);
			return;
		}

		customHandle.handle(request, response, securityProperties.getHandler().getException().getReturnType(),
				securityProperties.getHandler().getException().getRedirectUrl(),
				new Response<>(Response.Const.CODE_UNAUTHORIZED, Response.Const.MSG_UNAUTHORIZED, authException));

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

	public ExceptionAuthenticationEntryPoint(SecurityProperties securityProperties, CustomHandle customHandle) {
		this.securityProperties = securityProperties;
		this.customHandle = customHandle;
	}

	public ExceptionAuthenticationEntryPoint() {

	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public ExceptionAuthenticationEntryPoint(SecurityProperties securityProperties, CustomHandle customHandle,
			ApplicationContext context) {

		this.securityProperties = securityProperties;
		this.customHandle = customHandle;
		this.context = context;
	}
	
	

}
