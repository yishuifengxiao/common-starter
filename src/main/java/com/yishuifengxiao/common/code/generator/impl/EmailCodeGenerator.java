package com.yishuifengxiao.common.code.generator.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.entity.EmailCode;
import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.generator.BaseCodeGenerator;
import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * <p>
 * 邮件验证码生成器
 * </p>
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class EmailCodeGenerator extends BaseCodeGenerator {

	@Override
	public ValidateCode generate(ServletWebRequest servletWebRequest, CodeProperties codeProperties) {
		String code = RandomStringUtils.random(codeProperties.getEmail().getLength(),
				codeProperties.getEmail().getContainLetter(), codeProperties.getEmail().getContainNumber());
		return new EmailCode(codeProperties.getEmail().getExpireIn(), code);
	}

	@Override
	public String generateKey(ServletWebRequest request, CodeProperties codeProperties) throws CustomException {
		String value = this.extract(request.getRequest(), codeProperties.getEmail().getCodeKey());
		if (StringUtils.isBlank(value)) {
			throw new CustomException("获取目标邮箱失败");
		}
		return value;
	}

	@Override
	public String getCodeInRequest(ServletWebRequest request, CodeProperties codeProperties) throws CustomException {
		return this.extract(request.getRequest(), codeProperties.getEmail().getCodeValue());
	}

}
