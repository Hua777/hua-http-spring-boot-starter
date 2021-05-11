package com.github.hua777.huahttp.config.handler;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.github.hua777.huahttp.enumrate.ParamType;

import java.util.Map;

public interface HttpHandler {

    default void beforeHttpMethod(HttpRequest request, Map<ParamType, Map<String, Object>> params) {
    }

    default void afterHttpMethod(HttpResponse response) {
    }

    default String preHandleResponse(String originString) {
        return originString;
    }

}
