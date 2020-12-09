package com.github.hua777.huahttp.config.aop;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public interface HttpHandlerMethod {
    default void beforeHttpMethod(HttpRequest request) {
    }

    default void afterHttpMethod(HttpResponse response) {
    }

    default String preHandleResponse(String originString) {
        return originString;
    }
}
