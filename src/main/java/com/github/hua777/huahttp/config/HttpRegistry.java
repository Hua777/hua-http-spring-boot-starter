package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.property.HttpProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

public class HttpRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    static Logger log = LoggerFactory.getLogger(HttpRegistry.class);

    ApplicationContext applicationContext;

    Environment env;
    HttpProperty httpProperty;
    HttpHandlerConfig httpHandlerConfig;

    public void setEnv(Environment env) {
        this.env = env;
    }

    public void setHttpProperty(HttpProperty httpProperty) {
        this.httpProperty = httpProperty;
    }

    public void setHttpHandlerConfig(HttpHandlerConfig httpHandlerConfig) {
        this.httpHandlerConfig = httpHandlerConfig;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
        HttpScanner scanner = new HttpScanner(registry);
        scanner.setEnv(env);
        scanner.setHttpProperty(httpProperty);
        scanner.setHttpHandlerConfig(httpHandlerConfig);
        scanner.setResourceLoader(applicationContext);
        scanner.doScan(httpProperty.getScanPackages().split(","));
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
