package com.yishuifengxiao.common.code;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.yishuifengxiao.common.code.autoconfigure.MailExtendAutoConfiguration;
import com.yishuifengxiao.common.code.autoconfigure.RedisExtendAutoConfiguration;
import com.yishuifengxiao.common.code.generator.CodeGenerator;
import com.yishuifengxiao.common.code.generator.impl.EmailCodeGenerator;
import com.yishuifengxiao.common.code.generator.impl.ImageCodeGenerator;
import com.yishuifengxiao.common.code.generator.impl.SmsCodeGenerator;
import com.yishuifengxiao.common.code.repository.CodeRepository;
import com.yishuifengxiao.common.code.repository.impl.SimpleCodeRepository;
import com.yishuifengxiao.common.code.sender.CodeSender;
import com.yishuifengxiao.common.code.sender.impl.ImageCodeSender;
import com.yishuifengxiao.common.code.sender.impl.SmsCodeSender;

import lombok.extern.slf4j.Slf4j;

/**
 * 验证码组件自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({CodeProperties.class})
@Import({MailExtendAutoConfiguration.class, RedisExtendAutoConfiguration.class})
@AutoConfigureAfter(value = {RedisExtendAutoConfiguration.class})
@ConditionalOnProperty(prefix = "yishuifengxiao.code", name = {"enable"}, havingValue = "true", matchIfMissing = false)
public class CodeAutoConfiguration {

    /**
     * 注入一个名为codeRepository的验证码存储器
     *
     * @return 名为codeRepository的验证码存储器
     */
    @ConditionalOnMissingBean(name = {"redisTemplate"}, value = {CodeRepository.class})
    @Bean
    public CodeRepository codeRepository() {
        return new SimpleCodeRepository();
    }

    /**
     * 注入一个缺省的图形验证码生成器
     *
     * @return 图形验证码生成器
     */
    @ConditionalOnMissingBean(name = "imageCodeGenerator")
    @Bean("imageCodeGenerator")
    public CodeGenerator imageCodeGenerator() {
        return new ImageCodeGenerator();
    }

    /**
     * 注入一个缺省的图像验证码发送器
     *
     * @return 图像验证码发送器
     */
    @ConditionalOnMissingBean(name = "imageCodeSender")
    @Bean("imageCodeSender")
    public CodeSender imageCodeSender() {
        return new ImageCodeSender();
    }

    /**
     * 注入体格缺省的短信验证码发送器
     *
     * @return 短信验证码发送器
     */
    @ConditionalOnMissingBean(name = "smsCodeSender")
    @Bean("smsCodeSender")
    public CodeSender smsCodeSender() {
        return new SmsCodeSender();
    }

    /**
     * 注入一个缺省的短信验证码生成器
     *
     * @return 短信验证码生成器
     */
    @ConditionalOnMissingBean(name = "smsCodeGenerator")
    @Bean("smsCodeGenerator")
    public CodeGenerator smsCodeGenerator() {
        return new SmsCodeGenerator();
    }

    /**
     * 注入一个缺省的邮件验证码生成器
     *
     * @return 邮件验证码生成器
     */
    @ConditionalOnMissingBean(name = "emailCodeGenerator")
    @Bean("emailCodeGenerator")
    @ConditionalOnBean(name = "emailCodeSender")
    public CodeGenerator emailCodeGenerator() {
        return new EmailCodeGenerator();
    }

    /**
     * 注入一个验证码处理器
     *
     * @param codeProperties 验证码属性配置
     * @param codeGenerators 系统中所有的 {@link CodeGenerator} 验证码生成器接口的实现。key为bean的名字
     * @param codeSenders    系统中所有的 {@link CodeSender } 验证码发送器接口的实现，。key为bean的名字
     * @param repository     验证码存储器
     * @return 验证码处理器
     */
    @Bean
    @ConditionalOnMissingBean({CodeProcessor.class})
    public CodeProcessor codeProcessor(CodeProperties codeProperties, Map<String, CodeGenerator> codeGenerators,
                                       Map<String, CodeSender> codeSenders, CodeRepository repository) {
        SimpleCodeProcessor simpleCodeProcessor = new SimpleCodeProcessor(codeGenerators, codeSenders, repository,
                codeProperties);
        return simpleCodeProcessor;
    }

    /**
     * 配置检查
     */
    @PostConstruct
    public void checkConfig() {

        log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <验证码支持> 相关的配置");
    }

}
