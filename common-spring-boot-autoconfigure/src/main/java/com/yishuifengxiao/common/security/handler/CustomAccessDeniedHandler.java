package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.eunm.HandleEnum;
import com.yishuifengxiao.common.security.event.AccessDeniedEvent;
import com.yishuifengxiao.common.security.processor.CustomProcessor;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtil;
import com.yishuifengxiao.common.utils.StringUtil;

/**
 * 权限拒绝处理器
 * 
 * @author yishui
 * @Date 2019年4月2日
 * @version 1.0.0
 */
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
	private final static Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

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
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		// 发布事件
		context.publishEvent(new AccessDeniedEvent(accessDeniedException, request));

		log.debug("====================> 【资源服务】资源请求失败，失败的原因为 {}", accessDeniedException.getMessage());
		log.debug("====================> 【资源服务】资源请求失败，系统希望的处理方式为 {}",
				securityProperties.getHandler().getDenie().getReturnType());

		HandleEnum type = HttpUtil.handleType(request, securityProperties.getHandler().getHeaderName(),
				securityProperties.getHandler().getDenie().getReturnType());
		if (type == HandleEnum.DEFAULT) {
			super.handle(request, response, accessDeniedException);
			return;
		}
		String msg = Response.Const.MSG_UNAUTHORIZED;
		if (StringUtil.containChinese(accessDeniedException.getMessage())) {
			msg = accessDeniedException.getMessage();
		}

		customProcessor.handle(request, response, type == HandleEnum.REDIRECT,
				securityProperties.getHandler().getDenie().getRedirectUrl(),
				new Response<>(Response.Const.CODE_FORBIDDEN, msg, accessDeniedException));

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

	public CustomAccessDeniedHandler(SecurityProperties securityProperties, CustomProcessor customProcessor) {
		this.securityProperties = securityProperties;
		this.customProcessor = customProcessor;
	}

	public CustomAccessDeniedHandler() {

	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

}
