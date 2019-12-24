package com.yishuifengxiao.common.security.social.weixin.api;

import com.yishuifengxiao.common.security.social.weixin.entity.WechatUserInfo;
/**
 * 获取微信登陆用户信息
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public interface Wechat {
	/**
	 * 
	 * @param openId
	 * @return
	 */
    WechatUserInfo getUserInfo(String openId);
}