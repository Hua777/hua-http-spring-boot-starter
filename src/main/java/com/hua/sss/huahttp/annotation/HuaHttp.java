package com.hua.sss.huahttp.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface HuaHttp {
    String value() default "";
}
