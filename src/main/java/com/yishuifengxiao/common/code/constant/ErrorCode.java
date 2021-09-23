package com.yishuifengxiao.common.code.constant;

/**
 * 验证码模块错误码常量
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ErrorCode {

	/**
	 * 错误的验证码发送目标
	 */
	public final static int ERROR_CODE_TARGET = 100;

	/**
	 * 验证码生成器不存在
	 */
	public final static int GENERATOR_NO_EXTIS = 101;

	/**
	 * 存储的验证码不存在
	 */
	public final static int SESSION_CODE_NO_EXTIS = 102;

	/**
	 * 请求中的验证码不存在
	 */
	public final static int REQUEST_CODE_NO_EXTIS = 103;

	/**
	 * 验证码已过期
	 */
	public final static int CODE_EXPIRED = 104;

	/**
	 * 验证码不匹配
	 */
	public final static int CODE_NO_MATCH = 105;

	/**
	 * 发送器生成器不存在
	 */
	public final static int SENDER_NO_EXTIS = 106;

	/**
	 * 验证码发送异常
	 */
	public final static int CODE_SEND_ERROR = 107;

}
