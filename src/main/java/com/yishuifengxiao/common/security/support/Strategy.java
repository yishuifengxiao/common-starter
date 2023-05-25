package com.yishuifengxiao.common.security.support;

/**
 * 处理类型
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Strategy {

    /**
     * 认证成功
     */
    AUTHENTICATION_SUCCESS,

    /**
     * 认证失败
     */
    AUTHENTICATION_FAILURE,
    /**
     * 退出成功
     */
    LOGOUT_SUCCESS,
    /**
     * 无权访问
     */
    ACCESS_DENIED,
    /**
     * 发生异常
     */
    ON_EXCEPTION
}
