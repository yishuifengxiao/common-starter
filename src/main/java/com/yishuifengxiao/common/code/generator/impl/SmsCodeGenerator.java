/**
 * 
 */
package com.yishuifengxiao.common.code.generator.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.constant.ErrorCode;
import com.yishuifengxiao.common.code.entity.SmsCode;
import com.yishuifengxiao.common.code.generator.BaseCodeGenerator;
import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * <p>
 * 短信验证码生成器
 * </p>
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SmsCodeGenerator extends BaseCodeGenerator {

	@Override
	public SmsCode generate(ServletWebRequest servletWebRequest, CodeProperties codeProperties) {
		String code = RandomStringUtils.random(codeProperties.getSms().getLength(),
				codeProperties.getSms().getContainLetter(), codeProperties.getSms().getContainNumber());
		return new SmsCode(codeProperties.getSms().getExpireIn(), code);
	}

	@Override
	public String generateKey(ServletWebRequest request, CodeProperties codeProperties) throws ValidateException {
		String value = this.extract(request.getRequest(), codeProperties.getSms().getCodeKey());
		if (StringUtils.isBlank(value)) {
			throw new ValidateException(ErrorCode.ERROR_CODE_TARGET, "获取目标手机号失败");
		}
		return value;
	}

	@Override
	public String getCodeInRequest(ServletWebRequest request, CodeProperties codeProperties) throws ValidateException {
		return this.extract(request.getRequest(), codeProperties.getSms().getCodeValue());
	}

}
