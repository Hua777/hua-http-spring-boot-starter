package com.github.hua777.huahttp.test.http.config;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.github.hua777.huahttp.config.handler.HttpHandler;
import com.github.hua777.huahttp.test.global.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class HeaderHandler implements HttpHandler {

    public static ThreadLocal<String> URL = new ThreadLocal<>();

    public static ThreadLocal<String> RESPONSE_STRING = new ThreadLocal<>();

    @Override
    public void beforeHttpMethod(HttpRequest request) {
        URL.set(request.getUrl());
    }

    @Override
    public void afterHttpMethod(HttpResponse response) {
        RESPONSE_STRING.set(response.body());
    }

    @Override
    public String preHandleResponse(String originString) {
        Map<String, String> result = JSONObject.parseObject(originString, new TypeReference<Map<String, String>>() {
        });
        result.put(Constant.OTHER, Constant.TEST);
        return JSONObject.toJSONString(result);
    }
}
