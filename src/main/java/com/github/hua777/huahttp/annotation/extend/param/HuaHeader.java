package com.github.hua777.huahttp.annotation.extend.param;

import com.github.hua777.huahttp.annotation.HuaParam;
import com.github.hua777.huahttp.config.converter.DefaultParamConverter;
import com.github.hua777.huahttp.config.creator.DefaultParamCreator;
import com.github.hua777.huahttp.enumrate.ParamType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@HuaParam(type = ParamType.HEADER)
public @interface HuaHeader {

    @AliasFor(annotation = HuaParam.class)
    String name() default "";

    @AliasFor(annotation = HuaParam.class)
    Class<? extends Function> convert() default DefaultParamConverter.class;

    @AliasFor(annotation = HuaParam.class)
    boolean full() default false;

    @AliasFor(annotation = HuaParam.class)
    Class<? extends Supplier<Map<String, Object>>> create() default DefaultParamCreator.class;

}
