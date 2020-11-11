package com.github.hua777.huahttp.annotation.method;

import com.github.hua777.huahttp.config.stream.DefaultStreamLimit;
import com.github.hua777.huahttp.config.stream.StreamLimit;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaStream {
    Class<? extends StreamLimit> limit() default DefaultStreamLimit.class;
}
