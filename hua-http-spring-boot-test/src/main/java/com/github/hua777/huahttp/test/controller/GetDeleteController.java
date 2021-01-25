package com.github.hua777.huahttp.test.controller;

import com.github.hua777.huahttp.test.global.Constant;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/get-or-delete")
public class GetDeleteController {
    @RequestMapping(path = "/case/1", method = {RequestMethod.GET, RequestMethod.DELETE})
    public String case1() {
        return Constant.TEST;
    }

    @RequestMapping(path = "/case/2", method = {RequestMethod.GET, RequestMethod.DELETE})
    public String case2(@RequestParam String query) {
        return query;
    }

    @RequestMapping(path = "/case/3/{path}", method = {RequestMethod.GET, RequestMethod.DELETE})
    public String case3(@PathVariable String path) {
        return path;
    }

    @RequestMapping(path = "/case/4/{path}", method = {RequestMethod.GET, RequestMethod.DELETE})
    public String case4(@RequestParam String query, @PathVariable String path) {
        return query + "," + path;
    }

    @RequestMapping(path = "/case/5/{path}", method = {RequestMethod.GET, RequestMethod.DELETE})
    public String case5(@RequestParam String query1, @RequestParam String query2, @PathVariable String path) {
        return query1 + "," + query2 + "," + path;
    }

    @RequestMapping(path = "/case/6", method = {RequestMethod.GET, RequestMethod.DELETE})
    public Integer case6(@RequestParam Integer query) {
        return query * 2;
    }
}
