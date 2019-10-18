package com.yishuifengxiao.common.security.service.impl;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.yishuifengxiao.common.constant.Oauth2Constant;
import com.yishuifengxiao.common.security.service.AbstractClientDetailsService;
import com.yishuifengxiao.common.tool.encoder.DES;

/**
 * 缺省的ClientDetailsService实现类
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class DefaultClientDetailsService extends AbstractClientDetailsService {

	@Override
	public ClientDetails findClientByClientId(String clientId) throws ClientRegistrationException {
		BaseClientDetails client = new BaseClientDetails(clientId, "yishuifengxiao", Oauth2Constant.DEFAULT_SCOPE,
				Oauth2Constant.DEFAULT_GRANT_TYPE, Oauth2Constant.DEFAULT_AUTHORTY, Oauth2Constant.DEFAULT_URL);
		client.setClientSecret(DES.encrypt(clientId));
		return client;
	}

}
