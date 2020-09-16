package com.github.hua777.huahttp.tool;

import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MapTool {

    public static HashMap<String, String> merge(HashMap<String, String> left, HashMap<String, String> right) {
        if (right != null) {
            for (Map.Entry<String, String> entry : right.entrySet()) {
                if (!StrUtil.isEmpty(entry.getKey()) && !StrUtil.isEmpty(entry.getValue())) {
                    left.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return left;
    }

    public static HashMap<String, Object> toMap(Object object) {
        HashMap<String, Object> result = new HashMap<>();
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
