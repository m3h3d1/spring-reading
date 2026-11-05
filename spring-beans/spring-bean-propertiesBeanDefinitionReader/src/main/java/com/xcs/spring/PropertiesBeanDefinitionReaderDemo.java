package com.xcs.spring;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * @author xcs
 * @date 2023-11-06 10:09
 **/
public class PropertiesBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(beanFactory);

        // Load bean definitions from the properties file
        reader.loadBeanDefinitions(new ClassPathResource("bean-definitions.properties"));

        // Get the bean
        System.out.println("myBean = " + beanFactory.getBean("myBean"));
        System.out.println("myBean = " + beanFactory.getBean("myBean"));
    }
}
