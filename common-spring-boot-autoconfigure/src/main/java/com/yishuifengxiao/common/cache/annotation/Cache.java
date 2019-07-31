/**
 * 
 */
package com.yishuifengxiao.common.cache.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * 自定义注解
 * 
 * @author yishui
 * @date 2019年7月31日
 * @version 1.0.0
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Cache {

	/**
	 * 缓存key的名字
	 * 
	 * @return
	 */
	@AliasFor("name")
	String value() default "";

	/**
	 * 缓存key的名字
	 * 
	 * @return
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * 缓存过期时间，单位为秒，默认为30分钟
	 * 
	 * @return
	 */
	long expire() default 60 * 30;

	/**
	 * 执行清除方法时前缀包含包含的值
	 * 
	 * @return
	 */
	String[] deleteKey() default {};

	/**
	 * 执行保存方法时前缀包含包含的值
	 * 
	 * @return
	 */
	String[] saveKey() default {};

}
