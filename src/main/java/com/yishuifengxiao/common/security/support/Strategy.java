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
    AUTHENTICATION_SUCCESS("认证成功", 1),

    /**
     * 认证失败
     */
    AUTHENTICATION_FAILURE("认证失败", 2),
    /**
     * 退出成功
     */
    LOGOUT_SUCCESS("退出成功", 3),
    /**
     * 无权访问
     */
    ACCESS_DENIED("无权访问", 4),
    /**
     * 发生异常
     */
    ON_EXCEPTION("发生异常", 5);


    private String name;

    private int code;

    Strategy(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
