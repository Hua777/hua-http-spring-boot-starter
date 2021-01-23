package com.github.hua777.huahttp.test.http;

import com.github.hua777.huahttp.annotation.HuaHttp;
import com.github.hua777.huahttp.annotation.extend.method.HuaGet;
import com.github.hua777.huahttp.annotation.extend.param.HuaPath;
import com.github.hua777.huahttp.annotation.extend.param.HuaQuery;

@HuaHttp("${url.local}/get")
public interface GetService {
    @HuaGet(url = "/case/1")
    String case1();

    @HuaGet(url = "/case/2")
    String case2(String query);

    @HuaGet(url = "/case/2")
    String case2rename(@HuaQuery(name = "query") String name);

    @HuaGet(url = "/case/3/{path}")
    String case3(@HuaPath String path);

    @HuaGet(url = "/case/3/{path}")
    String case3rename(@HuaPath(name = "path") String name);

    @HuaGet(url = "/case/4/{path}")
    String case4(String query, @HuaPath String path);

    @HuaGet(url = "/case/5/{path}")
    String case5(String query1, String query2, @HuaPath String path);
}
