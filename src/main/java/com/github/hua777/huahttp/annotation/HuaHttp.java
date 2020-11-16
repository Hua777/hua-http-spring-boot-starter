package com.github.hua777.huahttp.annotation;

import com.github.hua777.huahttp.enumerate.JsonType;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaHttp {
    String value() default "";

    JsonType jsonType() default JsonType.FastJson;

    boolean throwException() default false;
}
