package com.yishuifengxiao.common.autoconfigure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.validation.CodeProcessorHolder;
import com.yishuifengxiao.common.validation.code.email.EmailCodeGenerator;
import com.yishuifengxiao.common.validation.code.email.EmailCodeProcessor;
import com.yishuifengxiao.common.validation.code.image.ImageCodeGenerator;
import com.yishuifengxiao.common.validation.code.image.ImageCodeProcessor;
import com.yishuifengxiao.common.validation.code.sms.SmsCodeGenerator;
import com.yishuifengxiao.common.validation.code.sms.SmsCodeProcessor;
import com.yishuifengxiao.common.validation.entity.EmailCode;
import com.yishuifengxiao.common.validation.entity.SmsCode;
import com.yishuifengxiao.common.validation.generator.CodeGenerator;
import com.yishuifengxiao.common.validation.processor.CodeProcessor;
import com.yishuifengxiao.common.validation.repository.CodeRepository;
import com.yishuifengxiao.common.validation.repository.impl.DefaultCodeRepository;
import com.yishuifengxiao.common.validation.repository.impl.RedisCodeRepository;
import com.yishuifengxiao.common.validation.sender.CodeSender;
import com.yishuifengxiao.common.validation.sender.impl.EmailCodeSender;

/**
 * 验证码启动类
 * 
 * @author yishui
 *
 */
@Configuration
@EnableConfigurationProperties({ CodeProperties.class })
public class ValidateCodeAutoConfiguration {

	@Autowired
	private CodeProperties codeProperties;

	/**
	 * 创建一个验证码管理器 <br/>
	 * 
	 * 收集项目里所有验证码处理器，并将其注入
	 * 
	 * @param codeProcessors
	 * @return
	 */
	@Bean
	public CodeProcessorHolder codeProcessorHolder(Map<String, CodeProcessor> codeProcessors) {
		return new CodeProcessorHolder(codeProcessors);
	}

	/**
	 * 验证码redis管理器
	 * 
	 * @return
	 */
	@ConditionalOnBean(name = "redisTemplate")
	@Bean("codeRepository")
	public CodeRepository redisRepository(RedisTemplate<String, Object> redisTemplate) {
		return new RedisCodeRepository(redisTemplate);
	}

	/**
	 * 验证码内存管理器
	 * 
	 * @return
	 */
	@Bean("codeRepository")
	@ConditionalOnMissingBean(name = "codeRepository")
	public CodeRepository codeRepository() {
		return new DefaultCodeRepository();
	}

	/**
	 * 图形验证码生成器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "imageCodeGenerator")
	@Bean("imageCodeGenerator")
	public CodeGenerator imageCodeGenerator() {
		return new ImageCodeGenerator(codeProperties);
	}

	/**
	 * 图形验证码处理器
	 * 
	 * @param codeGenerators
	 * @param codeRepository
	 * @return
	 */
	@ConditionalOnMissingBean(name = "imageCodeProcessor")
	@Bean("imageCodeProcessor")
	public CodeProcessor imageCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository codeRepository) {
		return new ImageCodeProcessor(codeGenerators, codeRepository, codeProperties);
	}

	/**
	 * 短信验证码生成器
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
	 * 短信验证码处理器
	 * 
	 * @param codeGenerators
	 * @param codeRepository
	 * @param smsCodeSender
	 * @return
	 */
	@ConditionalOnMissingBean(name = "smsCodeProcessor")
	@Bean("smsCodeProcessor")
	@ConditionalOnBean(name = "smsCodeSender")
	public CodeProcessor smsCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository codeRepository,
			CodeSender<SmsCode> smsCodeSender) {
		return new SmsCodeProcessor(codeGenerators, codeRepository, codeProperties, smsCodeSender);
	}

	@Autowired(required = false)
	private JavaMailSender javaMailSender;

	/**
	 * 邮箱验证码发送器
	 * 
	 * @param env
	 * @return
	 */
	@Bean("emailCodeSender")
	@ConditionalOnMissingBean(name = { "emailCodeSender", })
	@ConditionalOnProperty(prefix = "spring.mail", name = { "host", "username" })
	public CodeSender<EmailCode> emailCodeSender(Environment env) {
		return new EmailCodeSender(javaMailSender, env.getProperty("spring.mail.username"), codeProperties);
	}

	/**
	 * 邮箱验证码生成器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "emailCodeGenerator")
	@Bean("emailCodeGenerator")
	@ConditionalOnBean(name = "emailCodeSender")
	public CodeGenerator emailCodeGenerator() {
		return new EmailCodeGenerator(codeProperties);
	}

	/**
	 * 邮箱验证码处理
	 * 
	 * @param codeGenerators
	 * @param repository
	 * @return
	 */
	@ConditionalOnMissingBean(name = "emailCodeProcessor")
	@ConditionalOnBean(name = "emailCodeSender")
	@Bean("emailCodeProcessor")
	public CodeProcessor emailCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeSender<EmailCode> emailCodeSender) {
		return new EmailCodeProcessor(codeGenerators, repository, codeProperties, emailCodeSender);
	}

}
