package com.yishuifengxiao.common.security.social.qq.api.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.security.social.qq.api.QQ;
import com.yishuifengxiao.common.security.social.qq.entity.QQUserInfo;

/**
 * 必须继承 AbstractOAuth2ApiBinding
 * 
 * @author yishui
 * @date 2019年2月22日
 * @version v1.0.0
 */
public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {

	private Logger logger = LoggerFactory.getLogger(getClass());

	// http://wiki.connect.qq.com/openapi%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E_oauth2-0
	private static final String QQ_URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";

	// http://wiki.connect.qq.com/get_user_info(access_token由父类提供)
	private static final String QQ_URL_GET_USER_INFO = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";

	// appId 配置文件读取
	private String appId;

	// openId 请求QQ_URL_GET_OPENID返回
	private String openId;
	/**
	 * 工具类
	 */
	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 构造方法获取openId
	 */
	public QQImpl(String accessToken, String appId) {
		// access_token作为查询参数来携带
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);

		this.appId = appId;

		String url = String.format(QQ_URL_GET_OPENID, accessToken);
		String result = getRestTemplate().getForObject(url, String.class);

		logger.info("【QQImpl】 QQ_URL_GET_OPENID={} result={}", QQ_URL_GET_OPENID, result);

		this.openId = StringUtils.substringBetween(result, "\"openid\":\"", "\"}");
	}

	public QQUserInfo getUserInfo() {
		String url = String.format(QQ_URL_GET_USER_INFO, appId, openId);
		String result = getRestTemplate().getForObject(url, String.class);

		logger.info("【QQImpl】 QQ_URL_GET_USER_INFO={} result={}", QQ_URL_GET_USER_INFO, result);

		QQUserInfo userInfo = null;
		try {
			userInfo = objectMapper.readValue(result, QQUserInfo.class);
			userInfo.setOpenId(openId);
			logger.info("userinfo={}", userInfo);
			return userInfo;
		} catch (Exception e) {
			throw new RuntimeException("获取用户信息失败", e);
		}
	}
}