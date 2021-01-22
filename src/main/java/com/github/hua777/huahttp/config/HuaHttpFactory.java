package com.github.hua777.huahttp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class HuaHttpFactory<T> implements FactoryBean<T> {

    static Logger log = LoggerFactory.getLogger(HuaHttpFactory.class);

    public HuaHttpFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    Class<T> interfaceClass;

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        long start = System.currentTimeMillis();
        T object = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{
                interfaceClass
        }, new HttpHandler(interfaceClass));
        long end = System.currentTimeMillis();
        log.info("HuaHttp 为您生产 Bean {}（{}s）", interfaceClass.getName(), ((end - start) / 1000.0F));
        return object;
    }

    @Override
    public Class<T> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
