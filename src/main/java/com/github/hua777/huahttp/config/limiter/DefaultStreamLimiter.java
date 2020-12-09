package com.github.hua777.huahttp.config.limiter;

import cn.hutool.http.HttpResponse;

import java.util.function.Function;

public class DefaultStreamLimiter implements Function<HttpResponse, Long> {

    @Override
    public Long apply(HttpResponse response) {
        return Long.parseLong(response.header("USER-DEFINED-DATA-COUNT"));
    }
}
