package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.event.AuthenticationSuccessEvent;
import com.yishuifengxiao.common.security.handle.CustomHandle;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HeaderUtil;

/**
 * 自定义登陆成功处理器
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年6月30日
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private final static Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
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
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		//发布事件
		context.publishEvent(new AuthenticationSuccessEvent(authentication, request));
		
		log.debug("====================> 【认证服务】登录成功，此用户的信息为 {}",authentication);
		log.debug("====================> 【认证服务】登录成功，系统希望的处理方式为 {}",securityProperties.getHandler().getSuc().getReturnType());

		// 判断是否使用系统的默认处理方法
		if (HeaderUtil.useDefault(request, securityProperties.getHandler().getSuc().getReturnType())) {
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		customHandle.handle(request, response, securityProperties.getHandler().getSuc().getReturnType(),
				securityProperties.getHandler().getSuc().getRedirectUrl(),
				new Response<>(Response.Const.CODE_OK, Response.Const.MSG_OK, authentication));
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

	public CustomAuthenticationSuccessHandler(SecurityProperties securityProperties, CustomHandle customHandle) {
		this.securityProperties = securityProperties;
		this.customHandle = customHandle;
	}

	public CustomAuthenticationSuccessHandler() {

	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public CustomAuthenticationSuccessHandler(SecurityProperties securityProperties, CustomHandle customHandle,
			ApplicationContext context) {
		this.securityProperties = securityProperties;
		this.customHandle = customHandle;
		this.context = context;
	}

}