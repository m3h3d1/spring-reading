package com.xcs.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author xcs
 * @date 2023-09-14 14:17
 **/
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("Modify bean definition");
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("mySimpleBean");
        beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        System.out.println("Changed mySimpleBean from the default singleton to prototype");
        System.out.println("Finished modifying bean definition");
    }
}
