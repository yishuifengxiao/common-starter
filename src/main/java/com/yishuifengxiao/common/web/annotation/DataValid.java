/**
 * 
 */
package com.yishuifengxiao.common.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 数据校验注解
 * <p>
 * 主要用于标记捕获全局参数校验得到的异常信息
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataValid {

}
