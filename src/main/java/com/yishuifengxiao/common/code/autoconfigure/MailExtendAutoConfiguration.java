package com.yishuifengxiao.common.code.autoconfigure;

import javax.activation.MimeType;
import javax.mail.internet.MimeMessage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.sender.CodeSender;
import com.yishuifengxiao.common.code.sender.impl.EmailCodeSender;

/**
 * 邮箱验证码发送器自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass({ MimeMessage.class, MimeType.class, JavaMailSender.class })
@ConditionalOnProperty(prefix = "spring.mail", name = { "host", "username", "password" })
public class MailExtendAutoConfiguration {

	/**
	 * 注入一个名为emailCodeSender的邮箱验证码发送器
	 * 
	 * @param env            环境配置
	 * @param javaMailSender java邮件发送器
	 * @param codeProperties 验证码属性配置
	 * @return 名为emailCodeSender的邮箱验证码发送器
	 */
	@Bean("emailCodeSender")
	@ConditionalOnMissingBean(name = "emailCodeSender")
	public CodeSender emailCodeSender(Environment env, JavaMailSender javaMailSender, CodeProperties codeProperties) {
		EmailCodeSender emailCodeSender = new EmailCodeSender();
		emailCodeSender.setCodeProperties(codeProperties);
		emailCodeSender.setEmailSender(env.getProperty("spring.mail.username", "zhiyubujian@163.com"));
		emailCodeSender.setJavaMailSender(javaMailSender);
		return emailCodeSender;
	}
}
