package com.github.hua777.huahttp.property;

import com.github.hua777.huahttp.bean.HttpHandlerSetting;
import com.google.gson.Gson;

public interface HttpHandlerConfig {

    HttpHandlerSetting getSetting();

    default Gson getGson() {
        return new Gson();
    }

}
