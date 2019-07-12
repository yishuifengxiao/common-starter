package com.yishuifengxiao.common.security.social.weixin.api;

import com.yishuifengxiao.common.security.social.weixin.entity.WechatUserInfo;

public interface Wechat {
    WechatUserInfo getUserInfo(String openId);
}