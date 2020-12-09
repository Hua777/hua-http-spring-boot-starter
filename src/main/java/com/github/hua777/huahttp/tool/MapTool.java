package com.github.hua777.huahttp.tool;

import cn.hutool.core.util.StrUtil;

import java.util.Map;

public class MapTool {

    public static void mergeToLeft(Map<String, Object> left, Map<String, Object> right) {
        if (right != null) {
            for (Map.Entry<String, Object> entry : right.entrySet()) {
                if (StrUtil.isNotBlank(entry.getKey()) && entry.getValue() != null) {
                    left.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

}
