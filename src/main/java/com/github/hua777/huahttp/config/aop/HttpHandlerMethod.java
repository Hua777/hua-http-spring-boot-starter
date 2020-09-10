package com.github.hua777.huahttp.config.aop;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public interface HttpHandlerMethod<T> {
    default void beforeHttpMethod(HttpRequest request) {
    }

    default void afterHttpMethod(HttpResponse response) {
    }
}