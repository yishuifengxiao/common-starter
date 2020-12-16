package com.yishuifengxiao.common.code;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//gitee.com/zhiyubujian/common-starter.git
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.yishuifengxiao.common.code.autoconfigure.MailExtendAutoConfiguration;
import com.yishuifengxiao.common.code.autoconfigure.RedisExtendAutoConfiguration;
import com.yishuifengxiao.common.code.extractor.CodeExtractor;
import com.yishuifengxiao.common.code.extractor.SimpleCodeExtractor;
import com.yishuifengxiao.common.code.generator.CodeGenerator;
import com.yishuifengxiao.common.code.generator.impl.EmailCodeGenerator;
import com.yishuifengxiao.common.code.generator.impl.ImageCodeGenerator;
import com.yishuifengxiao.common.code.generator.impl.SmsCodeGenerator;
import com.yishuifengxiao.common.code.processor.CodeProcessor;
import com.yishuifengxiao.common.code.processor.SimpleCodeProcessor;
import com.yishuifengxiao.common.code.repository.CodeRepository;
import com.yishuifengxiao.common.code.repository.impl.SimpleCodeRepository;
import com.yishuifengxiao.common.code.sender.CodeSender;
import com.yishuifengxiao.common.code.sender.impl.ImageCodeSender;
import com.yishuifengxiao.common.code.sender.impl.SmsCodeSender;

import lombok.extern.slf4j.Slf4j;

/**
 * 验证码启动类
 * 
 * @author yishui
 *
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({ CodeProperties.class })
@Import({ MailExtendAutoConfiguration.class, RedisExtendAutoConfiguration.class })
@AutoConfigureAfter(value = { RedisExtendAutoConfiguration.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.code", name = { "enable" }, havingValue = "true", matchIfMissing = true)
public class CodeAutoConfiguration {

	@Autowired
	private CodeProperties codeProperties;

	/**
	 * 验证码内存管理器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = { "redisTemplate", "codeRepository" })
	@Bean("codeRepository")
	public CodeRepository codeRepository() {
		return new SimpleCodeRepository();
	}

	/**
	 * 验证码处理器
	 * 
	 * @param codeGenerators 系统中所有的 {@link CodeGenerator} 验证码生成器接口的实现。key为bean的名字
	 * @param codeSenders    系统中所有的 {@link CodeSender } 验证码发送器接口的实现，。key为bean的名字
	 * @param repository     验证码存取工具
	 * @param codeExtractor  验证码信息提取器
	 * @return
	 */
	@Bean("codeProcessor")
	@ConditionalOnMissingBean(name = "codeProcessor")
	public CodeProcessor codeProcessor(CodeProperties codeProperties, Map<String, CodeGenerator> codeGenerators,
			Map<String, CodeSender> codeSenders, CodeRepository repository, CodeExtractor codeExtractor) {
		SimpleCodeProcessor simpleCodeProcessor = new SimpleCodeProcessor();
		simpleCodeProcessor.setCodeExtractor(codeExtractor);
		simpleCodeProcessor.setCodeGenerators(codeGenerators);
		simpleCodeProcessor.setCodeProperties(codeProperties);
		simpleCodeProcessor.setCodeSenders(codeSenders);
		simpleCodeProcessor.setRepository(repository);
		return simpleCodeProcessor;
	}

	/**
	 * 验证码信息提取器
	 * 
	 * @return
	 */
	@Bean("codeExtractor")
	@ConditionalOnMissingBean(name = "codeExtractor")
	public CodeExtractor codeExtractor() {
		return new SimpleCodeExtractor();
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
	 * 图形验证码发送器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "imageCodeSender")
	@Bean("imageCodeSender")
	public CodeSender imageCodeSender() {
		return new ImageCodeSender();
	}

	/**
	 * 短信验证码发送器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "smsCodeSender")
	@Bean("smsCodeSender")
	public CodeSender smsCodeSender() {
		return new SmsCodeSender();
	}

	/**
	 * 短信验证码生成器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "smsCodeGenerator")
	@Bean("smsCodeGenerator")
	public CodeGenerator smsCodeGenerator() {
		return new SmsCodeGenerator(codeProperties);
	}

	/**
	 * 邮件验证码生成器
	 * 
	 * @return
	 */
	@ConditionalOnMissingBean(name = "emailCodeGenerator")
	@Bean("emailCodeGenerator")
	@ConditionalOnBean(name = "emailCodeSender")
	public CodeGenerator emailCodeGenerator() {
		return new EmailCodeGenerator(codeProperties);
	}

	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <验证码> 相关的配置");
	}

}
