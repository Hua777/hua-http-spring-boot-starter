package com.github.hua777.huahttp.annotation.param;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaPath {
    String name() default "";

    String[] names() default "";

    String[] values() default "";

    String method() default "";
}