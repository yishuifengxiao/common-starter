package com.yishuifengxiao.common;

import com.yishuifengxiao.common.support.PropertyHelper;
import com.yishuifengxiao.common.support.SpringContext;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有扩展功能的自动配置类
 * 当使用@EnableAllExtensions注解时，自动开启所有扩展功能
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnBean(annotation = EnableAllExtensions.class)
@EnableConfigurationProperties
public class AllExtensionsAutoConfiguration {

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringContext springContext;

    /**
     * 配置检查并设置所有扩展功能
     */
    @PostConstruct
    public void enableAllExtensions() {
        try {
            // 查找所有使用@EnableAllExtensions注解的类
            Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(EnableAllExtensions.class);

            if (beansWithAnnotation.isEmpty()) {
                log.debug("未找到@EnableAllExtensions注解，跳过所有扩展功能的自动配置");
                return;
            }

            // 获取第一个注解实例
            EnableAllExtensions annotation = null;
            for (Object bean : beansWithAnnotation.values()) {
                annotation = AnnotationUtils.findAnnotation(bean.getClass(), EnableAllExtensions.class);
                if (annotation != null) {
                    break;
                }
            }

            if (annotation == null) {
                log.debug("未找到有效的@EnableAllExtensions注解配置");
                return;
            }

            // 检查注解值，如果为false则不开启
            if (!annotation.value()) {
                log.debug("@EnableAllExtensions注解值为false，跳过所有扩展功能的自动配置");
                return;
            }

            // 设置所有扩展功能的配置属性
            setAllExtensionProperties(annotation);

            log.info("【yishuifengxiao-common-spring-boot-starter】: 通过@EnableAllExtensions注解开启所有扩展功能");

        } catch (Exception e) {
            log.error("开启所有扩展功能时发生异常", e);
        }
    }

    /**
     * 设置所有扩展功能的配置属性
     */
    private void setAllExtensionProperties(EnableAllExtensions annotation) {
        Map<String, String> properties = new HashMap<>();

        // 获取主开关值
        boolean mainSwitch = annotation.value();

        // 设置web相关功能（使用分项开关优先级逻辑）
        setPropertyWithOverride(properties, "yishuifengxiao.web.enable", mainSwitch, annotation.web());
        setPropertyWithOverride(properties, "yishuifengxiao.web.response.enable", mainSwitch, annotation.webResponse());
        setPropertyWithOverride(properties, "yishuifengxiao.web.cors.enable", mainSwitch, annotation.webCors());
        setPropertyWithOverride(properties, "yishuifengxiao.web.aop.enable", mainSwitch, annotation.webAop());
        setPropertyWithOverride(properties, "yishuifengxiao.web.traced.enable", mainSwitch, annotation.webTraced());
        setPropertyWithOverride(properties, "yishuifengxiao.web.error.enable", mainSwitch, annotation.webError());

        // 设置security功能
        setPropertyWithOverride(properties, "yishuifengxiao.security.enable", mainSwitch, annotation.security());
        setPropertyWithOverride(properties, "yishuifengxiao.security.oauth2server.enable", mainSwitch, annotation.securityOauth2Server());

        // 设置code功能
        setPropertyWithOverride(properties, "yishuifengxiao.code.enable", mainSwitch, annotation.code());

        // 设置redis功能
        setPropertyWithOverride(properties, "yishuifengxiao.redis.enable", mainSwitch, annotation.redis());

        // 设置swagger功能
        setPropertyWithOverride(properties, "yishuifengxiao.swagger.enable", mainSwitch, annotation.swagger());

        // 批量设置属性
        PropertyHelper.setPropertiesIfAbsent(environment, properties);

        log.debug("所有扩展功能的配置属性已设置完成，主开关：{}", mainSwitch);
    }

    /**
     * 设置属性值，支持分项开关优先级逻辑
     * 优先级：如果分项开关被设置（非默认值），则优先使用分项开关设置值
     * 如果分项开关未设置（使用默认值），则使用主开关值
     * 主开关为false时，所有功能都会被关闭
     *
     * @param properties    属性映射
     * @param propertyKey   属性键
     * @param mainSwitch    主开关值
     * @param featureSwitch 分项开关值
     */
    private void setPropertyWithOverride(Map<String, String> properties, String propertyKey,
                                         boolean mainSwitch, boolean featureSwitch) {
        // 如果主开关为false，强制关闭所有功能
        if (!mainSwitch) {
            properties.put(propertyKey, "false");
            return;
        }

        // 使用分项开关值（如果分项开关被设置，则优先使用）
        properties.put(propertyKey, String.valueOf(featureSwitch));
    }
}