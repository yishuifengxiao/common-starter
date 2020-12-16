package com.yishuifengxiao.common.social.weixin.entity;

import org.springframework.social.oauth2.AccessGrant;
/**
 * 微信登陆授权
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class WechatAccessGrant extends AccessGrant {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4525222786816799215L;
	private String openId;

    public WechatAccessGrant() {
        super("");
    }

    public WechatAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn) {
        super(accessToken, scope, refreshToken, expiresIn);
    }

    /**
     * @return the openId
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * @param openId the openId to set
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

}