package com.github.hua777.huahttp.config.aop;

import com.google.gson.Gson;

public interface HttpHandlerConfig {

    HttpHandlerSetting getSetting();

    default Gson getGson() {
        return new Gson();
    }

}
