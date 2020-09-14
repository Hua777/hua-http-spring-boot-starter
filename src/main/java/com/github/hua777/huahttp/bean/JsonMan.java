package com.github.hua777.huahttp.bean;

import com.alibaba.fastjson.JSONObject;
import com.github.hua777.huahttp.enumerate.JsonType;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;

public class JsonMan {

    private JsonMan() {

    }

    Gson gson = new Gson();

    public <T> HashMap<String, Object> toMap(T object) {
        HashMap<String, Object> map = new HashMap<>();
        return fromJson(toJson(object), map.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> T fromJson(String object, Type type) {
        String[] classNames = type.getTypeName().split("\\.");
        String typeName = classNames[classNames.length - 1];
        if (typeName.equals("Void") || typeName.equals("void")) {
            return null;
        }
        if (typeName.equals("String")) {
            return (T) object;
        }
        if (typeName.equals("Byte") || typeName.equals("byte")) {
            return (T) ((Byte) Byte.parseByte(object));
        }
        if (typeName.equals("Short") || typeName.equals("short")) {
            return (T) ((Short) Short.parseShort(object));
        }
        if (typeName.equals("Integer") || typeName.equals("int")) {
            return (T) ((Integer) Integer.parseInt(object));
        }
        if (typeName.equals("Long") || typeName.equals("long")) {
            return (T) ((Long) Long.parseLong(object));
        }
        if (typeName.equals("Float") || typeName.equals("float")) {
            return (T) ((Float) Float.parseFloat(object));
        }
        if (typeName.equals("Double") || typeName.equals("double")) {
            return (T) ((Double) Double.parseDouble(object));
        }
        if (typeName.equals("Boolean") || typeName.equals("boolean")) {
            return (T) ((Boolean) Boolean.parseBoolean(object));
        }
        if (typeName.equals("Character") || typeName.equals("char")) {
            return (T) (Character) object.charAt(0);
        }
        if (typeName.equals("BigDecimal")) {
            return (T) (new BigDecimal(object));
        }
        switch (jsonType) {
            case FastJson:
                return JSONObject.parseObject(object, type);
            case Gson:
                return gson.fromJson(object, type);
        }
        return null;
    }

    public <T> String toJson(T object) {
        switch (jsonType) {
            case FastJson:
                return JSONObject.toJSONString(object);
            case Gson:
                return gson.toJson(object);
        }
        return null;
    }

    JsonType jsonType;

    public static JsonMan of(JsonType jsonType) {
        JsonMan jsonMan = new JsonMan();
        jsonMan.jsonType = jsonType;
        return jsonMan;
    }

    public void setGson(Gson gson) {
        if (gson != null) {
            this.gson = gson;
        }
    }

}
