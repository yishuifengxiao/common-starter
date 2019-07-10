package com.yishuifengxiao.common.properties;

public class EmailCodeProperties extends SmsCodeProperties {

	/**
	 * 默认邮箱验证码的key
	 */
	private final static String CODE_KEY = "email";
	/**
	 * 默认的邮箱验证码的值
	 */
	private final static String CODE_VALUE = "email_code";

	/**
	 * 验证码的失效时间，单位秒,默认为30分钟
	 */
	private Integer expireIn = 60 * 30;
	/**
	 * 验证码的内容
	 */
	private String contentTemplate = "您的验证码的内容为 %s ,验证码的有效时间为 " + expireIn + " 秒";

	/**
	 * 邮箱验证码的标题
	 */
	private String title = "帐号保护验证";

	/**
	 * 验证码的参数
	 */
	private String codeKey = CODE_KEY;
	/**
	 * 验证码对应的值的参数
	 */
	private String codeValue = CODE_VALUE;

	public Integer getExpireIn() {
		return expireIn;
	}

	public void setExpireIn(Integer expireIn) {
		this.expireIn = expireIn;
	}

	public String getContentTemplate() {
		return contentTemplate;
	}

	public void setContentTemplate(String contentTemplate) {
		this.contentTemplate = contentTemplate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 默认的值为 email
	 */
	public String getCodeKey() {
		return codeKey;
	}

	public void setCodeKey(String codeKey) {
		this.codeKey = codeKey;
	}

	/**
	 * 默认的值为 email_code
	 */
	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

}
