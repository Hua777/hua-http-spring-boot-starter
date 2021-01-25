package com.github.hua777.huahttp.test.controller;

import cn.hutool.core.date.DateUtil;
import com.github.hua777.huahttp.test.bean.TestRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/post-or-put")
public class PostPutController {
    @RequestMapping(path = "/case/1", method = {RequestMethod.POST, RequestMethod.PUT})
    public String case1(@RequestParam String query1,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam LocalDateTime query2,
                        @RequestBody TestRequest request) {
        return query1 + "," + DateUtil.format(query2, "yyyyMMddHHmmss")
                + "," + request.getBody1() + "," + DateUtil.format(request.getBody2(), "yyyyMMddHHmmss");
    }

    @RequestMapping(path = "/case/2", method = {RequestMethod.POST, RequestMethod.PUT})
    public String case2(String body1, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime body2) {
        return body1 + "," + DateUtil.format(body2, "yyyyMMddHHmmss");
    }
}
