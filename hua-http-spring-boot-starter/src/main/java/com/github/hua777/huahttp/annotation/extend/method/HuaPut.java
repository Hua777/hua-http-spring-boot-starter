package com.github.hua777.huahttp.annotation.extend.method;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.github.hua777.huahttp.annotation.HuaMethod;
import com.github.hua777.huahttp.config.limiter.DefaultStreamLimiter;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.function.Function;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@HuaMethod(method = Method.PUT)
public @interface HuaPut {

    @AliasFor(annotation = HuaMethod.class)
    String url();

    @AliasFor(annotation = HuaMethod.class)
    boolean form() default false;

    @AliasFor(annotation = HuaMethod.class)
    Class<? extends Function<HttpResponse, Long>> streamLimit() default DefaultStreamLimiter.class;

}
