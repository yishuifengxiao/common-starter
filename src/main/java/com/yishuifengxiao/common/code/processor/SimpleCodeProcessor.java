/**
 * 
 */
package com.yishuifengxiao.common.code.processor;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.constant.ErrorCode;
import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.eunm.CodeType;
import com.yishuifengxiao.common.code.extractor.CodeExtractor;
import com.yishuifengxiao.common.code.generator.CodeGenerator;
import com.yishuifengxiao.common.code.repository.CodeRepository;
import com.yishuifengxiao.common.code.sender.CodeSender;
import com.yishuifengxiao.common.tool.exception.ValidateException;

import lombok.extern.slf4j.Slf4j;

/**
 * 抽象验证码处理器
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
@Slf4j
public class SimpleCodeProcessor implements CodeProcessor {
	/**
	 * 收集系统中所有的 {@link CodeGenerator} 验证码生成器接口的实现。key为bean的名字
	 */
	private Map<String, CodeGenerator> codeGenerators;
	/**
	 * 收集系统中所有的 {@link CodeSender } 验证码发送器接口的实现，。key为bean的名字
	 */
	private Map<String, CodeSender> codeSenders;

	/**
	 * 验证码存取工具
	 */
	private CodeRepository repository;

	/**
	 * 验证码信息提取器
	 */
	private CodeExtractor codeExtractor;

	private CodeProperties codeProperties;

	@Override
	public ValidateCode create(ServletWebRequest request, CodeType codeType) throws ValidateException {

		// 验证码的唯一标识符
		String key = generator(codeType).generateKey(request, codeExtractor);

		// 生成验证码
		ValidateCode validateCode = generator(codeType).generate(request);

		log.debug("【易水组件】生成的验证码的类型为 {}, 标识符为{}，内容为 {}", codeType, key, validateCode);

		// 保存验证码
		this.save(request, key, validateCode);

		// 发送验证码
		this.codeSender(codeType).send(request, key, validateCode);

		return validateCode;
	}

	/**
	 * 获取对应的验证码生成器
	 * 
	 * @param codeType 验证码的类型
	 * @return 验证码生成器
	 * @throws ValidateException
	 */
	private CodeGenerator generator(CodeType codeType) throws ValidateException {
		// 根据请求的url获取校验码的类型
		String generatorName = codeType.getCode() + "CodeGenerator";
		// 获取到对应的验证码生成器
		CodeGenerator codeGenerator = codeGenerators.get(generatorName);
		if (codeGenerator == null) {
			throw new ValidateException(ErrorCode.GENERATOR_NO_EXTIS, codeType.getName() + "生成器不存在");
		}
		return codeGenerator;
	}

	/**
	 * 获取对应的验证码发送器
	 * 
	 * @param codeType 验证码的类型
	 * @return 验证码发送器
	 * @throws ValidateException
	 */
	private CodeSender codeSender(CodeType codeType) throws ValidateException {

		String senderName = codeType.getCode() + "CodeSender";
		CodeSender codeSender = codeSenders.get(senderName);
		if (null == codeSender) {
			throw new ValidateException(ErrorCode.SENDER_NO_EXTIS, codeType.getName() + "发送器不存在");
		}

		return codeSender;
	}

	/**
	 * 保存验证码<br/>
	 * <br/>
	 * 生成验证码存储时的key<br/>
	 * 对于短信验证码,key为手机号<br/>
	 * 对于邮件验证码，key为邮箱<br/>
	 * 对于图像验证码，key为sessionid或指定的值
	 * 
	 * @param request      ServletWebRequest
	 * @param key          验证码的唯一标识符
	 * @param validateCode 验证码
	 * @throws ValidateException
	 */
	private void save(ServletWebRequest request, String key, ValidateCode validateCode) throws ValidateException {
		repository.save(request, key, validateCode);
	}

	@Override
	public void validate(ServletWebRequest request, CodeType codeType) throws ValidateException {
		// 验证码生成器
		CodeGenerator codeGenerator = generator(codeType);
		// 验证码的唯一标识符
		String key = codeGenerator.generateKey(request, codeExtractor);
		// 获取请求中的验证码
		String codeInRequest = codeGenerator.getCodeInRequest(request, codeExtractor);

		this.validate(request, key, codeInRequest);
	}

	@Override
	public void validate(ServletWebRequest request, String key, String codeInRequest) throws ValidateException {

		log.debug("【易水组件】从请求中获取的验证码的内容的为 {} ", codeInRequest);

		if (StringUtils.isBlank(codeInRequest)) {
			throw new ValidateException(ErrorCode.REQUEST_CODE_NO_EXTIS, "验证码不能为空");
		}
		/**
		 * 获取到存储的验证码
		 */
		ValidateCode codeInSession = repository.get(request, key);

		log.info("【易水组件】从系统中获取到存储的验证码为 {}", codeInSession);

		if (codeInSession == null) {
			throw new ValidateException(ErrorCode.SESSION_CODE_NO_EXTIS, "验证码不存在");
		}

		if (codeInSession.isExpired()) {
			repository.remove(request, key);
			throw new ValidateException(ErrorCode.CODE_EXPIRED, "验证码已过期");
		}

		if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
			throw new ValidateException(ErrorCode.CODE_NO_MATCH, "验证码不匹配");
		}
		if (BooleanUtils.isNotFalse(codeProperties.getDeleteOnSuccess())) {
			// 移除验证码
			repository.remove(request, key);
		}

	}

	public Map<String, CodeGenerator> getCodeGenerators() {
		return codeGenerators;
	}

	public void setCodeGenerators(Map<String, CodeGenerator> codeGenerators) {
		this.codeGenerators = codeGenerators;
	}

	public Map<String, CodeSender> getCodeSenders() {
		return codeSenders;
	}

	public void setCodeSenders(Map<String, CodeSender> codeSenders) {
		this.codeSenders = codeSenders;
	}

	public CodeRepository getRepository() {
		return repository;
	}

	public void setRepository(CodeRepository repository) {
		this.repository = repository;
	}

	public CodeExtractor getCodeExtractor() {
		return codeExtractor;
	}

	public void setCodeExtractor(CodeExtractor codeExtractor) {
		this.codeExtractor = codeExtractor;
	}

	public CodeProperties getCodeProperties() {
		return codeProperties;
	}

	public void setCodeProperties(CodeProperties codeProperties) {
		this.codeProperties = codeProperties;
	}

}
