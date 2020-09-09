package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.aware.HuaHttpHandlerConfigAware;
import com.github.hua777.huahttp.property.HttpHandlerConfig;
import com.github.hua777.huahttp.property.HttpProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;

import java.lang.reflect.Proxy;

public class HuaHttpFactory<T> implements FactoryBean<T>, HuaHttpHandlerConfigAware {

    static Logger log = LoggerFactory.getLogger(HuaHttpFactory.class);

    public HuaHttpFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    Class<T> interfaceClass;

    Environment environment;
    HttpProperty httpProperty;
    HttpHandlerConfig httpHandlerConfig;

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

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull T getObject() {
        log.info("HuaHttp 为您生产 Bean {}", interfaceClass.getName());
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{
                interfaceClass
        }, new HttpHandler()
                .setEnvironment(environment)
                .setHttpProperty(httpProperty)
                .setHttpHandlerConfig(httpHandlerConfig)
                .setInterfaceClass(interfaceClass));
    }

    @Override
    public @NotNull Class<T> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
