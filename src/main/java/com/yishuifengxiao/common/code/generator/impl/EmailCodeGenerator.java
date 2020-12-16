package com.yishuifengxiao.common.code.generator.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.constant.ErrorCode;
import com.yishuifengxiao.common.code.entity.EmailCode;
import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.extractor.CodeExtractor;
import com.yishuifengxiao.common.code.generator.CodeGenerator;
import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * 邮箱验证码生成器
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class EmailCodeGenerator implements CodeGenerator {

	private CodeProperties codeProperties;

	@Override
	public ValidateCode generate(ServletWebRequest servletWebRequest) {
		String code = RandomStringUtils.random(codeProperties.getEmail().getLength(),
				codeProperties.getEmail().getContainLetter(), codeProperties.getEmail().getContainNumber());
		return new EmailCode(codeProperties.getEmail().getExpireIn(), code);
	}

	@Override
	public String generateKey(ServletWebRequest request, CodeExtractor codeExtractor) throws ValidateException {
		String value = codeExtractor.extractKey(request.getRequest(), this.codeProperties.getEmail().getCodeKey());
		if (StringUtils.isBlank(value)) {
			throw new ValidateException(ErrorCode.ERROR_CODE_TARGET, "获取目标邮箱失败");
		}
		return value;
	}

	@Override
	public String getCodeInRequest(ServletWebRequest request, CodeExtractor codeExtractor) throws ValidateException {
		return codeExtractor.extractValue(request.getRequest(), this.codeProperties.getEmail().getCodeValue());
	}

	public CodeProperties getCodeProperties() {
		return codeProperties;
	}

	public void setCodeProperties(CodeProperties codeProperties) {
		this.codeProperties = codeProperties;
	}

	public EmailCodeGenerator(CodeProperties codeProperties) {

		this.codeProperties = codeProperties;
	}

	public EmailCodeGenerator() {

	}

}
