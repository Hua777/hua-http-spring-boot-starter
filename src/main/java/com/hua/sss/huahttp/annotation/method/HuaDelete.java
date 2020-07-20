package com.hua.sss.huahttp.annotation.method;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface HuaDelete {
    String value();
}
