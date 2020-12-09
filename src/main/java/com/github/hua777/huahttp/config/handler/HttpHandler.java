package com.github.hua777.huahttp.config.handler;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public interface HttpHandler {

    default void beforeHttpMethod(HttpRequest request) {
    }

    default void afterHttpMethod(HttpResponse response) {
    }

    default String preHandleResponse(String originString) {
        return originString;
    }

}
