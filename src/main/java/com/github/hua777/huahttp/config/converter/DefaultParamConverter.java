package com.github.hua777.huahttp.config.converter;

import java.util.function.Function;

public class DefaultParamConverter implements Function<Object, Object> {
    @Override
    public Object apply(Object o) {
        return o;
    }
}
