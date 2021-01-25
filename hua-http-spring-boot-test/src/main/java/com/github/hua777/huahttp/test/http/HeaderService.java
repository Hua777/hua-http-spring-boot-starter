package com.github.hua777.huahttp.test.http;

import com.github.hua777.huahttp.annotation.HuaAop;
import com.github.hua777.huahttp.annotation.HuaHttp;
import com.github.hua777.huahttp.annotation.extend.method.HuaGet;
import com.github.hua777.huahttp.annotation.extend.param.HuaHeader;
import com.github.hua777.huahttp.test.global.Constant;
import com.github.hua777.huahttp.test.http.config.HeaderCreator;
import com.github.hua777.huahttp.test.http.config.HeaderHandler;

import java.util.Map;

@HuaHeader(names = Constant.TOP_HEADER, values = Constant.TEST, create = HeaderCreator.class)
@HuaHttp("${url.local}/header")
public interface HeaderService {
    @HuaAop(HeaderHandler.class)
    @HuaHeader(names = Constant.PARAM_HEADER, values = Constant.TEST)
    @HuaGet(url = "/case/1")
    Map<String, String> case1(@HuaHeader(name = Constant.MY_HEADER) String name);
}
