package com.github.hua777.huahttp.bean;

import cn.hutool.core.util.StrUtil;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueMan {

    static Pattern pattern = Pattern.compile("\\$\\{[^}]+\\}");
    List<String> splits = new ArrayList<>();

    private ValueMan() {

    }

    public static ValueMan parse(String originValue) {
        ValueMan result = new ValueMan();
        int prevStart = 0;
        int prevEnd = originValue.length();
        Matcher matcher = pattern.matcher(originValue);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            result.addSplit(originValue.substring(prevStart, start));
            prevStart = end;
            String partValueWithSign = originValue.substring(start, end);
            result.addSplit(partValueWithSign);
        }
        result.addSplit(originValue.substring(prevStart, prevEnd));
        return result;
    }

    public List<String> getSplits() {
        return splits;
    }

    private void addSplit(String split) {
        if (StrUtil.isNotEmpty(split)) {
            splits.add(split);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String string : splits) {
            result.append(string);
        }
        return result.toString();
    }

    public String toString(Environment environment) {
        StringBuilder result = new StringBuilder();
        for (String string : splits) {
            if (string.startsWith("${") && string.endsWith("}")) {
                string = string.substring(2, string.length() - 1);
                string = environment.getProperty(string);
            }
            result.append(string);
        }
        return result.toString();
    }
}
