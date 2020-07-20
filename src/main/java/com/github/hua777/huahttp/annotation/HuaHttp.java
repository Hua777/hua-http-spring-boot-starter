package com.github.hua777.huahttp.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface HuaHttp {
    String value() default "";
}
