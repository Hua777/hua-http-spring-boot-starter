package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.aware.HuaHttpHandlerConfigAware;
import com.github.hua777.huahttp.config.aop.HttpHandlerConfig;
import com.github.hua777.huahttp.config.aop.HttpHandlerSetting;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Map;

public class HttpAwareProcessor implements BeanFactoryPostProcessor {

    static Logger log = LoggerFactory.getLogger(HttpAwareProcessor.class);

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //region HttpHandlerConfig
        HttpHandlerConfig httpHandlerConfig = HttpHandlerSetting::new;
        for (Map.Entry<String, HttpHandlerConfig> targetEntry :
                configurableListableBeanFactory.getBeansOfType(HttpHandlerConfig.class).entrySet()) {
            httpHandlerConfig.getSetting().addOtherSetting(targetEntry.getValue().getSetting());
        }
        for (Map.Entry<String, HuaHttpHandlerConfigAware> awareEntry :
                configurableListableBeanFactory.getBeansOfType(HuaHttpHandlerConfigAware.class).entrySet()) {
            awareEntry.getValue().setHttpHandlerConfig(httpHandlerConfig);
        }
        //endregion
    }
}
