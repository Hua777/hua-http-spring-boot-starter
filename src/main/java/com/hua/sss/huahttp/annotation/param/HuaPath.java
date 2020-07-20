package com.hua.sss.huahttp.annotation.param;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface HuaPath {
    String name() default "";

    String method() default "";
}
