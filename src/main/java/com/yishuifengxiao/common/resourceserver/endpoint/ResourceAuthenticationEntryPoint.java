package com.yishuifengxiao.common.resourceserver.endpoint;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.yishuifengxiao.common.resourceserver.provider.ResourceAuthorizeProvider;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * <p>
 * 异常处理
 * </p>
 * 
 * 
 * 在<code>ResourceAuthorizeProvider</code>中被配置为资源异常处理方式
 * 
 * @see ResourceAuthorizeProvider
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	private PropertyResource propertyResource;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		handlerProcessor.exception(propertyResource, request, response, authException);

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
