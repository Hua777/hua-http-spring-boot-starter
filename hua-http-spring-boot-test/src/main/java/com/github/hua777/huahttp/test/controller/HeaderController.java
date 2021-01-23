package com.github.hua777.huahttp.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/header")
public class HeaderController {
    @GetMapping("/case/1")
    public Map<String, String> case1(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        Enumeration<String> e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            result.put(key, request.getHeader(key));
        }
        return result;
    }
}
