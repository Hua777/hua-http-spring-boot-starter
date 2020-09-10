package com.github.hua777.huahttp.annotation.method;

import com.github.hua777.huahttp.config.convert.Converter;
import com.github.hua777.huahttp.config.convert.DefaultConverter;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HuaConvert {
    Class<? extends Converter> value() default DefaultConverter.class;
}
