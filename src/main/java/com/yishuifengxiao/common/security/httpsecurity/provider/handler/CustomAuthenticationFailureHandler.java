package com.yishuifengxiao.common.security.httpsecurity.provider.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.yishuifengxiao.common.security.httpsecurity.provider.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * <p>登陆失败处理器</p>
 * 
 * 1 采用实现AuthenticationFailureHandler接口的方法,
 * 2 采用继承 SimpleUrlAuthenticationFailureHandler 的方法
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	private PropertyResource propertyResource;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authenticationException) throws IOException, ServletException {

		handlerProcessor.failure(propertyResource, request, response, authenticationException);

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