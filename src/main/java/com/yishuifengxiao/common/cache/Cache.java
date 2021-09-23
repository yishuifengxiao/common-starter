package com.yishuifengxiao.common.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 * 缓存注解
 * </p>
 * 
 * 其实就是@Cacheable的快捷方式,它等价于以下代码
 * 
 * <pre>
 *  
 *   &#64;Cacheable(unless = "#result==null", keyGenerator = "simpleKeyGenerator")
 * 
 * </pre>
 * 
 * 对于其他复杂情况可以使用原生的 @Cacheable 注解，@Cacheable的使用方法参见
 * {@link org.springframework.cache.annotation.Cacheable }
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Cacheable(unless = "#result==null", keyGenerator = "simpleKeyGenerator")
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cache {

}
