package com.yishuifengxiao.common.resourceserver.endpoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import com.yishuifengxiao.common.resourceserver.provider.ResourceAuthorizeProvider;
import com.yishuifengxiao.common.security.constant.SecurityConstant;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理<br/>
 * <br/>
 * 
 * 在<code>ResourceAuthorizeProvider</code>中被配置为资源异常处理方式
 * 
 * @see ResourceAuthorizeProvider
 * @author yishui
 * @version 1.0.0
 * @date 2019-10-29
 */
@Slf4j
public class ResourceAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/**
	 * 声明了缓存与恢复操作
	 */
	private final RequestCache cache = new HttpSessionRequestCache();

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// 引起跳转的uri
		SavedRequest savedRequest = cache.getRequest(request, response);
		String url = savedRequest != null ? savedRequest.getRedirectUrl() : request.getRequestURL().toString();
		
		request.getSession().setAttribute(SecurityConstant.HISTORY_REDIRECT_URL, savedRequest.getRedirectUrl());
		request.getSession().setAttribute(SecurityConstant.HISTORY_REQUEST_URL, request.getRequestURL().toString());

		log.debug("【易水组件】获取资源{}失败, 失败的原因为 {}", url, authException);

		handlerProcessor.exception(request, response, authException);

	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

}
