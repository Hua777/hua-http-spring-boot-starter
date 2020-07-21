package com.github.hua777.huahttp.annotation.method;

import com.github.hua777.huahttp.annotation.enumrate.HttpMethod;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@HuaMethod(method = HttpMethod.Post)
public @interface HuaPost {
    String url();
}
