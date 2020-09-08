package com.github.hua777.huahttp.bean;

import cn.hutool.http.HttpResponse;
import com.github.hua777.huahttp.annotation.enumrate.HttpMethod;

import java.lang.reflect.Method;
import java.util.Map;

public interface HttpHandlerMethod<T> {
    default Object[] start(Method method, Object[] args) {
        return args;
    }

    void beforeHttpMethod(String fullUrl, HttpMethod httpMethod, Map<String, Object> bodies, Map<String, String> headers);

    void afterHttpMethod(HttpResponse result);

    default T end(Method method, T result) {
        return result;
    }
}
