package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.aware.HuaHttpHandlerConfigAware;
import com.github.hua777.huahttp.config.aop.HttpHandlerConfig;
import com.github.hua777.huahttp.property.HttpProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;

import java.lang.reflect.Proxy;

public class HuaHttpFactory<T> implements FactoryBean<T>, BeanFactoryAware, HuaHttpHandlerConfigAware {

    static Logger log = LoggerFactory.getLogger(HuaHttpFactory.class);

    public HuaHttpFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    Class<T> interfaceClass;

    Environment environment;
    HttpProperty httpProperty;
    HttpHandlerConfig httpHandlerConfig;

    BeanFactory beanFactory;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setHttpProperty(HttpProperty httpProperty) {
        this.httpProperty = httpProperty;
    }

    @Override
    public void setHttpHandlerConfig(HttpHandlerConfig httpHandlerConfig) {
        this.httpHandlerConfig = httpHandlerConfig;
    }

    @Override
    public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull
    T getObject() {
        long start = System.currentTimeMillis();
        T object = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{
                interfaceClass
        }, new HttpHandler()
                .setEnvironment(environment)
                .setHttpProperty(httpProperty)
                .setHttpHandlerConfig(httpHandlerConfig)
                .setInterfaceClass(interfaceClass)
                .setBeanFactory(beanFactory));
        long end = System.currentTimeMillis();
        log.info("HuaHttp 为您生产 Bean {}（{}s）", interfaceClass.getName(), ((end - start) / 1000.0F));
        return object;
    }

    @Override
    public @NotNull
    Class<T> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
