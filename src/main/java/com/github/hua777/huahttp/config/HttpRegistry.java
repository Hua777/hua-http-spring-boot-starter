package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.config.harder.HttpHarder;
import com.github.hua777.huahttp.property.HttpProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.List;

public class HttpRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    static Logger log = LoggerFactory.getLogger(HttpRegistry.class);

    public static ApplicationContext APP_CONTEXT;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        HttpRegistry.APP_CONTEXT = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
        List<String> defaultScanPackages = AutoConfigurationPackages.get(APP_CONTEXT);
        try {
            HttpProperty httpProperty = APP_CONTEXT.getBean(HttpProperty.class);
            defaultScanPackages.addAll(Arrays.asList(httpProperty.getScanPackages().split(",")));
        } catch (Exception ignored) {

        }
        try {
            HttpHarder httpHarder = APP_CONTEXT.getBean(HttpHarder.class);
            defaultScanPackages.addAll(httpHarder.getMoreScanPackages());
        } catch (Exception ignored) {

        }
        HttpScanner scanner = new HttpScanner(registry);
        scanner.setResourceLoader(APP_CONTEXT);
        scanner.doScan(defaultScanPackages.toArray(new String[0]));
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
