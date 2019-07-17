/**
 * 
 */
package com.yishuifengxiao.common.validation.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码父类
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public  class ValidateCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 251907201542125693L;

	/**
	 * 验证码的失效时间
	 */
	private LocalDateTime expireTime;

	/**
	 * 验证码内容
	 */
	private String code;

	/**
	 * 构造函数
	 * 
	 * @param expireTimeInSeconds 验证码的有效时间，单位 秒
	 */
	protected ValidateCode(long expireTimeInSeconds) {
		this.expireTime = LocalDateTime.now().plusSeconds(expireTimeInSeconds);
	}

	/**
	 * 构造函数
	 * 
	 * @param expireTimeInSeconds 验证码的有效时间，单位 秒
	 * @param code                验证码内容
	 */
	public ValidateCode(long expireTimeInSeconds, String code) {
		this.expireTime = LocalDateTime.now().plusSeconds(expireTimeInSeconds);
		this.code = code;
	}

	/**
	 * 获取验证码是否已经过期
	 * 
	 * @return
	 */
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(this.expireTime);
	}

	/**
	 * 获取验证码的失效日期
	 * 
	 * @return
	 */
	public LocalDateTime getExpireTime() {
		return expireTime;
	}

	/**
	 * 设置验证码的失效时间
	 * 
	 * @param expireTime
	 */
	public void setExpireTime(LocalDateTime expireTime) {
		this.expireTime = expireTime;
	}

	/**
	 * 获取验证码的内容
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置验证码的内容
	 * 
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "ValidateCode [expireTime=" + expireTime + ", code=" + code + "]";
	}

	
	
}
