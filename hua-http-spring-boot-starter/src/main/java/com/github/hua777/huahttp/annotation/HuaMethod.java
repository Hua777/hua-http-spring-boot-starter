package com.github.hua777.huahttp.annotation;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.github.hua777.huahttp.config.limiter.DefaultStreamLimiter;

import java.lang.annotation.*;
import java.util.function.Function;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaMethod {
    Method method();

    String url() default "";

    boolean form() default false;

    Class<? extends Function<HttpResponse, Long>> streamLimit() default DefaultStreamLimiter.class;
}
