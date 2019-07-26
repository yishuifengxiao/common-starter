/**
 * 
 */
package com.yishuifengxiao.common.validation.processor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.validation.entity.ValidateCode;
import com.yishuifengxiao.common.validation.eunm.CodeType;
import com.yishuifengxiao.common.validation.generator.CodeGenerator;
import com.yishuifengxiao.common.validation.repository.CodeRepository;

/**
 * 抽象验证码处理器
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public abstract class AbstractCodeProcessor<C extends ValidateCode> implements CodeProcessor {
	private final static Logger log = LoggerFactory.getLogger(AbstractCodeProcessor.class);
	/**
	 * 收集系统中所有的 {@link ValidateCodeGenerator} 接口的实现。key为bean的名字
	 */
	protected Map<String, CodeGenerator> codeGenerators;
	/**
	 * 验证码存取工具
	 */
	protected CodeRepository repository;

	/**
	 * 验证码配置属性
	 */
	protected CodeProperties codeProperties;

	@Override
	public void create(ServletWebRequest request) throws ValidateException {
		C validateCode = generate(request);
		save(request, validateCode);
		send(request, validateCode);
	}

	/**
	 * 生成校验码
	 * 
	 * @param request
	 * @return
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	private C generate(ServletWebRequest request) throws ValidateException {
		// 根据请求的url获取校验码的类型
		String type = getValidateCodeType(request).name().toLowerCase();
		String generatorName = type + "CodeGenerator";
		// 获取到对应的验证码生成器
		CodeGenerator codeGenerator = codeGenerators.get(generatorName);
		if (codeGenerator == null) {
			throw new ValidateException("验证码生成器" + generatorName + "不存在");
		}
		return (C) codeGenerator.generate(request);
	}

	/**
	 * 根据请求的url获取校验码的类型
	 * 
	 * @param request
	 * @return
	 */
	protected CodeType getValidateCodeType(ServletWebRequest request) {
		String type = StringUtils.substringBefore(getClass().getSimpleName(), "CodeProcessor");
		CodeType codeType = CodeType.parse(type.toLowerCase());
		return codeType;
	}

	/**
	 * 保存校验码
	 * 
	 * @param request
	 * @param validateCode
	 * @throws ValidateException
	 */
	private void save(ServletWebRequest request, C validateCode) throws ValidateException {
		log.debug("将验证码存入session时的验证码为 {},类形为 {}", validateCode, getValidateCodeType(request));
		repository.save(request, generateKey(request), validateCode);
	}

	/**
	 * 发送校验码，由子类实现
	 * 
	 * @param request
	 * @param validateCode
	 * @throws Exception
	 */
	protected abstract void send(ServletWebRequest request, C validateCode) throws ValidateException;

	/**
	 * 获取验证码存储时的key
	 * 
	 * @param request
	 * @return
	 * @throws ValidateException
	 */
	protected abstract String generateKey(ServletWebRequest request) throws ValidateException;

	@Override
	public void validate(ServletWebRequest request) throws ValidateException {
		/**
		 * 获取到存储的验证码
		 */
		ValidateCode codeInSession = repository.get(request, generateKey(request));
		/**
		 * 获取到验证码的类型
		 */
		CodeType validateCodeType = getValidateCodeType(request);
		log.info("从session中获取到验证码为 {}, 验证码类型为 {}", codeInSession, validateCodeType);
		if (codeInSession == null) {
			throw new ValidateException("验证码不存在");
		}

		if (codeInSession.isExpired()) {
			repository.remove(request, generateKey(request));
			throw new ValidateException("验证码已过期");
		}
		// 获取请求中的验证码
		String codeInRequest = getCodeInRequest(request);

		if (StringUtils.isBlank(codeInRequest)) {
			throw new ValidateException("验证码的值不能为空");
		}

		if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
			throw new ValidateException("验证码不匹配");
		}
		// 移除验证码
		repository.remove(request, generateKey(request));
	}

	/**
	 * 获取请求中携带的验证码
	 * 
	 * @param request
	 * @return
	 * @throws ValidateException
	 */
	protected abstract String getCodeInRequest(ServletWebRequest request) throws ValidateException;

	public Map<String, CodeGenerator> getCodeGenerators() {
		return codeGenerators;
	}

	public void setCodeGenerators(Map<String, CodeGenerator> codeGenerators) {
		this.codeGenerators = codeGenerators;
	}

	public CodeRepository getRepository() {
		return repository;
	}

	public void setRepository(CodeRepository repository) {
		this.repository = repository;
	}

	public AbstractCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeProperties codeProperties) {
		this.codeGenerators = codeGenerators;
		this.repository = repository;
		this.codeProperties = codeProperties;
	}

	public AbstractCodeProcessor() {

	}

}
