package com.yishuifengxiao.common.validation.code.email;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.validation.entity.EmailCode;
import com.yishuifengxiao.common.validation.eunm.CodeType;
import com.yishuifengxiao.common.validation.generator.CodeGenerator;
import com.yishuifengxiao.common.validation.processor.AbstractCodeProcessor;
import com.yishuifengxiao.common.validation.repository.CodeRepository;
import com.yishuifengxiao.common.validation.sender.CodeSender;

/**
 * 邮箱验证码处理器
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class EmailCodeProcessor extends AbstractCodeProcessor<EmailCode> {

	private final static Logger log = LoggerFactory.getLogger(EmailCodeProcessor.class);

	private CodeSender<EmailCode> emailCodeSender;

	@Override
	protected void send(ServletWebRequest request, EmailCode validateCode) throws ValidateException {
		emailCodeSender.send(generateKey(request), validateCode, CodeType.EMAIL);

	}

	@Override
	protected String generateKey(ServletWebRequest request) throws ValidateException {
		return getEmail(request);
	}

	/**
	 * 从请求参数中获取到手机号码
	 * 
	 * @param request
	 * @return
	 * @throws ValidateException
	 */
	private String getEmail(ServletWebRequest request) throws ValidateException {
		String email = null;
		try {
			// 获取到邮箱参数
			String paramName = this.codeProperties.getEmail().getCodeKey();
			// 获取到邮箱
			email = ServletRequestUtils.getRequiredStringParameter(request.getRequest(), paramName);
		} catch (ServletRequestBindingException e) {
			log.info("获取邮箱失败，失败的原因为 {}", e.getMessage());
			throw new ValidateException("获取邮箱失败");
		}
		return email;
	}

	@Override
	protected String getCodeInRequest(ServletWebRequest request) throws ValidateException {
		// 请求里的验证码
		String codeInRequest = null;
		try {
			codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),
					this.codeProperties.getEmail().getCodeValue());
		} catch (ServletRequestBindingException e) {
			log.info("从请求中获取验证码失败，失败的原因为 {}", e.getMessage());
			throw new ValidateException("从请求中获取验证码失败");
		}

		return codeInRequest;
	}

	public EmailCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeProperties codeProperties, CodeSender<EmailCode> emailCodeSender) {
		super(codeGenerators, repository, codeProperties);
		this.emailCodeSender = emailCodeSender;
	}

	public EmailCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeProperties codeProperties) {
		super(codeGenerators, repository, codeProperties);
	}

	public EmailCodeProcessor() {

	}

}
