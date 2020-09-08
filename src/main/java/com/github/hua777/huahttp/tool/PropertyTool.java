package com.github.hua777.huahttp.tool;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 参考：https://www.imooc.com/wenda/detail/604035
 * 作者：慕田峪4524236
 */
public class PropertyTool {

    static Logger log = LoggerFactory.getLogger(PropertyTool.class);

    public static <T> T getPropertiesStartingWith(ConfigurableEnvironment environment, String prefix, Type type) {
        Map<String, Object> map = getPropertiesStartingWith(environment, prefix, true);
        return new Gson().fromJson(new Gson().toJson(map), type);
    }

    public static Map<String, Object> getPropertiesStartingWith(ConfigurableEnvironment environment, String prefix, boolean removeMinus) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = getAllProperties(environment);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                String newKey = key.replace(prefix, "").replace(".", "");
                if (removeMinus) {
                    StringBuilder newKeyTmp = new StringBuilder();
                    boolean bigNext = false;
                    for (char c : newKey.toCharArray()) {
                        if (c == '-') {
                            bigNext = true;
                        } else if (bigNext) {
                            newKeyTmp.append(Character.toUpperCase(c));
                            bigNext = false;
                        } else {
                            newKeyTmp.append(c);
                        }
                    }
                    newKey = newKeyTmp.toString();
                }
                result.put(newKey, entry.getValue());
            }
        }
        return result;
    }

    public static Map<String, Object> getAllProperties(ConfigurableEnvironment environment) {
        Map<String, Object> result = new HashMap<>();
        environment.getPropertySources().forEach(ps -> addAll(result, getAllProperties(environment, ps)));
        return result;
    }

    public static Map<String, Object> getAllProperties(ConfigurableEnvironment environment, PropertySource<?> propertySource) {
        Map<String, Object> result = new HashMap<>();
        if (propertySource instanceof CompositePropertySource) {
            CompositePropertySource cps = (CompositePropertySource) propertySource;
            cps.getPropertySources().forEach(ps -> addAll(result, getAllProperties(environment, ps)));
            return result;
        }
        if (propertySource instanceof EnumerablePropertySource<?>) {
            EnumerablePropertySource<?> ps = (EnumerablePropertySource<?>) propertySource;
            Arrays.asList(ps.getPropertyNames()).forEach(key -> {
                try {
                    result.put(key, environment.getProperty(key));
                } catch (Exception ex) {
                    Object originValue = ps.getProperty(key);
                    if (originValue != null) {
                        result.put(key, originValue.toString());
                    } else {
                        log.error("获取环境配置错误！Key：{}", key);
                    }
                }
            });
            return result;
        }
        return result;
    }

    private static void addAll(Map<String, Object> destination, Map<String, Object> source) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            if (destination.containsKey(entry.getKey())) {
                continue;
            }
            destination.put(entry.getKey(), entry.getValue());
        }
    }
}
