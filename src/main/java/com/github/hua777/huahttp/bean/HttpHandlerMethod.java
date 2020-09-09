package com.github.hua777.huahttp.bean;

import cn.hutool.http.HttpResponse;

import java.lang.reflect.Method;
import java.util.Map;

public interface HttpHandlerMethod<T> {
    default Object[] start(Method method, Object[] args) {
        return args;
    }

    void beforeHttpMethod(String fullUrl, cn.hutool.http.Method httpMethod, Map<String, Object> bodies, Map<String, String> headers);

    void afterHttpMethod(HttpResponse result);

    default T end(Method method, T result) {
        return result;
    }
}
