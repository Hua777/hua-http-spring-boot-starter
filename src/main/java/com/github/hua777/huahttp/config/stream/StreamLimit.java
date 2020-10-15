package com.github.hua777.huahttp.config.stream;

import cn.hutool.http.HttpResponse;
import com.github.hua777.huahttp.bean.JsonMan;

import java.lang.reflect.Type;

public interface StreamLimit {
    default long getDataCount(HttpResponse response) {
        return Long.parseLong(response.header("USER-DEFINED-DATA-COUNT"));
    }
}
