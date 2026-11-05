package com.xcs.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author xcs
 * @date 2023-09-14 14:17
 **/
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("Start adding bean definition");
        // Create a new BeanDefinition
        BeanDefinition beanDefinition = new RootBeanDefinition(MySimpleBean.class);
        // Register the new bean definition with the registry
        registry.registerBeanDefinition("mySimpleBean", beanDefinition);
        System.out.println("Finished adding bean definition");
    }
}
