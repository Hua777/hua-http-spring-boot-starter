package com.github.hua777.huahttp.tool;

import cn.hutool.core.collection.ListUtil;

import java.util.ArrayList;

public class ReflectTool {
    public static boolean fromClass(Class<?> clazz, Class<?> from) {
        if (clazz.getTypeName().equals(from.getTypeName())) {
            return true;
        }
        ArrayList<Class<?>> interfaces = ListUtil.toList(clazz.getInterfaces());
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            interfaces.add(superClass);
        }
        for (Class<?> type : interfaces) {
            if (type.getTypeName().equals(from.getTypeName())) {
                return true;
            } else {
                if (fromClass(type, from)) {
                    return true;
                }
            }
        }
        return false;
    }
}
