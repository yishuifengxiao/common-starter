package com.yishuifengxiao.common.security.constant;

/**
 * Token相关的常量类
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
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
	public final static int MAX_SESSION_NUM = 8888;

	/**
	 * token信息的长度
	 */
	public final static int TOKEN_LENGTH = 3;

	/**
	 * 从请求头里取出认证信息时的参数名，默认为 xtoken
	 */
	public final static String TOKEN_HEADER_PARAM = "xtoken";

	/**
	 * 从请求参数里取出认证信息时的参数名，默认为 yishui_token
	 */
	public final static String TOKEN_REQUEST_PARAM = "yishui_token";

	/**
	 * 用户唯一标识符的标志
	 */
	public final static String USER_UNIQUE_IDENTIFIER = "user_unique_identitier";
}
