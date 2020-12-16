package com.yishuifengxiao.common.security.constant;

/**
 * 验证码模块错误码常量类
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public final class ErrorCode {

	/**
	 * 认证信息为空
	 */
	public final static int AUTH_NULL=200;
	
	/**
	 * tokenValue为空
	 */
	public final static int TOKEN_VALUE_NULL=102;
	
	/**
	 * 无效的token
	 */
	public final static int INVALID_TOKEN=102;
	
	/**
	 * 过期的token
	 */
	public final static int EXPIRED_ROKEN=103;
	
	/**
	 * 用户名不存在
	 */
	public final static int USERNAME_NO_EXTIS=104;
	
	/**
	 * 密码错误
	 */
	public final static int PASSWORD_ERROR=105;
	
	/**
	 * 用户名为空
	 */
	public final static int USERNAME_NULL=107;
	
	/**
	 * 会话ID为空
	 */
	public final static int  SESSION_ID_NULL=108;
	
	/**
	 * 最大用户限制
	 */
	public final static int  MAX_USER_LIMT=109;
	
	
	/**
	 * 密码为空
	 */
	public final static int PASSWORD_NULL=110;
	
	
	/**
	 * 账号已过期
	 */
	public final static int ACCOUNT_EXPIRED=111;
	

	/**
	 * 账号已锁定
	 */
	public final static int ACCOUNT_LOCKED=112;
	

	/**
	 * 密码已过期
	 */
	public final static int PASSWORD_EXPIRED=113;
	/**
	 * 账号未启用
	 */
	public final static int ACCOUNT_UNENABLE=114;
}
