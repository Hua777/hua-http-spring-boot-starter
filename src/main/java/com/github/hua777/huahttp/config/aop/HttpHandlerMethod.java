package com.github.hua777.huahttp.config.aop;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.github.hua777.huahttp.bean.JsonMan;

public interface HttpHandlerMethod {
    default void beforeHttpMethod(HttpRequest request) {
    }

    default void afterHttpMethod(HttpResponse response) {
    }

    default String preHandleResponse(String originString, JsonMan jsonMan) {
        return originString;
    }
}
