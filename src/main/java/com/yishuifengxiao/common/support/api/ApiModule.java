package com.yishuifengxiao.common.support.api;

import java.lang.annotation.*;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiModule {
    String value() default "";


    String description() default "";
}
