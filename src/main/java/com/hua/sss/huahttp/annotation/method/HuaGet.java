package com.hua.sss.huahttp.annotation.method;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface HuaGet {
    String value();
}
