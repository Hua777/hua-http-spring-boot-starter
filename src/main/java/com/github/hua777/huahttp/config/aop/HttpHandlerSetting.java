package com.github.hua777.huahttp.config.aop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HttpHandlerSetting {

    Map<String, HttpHandlerMethod> methods = new HashMap<>();

    public Map<String, HttpHandlerMethod> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, HttpHandlerMethod> methods) {
        this.methods = methods;
    }

    public HttpHandlerMethod getMethod(String methodName) {
        return methods.getOrDefault(methodName, null);
    }

    public void addMethod(String methodName, HttpHandlerMethod method) {
        methods.put(methodName, method);
    }

    Set<String> moreScanPackages = new HashSet<>();

    public Set<String> getMoreScanPackages() {
        return moreScanPackages;
    }

    public void setMoreScanPackages(Set<String> moreScanPackages) {
        this.moreScanPackages = moreScanPackages;
    }

    public void addMoreScanPackage(String moreScanPackage) {
        this.moreScanPackages.add(moreScanPackage);
    }

}
