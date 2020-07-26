package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.property.HttpProperty;
import com.github.hua777.huahttp.tool.PropertyTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

@Configuration
public class HttpConfig {

    static Logger log = LoggerFactory.getLogger(HttpConfig.class);

    @Bean
    @ConditionalOnMissingBean(HttpProperty.class)
    public HttpProperty httpProperty(ConfigurableEnvironment env) {
        return PropertyTool.getPropertiesStartingWith(env, "com.github.hua777.hua-http-spring-boot-starter", HttpProperty.class);
    }

    @Bean
    @ConditionalOnMissingBean(HttpRegistry.class)
    public HttpRegistry httpRegistry(Environment env, HttpProperty httpProperty, @Autowired(required = false) HttpHandlerConfig httpHandlerConfig) {
        if (httpHandlerConfig == null) {
            log.warn("看似没有自定义 HuaHttp 配置。");
        }
        HttpRegistry registry = new HttpRegistry();
        registry.setEnv(env);
        registry.setHttpProperty(httpProperty);
        registry.setHttpHandlerConfig(httpHandlerConfig);
        return registry;
    }

}
