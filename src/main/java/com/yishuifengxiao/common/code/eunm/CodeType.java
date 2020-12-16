/**
 * 
 */
package com.yishuifengxiao.common.code.eunm;

import org.springframework.util.Assert;

import com.yishuifengxiao.common.code.constant.Constant;

/**
 * 验证码类型
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public enum CodeType {

	/**
	 * 短信验证码
	 */
	SMS(Constant.DEFAULT_PARAMETER_NAME_CODE_SMS, "短信验证码"),
	/**
	 * 图片验证码
	 */
	IMAGE(Constant.DEFAULT_PARAMETER_NAME_CODE_IMAGE, "图片验证码"),
	/**
	 * 邮件验证码
	 */
	EMAIL(Constant.DEFAULT_PARAMETER_NAME_CODE_EMAIL, "邮件验证码");

	private String code;

	private String name;

	private CodeType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * 根据参数解析验证码类型
	 * 
	 * @param name
	 * @return
	 */
	public static CodeType parse(String code) {
		Assert.notNull(code, "参数不能为空");
		CodeType type = null;
		switch (code.trim()) {
		case Constant.DEFAULT_PARAMETER_NAME_CODE_SMS:
			type = CodeType.SMS;
			break;
		case Constant.DEFAULT_PARAMETER_NAME_CODE_IMAGE:
			type = CodeType.IMAGE;
			break;
		case Constant.DEFAULT_PARAMETER_NAME_CODE_EMAIL:
			type = CodeType.EMAIL;
			break;
		default:
			break;
		}
		return type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
