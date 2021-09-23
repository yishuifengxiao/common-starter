package com.yishuifengxiao.common.social.weixin.api;

import com.yishuifengxiao.common.social.weixin.entity.WechatUserInfo;

/**
 * 获取微信登陆用户信息
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Wechat {
	/**
	 * 获取到用户信息
	 * 
	 * @param openId openId
	 * @return 用户信息
	 */
	WechatUserInfo getUserInfo(String openId);
}