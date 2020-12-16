package com.yishuifengxiao.common.code.entity;

import java.io.Serializable;

/**
 * 邮件验证码
 * 
 * @author yishui
 * @Date 2019年5月5日
 * @version 1.0.0
 */
public class EmailCode extends ValidateCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8140554939757427074L;

	public EmailCode(long expireTimeInSeconds, String code) {
		super(expireTimeInSeconds, code);
	}

	public EmailCode() {

	}

	@Override
	public String toString() {
		return "EmailCode [toString()=" + super.toString() + "]";
	}

}
