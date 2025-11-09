package com.yishuifengxiao.common.support.api;

import java.lang.annotation.*;

/**
 * @author shi
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiMethod {
    String value() default "";

    String permission() default "";

    String description() default "";
}
