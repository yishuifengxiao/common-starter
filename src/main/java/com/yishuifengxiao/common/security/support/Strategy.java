package com.yishuifengxiao.common.security.support;

import com.yishuifengxiao.common.tool.entity.RootEnum;

import java.util.Arrays;
import java.util.Optional;

/**
 * 处理类型
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Strategy implements RootEnum {

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


    public static Optional<Strategy> code(Integer code) {
        if (null == code) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(v -> v.code == code).findFirst();
    }


    @Override
    public Integer code() {
        return this.code;
    }

}
