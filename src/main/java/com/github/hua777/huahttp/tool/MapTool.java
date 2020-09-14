package com.github.hua777.huahttp.tool;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

public class MapTool {

    public static HashMap<String, String> merge(HashMap<String, String> left, HashMap<String, String> right) {
        if (right != null) {
            for (Map.Entry<String, String> entry : right.entrySet()) {
                if (!StrUtil.isEmpty(entry.getKey()) && !StrUtil.isEmpty(entry.getValue())) {
                    left.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return left;
    }

}
