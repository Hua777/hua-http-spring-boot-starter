package com.github.hua777.huahttp.config.aop;

import java.util.HashMap;

public class HttpHandlerSetting {

    HashMap<String, HttpHandlerMethod> methods = new HashMap<>();

    public void defaultMethod(HttpHandlerMethod method) {
        methods.put("default", method);
    }

    public void addMethod(String methodName, HttpHandlerMethod method) {
        methods.put(methodName, method);
    }

    public HttpHandlerMethod getMethod(String methodName) {
        return methods.getOrDefault(methodName, null);
    }

}
