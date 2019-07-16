package com.yishuifengxiao.common.security.session;
import java.io.IOException;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

/**
 * 可以在此方法中记录谁把谁的登陆状态挤掉
 * @version 0.0.1
 * @author yishui
 * @date 2018年4月14日
 */
public class SessionInformationExpiredStrategyImpl implements SessionInformationExpiredStrategy {
   private final static Logger LOG=LoggerFactory.getLogger(SessionInformationExpiredStrategyImpl.class);

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		LOG.info("---------------- 并发登陆");
		event.getResponse().setContentType("application/json;charset=UTF-8");
		event.getResponse().getWriter().write("---------------- 并发登陆");

	}

}