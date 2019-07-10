package com.yishuifengxiao.common.properties.code;

import com.yishuifengxiao.common.constant.CodeConstant;

public class EmailCodeProperties extends SmsCodeProperties {

	/**
	 * 验证码邮箱的内容模板
	 */
	private String contentTemplate = "您的验证码的内容为{0} ,验证码的有效时间为 {1} 秒";

	/**
	 * 验证码邮箱的标题
	 */
	private String title = "帐号保护验证";

	public EmailCodeProperties() {
		this.setExpireIn(60 * 30);
		// 设置验证码的标识符为 email
		this.setCodeKey(CodeConstant.CODE_EMAIL_KEY);
		// 设置验证码对应的值的参数为 email_code
		this.setCodeValue(CodeConstant.CODE_EMAIL_VALUE);
	}

	/**
	 * 获取验证码邮箱的内容模板
	 * 
	 * @return
	 */
	public String getContentTemplate() {
		return contentTemplate;
	}

	/**
	 * 设置验证码邮箱的内容模板
	 * 
	 * @param contentTemplate
	 */
	public void setContentTemplate(String contentTemplate) {
		this.contentTemplate = contentTemplate;
	}

	/**
	 * 获取验证码邮箱的标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置验证码邮箱的标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
