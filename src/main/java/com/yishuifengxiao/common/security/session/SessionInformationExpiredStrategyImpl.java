package com.yishuifengxiao.common.security.session;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 可以在此方法中记录谁把谁的登陆状态挤掉
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年4月14日
 */
@Slf4j
public class SessionInformationExpiredStrategyImpl implements SessionInformationExpiredStrategy {

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		log.info("【易水组件】session失效-并发登陆");
		HttpUtil.out(event.getResponse(), Response.error("并发登陆"));

	}

}