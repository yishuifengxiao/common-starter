/**
 * 
 */
package com.yishuifengxiao.common.code;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.eunm.CodeType;
import com.yishuifengxiao.common.code.generator.CodeGenerator;
import com.yishuifengxiao.common.code.holder.CodeHolder;
import com.yishuifengxiao.common.code.sender.CodeSender;
import com.yishuifengxiao.common.tool.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统验证码处理器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleCodeProducer implements CodeProducer {
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
	private CodeHolder repository;

	/**
	 * 验证码配置属性
	 */
	private CodeProperties codeProperties;

	/**
	 * 是否显示加载日志，默认为false
	 */
	private boolean show = false;

	/**
	 * 创建校验码
	 * 
	 * @param request  用户请求
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws CustomException 创建或发送验证码时出现问题
	 */
	@Override
	public ValidateCode create(ServletWebRequest request, CodeType codeType) throws CustomException {

		// 验证码的唯一标识符
		String key = generator(codeType).generateKey(request, codeProperties);
		return this.create(request, key, codeType);
	}

	/**
	 * <p>
	 * 创建校验码
	 * </p>
	 * 
	 * 生成验证码存储时的key的含义如下:
	 * <ul>
	 * <li>对于短信验证码,key为手机号</li>
	 * <li>对于邮件验证码，key为邮箱</li>
	 * <li>对于图像验证码，key为sessionid或指定的值</li>
	 * </ul>
	 * 
	 * @param request  用户请求
	 * @param key      验证码的发送目标
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws CustomException 创建或发送验证码时出现问题
	 */
	@Override
	public ValidateCode create(ServletWebRequest request, String key, CodeType codeType) throws CustomException {

		// 生成验证码
		ValidateCode validateCode = generator(codeType).generate(request, codeProperties);

		if (show) {
			log.info("【易水组件】生成的验证码的类型为 {}, 标识符为{}，内容为 {}", codeType, key, validateCode);
		}

		// 保存验证码
		repository.save(key, validateCode);

		return validateCode;
	}

	/**
	 * 创建校验码并发送验证码
	 * 
	 * @param request  用户请求
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws CustomException 创建或发送验证码时出现问题
	 */
	@Override
	public ValidateCode createAndSend(ServletWebRequest request, CodeType codeType) throws CustomException {

		// 验证码的唯一标识符
		String key = generator(codeType).generateKey(request, codeProperties);
		return this.createAndSend(request, key, codeType);
	}

	/**
	 * <p>
	 * 创建校验码并发送验证码
	 * </p>
	 * 
	 * 生成验证码存储时的key的含义如下:
	 * <ul>
	 * <li>对于短信验证码,key为手机号</li>
	 * <li>对于邮件验证码，key为邮箱</li>
	 * <li>对于图像验证码，key为sessionid或指定的值</li>
	 * </ul>
	 * 
	 * @param request  用户请求
	 * @param key      验证码的发送目标
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws CustomException 创建或发送验证码时出现问题
	 */
	@Override
	public ValidateCode createAndSend(ServletWebRequest request, String key, CodeType codeType) throws CustomException {
		ValidateCode validateCode = this.create(request, key, codeType);
		// 发送验证码
		this.codeSender(codeType).send(request, key, validateCode);
		return validateCode;
	}

	/**
	 * 获取对应的验证码生成器
	 * 
	 * @param codeType 验证码的类型
	 * @return 验证码生成器
	 * @throws CustomException 获取对应的验证码生成器时发生问题
	 */
	private CodeGenerator generator(CodeType codeType) throws CustomException {
		// 根据请求的url获取校验码的类型
		String generatorName = codeType.getCode() + "CodeGenerator";
		// 获取到对应的验证码生成器
		CodeGenerator codeGenerator = codeGenerators.get(generatorName);
		if (codeGenerator == null) {
			throw new CustomException(codeType.getName() + "生成器不存在");
		}
		return codeGenerator;
	}

	/**
	 * 获取对应的验证码发送器
	 * 
	 * @param codeType 验证码的类型
	 * @return 验证码发送器
	 * @throws CustomException 获取对应的验证码生成器时发生问题
	 */
	private CodeSender codeSender(CodeType codeType) throws CustomException {

		String senderName = codeType.getCode() + "CodeSender";
		CodeSender codeSender = codeSenders.get(senderName);
		if (null == codeSender) {
			throw new CustomException(codeType.getName() + "发送器不存在");
		}

		return codeSender;
	}

	/**
	 * <p>
	 * 校验用户请求中携带的验证码是否正确
	 * </p>
	 * 在验证码不匹配时会抛出异常，主要异常场景有:
	 * <ul>
	 * <li>验证码不存在</li>
	 * <li>存储的验证码已过期</li>
	 * <li>存储的验证码与给定的验证码不匹配</li>
	 * </ul>
	 * 
	 * @param request  用户请求
	 * @param codeType 验证码类型
	 * @throws CustomException 验证码不匹配或已过期等未通过验证时
	 */
	@Override
	public void validate(ServletWebRequest request, CodeType codeType) throws CustomException {
		// 验证码生成器
		CodeGenerator codeGenerator = generator(codeType);
		// 验证码的唯一标识符
		String key = codeGenerator.generateKey(request, codeProperties);
		// 获取请求中的验证码
		String codeInRequest = codeGenerator.getCodeInRequest(request, codeProperties);

		this.validate(key, codeInRequest);
	}

	/**
	 * <p>
	 * 根据验证码的唯一标识符判断给定的验证码是否正确
	 * </p>
	 * 在验证码不匹配时会抛出异常，主要异常场景有:
	 * <ul>
	 * <li>验证码不存在</li>
	 * <li>存储的验证码已过期</li>
	 * <li>存储的验证码与给定的验证码不匹配</li>
	 * </ul>
	 * 
	 * @param key           验证码的唯一标识符
	 * @param codeInRequest 给定的验证码
	 * @throws CustomException 验证码不匹配或已过期等未通过验证时
	 */
	@Override
	public void validate(String key, String codeInRequest) throws CustomException {

		if (show) {
			log.info("【易水组件】从请求中获取的验证码的内容的为 {} ", codeInRequest);
		}

		if (StringUtils.isBlank(codeInRequest)) {
			throw new CustomException("验证码不能为空");
		}
		/**
		 * 获取到存储的验证码
		 */
		ValidateCode codeInSession = repository.get(key);

		if (show) {
			log.info("【易水组件】从系统中获取到存储的验证码为 {}", codeInSession);
		}

		if (codeInSession == null) {
			throw new CustomException("验证码不存在");
		}

		if (codeInSession.isExpired()) {
			repository.remove(key);
			throw new CustomException("验证码已过期");
		}

		if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
			throw new CustomException("验证码不匹配");
		}
		if (BooleanUtils.isNotFalse(codeProperties.getDeleteOnSuccess())) {
			// 移除验证码
			repository.remove(key);
		}

	}

	public SimpleCodeProducer(Map<String, CodeGenerator> codeGenerators, Map<String, CodeSender> codeSenders,
							  CodeHolder repository, CodeProperties codeProperties) {
		this.codeGenerators = codeGenerators;
		this.codeSenders = codeSenders;
		this.repository = repository;
		this.codeProperties = codeProperties;
		this.show = BooleanUtils.isTrue(codeProperties.getShowDetail());
	}

}
