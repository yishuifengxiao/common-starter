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
import com.yishuifengxiao.common.security.eunm.HandleEnum;
import com.yishuifengxiao.common.security.event.AuthenticationFailureEvent;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtil;
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
	private HandlerProcessor customProcessor;

	private ApplicationContext context;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		log.debug("====================> 【认证服务】登录失败，失败的原因为 {}", exception.getMessage());
		// 发布事件
		context.publishEvent(new AuthenticationFailureEvent(exception, request));

		// 获取系统的处理方式
		HandleEnum handleEnum = securityProperties.getHandler().getFail().getReturnType();

		HandleEnum type = HttpUtil.handleType(request, securityProperties.getHandler(), handleEnum);

		log.debug("====================> 【认证服务】登录失败，系统配置的处理方式为 {},最终的处理方式为 {}", handleEnum, type);
		// 判断是否使用系统的默认处理方法
		if (type == HandleEnum.DEFAULT) {
			super.onAuthenticationFailure(request, response, exception);
			return;
		}
		String msg = Response.Const.MSG_INTERNAL_SERVER_ERROR;
		if (StringUtil.containChinese(exception.getMessage())) {
			msg = exception.getMessage();
		}

		customProcessor.handle(request, response, type == HandleEnum.REDIRECT,
				securityProperties.getHandler().getFail().getRedirectUrl(),
				new Response<>(Response.Const.CODE_INTERNAL_SERVER_ERROR, msg, exception));

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

	public CustomAuthenticationFailureHandler(SecurityProperties securityProperties, HandlerProcessor customProcessor) {

		this.securityProperties = securityProperties;
		this.customProcessor = customProcessor;
	}

	public CustomAuthenticationFailureHandler() {

	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public CustomAuthenticationFailureHandler(SecurityProperties securityProperties, HandlerProcessor customProcessor,
			ApplicationContext context) {
		this.securityProperties = securityProperties;
		this.customProcessor = customProcessor;
		this.context = context;
	}

}