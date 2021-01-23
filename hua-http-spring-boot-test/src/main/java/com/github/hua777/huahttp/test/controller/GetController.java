package com.github.hua777.huahttp.test.controller;

import com.github.hua777.huahttp.test.global.Constant;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/get")
public class GetController {
    @GetMapping("/case/1")
    public String case1() {
        return Constant.TEST;
    }

    @GetMapping("/case/2")
    public String case2(@RequestParam String query) {
        return query;
    }

    @GetMapping("/case/3/{path}")
    public String case3(@PathVariable String path) {
        return path;
    }

    @GetMapping("/case/4/{path}")
    public String case4(@RequestParam String query, @PathVariable String path) {
        return query + "," + path;
    }

    @GetMapping("/case/5/{path}")
    public String case5(@RequestParam String query1, @RequestParam String query2, @PathVariable String path) {
        return query1 + "," + query2 + "," + path;
    }
}
