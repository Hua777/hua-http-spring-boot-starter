package com.github.hua777.huahttp.test.http.config;

import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class QueryConverter implements Function<String, Integer> {
    @Override
    public Integer apply(String o) {
        return Integer.parseInt(o);
    }
}
