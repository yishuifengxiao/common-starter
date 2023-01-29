package com.yishuifengxiao.common.security.httpsecurity.provider.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import com.yishuifengxiao.common.security.httpsecurity.provider.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * 权限拒绝处理器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	private PropertyResource propertyResource;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		handlerProcessor.deney(propertyResource, request, response, accessDeniedException);

	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

	public PropertyResource getPropertyResource() {
		return propertyResource;
	}

	public void setPropertyResource(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}

}
