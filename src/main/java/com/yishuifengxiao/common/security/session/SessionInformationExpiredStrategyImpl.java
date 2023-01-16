package com.yishuifengxiao.common.security.session;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 可以在此方法中记录谁把谁的登陆状态挤掉
 * 
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SessionInformationExpiredStrategyImpl implements SessionInformationExpiredStrategy {

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		log.info("【yishuifengxiao-common-spring-boot-starter】session失效-并发登陆");
		HttpUtils.out(event.getResponse(), Response.error("并发登陆"));

	}

}