package com.github.hua777.huahttp.annotation.param;

import com.github.hua777.huahttp.config.creator.DefaultHeadersCreator;
import com.github.hua777.huahttp.config.creator.HeadersCreator;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaHeader {

    /*
     * 以下适用于参数
     */

    String name() default "";

    String[] names() default "";

    /*
     * 以下适用于函数、类
     */

    String[] values() default "";

    String method() default "";

    Class<? extends HeadersCreator> creator() default DefaultHeadersCreator.class;
}
