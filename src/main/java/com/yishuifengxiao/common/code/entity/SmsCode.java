/**
 * 
 */
package com.yishuifengxiao.common.code.entity;

/**
 * 短信验证码
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmsCode extends ValidateCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2551649951684072174L;

	/**
	 * 构造函数
	 */
	public SmsCode() {

	}

	/**
	 * 构造函数
	 * 
	 * @param expireTimeInSeconds 过期时间，单位秒
	 * @param code                验证码内容
	 */
	public SmsCode(long expireTimeInSeconds, String code) {
		super(expireTimeInSeconds, code);
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
