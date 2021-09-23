package com.yishuifengxiao.common.social.qq.adapter;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import com.yishuifengxiao.common.social.qq.api.QQ;
import com.yishuifengxiao.common.social.qq.entity.QqUserInfo;

/**
 * <p>
 * QQ的信息与标志信息之间的适配
 * </p>
 * 在api和connection接口间提供适配 在生成 factory时需要使用到本实例
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class QqAdapter implements ApiAdapter<QQ> {

	@Override
	public boolean test(QQ qq) {
		return true;
	}

	@Override
	public void setConnectionValues(QQ qq, ConnectionValues connectionValues) {
		QqUserInfo userInfo = qq.getUserInfo();

		// openId 唯一标识，用户在服务商哪里的标识
		connectionValues.setProviderUserId(userInfo.getOpenId());
		connectionValues.setDisplayName(userInfo.getNickname());
		connectionValues.setImageUrl(userInfo.getFigureurlQq1());
		// 个人主页
		connectionValues.setProfileUrl(null);
	}

	@Override
	public UserProfile fetchUserProfile(QQ qq) {
		return null;
	}

	@Override
	public void updateStatus(QQ qq, String s) {

	}
}