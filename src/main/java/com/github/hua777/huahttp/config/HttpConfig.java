package com.github.hua777.huahttp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class HttpConfig {

    @Bean
    @ConditionalOnMissingBean(HttpRegistry.class)
    public static HttpRegistry openAPIRegistry(Environment env) {
        return new HttpRegistry(env);
    }

}
