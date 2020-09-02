package com.github.hua777.huahttp.annotation.method;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaPut {
    String url();
}
