package com.github.hua777.huahttp.annotation.method;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface HuaPost {
    String value();
}
