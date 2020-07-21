package com.github.hua777.huahttp.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaHttp {
    String value() default "";
}
