package com.github.hua777.huahttp.config.aop;

import com.github.hua777.huahttp.config.HttpRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface HttpHandlerConfig {

    HttpHandlerSetting getSetting();

    static HttpHandlerConfig merge() {
        Map<String, HttpHandlerMethod> methods = new HashMap<>();
        Set<String> moreScanPackages = new HashSet<>();
        for (Map.Entry<String, HttpHandlerConfig> targetEntry :
                HttpRegistry.APP_CONTEXT.getBeansOfType(HttpHandlerConfig.class).entrySet()) {
            for (Map.Entry<String, HttpHandlerMethod> entry : targetEntry.getValue().getSetting().getMethods().entrySet()) {
                methods.put(entry.getKey(), entry.getValue());
            }
            moreScanPackages.addAll(targetEntry.getValue().getSetting().getMoreScanPackages());
        }
        return () -> {
            HttpHandlerSetting setting = new HttpHandlerSetting();
            setting.setMethods(methods);
            setting.setMoreScanPackages(moreScanPackages);
            return setting;
        };
    }

}
