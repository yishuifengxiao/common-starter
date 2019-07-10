package com.yishuifengxiao.common.validation.entity;

import java.io.Serializable;
/**
 * 邮件验证码
 * @author yishui
 * @Date 2019年5月5日
 * @version 1.0.0
 */
public class EmailCode extends SmsCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8140554939757427074L;

	public EmailCode(long expireTimeInSeconds, String code) {
		super(expireTimeInSeconds, code);
	}

}
