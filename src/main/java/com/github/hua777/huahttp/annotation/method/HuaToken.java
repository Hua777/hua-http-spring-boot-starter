package com.github.hua777.huahttp.annotation.method;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaToken {
    String name();

    String key();

    String iss();

    String sub();

    String issuedAtTimeThresholdMs() default "-10000";

    String validityPeriodMs() default "300000";
}
