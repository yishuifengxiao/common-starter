package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import com.yishuifengxiao.common.security.constant.SecurityConstant;
import com.yishuifengxiao.common.security.event.AccessDeniedEvent;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.support.SpringContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 权限拒绝处理器
 * 
 * @author yishui
 * @Date 2019年4月2日
 * @version 1.0.0
 */
@Slf4j
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {

	/**
	 * 声明了缓存与恢复操作
	 */
	private RequestCache cache = new HttpSessionRequestCache();

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		// 发布事件
		SpringContext.publishEvent(new AccessDeniedEvent(this, request, accessDeniedException));
		// 引起跳转的uri
		SavedRequest savedRequest = cache.getRequest(request, response);
		String url = savedRequest != null ? savedRequest.getRedirectUrl() : request.getRequestURL().toString();

		request.getSession().setAttribute(SecurityConstant.HISTORY_REDIRECT_URL, savedRequest.getRedirectUrl());
		request.getSession().setAttribute(SecurityConstant.HISTORY_REQUEST_URL, request.getRequestURL().toString());

		log.debug("【易水组件】获取资源权限被拒绝,该资源的url为 {} , 失败的原因为 {}", url, accessDeniedException);

		handlerProcessor.deney(request, response, accessDeniedException);

	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

}
