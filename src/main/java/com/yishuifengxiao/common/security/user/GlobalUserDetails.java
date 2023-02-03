package com.yishuifengxiao.common.security.user;

import org.springframework.security.authentication.AuthenticationProvider;

/**
 * 全局用户校验提供器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface GlobalUserDetails {

    /**
     * 提供一个AuthenticationProvider
     *
     * @return AuthenticationProvider
     */
    AuthenticationProvider authenticationProvider();
}
