package com.github.hua777.huahttp.config.aop;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class HttpHandlerSetting {

    Map<String, HttpHandlerMethod> methods = new HashMap<>();

    Gson gson = new Gson();

    public void defaultMethod(HttpHandlerMethod method) {
        methods.put("default", method);
    }

    public void addMethod(String methodName, HttpHandlerMethod method) {
        methods.put(methodName, method);
    }

    public HttpHandlerMethod getMethod(String methodName) {
        return methods.getOrDefault(methodName, null);
    }

    public Map<String, HttpHandlerMethod> getMethods() {
        return methods;
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void addOtherSetting(HttpHandlerSetting setting) {
        if (setting.getGson() != null) {
            gson = setting.getGson();
        }
        for (Map.Entry<String, HttpHandlerMethod> entry : setting.getMethods().entrySet()) {
            methods.put(entry.getKey(), entry.getValue());
        }
    }

}
