package com.yishuifengxiao.common.properties;

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
	 * 验证码的长度
	 */
	private Integer length = CodeConstant.DEFAULT_CODE_LENGTH;
	/**
	 * 验证码的失效时间，单位秒
	 */
	private Integer expireIn = CodeConstant.DEFAULT_EXPIREIN;
	/**
	 * 验证码是否包含字母
	 */
	private Boolean isContainLetter = CodeConstant.IS_CONTAIN_LETTERS;
	/**
	 * 验证码是否包含数字
	 */
	private Boolean isContainNumber = CodeConstant.IS_CONTAIN_NUMBERS;

	/**
	 * 验证码的参数
	 */
	private String codeKey = CodeConstant.CODE_KEY;
	/**
	 * 验证码对应的值的参数
	 */
	private String codeValue = CodeConstant.CODE_VALUE;



	/**
	 * 验证码的长度,默认为6
	 * 
	 * @return
	 */
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * 验证码的失效时间，默认为60s
	 * 
	 * @return
	 */
	public Integer getExpireIn() {
		return expireIn;
	}

	public void setExpireIn(Integer expireIn) {
		this.expireIn = expireIn;
	}


	/**
	 * 验证码是否包含字母,默认为true
	 * 
	 * @return
	 */
	public Boolean isContainLetter() {
		return isContainLetter;
	}

	public void setContainLetter(Boolean isContainLetter) {
		this.isContainLetter = isContainLetter;
	}

	/**
	 * 验证码是否包含数字,默认为true
	 * 
	 * @return
	 */
	public Boolean isContainNumber() {
		return isContainNumber;
	}

	public void setContainNumber(Boolean isContainNumber) {
		this.isContainNumber = isContainNumber;
	}

	/**
	 * 验证码的参数，默认为code_key
	 * 
	 * @return
	 */
	public String getCodeKey() {
		return codeKey;
	}

	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}

	/**
	 * 验证码对应的值的参数,默认为 code
	 */
	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

}