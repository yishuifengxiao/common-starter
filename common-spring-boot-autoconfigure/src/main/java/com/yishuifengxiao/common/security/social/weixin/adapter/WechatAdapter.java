package com.yishuifengxiao.common.security.social.weixin.adapter;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import com.yishuifengxiao.common.security.social.weixin.api.Wechat;
import com.yishuifengxiao.common.security.social.weixin.entity.WechatUserInfo;
/**
 * 微信登陆适配器
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class WechatAdapter implements ApiAdapter<Wechat> {

    private String openId;

    public WechatAdapter() {}

    public WechatAdapter(String openId){
        this.openId = openId;
    }

    /**
     * @param api
     * @return
     */
    @Override
    public boolean test(Wechat api) {
        return true;
    }

    /**
     * @param api
     * @param values
     */
    @Override
    public void setConnectionValues(Wechat api, ConnectionValues values) {
        WechatUserInfo profile = api.getUserInfo(openId);
        values.setProviderUserId(profile.getOpenid());
        values.setDisplayName(profile.getNickname());
        values.setImageUrl(profile.getHeadimgurl());
    }

    /**
     * @param api
     * @return
     */
    @Override
    public UserProfile fetchUserProfile(Wechat api) {
        return null;
    }

    /**
     * @param api
     * @param message
     */
    @Override
    public void updateStatus(Wechat api, String message) {
        //do nothing
    }

}