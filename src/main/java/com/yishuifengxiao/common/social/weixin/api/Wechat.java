package com.yishuifengxiao.common.social.weixin.api;

import com.yishuifengxiao.common.social.weixin.entity.WechatUserInfo;

/**
 * 获取微信登陆用户信息
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public interface Wechat {
	/**
	 * 获取到用户信息
	 * 
	 * @param openId
	 * @return
	 */
	WechatUserInfo getUserInfo(String openId);
}