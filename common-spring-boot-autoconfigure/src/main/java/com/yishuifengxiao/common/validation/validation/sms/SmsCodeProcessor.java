package com.yishuifengxiao.common.validation.validation.sms;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.validation.entity.SmsCode;
import com.yishuifengxiao.common.validation.eunm.CodeType;
import com.yishuifengxiao.common.validation.generator.CodeGenerator;
import com.yishuifengxiao.common.validation.processor.AbstractCodeProcessor;
import com.yishuifengxiao.common.validation.repository.CodeRepository;
import com.yishuifengxiao.common.validation.sender.CodeSender;

/**
 * 短信验证码处理器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class SmsCodeProcessor extends AbstractCodeProcessor<SmsCode> {
	private final static Logger log = LoggerFactory.getLogger(SmsCodeProcessor.class);

	private CodeSender<SmsCode> smsCodeSender;

	@Override
	protected void send(ServletWebRequest request, SmsCode smsCode) throws ValidateException {
		smsCodeSender.send(getPhoneNumber(request), smsCode, CodeType.SMS);
	}

	@Override
	protected String generateKey(ServletWebRequest request) throws ValidateException {
		return getPhoneNumber(request);
	}

	/**
	 * 从请求参数中获取到手机号码
	 * 
	 * @param request
	 * @return
	 * @throws ValidateException
	 */
	private String getPhoneNumber(ServletWebRequest request) throws ValidateException {
		String mobile = null;
		try {
			// 获取到手机号参数
			String paramName = this.codeProperties.getSms().getCodeKey();
			// 获取到手机号
			mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(), paramName);
		} catch (ServletRequestBindingException e) {
			log.info("========================> 获取手机号码失败，失败的原因为 {}", e.getMessage());
			throw new ValidateException("获取手机号码失败");
		}
		return mobile;
	}

	@Override
	protected String getCodeInRequest(ServletWebRequest request) throws ValidateException {
		String codeInRequest = null;
		try {
			codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),
					this.codeProperties.getSms().getCodeValue());
		} catch (ServletRequestBindingException e) {
			log.info("========================> 从请求中获取验证码失败，失败的原因为 {}", e.getMessage());
			throw new ValidateException("从请求中获取验证码失败");
		}

		return codeInRequest;
	}

	public CodeSender<SmsCode> getSmsCodeSender() {
		return smsCodeSender;
	}

	public void setSmsCodeSender(CodeSender<SmsCode> smsCodeSender) {
		this.smsCodeSender = smsCodeSender;
	}

	public SmsCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeProperties codeProperties) {
		super(codeGenerators, repository, codeProperties);
	}

	public SmsCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeProperties codeProperties, CodeSender<SmsCode> smsCodeSender) {
		super(codeGenerators, repository, codeProperties);
		this.smsCodeSender = smsCodeSender;
	}

	public SmsCodeProcessor() {

	}

}
