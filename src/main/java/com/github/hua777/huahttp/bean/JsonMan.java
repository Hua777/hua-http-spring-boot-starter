package com.github.hua777.huahttp.bean;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

public class JsonMan {

    private JsonMan() {

    }

    @SuppressWarnings("unchecked")
    public static <T> T fromJsonCast(String object, Type type) {
        return (T) fromJson(object, type);
    }

    public static Object fromJson(String object, Type type) {
        if (object == null) {
            return null;
        }
        String[] classNames = type.getTypeName().split("\\.");
        String typeName = classNames[classNames.length - 1];
        switch (typeName) {
            case "Void":
            case "void":
                return null;
            case "String":
                return object;
            case "Byte":
            case "byte":
                return Byte.parseByte(object);
            case "Short":
            case "short":
                return Short.parseShort(object);
            case "Integer":
            case "int":
                return Integer.parseInt(object);
            case "Long":
            case "long":
                return Long.parseLong(object);
            case "Float":
            case "float":
                return Float.parseFloat(object);
            case "Double":
            case "double":
                return Double.parseDouble(object);
            case "Boolean":
            case "boolean":
                return Boolean.parseBoolean(object);
            case "Character":
            case "char":
                return object.charAt(0);
            case "BigDecimal":
                return new BigDecimal(object);
            case "LocalDateTime":
                return DateUtil.parseLocalDateTime(object, "yyyy-MM-dd HH:mm:ss");
            case "LocalDate":
                return DateUtil.parseLocalDateTime(object, "yyyy-MM-dd HH:mm:ss").toLocalDate();
            case "Date":
                return Date.from(DateUtil.parseLocalDateTime(object, "yyyy-MM-dd HH:mm:ss")
                        .atZone(ZoneId.systemDefault()).toInstant());
        }
        return JSONObject.parseObject(object, type);
    }

    public static <T> String toJson(T object) {
        return JSONObject.toJSONString(object);
    }

    public static Object prepareArgs(Object object) {
        if (object instanceof LocalDateTime) {
            return DateUtil.format((LocalDateTime) object, "yyyy-MM-dd HH:mm:ss");
        } else if (object instanceof LocalDate) {
            return ((LocalDate) object).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if (object instanceof Date) {
            return DateUtil.format((Date) object, "yyyy-MM-dd HH:mm:ss");
        }
        return object;
    }

    public static Map<String, String> toMapStringString(Object object) {
        return fromJsonCast(toJson(object), new TypeReference<Map<String, String>>() {
        }.getType());
    }

    public static Map<String, Object> toMapStringObject(Object object) {
        return fromJsonCast(toJson(object), new TypeReference<Map<String, Object>>() {
        }.getType());
    }

}
