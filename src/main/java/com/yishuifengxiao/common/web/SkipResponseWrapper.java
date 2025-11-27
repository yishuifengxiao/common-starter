package com.yishuifengxiao.common.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跳过响应包装
 * <p>将 @SkipResponseWrapper 添加到 Controller 类上时，整个类的所有请求方法都会跳过全局响应数据格式统一。</p>
 * <p>将 @SkipResponseWrapper 添加到具体的方法上时，只有该方法会跳过全局响应数据格式统一。</p>
 *
 * @author yishui
 * @version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipResponseWrapper {
}