package com.yishuifengxiao.common.security.social.qq.api;

import com.yishuifengxiao.common.security.social.qq.entity.QQUserInfo;

/**
 * QQ登陆接口
 * 
 * @author yishui
 * @date 2019年7月12日
 * @version 1.0.0
 */
public interface QQ {
	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	QQUserInfo getUserInfo();
}
