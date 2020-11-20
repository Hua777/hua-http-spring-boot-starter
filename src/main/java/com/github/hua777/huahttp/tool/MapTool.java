package com.github.hua777.huahttp.tool;

import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapTool {

    public static Map<Class<? extends RuntimeException>, Constructor<? extends RuntimeException>>
            RUNTIME_EXCEPTION_CONSTRUCTORS = new ConcurrentHashMap<>();

    public static Map<String, String> merge(Map<String, String> left, Map<String, String> right) {
        if (right != null) {
            for (Map.Entry<String, String> entry : right.entrySet()) {
                if (!StrUtil.isEmpty(entry.getKey()) && !StrUtil.isEmpty(entry.getValue())) {
                    left.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return left;
    }

    public static Map<String, Object> toMap(Object object) {
        Map<String, Object> result = new HashMap<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            for (Method method : object.getClass().getDeclaredMethods()) {
                String methodName = method.getName();
                if (("get" + fieldName.toLowerCase()).toLowerCase().equals(methodName.toLowerCase())) {
                    try {
                        Object fieldValue = method.invoke(object);
                        result.put(fieldName, fieldValue);
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        return result;
    }

}
