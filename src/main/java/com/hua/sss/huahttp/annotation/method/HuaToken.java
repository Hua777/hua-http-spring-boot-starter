package com.hua.sss.huahttp.annotation.method;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface HuaToken {
    String name();

    String key();

    String iss();

    String sub();

    String issuedAtTimeThresholdMs() default "-10000";

    String validityPeriodMs() default "300000";
}
