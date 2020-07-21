package com.github.hua777.huahttp.config;

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

    HttpHandler httpHandler;

    public void setEnv(Environment env) {
        this.env = env;
        httpHandler = new HttpHandler(env, interfaceClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull T getObject() {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{
                interfaceClass
        }, httpHandler);
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
