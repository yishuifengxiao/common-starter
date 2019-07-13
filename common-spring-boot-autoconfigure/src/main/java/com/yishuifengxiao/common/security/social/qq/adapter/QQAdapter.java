package com.yishuifengxiao.common.security.social.qq.adapter;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import com.yishuifengxiao.common.security.social.qq.api.QQ;
import com.yishuifengxiao.common.security.social.qq.entity.QQUserInfo;

/**
 * QQ的信息与标志信息之间的适配
 * @author yishui
 * @date 2019年2月23日
 * @version v1.0.0
 */
public class QQAdapter implements ApiAdapter<QQ> {
    public boolean test(QQ qq) {
        return true;
    }

    public void setConnectionValues(QQ qq, ConnectionValues connectionValues) {
        QQUserInfo userInfo = qq.getUserInfo();

        //openId 唯一标识，用户在服务商哪里的标识
        connectionValues.setProviderUserId(userInfo.getOpenId());
        connectionValues.setDisplayName(userInfo.getNickname());
        connectionValues.setImageUrl(userInfo.getFigureurl_qq_1());
        //个人主页
        connectionValues.setProfileUrl(null);
    }

    public UserProfile fetchUserProfile(QQ qq) {
        return null;
    }

    public void updateStatus(QQ qq, String s) {

    }
}