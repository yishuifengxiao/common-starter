package com.yishuifengxiao.common.code.entity;

import java.io.Serializable;

/**
 * 邮件验证码
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class EmailCode extends ValidateCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8140554939757427074L;

	/**
	 * 构造函数
	 * 
	 * @param expireTimeInSeconds 过期时间，单位秒
	 * @param code                验证码内容
	 */
	public EmailCode(long expireTimeInSeconds, String code) {
		super(expireTimeInSeconds, code);
	}

	/**
	 * 构造函数
	 */
	public EmailCode() {

	}

	@Override
	public String toString() {
		return super.toString();
	}

}
