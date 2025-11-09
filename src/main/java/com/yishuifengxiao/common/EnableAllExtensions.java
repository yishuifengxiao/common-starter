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
 *           enable: true
 *       swagger:
 *           enable: true
 * </pre>
 *
 * 分项开关说明：
 * - 如果分项开关被设置（非默认值），则优先使用分项开关设置值
 * - 如果分项开关未设置（使用默认值），则使用value属性的值
 * - value属性为false时，所有功能都会被关闭，忽略分项开关设置
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
     * 如果设置为false，则所有功能都会被关闭，忽略分项开关设置
     *
     * @return 是否开启所有扩展功能
     */
    boolean value() default true;

    /**
     * Web功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return Web功能开关
     */
    boolean web() default true;

    /**
     * Web响应功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return Web响应功能开关
     */
    boolean webResponse() default true;

    /**
     * CORS功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return CORS功能开关
     */
    boolean webCors() default true;

    /**
     * AOP功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return AOP功能开关
     */
    boolean webAop() default true;

    /**
     * 追踪功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return 追踪功能开关
     */
    boolean webTraced() default true;

    /**
     * 错误处理功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return 错误处理功能开关
     */
    boolean webError() default true;

    /**
     * 安全功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return 安全功能开关
     */
    boolean security() default true;

    /**
     * OAuth2服务器功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return OAuth2服务器功能开关
     */
    boolean securityOauth2Server() default true;

    /**
     * 代码功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return 代码功能开关
     */
    boolean code() default true;

    /**
     * Redis功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return Redis功能开关
     */
    boolean redis() default true;

    /**
     * Swagger功能开关
     * 默认值-1表示未设置，使用value属性值
     * 设置为true或false时，优先使用此设置
     *
     * @return Swagger功能开关
     */
    boolean swagger() default true;
}
