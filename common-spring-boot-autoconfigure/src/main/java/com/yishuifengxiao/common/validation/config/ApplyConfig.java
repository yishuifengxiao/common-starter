package com.yishuifengxiao.common.validation.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.validation.entity.EmailCode;
import com.yishuifengxiao.common.validation.entity.SmsCode;
import com.yishuifengxiao.common.validation.generator.CodeGenerator;
import com.yishuifengxiao.common.validation.holder.CodeProcessorHolder;
import com.yishuifengxiao.common.validation.processor.CodeProcessor;
import com.yishuifengxiao.common.validation.repository.CodeRepository;
import com.yishuifengxiao.common.validation.repository.impl.DefaultCodeRepository;
import com.yishuifengxiao.common.validation.sender.CodeSender;
import com.yishuifengxiao.common.validation.sender.impl.EmailCodeSender;
import com.yishuifengxiao.common.validation.validation.email.EmailCodeGenerator;
import com.yishuifengxiao.common.validation.validation.email.EmailCodeProcessor;
import com.yishuifengxiao.common.validation.validation.image.ImageCodeGenerator;
import com.yishuifengxiao.common.validation.validation.image.ImageCodeProcessor;
import com.yishuifengxiao.common.validation.validation.sms.SmsCodeGenerator;
import com.yishuifengxiao.common.validation.validation.sms.SmsCodeProcessor;

/**
 * 验证码注入配置(使验证码的配置生效)
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
@Configuration("validationApplyConfig")
@EnableConfigurationProperties(CodeProperties.class)
public class ApplyConfig {
	@Autowired
	private CodeProperties codeProperties;

	/**
	 * 收集项目里所有验证码处理器，并将其注入到验证码管理器中
	 * 
	 * @param codeProcessors
	 * @return
	 */
	@Bean
	public CodeProcessorHolder codeProcessorHolder(Map<String, CodeProcessor> codeProcessors) {
		return new CodeProcessorHolder(codeProcessors);
	}

	/**
	 * 注入一个默认的验证码存取管理器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "codeRepository")
	@Bean("codeRepository")
	public CodeRepository codeRepository() {
		return new DefaultCodeRepository();
	}

	/**
	 * 注入同一个名为smsCodeGenerator的短信验证码生成器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "smsCodeGenerator")
	@Bean("smsCodeGenerator")
	@ConditionalOnBean(name = "smsCodeSender")
	public CodeGenerator smsCodeGenerator() {
		return new SmsCodeGenerator(codeProperties);
	}

	/**
	 * 注入一个名为 smsCodeProcessor 的短信验证码处理器
	 * 
	 * @param codeGenerators
	 * @param repository
	 * @param smsCodeSender
	 * @return
	 */
	@ConditionalOnMissingBean(name = "smsCodeProcessor")
	@Bean("smsCodeProcessor")
	@ConditionalOnBean(name = "smsCodeSender")
	public CodeProcessor smsCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeSender<SmsCode> codeSender) {
		return new SmsCodeProcessor(codeGenerators, repository, codeProperties, codeSender);
	}

	/**
	 * 注入同一个名为imageCodeGenerator的图形验证码生成器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "imageCodeGenerator")
	@Bean("imageCodeGenerator")
	public CodeGenerator imageCodeGenerator() {
		return new ImageCodeGenerator(codeProperties);
	}

	/**
	 * 注入一个名为 imageCodeProcessor 的图形验证码处理器
	 * 
	 * @param codeGenerators
	 * @param repository
	 * @return
	 */
	@ConditionalOnMissingBean(name = "imageCodeProcessor")
	@Bean("imageCodeProcessor")
	public CodeProcessor imageCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository) {
		return new ImageCodeProcessor(codeGenerators, repository, codeProperties);
	}

	/**
	 * 注入一个名为emailCodeGenerator的邮箱验证码生成器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "emailCodeGenerator")
	@Bean("emailCodeGenerator")
	@ConditionalOnProperty(prefix = "spring.mail", name = { "host", "username" })
	public CodeGenerator emailCodeGenerator() {
		return new EmailCodeGenerator(codeProperties);
	}

	/**
	 * 注册一个名为emailCodeProcessor的邮箱验证码处理
	 * 
	 * @param codeGenerators
	 * @param repository
	 * @return
	 */
	@ConditionalOnMissingBean(name = "emailCodeProcessor")
	@Bean("emailCodeProcessor")
	@ConditionalOnProperty(prefix = "spring.mail", name = { "host", "username" })
	public CodeProcessor emailCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeSender<EmailCode> codeSender) {
		return new EmailCodeProcessor(codeGenerators, repository, codeProperties, codeSender);
	}

	@Autowired(required = false)
	private JavaMailSender javaMailSender;

	/**
	 * 注入一个邮箱发送器
	 * 
	 * @param javaMailSender
	 * @return
	 */
	@Bean("emailCodeSender")
	@ConditionalOnMissingBean(name = "emailCodeSender")
	@ConditionalOnProperty(prefix = "spring.mail", name = { "host", "username" })
	public EmailCodeSender emailCodeSender(Environment env) {
		return new EmailCodeSender(javaMailSender, env.getProperty("spring.mail.username"), codeProperties);
	}

}
