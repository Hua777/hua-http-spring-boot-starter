package com.github.hua777.huahttp.annotation;

import com.github.hua777.huahttp.config.handler.HttpHandler;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaAop {
    Class<? extends HttpHandler> value();
}
