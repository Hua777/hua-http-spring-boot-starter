package com.github.hua777.huahttp.config.stream;

import cn.hutool.http.HttpResponse;

public interface StreamLimit {
    default long getDataCount(HttpResponse response) {
        return Long.parseLong(response.header("USER-DEFINED-DATA-COUNT"));
    }
}
