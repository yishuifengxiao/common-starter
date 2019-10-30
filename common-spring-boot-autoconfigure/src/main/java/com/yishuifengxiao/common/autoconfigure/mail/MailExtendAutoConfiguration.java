package com.yishuifengxiao.common.autoconfigure.mail;

import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.validation.entity.EmailCode;
import com.yishuifengxiao.common.validation.sender.CodeSender;
import com.yishuifengxiao.common.validation.sender.impl.EmailCodeSender;

/**
 * 注入邮件发送相关的配置
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
@Configuration
@ConditionalOnClass({ MimeMessage.class, MimeType.class, JavaMailSender.class })
@ConditionalOnProperty(prefix = "spring.mail", name = { "host", "username", "password" })
public class MailExtendAutoConfiguration {

	@Bean("emailCodeSender")
	@ConditionalOnMissingBean(name = "emailCodeSender")
	public CodeSender<EmailCode> emailCodeSender(Environment env, JavaMailSender javaMailSender,
			CodeProperties codeProperties) {
		EmailCodeSender emailCodeSender = new EmailCodeSender();
		emailCodeSender.setCodeProperties(codeProperties);
		emailCodeSender.setEmailSender(env.getProperty("spring.mail.username", "zhiyubujian@163.com"));
		emailCodeSender.setJavaMailSender(javaMailSender);
		return emailCodeSender;
	}
}
