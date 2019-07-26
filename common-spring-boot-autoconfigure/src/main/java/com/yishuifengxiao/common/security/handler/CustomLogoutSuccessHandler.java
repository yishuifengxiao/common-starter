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
import com.yishuifengxiao.common.security.eunm.HandleEnum;
import com.yishuifengxiao.common.security.event.LogoutSuccessEvent;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtil;

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
	private HandlerProcessor customProcessor;

	private ApplicationContext context;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		log.debug("【认证服务】退出成功，此用户的信息为 {}", authentication);
		// 发布事件
		context.publishEvent(new LogoutSuccessEvent(authentication, request));
		

		//存储消息到session中
		request.getSession().setAttribute("yishuifengxiao.msg.exit", authentication);

		// 获取系统的处理方式
		HandleEnum handleEnum = securityProperties.getHandler().getExit().getReturnType();

		HandleEnum type = HttpUtil.handleType(request, securityProperties.getHandler(), handleEnum);

		log.debug("【认证服务】退出成功，系统配置的的处理方式为 {},最终的处理方式为 {}", handleEnum, type);

		// 判断是否使用系统的默认处理方法
		if (type == HandleEnum.DEFAULT) {
			super.onLogoutSuccess(request, response, authentication);
			return;
		}

		customProcessor.handle(request, response, type == HandleEnum.REDIRECT,
				securityProperties.getHandler().getExit().getRedirectUrl(),
				new Response<>(Response.Const.CODE_OK, Response.Const.MSG_OK, authentication));

	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public HandlerProcessor getCustomHandle() {
		return customProcessor;
	}

	public void setCustomHandle(HandlerProcessor customProcessor) {
		this.customProcessor = customProcessor;
	}

	public CustomLogoutSuccessHandler(SecurityProperties securityProperties, HandlerProcessor customProcessor) {
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

	public CustomLogoutSuccessHandler(SecurityProperties securityProperties, HandlerProcessor customProcessor,
			ApplicationContext context) {

		this.securityProperties = securityProperties;
		this.customProcessor = customProcessor;
		this.context = context;
	}

}