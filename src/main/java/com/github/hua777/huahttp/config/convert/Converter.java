package com.github.hua777.huahttp.config.convert;

import com.github.hua777.huahttp.bean.JsonMan;

import java.lang.reflect.Type;

public interface Converter<T> {
    default T convert(String responseBody, Type returnType, JsonMan jsonMan) {
        if (returnType.getTypeName().equals("void")) return null;
        return jsonMan.fromJson(responseBody, returnType);
    }
}
