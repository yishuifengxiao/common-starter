package com.yishuifengxiao.common.properties.code;

import com.yishuifengxiao.common.constant.CodeConstant;

/**
 * 短信验证码的配置参数
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class SmsCodeProperties {
	/**
	 * 验证码的长度,默认为4
	 */
	private Integer length = CodeConstant.DEFAULT_CODE_LENGTH;
	/**
	 * 验证码的失效时间，单位秒，默认为60s
	 */
	private Integer expireIn = CodeConstant.DEFAULT_EXPIREIN;
	/**
	 * 验证码是否包含字母,默认包含
	 */
	private Boolean isContainLetter = CodeConstant.IS_CONTAIN_LETTERS;
	/**
	 * 验证码是否包含数字,默认包含
	 */
	private Boolean isContainNumber = CodeConstant.IS_CONTAIN_NUMBERS;

	/**
	 * 验证码的参数
	 */
	private String codeKey = CodeConstant.CODE_SMS_KEY;
	/**
	 * 验证码对应的值的参数
	 */
	private String codeValue = CodeConstant.CODE_SMS_VALUE;

	/**
	 * 获取验证码的长度,默认为4
	 * 
	 * @return
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * 设置验证码的长度,默认为4
	 * 
	 * @param length
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * 获取验证码的失效时间，默认为60s
	 * 
	 * @return
	 */
	public Integer getExpireIn() {
		return expireIn;
	}

	/**
	 * 设置验证码的失效时间，默认为60s
	 * 
	 * @param expireIn
	 */
	public void setExpireIn(Integer expireIn) {
		this.expireIn = expireIn;
	}

	/**
	 * 获取验证码是否包含字母,默认为true
	 * 
	 * @return
	 */
	public Boolean isContainLetter() {
		return isContainLetter;
	}

	/**
	 * 设置验证码是否包含字母,默认为true
	 * 
	 * @param isContainLetter
	 */
	public void setContainLetter(Boolean isContainLetter) {
		this.isContainLetter = isContainLetter;
	}

	/**
	 * 获取验证码是否包含数字,默认为true
	 * 
	 * @return
	 */
	public Boolean isContainNumber() {
		return isContainNumber;
	}

	/**
	 * 设置验证码是否包含数字,默认为true
	 * 
	 * @param isContainNumber
	 */
	public void setContainNumber(Boolean isContainNumber) {
		this.isContainNumber = isContainNumber;
	}

	/**
	 * 获取验证码的参数
	 * 
	 * @return
	 */
	public String getCodeKey() {
		return codeKey;
	}

	/**
	 * 设置验证码的参数
	 * 
	 * @param codeKey
	 */
	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}

	/**
	 * 获取验证码对应的值的参数
	 */
	public String getCodeValue() {
		return codeValue;
	}

	/**
	 * 设置验证码对应的值的参数
	 * 
	 * @param codeValue
	 */
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public SmsCodeProperties() {

	}

	public SmsCodeProperties(String codeKey, String codeValue) {
		this.codeKey = codeKey;
		this.codeValue = codeValue;
	}

	public SmsCodeProperties(Integer length, Integer expireIn, Boolean isContainLetter, Boolean isContainNumber,
			String codeKey, String codeValue) {
		this.length = length;
		this.expireIn = expireIn;
		this.isContainLetter = isContainLetter;
		this.isContainNumber = isContainNumber;
		this.codeKey = codeKey;
		this.codeValue = codeValue;
	}
}