package com.github.hua777.huahttp.annotation.method;

import com.github.hua777.huahttp.annotation.enumrate.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HuaMethod {

    HttpMethod method();

}
