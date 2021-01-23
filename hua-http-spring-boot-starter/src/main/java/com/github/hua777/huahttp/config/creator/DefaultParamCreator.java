package com.github.hua777.huahttp.config.creator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DefaultParamCreator implements Supplier<Map<String, Object>> {
    @Override
    public Map<String, Object> get() {
        return new HashMap<>();
    }
}
