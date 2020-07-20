package com.github.hua777.huahttp.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

public class HttpRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    public HttpRegistry(Environment env) {
        this.env = env;
    }

    Environment env;
    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
        HttpScanner scanner = new HttpScanner(env, registry);
        scanner.setResourceLoader(applicationContext);
        scanner.doScan(env.getProperty("hua777.spring-boot-starter.http.scan-packages", "*").split(","));
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
