package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.yishuifengxiao.common.security.event.AuthenticationFailureEvent;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.support.SpringContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 登陆失败处理器
 * <hr/>
 * 1 采用实现AuthenticationFailureHandler接口的方法 <br/>
 * 2 采用继承 SimpleUrlAuthenticationFailureHandler 的方法
 * 
 * @author admin
 *
 */
@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authenticationException) throws IOException, ServletException {

		// 发布事件
		SpringContext.publishEvent(new AuthenticationFailureEvent(this, request, authenticationException));

		log.debug("【易水组件】登录失败，失败的原因为 {}", authenticationException.getMessage());
		handlerProcessor.failure(request, response, authenticationException);

	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

}