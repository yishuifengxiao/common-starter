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
import com.yishuifengxiao.common.code.extractor.CodeExtractor;
import com.yishuifengxiao.common.code.generator.CodeGenerator;
import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * 短信验证码生成器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class SmsCodeGenerator implements CodeGenerator {

	private CodeProperties codeProperties;

	@Override
	public SmsCode generate(ServletWebRequest servletWebRequest) {
		String code = RandomStringUtils.random(codeProperties.getSms().getLength(),
				codeProperties.getSms().getContainLetter(), codeProperties.getSms().getContainNumber());
		return new SmsCode(codeProperties.getSms().getExpireIn(), code);
	}

	@Override
	public String generateKey(ServletWebRequest request, CodeExtractor codeExtractor) throws ValidateException {
		String value = codeExtractor.extractKey(request.getRequest(), this.codeProperties.getSms().getCodeKey());
		if (StringUtils.isBlank(value)) {
			throw new ValidateException(ErrorCode.ERROR_CODE_TARGET, "获取目标手机号失败");
		}
		return value;
	}

	@Override
	public String getCodeInRequest(ServletWebRequest request, CodeExtractor codeExtractor) throws ValidateException {
		return codeExtractor.extractValue(request.getRequest(), this.codeProperties.getSms().getCodeValue());
	}

	public CodeProperties getCodeProperties() {
		return codeProperties;
	}

	public void setCodeProperties(CodeProperties codeProperties) {
		this.codeProperties = codeProperties;
	}

	public SmsCodeGenerator(CodeProperties codeProperties) {
		this.codeProperties = codeProperties;
	}

	public SmsCodeGenerator() {

	}

}
