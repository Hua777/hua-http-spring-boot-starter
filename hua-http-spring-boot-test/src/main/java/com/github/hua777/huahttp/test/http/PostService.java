package com.github.hua777.huahttp.test.http;

import com.github.hua777.huahttp.annotation.HuaHttp;
import com.github.hua777.huahttp.annotation.extend.method.HuaPost;
import com.github.hua777.huahttp.annotation.extend.param.HuaBody;
import com.github.hua777.huahttp.annotation.extend.param.HuaQuery;
import com.github.hua777.huahttp.test.bean.TestRequest;

import java.time.LocalDateTime;

@HuaHttp("${url.local}/post-or-put")
public interface PostService {

    @HuaPost(url = "/case/1")
    String case1(@HuaQuery String query1, @HuaQuery LocalDateTime query2, @HuaBody(full = true) TestRequest request);

    @HuaPost(url = "/case/1")
    String case1plat(@HuaQuery String query1, @HuaQuery LocalDateTime query2,
                     @HuaBody String body1,
                     @HuaBody LocalDateTime body2);

    @HuaPost(url = "/case/2", form = true)
    String case2(@HuaBody(full = true) TestRequest request);

    @HuaPost(url = "/case/2", form = true)
    String case2plat(@HuaBody String body1, @HuaBody LocalDateTime body2);
}
