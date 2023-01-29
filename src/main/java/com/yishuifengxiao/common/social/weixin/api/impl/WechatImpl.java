package com.yishuifengxiao.common.social.weixin.api.impl;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.social.weixin.api.Wechat;
import com.yishuifengxiao.common.social.weixin.entity.WechatUserInfo;

/**
 * 获取微信登陆用户信息实现类
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class WechatImpl extends AbstractOAuth2ApiBinding implements Wechat {

	/**
	 *
	 */
	private ObjectMapper objectMapper = new ObjectMapper();
	/**
	 * 获取用户信息的url
	 */
	private static final String URL_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo?openid=";

	public WechatImpl(String accessToken) {
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
	}

	@Override
	protected List<HttpMessageConverter<?>> getMessageConverters() {
		// 默认注册的StringHttpMessageConverter字符集为ISO-8859-1，而微信返回的是UTF-8的，所以覆盖了原来的方法。
		List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
		messageConverters.remove(0);
		messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return messageConverters;
	}

	@Override
	public WechatUserInfo getUserInfo(String openId) {
		String url = URL_GET_USER_INFO + openId;
		String response = getRestTemplate().getForObject(url, String.class);	
		WechatUserInfo profile = null;
		try {
			profile = objectMapper.readValue(response, WechatUserInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return profile;
	}

}