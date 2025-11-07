package com.yishuifengxiao.common;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启所有扩展功能的注解
 * 当设置value为true时，相当于配置了以下所有功能：
 * <pre>
 *     yishuifengxiao:
 *       web:
 *         enable: true
 *         response:
 *           enable: true
 *         cors:
 *           enable: true
 *         aop:
 *           enable: true
 *         traced:
 *           enable: true
 *         error:
 *           enable: true
 *       security:
 *         enable: true
 *         oauth2server:
 *           enable: true
 *       code:
 *         enable: true
 *       redis:
 *         enable: true
 *       swagger:
 *         enable: true
 * </pre>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AllExtensionsAutoConfiguration.class)
public @interface EnableAllExtensions {

    /**
     * 是否开启所有扩展功能
     * 默认为true，即开启所有功能
     *
     * @return 是否开启所有扩展功能
     */
    boolean value() default true;
}