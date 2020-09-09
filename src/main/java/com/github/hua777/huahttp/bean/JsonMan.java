package com.github.hua777.huahttp.bean;

import com.alibaba.fastjson.JSONObject;
import com.github.hua777.huahttp.enumerate.JsonType;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;

public class JsonMan {

    private JsonMan() {

    }

    Gson gson = new Gson();

    public <T> HashMap<String, Object> toMap(T object) {
        HashMap<String, Object> map = new HashMap<>();
        return fromJson(toJson(object), map.getClass());
    }

    public <T> T fromJson(String object, Type type) {
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
