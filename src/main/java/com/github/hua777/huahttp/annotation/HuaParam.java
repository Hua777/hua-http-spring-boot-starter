package com.github.hua777.huahttp.annotation;

import com.github.hua777.huahttp.config.converter.DefaultParamConverter;
import com.github.hua777.huahttp.config.creator.DefaultParamCreator;
import com.github.hua777.huahttp.enumrate.ParamType;

import java.lang.annotation.*;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(HuaParams.class)
public @interface HuaParam {
    ParamType type();

    //region 参数时使用

    String name() default "";

    Class<? extends Function> convert() default DefaultParamConverter.class;

    boolean full() default false;

    //endregion

    //region 函数时使用

    Class<? extends Supplier<Map<String, Object>>> create() default DefaultParamCreator.class;

    //endregion
}
