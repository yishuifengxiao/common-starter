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
 * @version 1.0.0
 * @since 1.0.0
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

	/**
	 * 类型代码
	 */
	private String code;

	/**
	 * 类型名称
	 */
	private String name;

	private CodeType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * 根据名字解析出验证码类型
	 * 
	 * @param code 名字
	 * @return 验证码类型
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

	/**
	 * 获取类型代码
	 * 
	 * @return 类型代码
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 获取类型名称
	 * 
	 * @return 类型名称
	 */
	public String getName() {
		return name;
	}

}
