package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.aware.HuaHttpHandlerConfigAware;
import com.github.hua777.huahttp.property.HttpHandlerConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class HttpAwareProcessor implements BeanFactoryPostProcessor {

    static Logger log = LoggerFactory.getLogger(HttpAwareProcessor.class);

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //region HttpHandlerConfig
        setAware(configurableListableBeanFactory, HttpHandlerConfig.class, HuaHttpHandlerConfigAware.class);
        //endregion
    }

    private <T, A> void setAware(ConfigurableListableBeanFactory configurableListableBeanFactory, Class<T> targetClazz, Class<A> awareClazz) {
        String methodName = "set";
        methodName += targetClazz.getSimpleName();
        setAware(configurableListableBeanFactory, targetClazz, awareClazz, methodName);
    }

    private <T, A> void setAware(ConfigurableListableBeanFactory configurableListableBeanFactory, Class<T> targetClazz, Class<A> awareClazz, String methodName) {
        T target = null;
        for (Map.Entry<String, T> targetEntry :
                configurableListableBeanFactory.getBeansOfType(targetClazz).entrySet()) {
            target = targetEntry.getValue();
        }
        for (Map.Entry<String, A> awareEntry :
                configurableListableBeanFactory.getBeansOfType(awareClazz).entrySet()) {
            try {
                Method method = awareEntry.getValue().getClass().getMethod(methodName, targetClazz);
                method.invoke(awareEntry.getValue(), target);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
