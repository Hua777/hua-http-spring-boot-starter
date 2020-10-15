package com.github.hua777.huahttp.tool;

import cn.hutool.core.collection.ListUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReflectTool {

    public static List<Class<?>> getClassExtendsClasses(Class<?> clazz) {
        ArrayList<Class<?>> interfaces = ListUtil.toList(clazz.getInterfaces());
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            interfaces.add(superClass);
        }
        return interfaces;
    }

    public static boolean fromClass(Class<?> clazz, Class<?> from) {
        if (clazz.getTypeName().equals(from.getTypeName())) {
            return true;
        }
        for (Class<?> type : getClassExtendsClasses(clazz)) {
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

    public static boolean isClass(Class<?> clazz, Class<?> target) {
        return clazz.getTypeName().equals(target.getTypeName());
    }

    public static Type[] getActualTypes(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments();
    }

    public static Class<?> toClass(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return (Class<?>) type;
        }
    }
}
