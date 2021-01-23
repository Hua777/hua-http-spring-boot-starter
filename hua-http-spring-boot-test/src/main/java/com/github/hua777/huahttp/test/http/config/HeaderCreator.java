package com.github.hua777.huahttp.test.http.config;

import com.github.hua777.huahttp.test.global.Constant;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Configuration
public class HeaderCreator implements Supplier<Map<String, Object>> {
    @Override
    public Map<String, Object> get() {
        return new HashMap<String, Object>() {{
            put(Constant.CREATE_HEADER, Constant.TEST);
        }};
    }
}
