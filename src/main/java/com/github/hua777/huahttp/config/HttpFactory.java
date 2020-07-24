package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.property.HttpProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.env.Environment;

import java.lang.reflect.Proxy;

public class HttpFactory<T> implements FactoryBean<T> {

    static Logger log = LoggerFactory.getLogger(HttpFactory.class);

    public HttpFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    Class<T> interfaceClass;

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

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull T getObject() {
        log.info("生产 Bean {}", interfaceClass.getName());
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{
                interfaceClass
        }, new HttpHandler()
                .setEnv(env)
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
