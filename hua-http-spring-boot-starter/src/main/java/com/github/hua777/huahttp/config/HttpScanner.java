package com.github.hua777.huahttp.config;

import com.github.hua777.huahttp.annotation.HuaHttp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

public class HttpScanner extends ClassPathBeanDefinitionScanner {

    static Logger log = LoggerFactory.getLogger(HttpScanner.class);

    public HttpScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected void registerDefaultFilters() {
        // 自定义注解过滤
        this.addIncludeFilter(new AnnotationTypeFilter(HuaHttp.class));
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        log.info("HuaHttp 扫描路径（{}）下的 @HuaHttp", String.join(",", basePackages));
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder beanDefinition : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) beanDefinition.getBeanDefinition();
            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName());
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            definition.setBeanClass(HttpFactory.class);
        }
        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        // 自定义扫描过滤
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
}
