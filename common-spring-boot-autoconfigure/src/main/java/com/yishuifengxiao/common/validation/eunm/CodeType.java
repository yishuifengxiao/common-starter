/**
 * 
 */
package com.yishuifengxiao.common.validation.eunm;

import org.springframework.util.Assert;

import com.yishuifengxiao.common.constant.CodeConstant;

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
	SMS(CodeConstant.DEFAULT_PARAMETER_NAME_CODE_SMS),
	/**
	 * 图片验证码
	 */
	IMAGE(CodeConstant.DEFAULT_PARAMETER_NAME_CODE_IMAGE),
	/**
	 * 邮件验证码
	 */
	EMAIL(CodeConstant.DEFAULT_PARAMETER_NAME_CODE_EMAIL);

	private String name;

	private CodeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 根据参数解析验证码类型
	 * 
	 * @param name
	 * @return
	 */
	public static CodeType parse(String name) {
		Assert.notNull(name, "参数不能为空");
		CodeType type = null;
		switch (name.trim()) {
		case CodeConstant.DEFAULT_PARAMETER_NAME_CODE_SMS:
			type = CodeType.SMS;
			break;
		case CodeConstant.DEFAULT_PARAMETER_NAME_CODE_IMAGE:
			type = CodeType.IMAGE;
			break;
		case CodeConstant.DEFAULT_PARAMETER_NAME_CODE_EMAIL:
			type = CodeType.EMAIL;
			break;
		default:
			break;
		}
		return type;
	}

}
