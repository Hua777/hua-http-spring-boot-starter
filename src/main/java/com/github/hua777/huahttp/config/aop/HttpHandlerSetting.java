package com.github.hua777.huahttp.config.aop;

import java.util.HashMap;
import java.util.Map;

public class HttpHandlerSetting {

    Map<String, HttpHandlerMethod> methods = new HashMap<>();

    public void defaultMethod(HttpHandlerMethod method) {
        methods.put("default", method);
    }

    public void addMethod(String methodName, HttpHandlerMethod method) {
        methods.put(methodName, method);
    }

    public HttpHandlerMethod getMethod(String methodName) {
        return methods.getOrDefault(methodName, null);
    }

    public void setMethods(Map<String, HttpHandlerMethod> methods) {
        this.methods = methods;
    }

    public Map<String, HttpHandlerMethod> getMethods() {
        return methods;
    }

}
