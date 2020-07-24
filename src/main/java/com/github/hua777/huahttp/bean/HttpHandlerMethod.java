package com.github.hua777.huahttp.bean;

import cn.hutool.http.HttpResponse;
import com.github.hua777.huahttp.annotation.enumrate.HttpMethod;

import java.lang.reflect.Method;
import java.util.Map;

public interface HttpHandlerMethod<T> {
    Object[] start(Method method, Object[] args);

    void beforeHttpMethod(String fullUrl, HttpMethod httpMethod, Map<String, Object> bodies, Map<String, String> headers);

    void afterHttpMethod(HttpResponse result);

    T end(Method method, T result);
}
