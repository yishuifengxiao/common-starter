package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.yishuifengxiao.common.security.event.LogoutSuccessEvent;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.context.SessionStorage;

import lombok.extern.slf4j.Slf4j;

/**
 * 登出成功处理器
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年4月14日
 */
@Slf4j
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	private TokenBuilder tokenBuilder;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		log.debug("【易水组件】退出成功，此用户的信息为 {}", authentication);
		// 发布事件
		SpringContext.publishEvent(new LogoutSuccessEvent(this, request, authentication));

		try {
			// 取出存储的信息
			SecurityToken token = SessionStorage.get(SecurityToken.class);
			if (null != token && StringUtils.isNotBlank(token.getValue())) {
				tokenBuilder.remove(token.getValue());
			}
		} catch (Exception e) {
			log.debug("【易水组件】退出成功后移出访问令牌时出现问题，出现问题的原因为  {}", e.getMessage());

			handlerProcessor.exception(request, response, e);
		}

		handlerProcessor.exit(request, response, authentication);

	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

	public TokenBuilder getTokenBuilder() {
		return tokenBuilder;
	}

	public void setTokenBuilder(TokenBuilder tokenBuilder) {
		this.tokenBuilder = tokenBuilder;
	}

}