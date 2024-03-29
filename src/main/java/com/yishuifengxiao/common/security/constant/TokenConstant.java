package com.yishuifengxiao.common.security.constant;

import org.springframework.http.HttpHeaders;

/**
 * Token相关的常量类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class TokenConstant {

    /**
     * token的过期时间，单位为秒，默认的token有效时间，默认为24小时
     */
    public final static int TOKEN_VALID_TIME_IN_SECOND = 60 * 60 * 24;

    /**
     * token信息分割符 :
     */
    public final static String TOKEN_SPLIT_CHAR = ":";

    /**
     * 默认的最大登陆用户数量
     */
    public final static int MAX_SESSION_NUM = 10;

    /**
     * token信息的长度
     */
    public final static int TOKEN_LENGTH = 3;


    /**
     * 从请求URL参数里取出认证信息时的参数名，默认为 xtoken
     */
    public final static String TOKEN_REQUEST_PARAM = "xtoken";
    /**
     * 从请求头参数里取出认证信息时的参数名，默认为 Authorization
     */
    public final static String TOKEN_HEADER_PARAM = HttpHeaders.AUTHORIZATION;
    /**
     * 用户唯一标识符的标志,即设备id
     */
    public final static String USER_DEVICE_ID = "user_device_id";
}
