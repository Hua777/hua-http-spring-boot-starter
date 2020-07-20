package com.hua.sss.huahttp.annotation.param;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface HuaPath {
    String name() default "";

    String[] names() default "";

    String[] values() default "";

    String method() default "";
}
