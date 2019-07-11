package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.event.LogoutSuccessEvent;
import com.yishuifengxiao.common.security.processor.CustomProcessor;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HeaderUtil;

/**
 * 自定义登陆退出处理
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年4月14日
 */
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
	private final static Logger log = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

	/**
	 * 自定义属性配置
	 */
	private SecurityProperties securityProperties;

	/**
	 * 协助处理器
	 */
	private CustomProcessor customProcessor;
	
	private ApplicationContext context;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		
		//发布事件
		context.publishEvent(new LogoutSuccessEvent(authentication, request));
		
		log.debug("====================> 【认证服务】退出成功，此用户的信息为 {}",authentication);
		log.debug("====================> 【认证服务】退出成功，系统希望的处理方式为 {}",securityProperties.getHandler().getExit().getReturnType());

		// 判断是否使用系统的默认处理方法
		if (HeaderUtil.useDefault(request, securityProperties.getHandler().getExit().getReturnType())) {
			super.onLogoutSuccess(request, response, authentication);
			return;
		}

		customProcessor.handle(request, response, securityProperties.getHandler().getExit().getReturnType(),
				securityProperties.getHandler().getExit().getRedirectUrl(),
				new Response<>(Response.Const.CODE_OK, Response.Const.MSG_OK, authentication));

	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public CustomProcessor getCustomHandle() {
		return customProcessor;
	}

	public void setCustomHandle(CustomProcessor customProcessor) {
		this.customProcessor = customProcessor;
	}

	public CustomLogoutSuccessHandler(SecurityProperties securityProperties, CustomProcessor customProcessor) {
		this.securityProperties = securityProperties;
		this.customProcessor = customProcessor;
	}

	public CustomLogoutSuccessHandler() {

	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public CustomLogoutSuccessHandler(SecurityProperties securityProperties, CustomProcessor customProcessor,
			ApplicationContext context) {

		this.securityProperties = securityProperties;
		this.customProcessor = customProcessor;
		this.context = context;
	}

}