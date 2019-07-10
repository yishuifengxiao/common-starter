/**
 * 
 */
package com.yishuifengxiao.common.validation.validation.sms;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.validation.entity.SmsCode;
import com.yishuifengxiao.common.validation.generator.CodeGenerator;

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
				codeProperties.getSms().isContainLetter(), codeProperties.getSms().isContainNumber());
		return new SmsCode(codeProperties.getSms().getExpireIn(), code);
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
