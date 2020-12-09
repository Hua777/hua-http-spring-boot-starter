package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.property.HttpProperty;
import com.github.hua777.huahttp.tool.PropertyTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class HttpConfig {

    static Logger log = LoggerFactory.getLogger(HttpConfig.class);

    @Bean
    @ConditionalOnMissingBean(HttpProperty.class)
    public static HttpProperty httpProperty(ConfigurableEnvironment env) {
        return PropertyTool.getPropertiesStartingWith(
                env, "com.github.hua777.hua-http-spring-boot-starter", HttpProperty.class);
    }

    @Bean
    @ConditionalOnMissingBean(HttpRegistry.class)
    public static HttpRegistry httpRegistry() {
        return new HttpRegistry();
    }

}
