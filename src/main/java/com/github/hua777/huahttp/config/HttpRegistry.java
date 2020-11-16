package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.property.HttpProperty;
import com.github.hua777.huahttp.property.HttpPropertyExtend;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

public class HttpRegistry implements BeanDefinitionRegistryPostProcessor,
        ApplicationContextAware, BeanFactoryAware, EnvironmentAware {

    static Logger log = LoggerFactory.getLogger(HttpRegistry.class);

    ApplicationContext applicationContext;
    Environment environment;
    BeanFactory beanFactory;

    HttpProperty httpProperty;

    public void setHttpProperty(HttpProperty httpProperty) {
        this.httpProperty = httpProperty;
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
        List<String> defaultScanPackages = AutoConfigurationPackages.get(beanFactory);
        if (httpProperty.getScanPackages() != null) {
            defaultScanPackages.addAll(Arrays.asList(httpProperty.getScanPackages().split(",")));
        }
        try {
            HttpPropertyExtend httpPropertyExtend = beanFactory.getBean(HttpPropertyExtend.class);
            defaultScanPackages.addAll(httpPropertyExtend.getMoreScanPackages());
        } catch (Exception ignored) {

        }
        HttpScanner scanner = new HttpScanner(registry);
        scanner.setEnvironment(environment);
        scanner.setHttpProperty(httpProperty);
        scanner.setResourceLoader(applicationContext);
        scanner.doScan(defaultScanPackages.toArray(new String[0]));
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
