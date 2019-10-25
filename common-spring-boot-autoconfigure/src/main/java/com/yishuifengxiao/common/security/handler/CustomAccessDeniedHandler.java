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
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import com.yishuifengxiao.common.constant.SessionConstant;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.context.SecurityHolder;
import com.yishuifengxiao.common.security.eunm.HandleEnum;
import com.yishuifengxiao.common.security.event.AccessDeniedEvent;
import com.yishuifengxiao.common.security.processor.ProcessHandler;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtil;
import com.yishuifengxiao.common.utils.RegexUtil;

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
	 * 声明了缓存与恢复操作
	 */
	private RequestCache cache = new HttpSessionRequestCache();

	/**
	 * 自定义属性配置
	 */
	private SecurityProperties securityProperties;

	/**
	 * 协助处理器
	 */
	private ProcessHandler customProcessor;

	private ApplicationContext context;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		// 发布事件
		context.publishEvent(new AccessDeniedEvent(accessDeniedException, request));
		// 引起跳转的url
		String url = cache.getRequest(request, response).getRedirectUrl();
		// 存储消息到session中
		request.getSession().setAttribute(SessionConstant.DENIE_MSG, accessDeniedException);
		// 将被拦截的url存放到session中
		request.getSession().setAttribute(SessionConstant.DENIE_URL, url);
		// 存储异常信息
		SecurityHolder.getContext().setSecurityExcepion(request,accessDeniedException);
		// 获取系统的处理方式
		HandleEnum handleEnum = securityProperties.getHandler().getDenie().getReturnType();

		HandleEnum type = HttpUtil.handleType(request, securityProperties.getHandler(), handleEnum);
		log.debug("【资源服务】资源请求,该资源的url为 {}", request.getRequestURL().toString());
		log.debug("【资源服务】资源请求 {} 失败 , 失败的原因为 {} ,系统配置的处理方式为 {} , 最终的处理方式为 {}", url, accessDeniedException.getMessage(),
				handleEnum, type);
		if (type == HandleEnum.DEFAULT) {
			super.handle(request, response, accessDeniedException);
			return;
		}
		String msg = Response.Const.MSG_UNAUTHORIZED;
		if (RegexUtil.containChinese(accessDeniedException.getMessage())) {
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

	public ProcessHandler getCustomHandle() {
		return customProcessor;
	}

	public void setCustomHandle(ProcessHandler customProcessor) {
		this.customProcessor = customProcessor;
	}

	public CustomAccessDeniedHandler(SecurityProperties securityProperties, ProcessHandler customProcessor) {
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
